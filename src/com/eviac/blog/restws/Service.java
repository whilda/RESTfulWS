package com.eviac.blog.restws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import connector.MONGODB;

@Path("f")
public class Service {
	@POST
	@Path("/isexist")
	@SuppressWarnings("unchecked")
	public String isUserExist(String jsonString)
	{
		BasicDBObject where_query;
		DBObject find_objek_student;
		DBObject find_objek_supervisor;
		
		JSONObject output_json = new JSONObject();
		JSONObject input_json ;
		String user_name;
		
		try
		{
			DB db = MONGODB.GetMongoDB();
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			input_json = (JSONObject) JSONValue.parse(jsonString);
			user_name = input_json.get("username").toString();
			
			where_query = new BasicDBObject("_id",user_name);
			find_objek_student = collStudent.findOne(where_query);
			find_objek_supervisor = collSupervisor.findOne(where_query);
			
			if (find_objek_student != null || find_objek_supervisor != null)
			{
				output_json.put("code",1);
				output_json.put("message","username sudah terdaftar");
			}
			else
			{
				output_json.put("code",0);
				output_json.put("message","username belum terdaftar");
			}			
		}
		catch (Exception ex)
		{
			output_json.put("code",-1);
			output_json.put("message",ex.toString());
		}
		
		return output_json.toString();
	}
	
	@POST
	@Path("/login")
	@SuppressWarnings("unchecked")
	public String funct_login (String jsonString)
	{
		BasicDBObject where_query;
		DBObject find_objek_student;
		DBObject find_objek_supervisor;
		
		JSONObject output_json = new JSONObject();
		JSONObject input_json ;
		String user_name;
		String password;
		
		try
		{
			DB db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			user_name = input_json.get("username").toString();
			password = GeneralService.md5(input_json.get("password").toString());
			
			
			where_query = new BasicDBObject("_id",user_name).
								append("password", password);
			find_objek_student = collStudent.findOne(where_query);
			find_objek_supervisor = collSupervisor.findOne(where_query);
			
			if (find_objek_student != null)
			{
				output_json.put("code",1);
				output_json.put("message","Login as student");
				output_json.put("username",user_name);
			}else if(find_objek_supervisor != null)
			{
				output_json.put("code",2);
				output_json.put("message","Login as student");
				output_json.put("username",user_name);
			}
			else
			{
				output_json.put("code",0);
				output_json.put("message","Login failue");
				output_json.put("username","");
			}			
		}
		catch (Exception ex)
		{
			output_json.put("code",-1);
			output_json.put("message",ex.toString());
			output_json.put("username","");	
		}
		
		return output_json.toString();
	}
	
	@POST
	@Path("/savefield")
	@SuppressWarnings("unchecked")
	public String SaveField(String jsonString)
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collField = db.getCollection("field");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String name = input_json.get("name").toString();
			String description = input_json.get("description").toString();
			
			if(!GeneralService.IsExistData("_id", name, collField)){
				BasicDBObject objek_db = new BasicDBObject();
				objek_db.put("_id",name);
				objek_db.put("description",description);
				
				collField.insert(objek_db);
			}else{
				BasicDBObject data_object = new BasicDBObject();
				data_object.put("description",description);
				
				BasicDBObject query_object = new BasicDBObject();
				query_object.put("$set",data_object);
				
				collField.updateMulti(new BasicDBObject("_id",name), query_object);
			}
			
			output_json.put("code", 1);
			output_json.put("message","Success");
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	@GET
	@Path("/getallfield/{appkey}/{keysearch}")
	@SuppressWarnings("unchecked")
	public String GetStudent(@PathParam("keysearch") String keySearch, @PathParam("appkey") String appkey) 
	{
		JSONObject output_json = new JSONObject();
		JSONArray data_json = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collField = db.getCollection("field");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
			BasicDBObject query_like = new BasicDBObject();
			query_like.put("_id",  java.util.regex.Pattern.compile(keySearch));
			
			DBCursor cursor = collField.find(query_like);
			while (cursor.hasNext()) {
				data_json.add(cursor.next());
			}
			
			if (data_json.size() == 0)
			{
				output_json.put("code",0);
				output_json.put("message","not found");
				output_json.put("data","");
			}else
			{
				output_json.put("code",1);
				output_json.put("message","Success");
				output_json.put("data",data_json.toString());
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
}

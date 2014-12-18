package com.eviac.blog.restws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import connector.MONGODB;

@Path("s")
public class Student {
	public static int STATUS_IDDLE = -1;
	public static int STATUS_PROPOSE = 0;
	public static int STATUS_ASSIGN = 1;
	public static int STATUS_GRADUATE = 2;
	
	@POST
	@Path("/register")
	@SuppressWarnings("unchecked")
	public String AppReg(String jsonString) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String username = input_json.get("username").toString();
			String password = input_json.get("password").toString();
			String nim = input_json.get("nim").toString();
			String name = input_json.get("name").toString();
			String address = input_json.get("address").toString();
			String handphone = input_json.get("handphone").toString();
			String email = input_json.get("email").toString();
			
			BasicDBObject objek_db = new BasicDBObject();
			objek_db.put("_id",username);
			objek_db.put("password",GeneralService.md5(password));
			objek_db.put("nim",nim);
			objek_db.put("name",name);
			objek_db.put("address",address);
			objek_db.put("handphone",handphone);
			objek_db.put("email",email);
			
			objek_db.put("status",Student.STATUS_IDDLE);
			objek_db.put("supervisor","");
			objek_db.put("thesis","");
			objek_db.put("task","");
			objek_db.put("activity","");
			
			collStudent.insert(objek_db);
			
			output_json.put("code", 1);
			output_json.put("message","Registrasi sukses");
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	@GET
	@Path("/get/{username}/{appkey}")
	@SuppressWarnings("unchecked")
	public String GetStudent(@PathParam("username") String username, @PathParam("appkey") String appkey) 
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
			BasicDBObject where_query = new BasicDBObject("_id",username);
			DBObject find_objek_student = collStudent.findOne(where_query);
			
			if (find_objek_student != null)
			{
				output_json.put("code",1);
				output_json.put("message","Success");
				output_json.put("data",find_objek_student.toString());
			}else
			{
				output_json.put("code",0);
				output_json.put("message","not found");
				output_json.put("data","");
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	@GET
	@Path("/getall/{appkey}")
	@SuppressWarnings("unchecked")
	public String GetAll(@PathParam("appkey") String appkey) 
	{
		JSONObject output_json = new JSONObject();
		JSONArray data_json = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
			DBCursor cursor = collStudent.find();
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
	
	@POST
	@Path("/thesis")
	@SuppressWarnings("unchecked")
	public String CreateThesis(String jsonString) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String topic = input_json.get("topic").toString();
			String title = input_json.get("title").toString();
			String description = input_json.get("description").toString();
			String field = input_json.get("field").toString();
			String username = input_json.get("username").toString();
			
			BasicDBObject ObjectId = new BasicDBObject();
			ObjectId.put("_id",username);
			
			BasicDBObject ObjectSet = new BasicDBObject(); 
			ObjectSet.put("thesis.topic",topic);
			ObjectSet.put("thesis.title",title);
			ObjectSet.put("thesis.description",description);
			ObjectSet.put("thesis.field",field);
			
			BasicDBObject ObjectQuery = new BasicDBObject();
			ObjectId.put("$set", ObjectSet);
			
			collStudent.updateMulti(ObjectId, ObjectQuery);
			
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
	
	@POST
	@Path("/resetthesis")
	@SuppressWarnings("unchecked")
	public String ResetThesis(String jsonString) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String username = input_json.get("username").toString();
			
			BasicDBObject ObjectId = new BasicDBObject();
			ObjectId.put("_id",username);
			
			BasicDBObject ObjectSet = new BasicDBObject(); 
			ObjectSet.put("thesis.topic","");
			ObjectSet.put("thesis.title","");
			ObjectSet.put("thesis.description","");
			ObjectSet.put("thesis.field","");
			
			BasicDBObject ObjectQuery = new BasicDBObject();
			ObjectId.put("$set", ObjectSet);
			
			collStudent.updateMulti(ObjectId, ObjectQuery);
			
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
}

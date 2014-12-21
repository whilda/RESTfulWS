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
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import connector.MONGODB;

@Path("su")
public class Supervisor {
	@POST
	@Path("/register")
	@SuppressWarnings("unchecked")
	
	public String regisSupervisor (String jsonString)
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection CollSupervisor = db.getCollection("supervisor");
			
			
			JSONObject input_json =(JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String username = input_json.get("username").toString();
			String password = input_json.get("password").toString();
			String nik = input_json.get("nik").toString();
			String name = input_json.get("name").toString();
			String address = input_json.get("address").toString();
			String handphone = input_json.get("handphone").toString();
			String email = input_json.get("email").toString();
			
			BasicDBObject objek_db = new BasicDBObject();
			objek_db.put("_id",username);
			objek_db.put("password",GeneralService.md5(password));
			objek_db.put("nik",nik);
			objek_db.put("name",name);
			objek_db.put("address",address);
			objek_db.put("handphone",handphone);
			objek_db.put("email",email);
			
			objek_db.put("student",new JSONArray());
			objek_db.put("field",new JSONArray());
			objek_db.put("template",new JSONArray());
			objek_db.put("proposal",new JSONArray());
			
			CollSupervisor.insert(objek_db);
			
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
	public String GetSupervisor(@PathParam("username") String username, @PathParam("appkey") String appkey) 
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
			BasicDBObject where_query = new BasicDBObject("_id",username);
			DBObject find_objek_supervisor = collSupervisor.findOne(where_query);
			
			if (find_objek_supervisor != null)
			{
				output_json.put("code",1);
				output_json.put("message","Success");
				output_json.put("data",find_objek_supervisor.toString());
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
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
			DBCursor cursor = collSupervisor.find();
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
	@Path("/issupervisor")
	@SuppressWarnings("unchecked")
	public String isUserExist(String jsonString)
	{
		BasicDBObject where_query;
		DBObject find_objek_supervisor;
		
		JSONObject output_json = new JSONObject();
		JSONObject input_json ;
		String student;
		String supervisor;
		
		try
		{
			DB db = MONGODB.GetMongoDB();
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collSupervisor);
			
			student = input_json.get("student").toString();
			supervisor = input_json.get("supervisor").toString();
			
			where_query = new BasicDBObject("_id",supervisor);
			find_objek_supervisor = collSupervisor.findOne(where_query);
			
			if (find_objek_supervisor != null)
			{
				String studentString = find_objek_supervisor.get("student").toString();
				JSONArray student_array = (JSONArray) JSONValue.parse(student);
				if(student_array.contains(student)){
					output_json.put("code",1);
					output_json.put("message","true");
				}else{
					output_json.put("code",0);
					output_json.put("message","false");
				}
			}
			else
			{
				throw new Exception("Supervisor not found");
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
	@Path("/response")
	@SuppressWarnings("unchecked")
	public String Propose(String jsonString) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String student = input_json.get("student").toString();
			String supervisor = input_json.get("supervisor").toString();
			String code= input_json.get("code").toString();
			
			UpdateProposeStudent(collStudent, student, supervisor, code);
			UpdateProposeSupervisor(collSupervisor, student, supervisor);
				
			output_json.put("code", 1);
			output_json.put("message","success");
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	// Sini Belum
	private void UpdateProposeSupervisor(DBCollection collSupervisor, String student, String supervisor) {
		BasicDBObject ObjectId = new BasicDBObject();
		ObjectId.put("_id",supervisor);
		
		BasicDBObject ObjectToBeSet = new BasicDBObject();
		ObjectToBeSet.put("username",student);
		ObjectToBeSet.put("thesis",studentObject.get("thesis"));
		
		BasicDBObject ObjectSet = new BasicDBObject(); 
		ObjectSet.put("proposal",ObjectToBeSet);
		
		BasicDBObject ObjectQuery = new BasicDBObject();
		ObjectQuery.put("$push", ObjectSet);
		
		collSupervisor.update(ObjectId, ObjectQuery);
	}

	private void UpdateProposeStudent(DBCollection collStudent, String student,
			String supervisor, String code) {
		BasicDBObject ObjectId = new BasicDBObject();
		ObjectId.put("_id",student);
		
		BasicDBObject ObjectSet = new BasicDBObject();
		if(code.equals("1")){
			ObjectSet.put("status",1);
		}else{
			ObjectSet.put("status",0);
			ObjectSet.put("supervisor","");
		}
		
		BasicDBObject ObjectQuery = new BasicDBObject();
		ObjectId.put("$set", ObjectSet);
		
		collStudent.updateMulti(ObjectId, ObjectQuery);
	}
}

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
			
			objek_db.put("student","");
			objek_db.put("field","");
			objek_db.put("template",email);
			objek_db.put("proposal",email);
			
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
}

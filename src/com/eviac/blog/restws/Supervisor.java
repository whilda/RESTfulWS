package com.eviac.blog.restws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
		try 
		{
			MongoClient mongoClient = MONGODB.GetMongoClient();
			DB db = mongoClient.getDB( "semanticwebservice" );
			DBCollection coll = db.getCollection("application");
			
			// Initiate Parameter
			JSONObject input_json =(JSONObject) JSONValue.parse(jsonString);
			String appKey = input_json.get("appkey").toString();
			String username = input_json.get("username").toString();
			String password = input_json.get("password").toString();
			//byte privilege = Byte.parseByte(input_json.get("privilege").toString());
			
			
			BasicDBObject objek_db = new BasicDBObject();
			objek_db.put("appkey",appKey);
			objek_db.put("username",username);
			objek_db.put("password",password);
			objek_db.put("privilege",2);
			
			coll.insert(objek_db);
			
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
}

package com.eviac.blog.restws;

import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;

import connector.MONGODB;

@Path("s")
public class Student {
	@POST
	@Path("/register")
	@SuppressWarnings("unchecked")
	public String AppReg(String jsonString) 
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
			objek_db.put("privilege",1);
			
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
	
	@POST
	@Path("/isUsernameExist")
	@SuppressWarnings("unchecked")
	
	public String isUserExist(String jsonString)
	{
		BasicDBObject where_query;
		DBObject find_objek;
		JSONObject output_json = new JSONObject();
		JSONObject input_json ;
		String user_name;
		
		try
		{
			MongoClient mongoClient = MONGODB.GetMongoClient();
			DB db = mongoClient.getDB( "semanticwebservice" );
			DBCollection coll = db.getCollection("application");
			
			input_json = (JSONObject) JSONValue.parse(jsonString);
			user_name = input_json.get("username").toString();
			
			where_query = new BasicDBObject("username",user_name);
			find_objek = coll.findOne(where_query);
			
			if (find_objek==null)
			{
				output_json.put("code",0);
				output_json.put("message","username belum terdaftar");
			}
			else
			{
				output_json.put("code",1);
				output_json.put("message","username sudah terdaftar");
			}
			
			
		}
		catch (Exception ex)
		{
			output_json.put("code",-1);
			output_json.put("message",ex.toString());
		}
		
		return output_json.toString();
	}
	
	@GET
	@Path("/{username}/{Appkey}")
	@SuppressWarnings("unchecked")
	public String getStudent(@PathParam("username") String username,@PathParam("AppKey") String appKey) 
	{	
		JSONObject output_json = new JSONObject();
		try
		{
			MongoClient mongoClient = MONGODB.GetMongoClient();
			DB db = mongoClient.getDB( "semanticwebservice" );
			DBCollection coll = db.getCollection("application");
			
			BasicDBObject objek_db = new BasicDBObject("username",username);
			
			DBObject objek_db_find = coll.findOne(objek_db);
			
			if (objek_db_find!=null)
			{
				output_json.put("code",1);
				output_json.put("message","Successs");
				output_json.put("data",objek_db_find.toString());
			}
			else
			{
				output_json.put("code",0);
				output_json.put("message","username or appkey not exist");
			}
		}
		catch (Exception ex)
		{
			output_json.put("code",-1);
			output_json.put("message","username or appkey not exist");
		}
		
		return output_json.toString();
	}
}

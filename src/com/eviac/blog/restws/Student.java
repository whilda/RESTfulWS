package com.eviac.blog.restws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import connector.MONGODB;

@Path("s")
public class Student {
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
			DBCollection coll = db.getCollection("application");
			DBCollection studentColl = db.getCollection("student");
			
			
			JSONObject input_json =(JSONObject) JSONValue.parse(jsonString);
			String appKey = input_json.get("appkey").toString();
			if(!GeneralService.IsExistData("key", appKey, coll)){
				throw new Exception("Appkey was wrong. Please register your application to admin@udinus.com");
			}
			
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
			
			studentColl.insert(objek_db);
			
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
			DB db = MONGODB.GetMongoDB();
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
			DB db = MONGODB.GetMongoDB();
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

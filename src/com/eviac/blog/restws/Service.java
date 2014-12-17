package com.eviac.blog.restws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import connector.MONGODB;

@Path("f")
public class Service {
	@POST
	@Path("/login")
	@SuppressWarnings("unchecked")
	
	public String funct_login (String jsonString)
	{
		JSONObject output_json = new JSONObject();
		String user_id;
		String pass;
		
		
		try
		{
			DB db = MONGODB.GetMongoDB();
			
			DBCollection coll = db.getCollection("application");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			user_id = input_json.get("username").toString();
			pass = input_json.get("password").toString();
			
			
			BasicDBObject objek_where = new BasicDBObject();
			objek_where.put("username",user_id);
			
			DBObject objek_db_find = coll.findOne(objek_where);
			
			if (objek_db_find!=null)
			{
				if ((objek_db_find.get("username").equals(user_id)) && (objek_db_find.get("password").equals(pass)) && (Byte.parseByte(objek_db_find.get("privilege").toString())==1))
				{
					output_json.put("code",1);
					output_json.put("message","Login as Student");
					output_json.put("data",objek_db_find.toString());
				}
				else if ((objek_db_find.get("username").equals(user_id)) && (objek_db_find.get("password").equals(pass)) && (Byte.parseByte(objek_db_find.get("privilege").toString())==2))
				{
					
				}
				else
				{
					output_json.put("code",2);
					output_json.put("message","Login as Supervisor");
					output_json.put("data",objek_db_find.toString());
				}
			}
			else
			{
				output_json.put("code",0);
				output_json.put("message","Login Failure");
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
}

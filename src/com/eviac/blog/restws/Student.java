package com.eviac.blog.restws;

import java.io.Reader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import connector.MONGODB;

@Path("StudentService")
public class Student {
	@GET
	@Path("/GetAllStudent")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetAllStudent() {
		MongoClient mongoClient = MONGODB.GetMongoClient();
		JSONObject output = new JSONObject();
		if(mongoClient != null){
			try{
				DB database = mongoClient.getDB("tutorialdb");
				DBCollection coll = database.getCollection("student");
				
				JSONArray data = new JSONArray();
				DBCursor cursor = coll.find();
				while (cursor.hasNext()) {
					JSONObject obj = (JSONObject) JSONValue.parse(cursor.next().toString());
					data.add(obj);
				}
				output.put("code", 1);
				output.put("data", data.toString());
			}catch(Throwable e){
				output.put("code", -1);
				output.put("message", e.toString());
				e.printStackTrace();
			}
			mongoClient.close();
		}else{
			output.put("code", 0);
			output.put("message", "Mongo DB connection can't established.");
		}
		return output.toString();
	}
}

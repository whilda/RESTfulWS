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
import com.mongodb.MongoClient;

import connector.MONGODB;

@Path("s")
public class Student {
	@POST
	@Path("/template")
	@SuppressWarnings("unchecked")
	public String AppReg(String jsonString) {		
		JSONObject produceObject = new JSONObject();
		try {
			// Initiate Parameter
			JSONObject paramObject =(JSONObject) JSONValue.parse(jsonString);
			String appKey = paramObject.get("appkey").toString();
			String nim = paramObject.get("nim").toString();
			
			// do your stuff here
			
			produceObject.put("code", 1);
			produceObject.put("message", paramObject.toString());
		} catch (Exception e) {
			produceObject.put("code", -1);
			produceObject.put("message", e.toString());
		}
		
		return produceObject.toString();
	}
}

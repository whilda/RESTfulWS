package com.eviac.blog.restws;

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

@Path("r")
public class Registration {	
	
	@POST
	@Path("/app")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	
	public String AppReg(String jsonString) {
		JSONObject outputJsonObj = new JSONObject();
		JSONObject paramObj = (JSONObject) JSONValue.parse(jsonString);
		String name = paramObj.get("AppName").toString();
		
		try {
			MongoClient mongoClient = MONGODB.GetMongoClient();
			DB db = mongoClient.getDB( "semanticwebservice" );
			DBCollection coll = db.getCollection("application");
			if(!IsExistData("_id" , name, coll)){
				String key = GetKey(coll);
				coll.insert(new BasicDBObject("_id",name).append("key", key));
				outputJsonObj.put("code", 1);
			    outputJsonObj.put("message", key);
			}else{
				outputJsonObj.put("code", 0);
			    outputJsonObj.put("message", "Application name already taken. Try another name.");
			}
		} catch (Exception e) {
			outputJsonObj.put("code", -1);
		    outputJsonObj.put("message", e.toString());
		}		
		return outputJsonObj.toString();
	}

	private String GetKey(DBCollection coll) {
		String temp_key = RandomStringUtils.randomAlphanumeric(30);
		while(IsExistData("key", temp_key, coll)){
			temp_key = RandomStringUtils.randomAlphanumeric(30);
		}
		return temp_key;
	}

	private Boolean IsExistData(String key, String value, DBCollection coll) 
	{
		BasicDBObject whereQuery = new BasicDBObject(key, value);
		DBCursor cursor = coll.find(whereQuery);
		Boolean result = false;
		while(cursor.hasNext()) {
			cursor.next();
		    result = true;
		}
		return result;
	}
	
	@GET
	@Path("/appkey/{AppKey}")
	@SuppressWarnings("unchecked")
	public String AppKey(@PathParam("AppKey") String appKey) 
	{		
		JSONObject outputJsonObj = new JSONObject();
		outputJsonObj.put("code", 1);
	    outputJsonObj.put("data", appKey);
		return outputJsonObj.toString();
	}
}
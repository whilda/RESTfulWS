package com.eviac.blog.restws;

import javax.ws.rs.Consumes;
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

import connector.MONGODB;

@Path("g")
public class GeneralService {	
	public static String adminContact = "admin@udinus.com";
	
	@POST
	@Path("/app")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	
	public String AppReg(String jsonString) {
		JSONObject outputJsonObj = new JSONObject();
		JSONObject paramObj = (JSONObject) JSONValue.parse(jsonString);
		String name = paramObj.get("AppName").toString();
		
		try {
			DB db = MONGODB.GetMongoDB();
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

	public static Boolean IsExistData(String key, String value, DBCollection coll) 
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
	public static String getHash(String txt, String hashType) {
        try {
                    java.security.MessageDigest md = java.security.MessageDigest.getInstance(hashType);
                    byte[] array = md.digest(txt.getBytes());
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < array.length; ++i) {
                        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
                 }
                    return sb.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
                //error action
            }
            return null;
    }

    public static String md5(String txt) {
        return GeneralService.getHash(txt, "MD5");
    }
    
    public static void AppkeyCheck(String appKey,DBCollection coll) throws Exception{
		if(!GeneralService.IsExistData("key", appKey, coll)){
			throw new Exception("Appkey was wrong. Please register your application to "+GeneralService.adminContact);
		}
    }
}
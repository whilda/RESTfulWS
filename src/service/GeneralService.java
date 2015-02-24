package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import connector.MONGODB;

@Path("g")
public class GeneralService {	
	
public static String adminContact = "admin@udinus.com";
public static String dirAttachment = "/files";
public static String dirFinalWork = "/final";
	
	@POST
	@Path("/app")
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
    
    public static DBObject GetDBObjectFromId(DBCollection collection, String _id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id",_id);
		return collection.findOne(query);
	}
    
    public static String TokenCheck(String token,DBCollection collToken) throws Exception{
    	String output = "";
    	DBObject tokenObj = (DBObject) collToken.findOne(new BasicDBObject("token",token));
    	if(tokenObj == null){
    		throw new Exception("Token is wrong.");
    	}else{
    		Date valid_date = (Date) tokenObj.get("valid_date");
			Date today = new Date();
			if(today.after(valid_date)){
				throw new Exception("Token was expired. Please login again");
			}else{
				DBObject objectId = new BasicDBObject("_id", tokenObj.get("_id"));
				DBObject objectToSet = new BasicDBObject("valid_date", DateUtils.addMonths(new Date(), Service.tokenLength));
				DBObject objectSet = new BasicDBObject("$set", objectToSet);
				
				collToken.update(objectId, objectSet);
				output = tokenObj.get("_id").toString();
			}
    	}
    	return output;
    }
    
    private String GetKey(DBCollection coll) {
		JSONObject objectDB = null;
		String tempKey = "";
		do{
			tempKey = RandomStringUtils.randomAlphanumeric(20);
			objectDB = (JSONObject) coll.findOne(new BasicDBObject("key",tempKey));
		}while(objectDB != null);
		return tempKey;
	}
    
    public static String GetTokenID(DBCollection coll) {
		JSONObject objectDB = null;
		String tempKey = "";
		do{
			tempKey = RandomStringUtils.randomAlphanumeric(20);
			objectDB = (JSONObject) coll.findOne(new BasicDBObject("token",tempKey));
		}while(objectDB != null);
		return tempKey;
	}
    public static String GetFileID(String fileName,DBCollection coll, String dirPath) {
    	JSONObject objectDB = null;
		String tempKey = "";
		do{
			tempKey = RandomStringUtils.randomAlphanumeric(20);
			objectDB = (JSONObject) coll.findOne(new BasicDBObject("_id",tempKey));
		}while(objectDB != null);
		
		BasicDBObject obj = new BasicDBObject()
			.append("_id",tempKey)
			.append("filename", fileName)
			.append("fullpath", dirPath+"/"+tempKey)
			.append("upload_date", new Date());
		coll.insert(obj);
		return tempKey;
    }
    public static String GetTaskID(DBCollection coll,String username) {
		JSONObject objectDB = null;
		String tempKey = "";
		do{
			tempKey = RandomStringUtils.randomAlphanumeric(5);
			objectDB = (JSONObject) coll.findOne(new BasicDBObject("_id",username).append("task.id_task", tempKey));
		}while(objectDB != null);
		return tempKey;
	}
    
    public static String GetCommentID(DBCollection coll,String username,String task) {
		JSONObject objectDB = null;
		String tempKey = "";
		do{
			tempKey = RandomStringUtils.randomAlphanumeric(5);
			objectDB = (JSONObject) coll.findOne(new BasicDBObject("_id",username)
				.append("task.id_task", task)
				.append("task.comment.id_comment", tempKey)
				);
		}while(objectDB != null);
		return tempKey;
	}
    
    public static String GetFileName(Part part) {
        String partHeader = part.getHeader("content-disposition");
        for (String cd : partHeader.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    
	@GET
    @Path("/download")
    @SuppressWarnings("unchecked")
    public Response getFile(@Context HttpServletRequest request){
    	StreamingOutput stream = null;
    	File file = null;
    	JSONObject output_obj = new JSONObject();
    	try{
    		String fileID = request.getParameter("id");
    		
    		DB db = MONGODB.GetMongoDB();
    		DBCollection collFile = db.getCollection("file");
    		DBObject fileObj = GetDBObjectFromId(collFile, fileID);
    		if(fileObj == null) throw new Exception("file Not Found!"); 
    		String filePath = fileObj.get("fullpath").toString();
    		
            file = new File(filePath);
            try {
	            final InputStream in = new FileInputStream(file);
	            stream = new StreamingOutput() {
	                public void write(OutputStream out) throws IOException, WebApplicationException {
	                    try {
	                        int read = 0;
	                            byte[] bytes = new byte[1024];
	
	                            while ((read = in.read(bytes)) != -1) {
	                                out.write(bytes, 0, read);
	                            }
	                    } catch (Exception e) {
	                        throw new WebApplicationException(e);
	                    }
	                }
	            };
	            in.close();
	        } catch (FileNotFoundException e) {
	        	throw new Exception(e.toString());
	        }
            return Response.ok(stream).header("content-disposition","attachment; filename = "+fileObj.get("filename").toString()).build();
    	}catch(Exception ex){
    		output_obj.put("code", -1);
    		output_obj.put("message", ex.toString());
    		return Response.ok(output_obj.toString()).build();
    	}
    }
}
























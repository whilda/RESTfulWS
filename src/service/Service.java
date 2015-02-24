package service;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import connector.MONGODB;

@Path("f")
public class Service {
	public static int COMMENT_TYPE_SUPERVISOR_INSTRUCT = 11;
	public static int COMMENT_TYPE_SUPERVISOR_COMMENT = 12;
	public static int COMMENT_TYPE_SUPERVISOR_CLARIFY = 13;
	public static int COMMENT_TYPE_STUDENT_COMMENT = 21;
	public static int COMMENT_TYPE_STUDENT_ASKING = 22;
	
	public static int tokenLength = 1;
	
	@POST
	@Path("/auth")
	@SuppressWarnings("unchecked")
	public String Authentication(String jsonString)
	{
		BasicDBObject where_query;
		DBObject find_objek_student;
		DBObject find_objek_supervisor;
		
		JSONObject output_json = new JSONObject();
		JSONObject input_json ;
		String user_name;
		String password;
		
		try
		{
			DB db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collToken = db.getCollection("token");
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			user_name = input_json.get("username").toString();
			password = GeneralService.md5(input_json.get("password").toString());
			
			where_query = new BasicDBObject("_id",user_name).
								append("password", password);
			find_objek_student = collStudent.findOne(where_query);
			find_objek_supervisor = collSupervisor.findOne(where_query);
			
			if (find_objek_student != null)
			{
				output_json.put("code",1);
				output_json.put("message","Login as student");
				output_json.put("token",GetToken(collToken,user_name));
				output_json.put("username",user_name);
			}else if(find_objek_supervisor != null)
			{
				output_json.put("code",2);
				output_json.put("message","Login as supervisor");
				output_json.put("token",GetToken(collToken,user_name));
				output_json.put("username",user_name);
			}
			else
			{
				output_json.put("code",0);
				output_json.put("message","Login failed");
				output_json.put("username","");
			}
		}
		catch (Exception ex)
		{
			output_json.put("code",-1);
			output_json.put("message",ex.toString());
			output_json.put("username","");	
		}
		
		return output_json.toString();
	}
	
	private String GetToken(DBCollection collToken,String username) {
		String output = "";
		DBObject tokenObj = collToken.findOne(new BasicDBObject("_id",username));
		if(tokenObj == null){
			output = GeneralService.GetTokenID(collToken);
			
			BasicDBObject obj = new BasicDBObject()
			.append("_id", username)
			.append("token", output)
			.append("valid_date", DateUtils.addMonths(new Date(), tokenLength));
			collToken.insert(obj);
		}else{
			Date valid_date = (Date) tokenObj.get("valid_date");
			Date today = new Date();
			if(today.before(valid_date)){
				output = tokenObj.get("token").toString();
				
				DBObject objectFind	= new BasicDBObject("_id",username);
				DBObject objectToSet= new BasicDBObject("valid_date", DateUtils.addMonths(new Date(), tokenLength));
				DBObject objectSet= new BasicDBObject("$set",objectToSet);
				collToken.update(objectFind, objectSet);
			}else{
				output = GeneralService.GetTokenID(collToken);
				
				DBObject objectFind	= new BasicDBObject("_id",username);
				DBObject objectToSet= new BasicDBObject("token",output)
				.append("valid_date", DateUtils.addMonths(new Date(), tokenLength));
				DBObject objectSet= new BasicDBObject("$set",objectToSet);
				collToken.update(objectFind, objectSet);
			}
		}
		return output;
	}

	@POST
	@Path("/isexist")
	@SuppressWarnings("unchecked")
	public String IsUsernameExist(String jsonString)
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
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(), collApp);
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
	
	@GET
	@Path("/getallfield/{keysearch}/{appkey}/{token}")
	@SuppressWarnings("unchecked")
	public String SearchField(@PathParam("keysearch") String keySearch, @PathParam("appkey") String appkey, @PathParam("token") String token) 
	{
		JSONObject output_json = new JSONObject();
		JSONArray data_json = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collToken = db.getCollection("token");
			DBCollection collField = db.getCollection("field");
			
			GeneralService.AppkeyCheck(appkey, collApp);
			GeneralService.TokenCheck(token, collToken);
			
			BasicDBObject query_like = new BasicDBObject();
			query_like.put("_id",  java.util.regex.Pattern.compile(keySearch));
			
			DBCursor cursor = collField.find(query_like);
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
	
	@GET
	@Path("/gettask/{student}/{id_task}/{appkey}/{token}")
	@SuppressWarnings("unchecked")
	public String GetTask(@PathParam("student") String student,@PathParam("id_task") String taskId,@PathParam("appkey") String appKey,@PathParam("token") String token) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collToken = db.getCollection("token");
			DBCollection collStudent = db.getCollection("student");
			
			GeneralService.AppkeyCheck(appKey,collApp);
			GeneralService.TokenCheck(token, collToken);
			
			DBObject objectId = new BasicDBObject("_id",student).append("task.id_task", taskId);
			if(objectId != null){
				DBObject findOne = collStudent.findOne(objectId,new BasicDBObject("task.$",1));
				JSONObject obj = (JSONObject) ((JSONArray) JSONValue.parse(findOne.get("task").toString())).get(0);
				
				output_json.put("code", 1);
				output_json.put("message","success");
				output_json.put("data",obj);
			}else
			{
				output_json.put("code", 0);
				output_json.put("message","success");
				output_json.put("data",new JSONObject());
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	@POST
	@Path("/editcomment")
	@Deprecated
	@SuppressWarnings("unchecked")
	public String EditComment(String jsonString) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			int studentId = Integer.parseInt(input_json.get("student").toString());
			int taskId = Integer.parseInt(input_json.get("id_task").toString());
			int commentId = Integer.parseInt(input_json.get("id_comment").toString());
			String type = input_json.get("type").toString();
			String text = input_json.get("text").toString();
			
			DBObject query = new BasicDBObject("_id",studentId)
				.append("task.id_task", taskId);
			DBObject findOne = collStudent.findOne(query);
			if(findOne != null){				
				DBObject objectFind	= new BasicDBObject("_id",studentId)
					.append("task.id_task", taskId)
					.append("task.comment.id_comment", commentId);
				DBCursor cursor = collStudent.find(objectFind);
				DBObject student = cursor.next();
				JSONArray tasks = (JSONArray) JSONValue.parse(student.get("task").toString());
				JSONArray comments = GetComment(tasks,taskId);
				int index = GetIndexComment(comments,commentId);
				if(index != -1){
					DBObject objectToSet= new BasicDBObject("task.$.comment."+index+".text",text)
													.append("task.$.comment."+index+".type", type);
					DBObject objectSet= new BasicDBObject("$set",objectToSet);
					collStudent.update(objectFind, objectSet,false,true);
				}else{
					output_json.put("code", 0);
					output_json.put("message","Parameter value was fault");
				}
				
				output_json.put("code", 1);
				output_json.put("message","success");
			}else{
				output_json.put("code", 0);
				output_json.put("message","Parameter value was fault");
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	private int GetIndexComment(JSONArray comment,int commentID) {
		int i = 0;
		Boolean find = false;
		while(i<comment.size() && !find){
			JSONObject task = (JSONObject) JSONValue.parse(comment.get(i).toString());
			if(Integer.parseInt(task.get("id_comment").toString()) == commentID){
				find = true;
			}else
				i++;
		}
		
		if(i == comment.size()) i = -1;
		return i;
	}
	
	private JSONArray GetComment(JSONArray tasks,int taskID) {
		JSONArray comments = null;
		int i = 0;
		Boolean find = false;
		while(i<tasks.size() && !find){
			JSONObject task = (JSONObject) JSONValue.parse(tasks.get(i).toString());
			if(Integer.parseInt(task.get("id_task").toString()) == taskID){
				find = true;
				comments = (JSONArray) JSONValue.parse(task.get("comment").toString());
			}else
				i++;
		}
		return comments;
	}
	
	@POST
	@Path("/deletecomment")
	@SuppressWarnings("unchecked")
	public String DeleteComment(String jsonString) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collToken = db.getCollection("token");
			DBCollection collStudent = db.getCollection("student");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			String username = GeneralService.TokenCheck(input_json.get("token").toString(), collToken);
			String student = input_json.get("student").toString();
			String taskId = input_json.get("id_task").toString();
			String commentId = input_json.get("id_comment").toString();
			
			DBObject query = new BasicDBObject("_id",student)
				.append("task.id_task", taskId)
				.append("task.comment.id_comment", commentId);
			DBObject findOne = collStudent.findOne(query);
			
			JSONArray taskArray = (JSONArray) JSONValue.parse(findOne.get("task").toString());
			JSONObject taskObject = (JSONObject) JSONValue.parse(taskArray.get(0).toString());
			JSONArray commentArray = (JSONArray) JSONValue.parse(taskObject.get("comment").toString());
			JSONObject commentObject = (JSONObject) JSONValue.parse(commentArray.get(0).toString());
			String commentedBy = commentObject.get("by").toString();
			
			if(findOne.equals(null)) {
				if(username.equals(commentedBy)) {
					DBObject objectId = new BasicDBObject("_id",student)
						.append("task.id_task", taskId);
					DBObject objectToSet= new BasicDBObject("task.$.comment",new BasicDBObject("id_comment",commentId));
					DBObject objectSet= new BasicDBObject("$pull",objectToSet);
					collStudent.update(objectId, objectSet);
				
					output_json.put("code", 1);
					output_json.put("message", "Success");
				}
				else {
					output_json.put("code", 0);
					output_json.put("message", "False Comment");
				}
			}
			else {
				output_json.put("code", 0);
				output_json.put("message", "Cannot Find Comment");
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
	@Path("/search/{keysearch}/{appkey}/{token}")
	@SuppressWarnings("unchecked")
	public String SearchSupervisor(@PathParam("keysearch") String key, @PathParam("appkey") String appkey, @PathParam("token") String token) 
	{		
		JSONObject output_json = new JSONObject();
		JSONArray output_data = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collToken = db.getCollection("token");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			GeneralService.TokenCheck(token, collToken);
			
			DBCursor cursor = collSupervisor.find(new BasicDBObject("field", java.util.regex.Pattern.compile(key)));
			while (cursor.hasNext()) {
			    DBObject currObj = cursor.next();
			    JSONObject tempObj = (JSONObject) JSONValue.parse(currObj.toString());
			    output_data.add(tempObj);
			}
			if(cursor.size() == 0) {
				output_json.put("code", 0);
				output_json.put("message","No Data");
				output_json.put("data", output_data);
			}
			else {
				output_json.put("code", 1);
				output_json.put("message","Success");
				output_json.put("data", output_data);
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
			output_json.put("data", new JSONArray());
		}
		
		return output_json.toString();
	}

	@GET
	@Path("/getgraduated/{supervisor}/{appkey}/{token}")
	@SuppressWarnings("unchecked")
	public String GetGraduated(@PathParam("supervisor") String supervisor, @PathParam("appkey") String appkey,
			@PathParam("token") String token) 
	{		
		JSONObject output_json = new JSONObject();
		JSONArray output_data = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collToken = db.getCollection("token");
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			GeneralService.TokenCheck(token, collToken);
			
			DBObject supervisorObj = GeneralService.GetDBObjectFromId(collSupervisor, supervisor);
			JSONArray arr = (JSONArray) JSONValue.parse(supervisorObj.get("graduated").toString());
			
			DBCursor cursor = collStudent.find(new BasicDBObject("_id",new BasicDBObject("$in",arr)));
			while (cursor.hasNext()) {
			    DBObject currObj = cursor.next();
			    JSONObject tempObj = (JSONObject) JSONValue.parse(currObj.toString());
			    output_data.add(tempObj);
			}
			
			if(output_data.size() == 0) {
				output_json.put("code", 0);
				output_json.put("data", output_data);
				output_json.put("message","No Data");
			}
			else {
				output_json.put("code", 1);
				output_json.put("data", output_data);
				output_json.put("message","Success");
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("data", new JSONArray());
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	@GET
	@Path("/getungraduated/{supervisor}/{appkey}/{token}")
	@SuppressWarnings("unchecked")
	public String GetUnGraduated(@PathParam("supervisor") String supervisor, @PathParam("appkey") String appkey,
			@PathParam("token") String token) 
	{		
		JSONObject output_json = new JSONObject();
		JSONArray output_data = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collToken = db.getCollection("token");
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			GeneralService.TokenCheck(token, collToken);
			
			DBObject supervisorObj = GeneralService.GetDBObjectFromId(collSupervisor, supervisor);
			JSONArray arr = (JSONArray) JSONValue.parse(supervisorObj.get("student").toString());
			
			DBCursor cursor = collStudent.find(new BasicDBObject("_id",new BasicDBObject("$in",arr)));
			while (cursor.hasNext()) {
			    DBObject currObj = cursor.next();
			    JSONObject tempObj = (JSONObject) JSONValue.parse(currObj.toString());
			    output_data.add(tempObj);
			}

			if(output_data.size() == 0) {
				output_json.put("code", 0);
				output_json.put("data", output_data);
				output_json.put("message","No Data");
			}
			else {
				output_json.put("code", 1);
				output_json.put("data", output_data);
				output_json.put("message","Success");
			}
		}
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("data", new JSONArray());
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
}

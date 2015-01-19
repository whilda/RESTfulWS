package com.eviac.blog.restws;

import java.io.InputStream;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import connector.MONGODB;

@Path("f")
public class Service {
	public static String COMMENT_TYPE_SUPERVISOR_INSTRUCT = "Instruct";
	public static String COMMENT_TYPE_SUPERVISOR_COMMENT = "Comment-Su";
	public static String COMMENT_TYPE_SUPERVISOR_CLARIFY = "Clarify";
	public static String COMMENT_TYPE_STUDENT_COMMENT = "Comment-S";
	public static String COMMENT_TYPE_STUDENT_ASKING = "Ask";
	
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
	
	@POST
	@Path("/auth")
	@SuppressWarnings("unchecked")
	public String funct_login (String jsonString)
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
				output_json.put("username",user_name);
			}else if(find_objek_supervisor != null)
			{
				output_json.put("code",2);
				output_json.put("message","Login as student");
				output_json.put("username",user_name);
			}
			else
			{
				output_json.put("code",0);
				output_json.put("message","Login failue");
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
	
	@POST
	@Path("/savefield")
	@SuppressWarnings("unchecked")
	public String SaveField(String jsonString)
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collField = db.getCollection("field");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String name = input_json.get("name").toString();
			String description = input_json.get("description").toString();
			
			if(!GeneralService.IsExistData("_id", name, collField)){
				BasicDBObject objek_db = new BasicDBObject();
				objek_db.put("_id",name);
				objek_db.put("description",description);
				
				collField.insert(objek_db);
			}else{
				BasicDBObject data_object = new BasicDBObject();
				data_object.put("description",description);
				
				BasicDBObject query_object = new BasicDBObject();
				query_object.put("$set",data_object);
				
				collField.updateMulti(new BasicDBObject("_id",name), query_object);
			}
			
			output_json.put("code", 1);
			output_json.put("message","Success");
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}
	
	@GET
	@Path("/getallfield/{appkey}/{keysearch}")
	@SuppressWarnings("unchecked")
	public String GetStudent(@PathParam("keysearch") String keySearch, @PathParam("appkey") String appkey) 
	{
		JSONObject output_json = new JSONObject();
		JSONArray data_json = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collField = db.getCollection("field");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
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
	@Path("/gettask/{student}/{id_task}/{appkey}")
	@SuppressWarnings("unchecked")
	public String GetTask(@PathParam("student") String student,@PathParam("id_task") String taskId,@PathParam("appkey") String appKey) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			GeneralService.AppkeyCheck(appKey,collApp);
			
			DBObject objectID = new BasicDBObject("_id",student).append("task.id_task", taskId);
			if(objectID != null){
				DBObject findOne = collStudent.findOne(objectID,new BasicDBObject("task.$",1));
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
	@Path("/createcomment")
	@SuppressWarnings("unchecked")
	public String InputCode(String jsonString) 
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
			
			// Initiate Parameter
			String student = input_json.get("username").toString();
			String taskId = input_json.get("id_task").toString();
			
			String by = input_json.get("by").toString();
			String type = input_json.get("type").toString();
			String text = input_json.get("text").toString();
			
			DBObject query = new BasicDBObject("_id",student)
				.append("task.id_task", taskId);
			DBObject findOne = collStudent.findOne(query);
			if(findOne != null){
				JSONObject commentObj1 = new JSONObject();
				
				commentObj1.put("id_comment", GeneralService.GetCommentID(collStudent, student, taskId));
				commentObj1.put("by", by);
				commentObj1.put("type", type);
				commentObj1.put("text", text);
				commentObj1.put("post_date", new Date());
				
				DBObject ObjectId = new BasicDBObject();
				ObjectId.put("_id",student);
				ObjectId.put("task.id_task",taskId);
				
				DBObject ObjectToBeSet = new BasicDBObject();
				ObjectToBeSet.put("task.$.comment",commentObj1);
				
				DBObject ObjectQuery = new BasicDBObject();
				ObjectQuery.put("$push", ObjectToBeSet);
				
				collStudent.update(ObjectId, ObjectQuery);
				
				output_json.put("code", 1);
				output_json.put("message","success");
			}else{
				output_json.put("code", 0);
				output_json.put("message","Task not found");
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
	@Path("/creatework")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@SuppressWarnings("unchecked")
	public String CreateWork(FormDataMultiPart form) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			String appKey = form.getField("appkey").toString();
			GeneralService.AppkeyCheck(appKey,collApp);
			
			// Initiate Parameter
			FormDataBodyPart filePart = form.getField("file");
			String username = form.getField("username").toString();
			String taskid = form.getField("id_task").toString();
			
			ContentDisposition headerOfFilePart =  filePart.getContentDisposition();		 
			InputStream fileInputStream = filePart.getValueAs(InputStream.class); 
			String filePath = "./fileupload/"+username+"/"+taskid +"/"+ headerOfFilePart.getFileName(); 
			GeneralService.saveFile(fileInputStream, filePath); 
			
			JSONObject fileObj = new JSONObject();
			fileObj.put("filename", headerOfFilePart.getFileName());
			fileObj.put("url", filePath);
			fileObj.put("upload_date", new Date());
			
			DBObject ObjectId = new BasicDBObject();
			ObjectId.put("_id",username);
			ObjectId.put("task.id_task",taskid);
			
			DBObject ObjectToBeSet = new BasicDBObject();
			ObjectToBeSet.put("task.$.file",fileObj);
			
			DBObject ObjectQuery = new BasicDBObject();
			ObjectQuery.put("$push", ObjectToBeSet);
			
			collStudent.update(ObjectId, ObjectQuery);
			
			output_json.put("code", 1);
			output_json.put("message","success");
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
	@Path("/removecomment")
	@SuppressWarnings("unchecked")
	public String RemoveComment(String jsonString) 
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
			
			DBObject query = new BasicDBObject("_id",studentId)
				.append("task.id_task", taskId);
			DBObject findOne = collStudent.findOne(query);
			if(findOne != null){				
				DBObject objectFind	= new BasicDBObject("_id",studentId)
					.append("task.id_task", taskId);
				DBObject objectToSet= new BasicDBObject("task.$.comment",new BasicDBObject("id_comment",commentId));
				DBObject objectSet= new BasicDBObject("$pull",objectToSet);
				collStudent.update(objectFind, objectSet);
			
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
}

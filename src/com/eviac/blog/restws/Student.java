package com.eviac.blog.restws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import connector.MONGODB;

@Path("s")
public class Student {
	public static int STATUS_IDDLE = -1;
	public static int STATUS_PROPOSE = 0;
	public static int STATUS_ASSIGN = 1;
	public static int STATUS_ACTIVE = 2;
	public static int STATUS_GRADUATE = 3;
	
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
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
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
			
			objek_db.put("status",Student.STATUS_IDDLE);
			objek_db.put("supervisor","");
			objek_db.put("thesis.topic", "");
			objek_db.put("thesis.title", "");
			objek_db.put("thesis.description", "");
			objek_db.put("thesis.field", new JSONArray());
			objek_db.put("task",new JSONArray());
			objek_db.put("activity",new JSONArray());
			
			collStudent.insert(objek_db);
			
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
	
	@GET
	@Path("/get/{username}/{appkey}")
	@SuppressWarnings("unchecked")
	public String GetStudent(@PathParam("username") String username, @PathParam("appkey") String appkey) 
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
			BasicDBObject where_query = new BasicDBObject("_id",username);
			DBObject find_objek_student = collStudent.findOne(where_query);
			
			if (find_objek_student != null)
			{
				output_json.put("code",1);
				output_json.put("message","Success");
				output_json.put("data",find_objek_student.toString());
			}else
			{
				output_json.put("code",0);
				output_json.put("message","not found");
				output_json.put("data","");
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
	@Path("/getall/{appkey}")
	@SuppressWarnings("unchecked")
	public String GetAll(@PathParam("appkey") String appkey) 
	{
		JSONObject output_json = new JSONObject();
		JSONArray data_json = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			
			GeneralService.AppkeyCheck(appkey,collApp);
			
			DBCursor cursor = collStudent.find();
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
	
	@POST
	@Path("/thesis")
	@SuppressWarnings("unchecked")
	public String CreateThesis(String jsonString) 
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
			String topic = input_json.get("topic").toString();
			String title = input_json.get("title").toString();
			String description = input_json.get("description").toString();
			String field = input_json.get("field").toString();
			String username = input_json.get("username").toString();
			
			BasicDBObject ObjectId = new BasicDBObject();
			ObjectId.put("_id",username);
			
			BasicDBObject ObjectSet = new BasicDBObject(); 
			ObjectSet.put("thesis.topic",topic);
			ObjectSet.put("thesis.title",title);
			ObjectSet.put("thesis.description",description);
			ObjectSet.put("thesis.field",(JSONArray) JSONValue.parse(field));
			
			BasicDBObject ObjectQuery = new BasicDBObject();
			ObjectId.put("$set", ObjectSet);
			
			collStudent.updateMulti(ObjectId, ObjectQuery);
			
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
	
	@POST
	@Path("/resetthesis")
	@SuppressWarnings("unchecked")
	public String ResetThesis(String jsonString) 
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
			String username = input_json.get("username").toString();
			
			BasicDBObject ObjectId = new BasicDBObject();
			ObjectId.put("_id",username);
			
			BasicDBObject ObjectSet = new BasicDBObject(); 
			ObjectSet.put("thesis.topic","");
			ObjectSet.put("thesis.title","");
			ObjectSet.put("thesis.description","");
			ObjectSet.put("thesis.field",new JSONArray());
			
			BasicDBObject ObjectQuery = new BasicDBObject();
			ObjectId.put("$set", ObjectSet);
			
			collStudent.updateMulti(ObjectId, ObjectQuery);
			
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
	
	@POST
	@Path("/propose")
	@SuppressWarnings("unchecked")
	public String Propose(String jsonString) 
	{		
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collStudent = db.getCollection("student");
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String student = input_json.get("student").toString();
			String supervisor = input_json.get("supervisor").toString();
			
			DBObject studentObject = GeneralService.GetDBObjectFromId(collStudent, student);
			
			if((int) studentObject.get("status") == STATUS_IDDLE){
				UpdateProposeStudent(collStudent, student, supervisor);
				UpdateProposeSupervisor(collSupervisor, student, supervisor, 
						studentObject);
				
				output_json.put("code", 1);
				output_json.put("message","success");
			}else{
				output_json.put("code", 0);
				output_json.put("message","Wrong status");
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();
	}

	private void UpdateProposeSupervisor(DBCollection collSupervisor,
			String student, String supervisor, DBObject studentObject) {
		BasicDBObject ObjectId = new BasicDBObject();
		ObjectId.put("_id",supervisor);
		
		BasicDBObject ObjectToBeSet = new BasicDBObject();
		ObjectToBeSet.put("username",student);
		ObjectToBeSet.put("thesis",studentObject.get("thesis"));
		
		BasicDBObject ObjectSet = new BasicDBObject(); 
		ObjectSet.put("proposal",ObjectToBeSet);
		
		BasicDBObject ObjectQuery = new BasicDBObject();
		ObjectQuery.put("$push", ObjectSet);
		
		collSupervisor.update(ObjectId, ObjectQuery);
	}

	private void UpdateProposeStudent(DBCollection collStudent, String student,
			String supervisor) {
		BasicDBObject ObjectId = new BasicDBObject();
		ObjectId.put("_id",student);
		
		BasicDBObject ObjectSet = new BasicDBObject(); 
		ObjectSet.put("status",0);
		ObjectSet.put("supervisor",supervisor);
		
		BasicDBObject ObjectQuery = new BasicDBObject();
		ObjectId.put("$set", ObjectSet);
		
		collStudent.updateMulti(ObjectId, ObjectQuery);
	}
	
	@POST
	@Path("/inputcode")
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
			DBCollection collSupervisor = db.getCollection("supervisor");
			
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String student = input_json.get("username").toString();
			String code = input_json.get("code").toString();
			
			DBObject studentObject = GeneralService.GetDBObjectFromId(collStudent, student);			
			if((int) studentObject.get("status") == Student.STATUS_ASSIGN){
				DBObject queryObject = new BasicDBObject();
				queryObject.put("_id", studentObject.get("supervisor").toString());
				queryObject.put("template.code", code);
				DBObject supervisorObject = collSupervisor.findOne(queryObject,new BasicDBObject("template.$",1)); 
				if(supervisorObject != null){
					JSONArray templates = (JSONArray) JSONValue.parse(supervisorObject.get("template").toString());
					JSONObject templateObj = (JSONObject ) templates.get(0);
					JSONArray tasks = (JSONArray) JSONValue.parse(templateObj.get("task").toString());
					for(int i = 0; i < tasks.size(); i++){
						JSONObject obj = (JSONObject) tasks.get(i);
						obj.put("id_task", GeneralService.GetTaskID(collStudent,student));
						obj.put("status", 0);
						obj.put("created_date", new Date());
						obj.put("updated_date", new Date());
						obj.put("file", new JSONArray());
						obj.put("comment", new JSONArray());
						
						BasicDBObject ObjectQuery = new BasicDBObject();
						ObjectQuery.put("$push", new BasicDBObject("task",obj));
						
						collStudent.update(new BasicDBObject("_id",student), ObjectQuery);
					}
					
					BasicDBObject ObjectQuery = new BasicDBObject();
					ObjectQuery.put("$set", new BasicDBObject("status",STATUS_ACTIVE));
					
					collStudent.update(new BasicDBObject("_id",student), ObjectQuery);
					
					output_json.put("code", 1);
					output_json.put("message","success");
				}else{
					output_json.put("code", 0);
					output_json.put("message","Wrong code");
				}
			}else{
				output_json.put("code", 0);
				output_json.put("message","Wrong status");
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
			
			ContentDisposition headerOfFilePart =  filePart.getContentDisposition();		 
			InputStream fileInputStream = filePart.getValueAs(InputStream.class); 
			String filePath = "./fileupload/"+username+"/"+ headerOfFilePart.getFileName(); 
			GeneralService.saveFile(fileInputStream, filePath); 
			
			UpdateFinalFile(collStudent, username, headerOfFilePart, filePath);
			ChangeStatus(collStudent, username);
			
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

	private void ChangeStatus(DBCollection collStudent, String username) {
		DBObject ObjectId = new BasicDBObject();
		ObjectId.put("_id",username);
		
		DBObject ObjectToBeSet = new BasicDBObject();
		ObjectToBeSet.put("status",STATUS_GRADUATE);
		
		DBObject ObjectQuery = new BasicDBObject();
		ObjectQuery.put("$set", ObjectToBeSet);
		
		collStudent.update(ObjectId, ObjectQuery);
	}

	private void UpdateFinalFile(DBCollection collStudent, String username,
			ContentDisposition headerOfFilePart, String filePath) {
		JSONObject fileObj = new JSONObject();
		fileObj.put("filename", headerOfFilePart.getFileName());
		fileObj.put("url", filePath);
		fileObj.put("upload_date", new Date());
		
		DBObject ObjectId = new BasicDBObject();
		ObjectId.put("_id",username);
		
		DBObject ObjectToBeSet = new BasicDBObject();
		ObjectToBeSet.put("final",fileObj);
		
		DBObject ObjectQuery = new BasicDBObject();
		ObjectQuery.put("$set", ObjectToBeSet);
		
		collStudent.update(ObjectId, ObjectQuery);
	}
}

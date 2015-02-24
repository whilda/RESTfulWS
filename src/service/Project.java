package service;
// appkey  :  4YjFrLIY5amwajVOKfZH
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import service.GeneralService;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import connector.MONGODB;
@Path("fp")
public class Project {
	@GET
	@Path("/test")
	@SuppressWarnings("unchecked")
	public String IsUnique(@PathParam("appkey") String appkey, @PathParam("token") String token,DBObject nameFile) 
	{
		JSONObject output_json = new JSONObject();
		JSONArray data_json = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collProject = db.getCollection("project");
			
			GeneralService.AppkeyCheck(appkey,collProject);
			
			DBCursor cursor = collProject.find(nameFile);
			if(cursor.count()>1)
			{
				data_json.add(nameFile.toString());
				output_json.put("code", 1);
				output_json.put("message", "Success");
				output_json.put("data", data_json.toString());
			}
			else
			{
				output_json.put("code", 0);
				output_json.put("message", "Not Found");
				output_json.put("data", null);
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
	@Path("/addcrawl")
	@SuppressWarnings("unchecked")
	public String CrawPdf(String jsonString)
	{
		/**
		idurl,url,status,crawlTime (pdf only)
		*/
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection CollProject = db.getCollection("crawler_pdf");
			
			JSONObject input_json =(JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String idurl = input_json.get("idurl").toString();
			String url = input_json.get("url").toString();
			String status = input_json.get("status").toString();
			BasicDBObject objek_db = new BasicDBObject();
			objek_db.put("idurl", idurl);
			objek_db.put("url", url);
			objek_db.put("status", status);
			objek_db.put("crawltime", new Date());
			
			CollProject.insert(objek_db);
			
			output_json.put("code", 1);
			output_json.put("message", "Add crawl Sucess");
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		
		return output_json.toString();
	}
	@POST
	@Path("/addproject")
	@SuppressWarnings("unchecked")
	
	public String AddProject(String jsonString)
	{
		// add project after done stopwords to db
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection CollProject = db.getCollection("project");
			
			JSONObject input_json =(JSONObject) JSONValue.parse(jsonString);
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			
			// Initiate Parameter
			String idproject = input_json.get("idproject").toString();
			String namefile = input_json.get("namefile").toString();
			String titleproject = input_json.get("titleproject").toString();
			String content = input_json.get("content").toString();
			String nim = input_json.get("nim").toString();
			String progdi = input_json.get("progdi").toString();
			String faculty = input_json.get("faculty").toString();
			//new object author
			DBObject author = new BasicDBObject();
			author.put("nim", nim);
			author.put("progdi", progdi);
			author.put("nim", faculty);
			//end object author
			BasicDBObject objek_db = new BasicDBObject();
			objek_db.put("idproject", idproject);
			objek_db.put("namefile", namefile);
			objek_db.put("titleproject", titleproject);
			objek_db.put("content", content);
			objek_db.put("author",  author);
			objek_db.put("timeprocess", new Date());
			
			CollProject.insert(objek_db);
			
			output_json.put("code", 1);
			output_json.put("message", "Preprocessing Sucess");
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		
		return output_json.toString();
	}
	@GET
	@Path("/getallproject/{appkey}")
	@SuppressWarnings("unchecked")
	public String GetAllProject(@PathParam("appkey") String appkey, @PathParam("token") String token) 
	{
		JSONObject output_json = new JSONObject();
		JSONArray data_json = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collApp = db.getCollection("application");
			DBCollection collProject = db.getCollection("project");
			
			GeneralService.AppkeyCheck(appkey, collApp);
			
			DBCursor cursor = collProject.find();
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
}
/**
addproject => {"username" : "plagiarism", "appkey":"4YjFrLIY5amwajVOKfZH", "idproject":"1","namefile":"testname","titleproject":"testtp","content":"testc"}
getallproject => http://localhost:8080/PlagiarismW/rest/fp/getallproject/4YjFrLIY5amwajVOKfZH
 */
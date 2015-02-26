package service;
import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import main.preprocess;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import connector.MONGODB;
@Path("clocal")
public class CrawLocal {
	// path final project
	public static String FinalProjectPath = "C:/bima data/skripsi/luna/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/RESTfulWS/final/";
	@POST
	@Path("/getuniq")
	@SuppressWarnings("unchecked")
	public String GetUniqueProject()
	{
		//contoh list
		String[] ListFiles = new String[] {"A11.2011.05929.pdf","A11.2011.05930.pdf","A11.2011.05931.pdf","A11.2011.05932.pdf","A11.2011.05933.pdf"};
		ArrayList<String> UniqFile = new ArrayList<String>();
		JSONObject output_json = new JSONObject();
		try 
		{
			int i=0,count = ListFiles.length;
			while(i<count)
			{
				if(this.IsUniq(ListFiles[i]))
				{
					UniqFile.add(ListFiles[i]);
				}
				i++;
			}
			if(UniqFile.size()>0)
			{
				output_json.put("code", 1);
				output_json.put("message","sucess");
				output_json.put("data", UniqFile.toString());
			}
			else
			{
				output_json.put("code", 0);
				output_json.put("message","New Project Not Found");
			}
		}catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		return output_json.toString();
	}
	// check url isuniq
	private boolean IsUniq(String url)
	{
		boolean uniq = false;
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collLocal = db.getCollection("local");
			BasicDBObject where_query = new BasicDBObject("_id",url);
			DBObject find_objek_student = collLocal.findOne(where_query);
			if (find_objek_student == null)
			{
				uniq = true;
			}
		}catch (Exception e)
		{
			System.out.println(e);
		}
		return uniq;
	}
	//
	@POST
	@Path("/preprocess")
	@SuppressWarnings("unchecked")
	public String PreProcessList()
	{
		JSONObject output_json = new JSONObject();
		JSONObject input_json = new JSONObject();
		DB db = null;
		String StrPdf = null;
		String[] listString = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection CollCrawl = db.getCollection("local");
			BasicDBObject objek_db = new BasicDBObject();
			
			String list = this.GetUniqueProject();
			input_json = (JSONObject) JSONValue.parse(list);
			
			list = input_json.get("data").toString();
			list = list.replace("[","");
			list = list.replace("]","");
			list = list.replace(" ","");
			listString = list.split(",");
			int i=0,count = listString.length;
			preprocess ReadPdf = new preprocess();
			while(i<count)
			{
				StrPdf = null;
				objek_db.put("_id",listString[i]);
				objek_db.put("judul", "judul TA "+listString[i]);
				objek_db.put("date", new Date());
				StrPdf = ReadPdf.readOnePdf(FinalProjectPath+listString[i]);
				objek_db.put("rawcontent",StrPdf);
				objek_db.put("cleancontent",ReadPdf.StopWords(StrPdf));
				objek_db.put("keyword","keyword "+listString[i]);
				CollCrawl.insert(objek_db);
				i++;
			}
			output_json.put("code", 1);
			output_json.put("message", "sucess");
		}catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		return output_json.toString();
	}
	public String PlagiarismCheck(String JsonInput)
	{
		input_json = (JSONObject) JSONValue.parse(list);
	}
	
}
/**
	db.local.insert({"_id":"A11.2011.05929.pdf","judul":"crawling berbasisi ontology","rawcontent":"wkwkkwkwkwkwkwkkwkwkwkkkwkwkwkwkwkkw kwkwkwkwkwk wkwk kwkwkwkwk wkkw kwwkwkk wkkwkwkwkwkwk"})
**/
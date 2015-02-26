package service;
import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import connector.MONGODB;

@Path("crawl")
public class CrawlClass {
	// get URL from data_crawler where status 0
	@POST
	@Path("/docrawl")
	public void crawl() throws IOException
	{
		JSONObject input_json =(JSONObject) JSONValue.parse(this.getUrl());
		String sem = input_json.get("data").toString();
		input_json = (JSONObject) JSONValue.parse(sem);
		Document doc = this.getPage(input_json.get("url").toString());
		if(doc!=null)
		{
			Elements questions = doc.select("a[href]");
			for(Element link: questions){
				this.InsertUrl(link.attr("abs:href"));
			}
			this.UpdateUrl(input_json.get("_id").toString());
			this.crawl();
		}
	}
	// download url to document
	public Document getPage(String url) throws IOException
	{
		return Jsoup.connect(url).get();
	}
	public boolean IsAvailableDomain(String domain)
	{
		return false;
	}
	public boolean IsPdf(String PageHtml)
	{
		return false;
	}
	// add url into database
	@POST
	@Path("/addurltocrawl")
	@SuppressWarnings("unchecked")
	public String InsertUrl(String jsonString)
	{
		/**
		idurl,url,status,crawlTime (pdf only)
		*/
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
			DBCollection CollCrawl = db.getCollection("tcrawler");
			String link = input_json.get("url").toString();
			// Initiate Parameter
			BasicDBObject objek_db = new BasicDBObject();
			//objek_db.put("idurl", link);
			objek_db.put("_id", link);
			objek_db.put("status", 0);
			objek_db.put("crawltime", new Date());
			
			CollCrawl.insert(objek_db);
			
			output_json.put("code", 1);
			output_json.put("message", "Add url Sucess");
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		
		return output_json.toString();
	}
	//get all url from page and filtering
	public void getUrlPage(Elements arrUrl)
	{
		for(Element link: arrUrl){
			try {
				this.InsertUrl(link.attr("abs:href"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// get url in tcrawler to crawl
	@GET
	@Path("/geturl")
	@SuppressWarnings("unchecked")
	public String getUrl()
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collCrawl = db.getCollection("rawcrawler");
			
			BasicDBObject where_query = new BasicDBObject("status",0);
			DBObject find_objek_crawl = collCrawl.findOne(where_query);
			
			if (find_objek_crawl != null)
			{
				output_json.put("code", 1);
				output_json.put("message", "Success");
				output_json.put("data", find_objek_crawl.toString());
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
			output_json.put("message", e.toString());
		}
		
		return output_json.toString();
	}
	@SuppressWarnings("unused")
	private void UrlCrawled(DBCollection collStudent, DBObject query,DBObject updateset)
	{
		BasicDBObject ObjectSet = new BasicDBObject(); 
		ObjectSet.put("status",1);
		ObjectSet.put("crawltime",new Date());
		
		BasicDBObject ObjectQuery = new BasicDBObject();
		ObjectQuery.put("$set", ObjectSet);
		
		collStudent.update(query, ObjectQuery);
	}
	@SuppressWarnings("unchecked")
	public void UpdateUrl(String jsonString)
	{
		
		JSONObject input_json = (JSONObject) JSONValue.parse(jsonString);
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collCrawl = db.getCollection("tcrawler");
			BasicDBObject ObjectId = new BasicDBObject();// get a row to update
			BasicDBObject ObjectSet = new BasicDBObject(); // change field
			BasicDBObject ObjectQuery = new BasicDBObject();
			
			ObjectId.put("_id",input_json.get("_id").toString());
			
			ObjectSet.put("status",1);
			ObjectSet.put("crawltime",new Date());
			
			ObjectQuery.put("$set", ObjectSet);
			
			collCrawl.update(ObjectId, ObjectQuery);
		}catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
	}
}
/**
JSONObject output_json = new JSONObject();
DB db = null;
try 
{
	db = MONGODB.GetMongoDB();
	DBCollection collCrawl = db.getCollection("tcrawler");
}catch (Exception e) 
{
	output_json.put("code", -1);
	output_json.put("message", e.toString());
}
return output_json.toString();
	**/
package service;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import main.preprocess;
import main.fingerprintWinnowing.FilterTransform;
import main.fingerprintWinnowing.JaccardCoefficient;
import main.fingerprintWinnowing.WinnowingFingerprinter;
import main.fingerprintWinnowing.WinnowingTextTransformer;
import main.fingerprintWinnowing.WinnowingWhitespaceFilter;

import org.bson.BSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import connector.MONGODB;
@Path("clocal")
public class CrawLocal {
	// path final project
	public static String FinalProjectPath = "C:/Users/Yehezkiel/Workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/RESTfulWS/final/";
	@POST
	@Path("/getuniq")
	@SuppressWarnings("unchecked")
	public String GetUniqueProject() // google docs (v)
	{
		//list examples from service /g/getlistthesis
		String[] ListFiles = new String[] {"A11.2012.06601.pdf","A11.2012.06602.pdf","A11.2012.06603.pdf","A11.2012.06604.pdf","A11.2012.06605.pdf"};
		JSONArray UniqFile = new JSONArray();
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
				output_json.put("message","Success Di Upload");
				output_json.put("data", UniqFile);
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
	public String PreProcessList() // google docs (v)
	{
		
		JSONObject output_json = new JSONObject();
		JSONObject input_json = new JSONObject();
		DB db = null;
		String StrPdf = null;
		JSONArray listString = new JSONArray();
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection CollCrawl = db.getCollection("local");
			BasicDBObject objek_db = new BasicDBObject();
			
			String list = this.GetUniqueProject();
			input_json = (JSONObject) JSONValue.parse(list);
			
			listString = (JSONArray) input_json.get("data");
			
			int i=0,count = listString.size();
			preprocess ReadPdf = new preprocess();
			while(i<count)
			{
				StrPdf = null;
				objek_db.put("_id",listString.get(i));
				objek_db.put("judul", "Judul TA "+listString.get(i));
				objek_db.put("date", new Date());
				StrPdf = ReadPdf.readOnePdf(FinalProjectPath+listString.get(i));
				
				if(StrPdf != null){
				objek_db.put("rawcontent",StrPdf);
				}
				else
					objek_db.put("rawcontent", null);
				
				objek_db.put("cleancontent","");//ReadPdf.StopWords(StrPdf)
				objek_db.put("keyword","keyword "+listString.get(i));
				CollCrawl.insert(objek_db);
				i++;
			}
			output_json.put("code", 1);
			output_json.put("message", "Success");
		}catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		return output_json.toString();
	}
	// check a project has been add to local tabel
	private boolean ProjectExist(DBCollection collLocal,String NameFile)
	{
		boolean uniq = false;
		try 
		{
			BasicDBObject where_query = new BasicDBObject("_id",NameFile);
			DBObject find_objek_project = collLocal.findOne(where_query);
			if (find_objek_project != null)
			{
				uniq = true;
			}

		}catch (Exception e)
		{
			System.out.println(e);
		}
		return uniq;
	}
	private boolean ProjectAlreadyProcessed(DBCollection collCheckPlag,String NameFile)
	{
		boolean uniq = false;
		try 
		{
			BasicDBObject where_query = new BasicDBObject("_id",NameFile);
			DBObject find_objek_project = collCheckPlag.findOne(where_query);
			if (find_objek_project != null)
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
	@Path("/pbynamefile/{appkey}")
	@SuppressWarnings("unchecked")
	public String GetAllProjectByNameFile(@PathParam("appkey") String appkey) // google docs (v)
	{
		JSONObject output_json = new JSONObject();
		JSONArray List = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collLocal = db.getCollection("local");
			DBCursor cursor = collLocal.find();
			while (cursor.hasNext()) {
				List.add(cursor.next().get("_id").toString());
			}
			
			if (List.size() == 0)
			{
				output_json.put("code", 0);
				output_json.put("message", "Not Found");
				output_json.put("data", null);
			}else
			{
				output_json.put("code", 1);
				output_json.put("message", "Success");
				output_json.put("data", List);
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		
		return output_json.toString();	
	}
	@SuppressWarnings("unchecked")
	private String GetAllProjectByNameFileExcept(String NameFile)
	{
		JSONObject output_json = new JSONObject();
		JSONArray List = new JSONArray();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collLocal = db.getCollection("local");
			BasicDBObject objek_db = new BasicDBObject();
			BasicDBObject objek_db2 = new BasicDBObject();
			objek_db.put("$ne", NameFile);
			objek_db2.put("_id",objek_db);
			DBCursor cursor = collLocal.find(objek_db2);
			while (cursor.hasNext()) {
				List.add(cursor.next().get("_id").toString());
			}
		} 
		catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message",e.toString());
		}
		if (List.size() == 0){return "0";}
		else{return List.toString();}	
	}
	// check
	@POST
	@Path("/plagcheck")
	@SuppressWarnings("unchecked")
	public String PlagiarismCheck(String JsonInput)// google docs (v)|{"appkey":"4YjFrLIY5amwajVOKfZH","NameFile":"A11.2011.05929.pdf"}
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		
		JSONObject ResEnemy;
		String dataEnemy;
		JSONObject EnemyJson;
		String EnemyRawContent=null;
		try 
		{
			db = MONGODB.GetMongoDB();
			JSONObject input_json = (JSONObject) JSONValue.parse(JsonInput);
			DBCollection collApp = db.getCollection("application");
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			DBCollection collLocal = db.getCollection("local");
			DBCollection collCheckPlag = db.getCollection("checkplagiarism");
			BasicDBObject newField = new BasicDBObject();
			String NameFile = input_json.get("NameFile").toString();
			if(this.ProjectAlreadyProcessed(collCheckPlag, NameFile))
			{
				output_json.put("code", -1);
				output_json.put("message","Tugas Akhir Sudah Dicheck Silakan Lihat Hasil");
			}
			else
			{
			if(!this.ProjectExist(collLocal, NameFile))
			{
				output_json.put("code", 2);
				output_json.put("message", "Tugas Akhir belum Anda Upload");
			}
			
			else
			{
				//get data by namefile(_id)
				JSONObject Res = (JSONObject) JSONValue.parse(this.PriGetProjectBy_id(NameFile));
				String Data = Res.get("data").toString();
				JSONObject DataTarget = (JSONObject) JSONValue.parse(Data);
				String RawContent = DataTarget.get("rawcontent").toString();
				//
				//
				String list = this.GetAllProjectByNameFileExcept(NameFile);
				// get NameFile except target from local table
				if(list!="0")
				{
					
					JSONArray listString = (JSONArray) JSONValue.parse(list);
					JSONArray documents = new JSONArray();
					int i=0,count = listString.size();
					// end
					// prepare check plagiarism
					while(i<count)
					{
						BasicDBObject compare = new BasicDBObject();
						ResEnemy = (JSONObject) JSONValue.parse(this.PriGetProjectBy_id(listString.get(i).toString()));
						dataEnemy = ResEnemy.get("data").toString();
						EnemyJson = (JSONObject) JSONValue.parse(dataEnemy);
						EnemyRawContent = EnemyJson.get("rawcontent").toString();
						Set<BigInteger> fpTarget=null, fpEnemy=null;
						FilterTransform ft=new FilterTransform();
						List<WinnowingWhitespaceFilter> ws=new ArrayList<WinnowingWhitespaceFilter>(1);
						List<WinnowingTextTransformer> wt=new ArrayList<WinnowingTextTransformer>(1);
						wt.add(ft);
						ws.add(ft);
						WinnowingFingerprinter WN = new WinnowingFingerprinter(ws, wt, 4, 6, "MD5");
						try {
							fpTarget = WN.fingerprint(RawContent);
							fpEnemy = WN.fingerprint(EnemyRawContent);
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();		
						}
						JaccardCoefficient JC = new JaccardCoefficient();
						compare.put("nim", listString.get(i));
						compare.put("similarity", JC.similaritylist(fpTarget, fpEnemy));
						documents.add(compare);
						i++;
					}
					newField.put("_id",NameFile);
					newField.put("plagdetails",documents);
					newField.put("date", new Date());
					collCheckPlag.insert(newField);
					
					output_json.put("code",1);
					output_json.put("message","check plagiarism sucess");
				}else
				{
					output_json.put("code",2);
					output_json.put("message","Plagiarism Check Failed");
				}
			}
			}
		}catch (Exception e) 
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		return output_json.toString();
	}
	@POST
	@Path("/getpby_id")
	@SuppressWarnings("unchecked")
	public String GetProjectBy_id(String JsonInput)// google docs (v)
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			JSONObject input_json = (JSONObject) JSONValue.parse(JsonInput);
			DBCollection collApp = db.getCollection("application");
			GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
			DBCollection collLocal = db.getCollection("local");
			BasicDBObject where_query = new BasicDBObject("_id",input_json.get("NameFile").toString());
			DBObject find_objek_project = collLocal.findOne(where_query);
			if (find_objek_project != null)
			{
				output_json.put("code", 1);
				output_json.put("data", find_objek_project);
			}
			else
			{
				output_json.put("code", 2);
				output_json.put("message", "Project not found");
			}
		}catch (Exception e)
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		return output_json.toString();
	}
	@SuppressWarnings("unchecked")
	private String PriGetProjectBy_id(String NameFile)
	{
		JSONObject output_json = new JSONObject();
		DB db = null;
		try 
		{
			db = MONGODB.GetMongoDB();
			DBCollection collLocal = db.getCollection("local");
			BasicDBObject where_query = new BasicDBObject("_id",NameFile);
			DBObject find_objek_project = collLocal.findOne(where_query);
			if (find_objek_project != null)
			{
				output_json.put("data", find_objek_project);
			}
			else
			{
				output_json.put("code", 2);
				output_json.put("message", "Project not found");
			}
		}catch (Exception e)
		{
			output_json.put("code", -1);
			output_json.put("message", e.toString());
		}
		return output_json.toString();
	}

@POST
@Path("/getresult")
@SuppressWarnings("unchecked")
public String GetProjectResult(String JsonInput)// google docs (v)
{
	JSONObject output_json = new JSONObject();
	DB db = null;
	try 
	{
		db = MONGODB.GetMongoDB();
		JSONObject input_json = (JSONObject) JSONValue.parse(JsonInput);
		DBCollection collApp = db.getCollection("application");
		GeneralService.AppkeyCheck(input_json.get("appkey").toString(),collApp);
		DBCollection collPlag = db.getCollection("checkplagiarism");
		BasicDBObject where_query = new BasicDBObject("_id",input_json.get("NameFile").toString());
		DBObject find_objek_project = collPlag.findOne(where_query);
		String NameFile = input_json.get("NameFile").toString();
		if (find_objek_project == null)
		{
			output_json.put("code", 1);
			output_json.put("message", "Result File Belum Ada Silahkan Check Dahulu");			
		}
		else
		{
			BasicDBObject query = new BasicDBObject();
			DBObject Result = collPlag.findOne(query);
			
			//DBObject result = collPlag.findOne("plagdetails");
			output_json.put("code", 1);
			output_json.put("Hasil Check Plagiarisme [Nim / Similarity]", Result);
		}
	}catch (Exception e)
	{
		output_json.put("code", -1);
		output_json.put("message", e.toString());
	}
	return output_json.toString();
}
}
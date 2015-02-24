package main;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.DB;

public class CrawlClass {
	protected String sql;
	private ArrayList<String> result;
	public CrawlClass()
	{
		sql = null;
	}
	// get URL from data_crawler where status 0
	public ResultSet getUrlToCrawl()
	{
		
		ResultSet rs = null;
		return rs;
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
	public List<String> GetDomain()
	{
		return null;
//		DB db = new DB();
//		result = new ArrayList<String>(); 
//		String sql = "select * from data_domain";
//		ResultSet res = null;
//		try {
//			res = db.runSql(sql);
//			while (res.next()) {
//			    result.add(res.getString("idDomain"));
//			    result.add(res.getString("domain"));
//			    result.add(res.getString("status"));
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.out.println(e);//e.printStackTrace();
//		}
//		return result;
////		result = new ArrayList<String>(); 
////		result.add("sdfsdfsdf");
////		result.add("sdf");
////		result.add("wkwkw");
////		return result;
	}
	public void test()
	{
//		String sql = "insert into data_domain (domain) values ('http:www.wkwk.com')";
//		//ResultSet res = null;
//		try {
//			db.runSql(sql);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//return res.toString();
	}
	// add url into database
	protected void InsertUrl(String url) throws SQLException
	{
//		sql = "INSERT INTO  `db_crawler`.`data_crawler` " + "(`url`) VALUES " + "(?);";
//		PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//		stmt.setString(1, url);
//		stmt.execute();
	}
	//get all url from page and filtering
	public void getUrlPage(Elements arrUrl)
	{
		for(Element link: arrUrl){
			try {
				this.InsertUrl(link.attr("abs:href"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// getdomain
	protected String getDomain(String Url)
	{
		String domain = null;
		return domain;
	}
	public String SelectDomainToCrawl(int idUrl)
	{
		return null;
//		String sql = "select * from data_domain where idDomain = "+idUrl+" LIMIT 1";
//		ResultSet rs = null;
//		String domain = null;
//		try {
//			rs = db.runSql(sql);
//			if(rs.next())
//			{
//				domain = rs.getString("domain");
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return domain;
	}
}

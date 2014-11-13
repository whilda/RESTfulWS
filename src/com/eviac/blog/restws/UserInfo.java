package com.eviac.blog.restws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;

@Path("UserInfoService")
public class UserInfo {
	@GET
	@Path("/name/{i}")
	@Produces(MediaType.APPLICATION_JSON)
	public String userName(@PathParam("i") String i) {
		JSONObject outputJsonObj = new JSONObject();
	    outputJsonObj.put("output", i);
		return outputJsonObj.toString();
	}
		
	@GET
	@Path("/age/{j}")
	@Produces(MediaType.APPLICATION_JSON)
	public String userAge(@PathParam("j") int j) {
		JSONObject outputJsonObj = new JSONObject();
	    outputJsonObj.put("output", j);
		return outputJsonObj.toString();
	}
}

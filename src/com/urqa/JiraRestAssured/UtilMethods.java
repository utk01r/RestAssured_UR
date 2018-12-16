package com.urqa.JiraRestAssured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UtilMethods {
	
	public static JsonPath rawToJson(Response rawResponse_json){
		String responseString = rawResponse_json.asString();
		JsonPath json_response = new JsonPath(responseString);
		return json_response;
	}
}

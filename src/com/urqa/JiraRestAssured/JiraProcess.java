package com.urqa.JiraRestAssured;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.out;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class JiraProcess {

	/**~~~~~~~~~~~~~~~~~~~~~Jira API~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * --> 1st Download and setup the Jira (Software Server)to perform API Testing (ignore if already Done/have)
	 * --> 2nd Search for "Jira api authentication" and get the API to get the Key for Jira to using the your jira
	 *     credentials to perfrom any api activity using api like  raising an issue, status changeing, Type ...etc
	 * --> We have to perfrom action based on different type of requirements and need   
	 * 
	 */
	Properties prop = new Properties();
	
	@BeforeTest
	public void getProperty(){
		FileInputStream fis;
		try {
			fis = new FileInputStream("C:\\myEclipse\\RestAssured\\src\\com\\urqa\\JiraRestAssured\\env.properties");
			prop.load(fis);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test (priority = 1)
	public void getAuthentication(){
		out.println("--------------------Authenticatinon Start--------------");
		RestAssured.baseURI = prop.getProperty("HOST");
		Response rawResponse = given().
			header("Content-Type", "application/json").
			body("{ \"username\": \""+prop.getProperty("jiraUN")+"\", \"password\": \""+prop.getProperty("jiraPWD")+"\" }").
		when().
			post(prop.getProperty("authentResorce")).
		then().assertThat().statusCode(200).
		extract().response();
	
		JsonPath jsonResponse = UtilMethods.rawToJson(rawResponse);
		//out.println(jsonResponse);
		prop.setProperty("sessionName", jsonResponse.get("session.name"));
		out.println("sessionName : "+prop.get("sessionName"));
		prop.setProperty("sessionValue", jsonResponse.get("session.value"));
		out.println("sessionValue : "+prop.get("sessionValue"));
		
		//--> Getting all cooking from raw response as cookies is not avaiable in json response
	   //String tokenCookie = rawResponse.getCookie(prop.getProperty("tokenCookieName"));
		prop.setProperty("tokenCookieValue", rawResponse.getCookie(prop.getProperty("tokenCookieName")));
		out.println("\n************************ E.N.D.*************************");
	}
	
	@Test(dependsOnMethods="createProject")
	public void getAllProject(){
		out.println("----------------------Get All Project Start--------------");
		RestAssured.baseURI = prop.getProperty("HOST");
		Response rawResponse = 
		given().
			header("Content-Type", "application/json").
			headers("Cookie", prop.getProperty("sessionName")+"="+prop.getProperty("sessionValue")).
			header(prop.getProperty("tokenCookieName"), prop.getProperty("tokenCookieValue")).
			param("description", prop.getProperty("projectDescription")).
			param("recent", prop.getProperty("recentParam")).
		when().
			get(prop.getProperty("getProjectResource")).
		then().assertThat().statusCode(200).
		extract().response();
		
		JsonPath jsonResponse = UtilMethods.rawToJson(rawResponse);
		
		out.println("expand value : "+jsonResponse.get("expand"));
		
		out.println("project URL => Self : "+ jsonResponse.get("self"));
		//prop.setProperty("projectURL",  jsonResponse.getString("self")).substring(1, jsonResponse.getString("self").length()-1);
	
		prop.setProperty("projectId", jsonResponse.getString("id").substring(1, jsonResponse.getString("id").length()-1));
		out.println("project Id : "+jsonResponse.getString("id").substring(1, jsonResponse.getString("id").length()-1));
		
		prop.setProperty("projectKey",  jsonResponse.getString("key").substring(1, jsonResponse.getString("key").length()-1));
		out.println("Project Key : "+ jsonResponse.getString("key").substring(1, jsonResponse.getString("key").length()-1));
		
		prop.setProperty("projectName", jsonResponse.getString("name").substring(1, jsonResponse.getString("name").length()-1));
		out.println("Project name : "+jsonResponse.getString("name").substring(1, jsonResponse.getString("name").length()-1));
		
		out.println("\n************************ E.N.D.*************************");
	}
	
	@Test(dependsOnMethods="getAuthentication")
	public void createProject(){
		out.println("----------------------Create Project Start--------------");
		
		RestAssured.baseURI = prop.getProperty("HOST");
		File fileBody = new File(System.getProperty("user.dir")+"\\src\\com\\urqa\\JiraRestAssured\\createProjectBody.json");//.\\RestAssured\\src\\com\\urqa\\JiraRestAssured\\createProjectBody.json");
		
		Response rawResponse = 
		given().
			header("Content-Type", "application/json").
			headers("Cookie", prop.getProperty("sessionName")+"="+prop.getProperty("sessionValue")).
			header(prop.getProperty("tokenCookieName"), prop.getProperty("tokenCookieValue")).
			body(fileBody).
		when().
			post(prop.getProperty("createProjectResource")).
		then().
			assertThat().statusCode(201).and().
			contentType(ContentType.JSON).and().
			body("key", equalTo("RAB")).
			extract().response();
		
		JsonPath jsonResponse = UtilMethods.rawToJson(rawResponse);
		
		out.println("Created Project Id : "+jsonResponse.get("id"));
		prop.setProperty("createdproj_id", jsonResponse.getString("id"));
		
		out.println("Created project Key : "+jsonResponse.get("key"));
		prop.setProperty("createdproj_key", jsonResponse.get("key"));
	
		out.println("Created project URL : "+jsonResponse.get("self"));
		prop.setProperty("createdproj_url", jsonResponse.get("self"));
		
		out.println("		\"issueSecurityScheme\": 10001,\n" + 
				"    \"permissionScheme\": 10011,\n" + 
				"    \"notificationScheme\": 10021,\n" + 
				"    \"categoryId\": 10120" + " These permmission are not used in project cration use if need.");
		out.println("STATUS 400 :Returned if the request is not valid and the project could not be created.\n" + 
		     		"STATUS 401 :Returned if the user is not logged in.\n" + 
			    	"STATUS 403 :Returned if the user does not have rights to create projects.");	
		
		out.println("\n************************ E.N.D.*************************");
	}
	
	@Test(dependsOnMethods="createProject")
	public void updateProject(){
		out.println("----------------------Project Updating Start--------------");
		
		out.println("Projected Id : "+prop.getProperty("createdproj_Id"));
		out.println("put url : "+prop.getProperty("updateProjectResource")+prop.getProperty("createdproj_Id"));
		RestAssured.baseURI = prop.getProperty("HOST");
		File updateFileBody = new File(System.getProperty("user.dir")+"\\src\\com\\urqa\\JiraRestAssured\\updateProject_Body.json");//.\\RestAssured\\src\\com\\urqa\\JiraRestAssured\\updateProject_Body.json");
		
		Response rawResponse = 
		given().
			header("Content-Type", "application/json").
			headers("Cookie", prop.getProperty("sessionName")+"="+prop.getProperty("sessionValue")).
			header(prop.getProperty("tokenCookieName"), prop.getProperty("tokenCookieValue")).
			body(updateFileBody).
		when().
			put(prop.getProperty("updateProjectResource")+prop.getProperty("createdproj_Id")).
		then().assertThat().statusCode(201).and().
			contentType(ContentType.JSON).extract().response();
			
		out.println("STATUS 400 Returned if the request is not valid and the project could not be updated.\n" + 
				"STATUS 401 Returned if the user is not logged in.\n" + 
				"STATUS 403 Returned if the user does not have rights to update projects.\n" + 
				"STATUS 404 Returned if the project does not exist");
		out.println("\n************************ E.N.D.*************************");
	}
	
	@Test(dependsOnMethods="updateProject")
	public void deleteProject(){
		out.println("----------------------Project Deletion Start--------------");
		
		RestAssured.baseURI = prop.getProperty("HOST");
		Response rawResponse = 
		given().
			header("Content-Type", "application/json").
			headers("Cookie", prop.getProperty("sessionName")+"="+prop.getProperty("sessionValue")).
			header(prop.getProperty("tokenCookieName"), prop.getProperty("tokenCookieValue")).
		when().
			delete(prop.getProperty("deleteProjectResource")+prop.getProperty("createdproj_Id")).
		then().
			assertThat().statusCode(204).and().
			contentType(ContentType.JSON).extract().response();
		
		out.println("STATUS 401 : Returned if the user is not logged in.\n" + 
				"STATUS 204 : application/jsonReturned if the project is successfully deleted.\n" + 
				"STATUS 403 : Returned if the currently authenticated user does not have permission to delete the project.\n" + 
				"STATUS 404 : Returned if the project does not exist.");
		out.println("\n************************ E.N.D.*************************");
	}
	
	@Test (enabled = false)
	public void raisingIssue(){
		out.println("----------------------Raising issue Start--------------");
		out.println("SessionName : "+prop.get("sessionName"));
		out.println("SessionValue : "+prop.get("sessionValue"));
		out.println("tookenValue : "+prop.get("tokenCookieValue"));
		out.println("tookenName : "+prop.get("tokenCookieName"));
		
		RestAssured.baseURI = prop.getProperty("HOST");
		String bodyValue = "{\"fields\": {\"project\": {\"id\": \""+prop.getProperty("projectId")+"\"},\"summary\": "+prop.getProperty("issueSummery")+",\"issuetype\": {\"id\": \""+prop.getProperty("projectId")+"\" }}}";
		out.println("bodyValue: "+bodyValue);
		Response rawResponse =
		given().
			header("Content-Type", "application/json").
			headers("Cookie", prop.getProperty("sessionName")+"="+prop.getProperty("sessionValue")).
			header(prop.getProperty("tokenCookieName"), prop.getProperty("tokenCookieValue")).
			body(bodyValue).
		when().
			post(prop.getProperty("reportIssueResource")).
		then().assertThat().statusCode(201).
		extract().response();
		
		JsonPath jsonResponse = UtilMethods.rawToJson(rawResponse);
		
		prop.setProperty("bug_id", jsonResponse.get("id"));
		out.println("bug_id : "+jsonResponse.get("id"));
		
		prop.setProperty("bug_Key", jsonResponse.get("key"));
		out.println("bug_Key : "+jsonResponse.get("key"));
		
		prop.setProperty("bug_url", jsonResponse.get("self"));
		out.println("bug_url : "+jsonResponse.get("self"));
		
		out.println("\n************************ E.N.D.*************************");
	}
	
}

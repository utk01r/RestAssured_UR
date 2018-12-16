package com.urqa.restAsssured;

import io.restassured.RestAssured;  //Need to import manually 
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;  //Need to import manually *static
import static org.hamcrest.Matchers.equalTo;	//Need to import manually *static

import org.testng.annotations.Test;


public class RestAssuredTest {
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 1. first provide the BaseURL or Host 
	 * 2. Use given parameters in side the given block
	 * 		given().
	 * 			-Request header
	 * 			-request cookies
	 * 			-body
	 * 			-Parameters
	 * 			--param(key, value).and().
	 * 		 ** provide all the parameters here like above
	 * 		:
	 * 		when().
	 * 			get("resource").
	 * 		 **	provide the Method(GET, POST, DELETE) along with the resource
	 * 		then().
	 *		 ** provide assertion for the different data to make sure we are correct response
	 *			- status
	 *			- body : equalTo() Apply only body not other or header to assert
	 *			- header
	 * 			- others
	 * 
	 * 
	 *-------------------------------------------------------------*/
	//https://reqres.in/
	//https://reqres.in/api/users?page=2
	@Test
	public void getCall() {
		
		RestAssured.baseURI = "https://reqres.in/";
		
		given().
			param("page", "2").
		when().
			get("api/users").
		then().
			assertThat().statusCode(200).and().contentType(ContentType.JSON).and().
			body("data[0].first_name", equalTo("Eve")).and().
			body("data[1].id", equalTo(5)).and().
			body("data[2].avatar", equalTo("https://s3.amazonaws.com/uifaces/faces/twitter/bigmancho/128.jpg")).and().
			header("Server", "cloudflare").and().
			header("Content-Encoding", "gzip");
		//NOTE : We use equalTo to compare the data of the body only not other cases
	}

	//https://reqres.in/
	@Test
	public void getCallWithoutParam(){
		RestAssured.baseURI="https://reqres.in";
		
		given().
		
		when().
			get("/api/unknown/2").
		then().
			assertThat().statusCode(200).and().contentType(ContentType.JSON).and().
			body("data.name", equalTo("fuchsia rose")).and().
			body("data.year", equalTo(2001)).and().
			body("data.color", equalTo("#C74375")).and().
			body("data.id", equalTo(2)).and().
			body("data.pantone_value", equalTo("17-2031")).and().
			header("Server", "cloudflare")/*.and().
			cookie("__cfduid", "false")*/;
	}

	//Test URL : http://www.groupkt.com/post/5c85b92f/restful-rest-webservice-to-get-and-search-states-and-territories-of-a-country.htm
	@Test
	public void getCallExp2(){
		String Country = "IND";
		String state = "pradesh";
		RestAssured.baseURI = "http://services.groupkt.com/";
		
		given().
			param("text","pradesh").
		when().
			get("state/search/IND").
		then().
			assertThat().statusCode(200).and().contentType(ContentType.JSON).and().
			/*body("result.name", equalTo("Madhya Pradesh")).and().
			body("result[4].capital", equalTo("Shimla Dharamshala")).and().*/
			header("Server", "Apache/2.4.25 (Debian)");
	}
	
	//https://reqres.in/
	@Test
	public void getCallDelayedResponse(){
		RestAssured.baseURI = "https://reqres.in/";
		
		given().
			param("delay", "3").
		when().
			get("api/users").
		then().
			assertThat().statusCode(200).and().contentType(ContentType.JSON).and().
			body("total", equalTo(12)).and().
			body("total_pages", equalTo(4)).and().
			body("data[0].first_name", equalTo("George")).and().
			body("data[1].id", equalTo(2)).and().
			body("data[2].last_name", equalTo("Wong")).and().
			body("data[2].avatar", equalTo("https://s3.amazonaws.com/uifaces/faces/twitter/olegpogodaev/128.jpg")).and().
			header("Server", "cloudflare").and().
			header("Content-Encoding", "gzip");
	}
	
}

package com.urqa.restAsssured;

import org.testng.annotations.Test;
import io.restassured.RestAssured; //Need to import manually 
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;   //Need to import manually *static

public class RestAssuredPostCallTest {
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 1. first provide the BaseURL or Host 
	 * 2. provide the request type parameter in side the given block
	 * 		given().
	 * 			-Path Parameter : is use to match a part of URL as a parameter. For e.g. in an URL of the form 
	 * 							  http://example.com/books/{bookid}, we can use @PathParam("bookid") to get the id of a book.
	 * 			-Query Parameter : is use to access key/value pairs in the query string of the URL (the part after the ?). For
	 * 							   e.g. in the url http://example.com?q=searchterm, we can use @QueryParam("q") to  get the value
	 * 							   of q.
	 * 			-Header Parameter
	 * 			-body{with the data in JSON(or particular format as per request)format as String} 			
	 * 			:
	 * 		when().
	 * 			post("resource").
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
	@Test
	public void postCallTest(){
		RestAssured.baseURI = "https://reqres.in/";
		
		given().
			body("{" + 
					"\"name\": \"morpheus\"," + 
					"\"job\": \"leader\"" + 
					"}").
		when().
			post("/api/users").
		then().
			assertThat().statusCode(201).and().contentType(ContentType.JSON).and().
			header("Content-Length", "51").and().
			header("Server", "cloudflare");
		
	}
}

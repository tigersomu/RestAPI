package examples;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubForGet {

	/*
	 * Swagger / PostMan / JIRA / HTML
	 * 
	 * resources: /api/table/issues
	 * Create a new post : response will return 201 (status code)
	 * response would have only json / no body 
	 * 
	 */

	// Mock for create issue
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8081);
	
	@Before
	public void mockForIssue() {
		stubFor(get(urlEqualTo("/api/table/issues"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("{ \"data\" : \"success\" }")
						));
	}

	@Test
	public void testCreateIssues() {

		// Step 1: base URL
		RestAssured.baseURI = "http://localhost:8081/api/table/issues";

		// Step 2: authentication
		RestAssured.authentication = RestAssured.basic("admin", "admin");

		// Step 3: body content
		Response response = RestAssured
				.given()
				.get();

		// Step 5: verify the response	
		response.prettyPrint();
		System.out.println(response.getStatusCode());


	}



}

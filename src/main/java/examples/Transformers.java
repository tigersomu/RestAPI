package examples;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;

public class Transformers {

    private RequestSpecification requestSpec;
    private int port = 8089;

    @Rule
    public WireMockRule wireMockRule =
        new WireMockRule(wireMockConfig().port(port).extensions(new ResponseTemplateTransformer(false))
        );
    
    @Before
    public void createRequestSpec() {
    	
       requestSpec = new RequestSpecBuilder().
            setBaseUri("http://localhost").
            setPort(port).
            build()
            .log().all();
    }

    public void setupTransformers() {
        stubFor(get(urlEqualTo("/echo-port"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("Listening on port {{request.requestLine.port}}")
                .withTransformers("response-template")
            ));
    }

    public void setupTransformerExtraction() {

        stubFor(post(urlEqualTo("/echo-car-model"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{{jsonPath request.body '$.car.model'}}")
                .withTransformers("response-template")
            ));
    }

   // @Test
    public void testTransformers() {

    	setupTransformers(); 

        given().
            spec(requestSpec).
        when().
            get("/echo-port").
        then().
            assertThat().
            statusCode(200).
            log().all().
        and().
            body(org.hamcrest.Matchers.equalTo("Listening on port "+port));
    }

    @Test
    public void testVerification() {

    	setupTransformerExtraction(); 

        given().
            spec(requestSpec).
            contentType(ContentType.JSON).
            body("{\"car\": {\"make\": \"BMW\", \"model\": \"X7\",\"top_speed\": 420}}").
        when().
            post("/echo-car-model").
        then().
            assertThat().
            statusCode(200).
            log().all().
        and().
            body(org.hamcrest.Matchers.equalTo("X7"));
    }
}

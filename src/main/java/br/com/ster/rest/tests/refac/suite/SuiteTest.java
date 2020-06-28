package br.com.ster.rest.tests.refac.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.com.ster.rest.corre.BaseTest;
import br.com.ster.rest.tests.refac.AuthTest;
import br.com.ster.rest.tests.refac.ContasTest;
import br.com.ster.rest.tests.refac.MovimentacaoTest;
import br.com.ster.rest.tests.refac.SaldoTest;
// aula 69
import io.restassured.RestAssured;

@RunWith(Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AuthTest.class
})
public class SuiteTest extends BaseTest {
	
	@BeforeClass
	public static void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "ster@teste.com");
		login.put("senha", "123456");
		
		String TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		
		RestAssured.get("/reset").then().statusCode(200);
	}

}

package br.com.ster.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

import br.com.ster.rest.Utils.BarrigaUtils;
import br.com.ster.rest.corre.BaseTest;

public class SaldoTest extends BaseTest {
	/*
	 * aula 68
	 */
	
	@Test
	public void deveCalcularSaldoContas() {
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
		;
	}	
	
	

}

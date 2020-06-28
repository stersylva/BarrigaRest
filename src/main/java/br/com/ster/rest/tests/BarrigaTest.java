package br.com.ster.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.ster.rest.Utils.DataUtiuls;
import br.com.ster.rest.corre.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {
	
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
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
	}
	
	
	@Test
	public void t02_deveIncluirContaComSucesso() {
		
		CONTA_ID = given()
			.body("{\"nome\": \""+CONTA_NAME+"\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_deveAlterarContaComSucesso() {
		
		given()		
			.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(""+CONTA_NAME+ " alterada"))
		;
	}
	/*
	 * aula 57
	 */
	@Test
	public void t04_naoDeveInserirContaMesmoNome() {
		
		given()
			.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	/*
	 * aula 58 movimenção 
	 */
	
	@Test
	public void t05_deveInserirMovimentacao() {
		Movimentacao mov = getMovimentacaoValida();
		MOV_ID = given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	//aula 60
	@Test
	public void t06_naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao(DataUtiuls.getDataDiferencaDias(2));
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItems("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	//aula 61
	@Test
	public void t07_naoDeveRemoverContaComMovimentacao() {
			given()
				.pathParam("id", CONTA_ID)
			.when()
				.delete("/contas/{id}")
			.then()
				.statusCode(500)
				.body("constraint", is("transacoes_conta_id_foreign"))
			;
		}
	
	// aula 62
	@Test
	public void t08_deveCalcularSaldoContas() {
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			//.body("$", hasSize(1))
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
		;
	}	
	// aula 63
	@Test
	public void t09_deveRemoverMovimentacao() {
		
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}	
	
	
	
	@Test
	public void t10_validarCamposObrigorios() {
		
		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems("Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"))
		;
	}
	
	
	@Test // esse teste tem que ser o ultimo ele não precisa de token
	public void t11_naoveDeveAcessarAPISemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	/*
	 * aula 60 cria uma movitação 
	 */
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		mov.setDescricao("Descrição da movimentação");
		mov.setEnvolvido("Envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao(DataUtiuls.getDataDiferencaDias(-1));
		mov.setData_pagamento(DataUtiuls.getDataDiferencaDias(5));
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}

}

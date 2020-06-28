package br.com.ster.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

import br.com.ster.rest.Utils.BarrigaUtils;
import br.com.ster.rest.Utils.DataUtiuls;
import br.com.ster.rest.corre.BaseTest;
import br.com.ster.rest.tests.Movimentacao;

public class MovimentacaoTest extends BaseTest {
	/*
	 * aula 67
	 */
	
	@Test
	public void deveInserirMovimentacao() {
		Movimentacao mov = getMovimentacaoValida();
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void validarCamposObrigorios() {
		
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
	
	@Test
	public void naoDeveInserirMovimentacaoComDataFutura() {
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
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");
			given()
				.pathParam("id", CONTA_ID)
			.when()
				.delete("/contas/{id}")
			.then()
				.statusCode(500)
				.body("constraint", is("transacoes_conta_id_foreign"))
			;
		}
	@Test
	public void deveRemoverMovimentacao() {
		Integer MOV_ID = BarrigaUtils.getIdMovPelaDescricao("Movimentacao para exclusao");
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}

	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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

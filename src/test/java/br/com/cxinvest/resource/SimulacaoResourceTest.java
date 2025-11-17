package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class SimulacaoResourceTest {

    @Test
    public void porProdutoDia_deveRetornarResumoComCamposEsperados() {
        given()
          .when().get("/simulacos/por-produto-dia")
          .then()
             .statusCode(200)
             .body("size()", greaterThan(0))
             .body("[0].produtoNome", notNullValue())
             .body("[0].data", notNullValue())
             .body("[0].totalSimulacoes", notNullValue())
             .body("[0].mediaValorFinal", notNullValue());
    }
}


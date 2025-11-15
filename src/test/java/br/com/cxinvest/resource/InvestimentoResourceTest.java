package br.com.cxinvest.resource;

import br.com.cxinvest.dto.SimulacaoRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class InvestimentoResourceTest {

    @Test
    void testSimularInvestimento_Success() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setClienteId(123L);
        request.setValor(10000.0);
        request.setPrazoMeses(12);
        request.setTipoProduto("CDB");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/simular-investimento")
        .then()
            .log().all()  // Log the response to see what's happening
            .statusCode(200)
            .body("produtoValidado", notNullValue())
            .body("produtoValidado.nome", notNullValue())
            .body("produtoValidado.tipo", equalTo("CDB"))
            .body("resultadoSimulacao", notNullValue())
            .body("resultadoSimulacao.valorFinal", notNullValue())
            .body("resultadoSimulacao.prazoMeses", equalTo(12))
            .body("dataSimulacao", notNullValue());
    }

    @Test
    void testSimularInvestimento_ClienteIdNull() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setClienteId(null);
        request.setValor(10000.0);
        request.setPrazoMeses(12);
        request.setTipoProduto("CDB");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(400)
            .body("message", containsString("ClienteId é obrigatório"));
    }

    @Test
    void testSimularInvestimento_ValorZero() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setClienteId(123L);
        request.setValor(0.0);
        request.setPrazoMeses(12);
        request.setTipoProduto("CDB");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(400)
            .body("message", containsString("Valor deve ser maior que zero"));
    }

    @Test
    void testSimularInvestimento_PrazoZero() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setClienteId(123L);
        request.setValor(10000.0);
        request.setPrazoMeses(0);
        request.setTipoProduto("CDB");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(400)
            .body("message", containsString("Prazo deve ser maior que zero"));
    }

    @Test
    void testSimularInvestimento_TipoProdutoNull() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setClienteId(123L);
        request.setValor(10000.0);
        request.setPrazoMeses(12);
        request.setTipoProduto(null);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(400)
            .body("message", containsString("Tipo de produto é obrigatório"));
    }

    @Test
    void testSimularInvestimento_ProdutoNaoEncontrado() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setClienteId(123L);
        request.setValor(10000.0);
        request.setPrazoMeses(12);
        request.setTipoProduto("PRODUTO_INEXISTENTE");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(400)
            .body("message", containsString("Nenhum produto encontrado"));
    }

    @Test
    void testListarSimulacoes_Success() {
        given()
        .when()
            .get("/simulacoes")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void testListarSimulacoesPorProdutoDia_Success() {
        given()
        .when()
            .get("/simulacoes/por-produto-dia")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void testObterPerfilRisco_Success() {
        given()
        .when()
            .get("/perfil-risco/123")
        .then()
            .statusCode(200)
            .body("clienteId", equalTo(123))
            .body("perfil", notNullValue())
            .body("pontuacao", notNullValue())
            .body("descricao", notNullValue());
    }

    @Test
    void testListarProdutosRecomendados_Conservador() {
        given()
        .when()
            .get("/produtos-recomendados/Conservador")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void testListarProdutosRecomendados_Moderado() {
        given()
        .when()
            .get("/produtos-recomendados/Moderado")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void testListarProdutosRecomendados_Agressivo() {
        given()
        .when()
            .get("/produtos-recomendados/Agressivo")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void testListarInvestimentos_Success() {
        given()
        .when()
            .get("/investimentos/123")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void testObterTelemetria_Success() {
        given()
        .when()
            .get("/telemetria")
        .then()
            .statusCode(200)
            .body("servicos", notNullValue())
            .body("periodo", notNullValue())
            .body("periodo.inicio", notNullValue())
            .body("periodo.fim", notNullValue());
    }
}

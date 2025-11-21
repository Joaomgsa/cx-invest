package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class ProdutoResourceSecurityTest {

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void testListarComRoleAdmin() {
        given()
            .when()
            .get("/produtos")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testListarComRoleAnalista() {
        given()
            .when()
            .get("/produtos")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "cliente", roles = "cliente")
    void testListarComRoleCliente() {
        given()
            .when()
            .get("/produtos")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testCriarComRoleAnalistaNegado() {
        String produtoJson = """
        {
            "nome": "CDB Test",
            "tipo": "CDB",
            "rentabilidadeMensal": 0.01,
            "perfilId": 1
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(produtoJson)
            .when()
            .post("/produtos")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "cliente", roles = "cliente")
    void testCriarComRoleClienteNegado() {
        String produtoJson = """
        {
            "nome": "CDB Test",
            "tipo": "CDB",
            "rentabilidadeMensal": 0.01,
            "perfilId": 1
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(produtoJson)
            .when()
            .post("/produtos")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testDeletarComRoleAnalistaNegado() {
        given()
            .when()
            .delete("/produtos/1")
            .then()
            .statusCode(403);
    }
}

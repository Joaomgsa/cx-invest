package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class ClienteResourceSecurityTest {

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void testListarComRoleAdmin() {
        given()
            .when()
            .get("/clientes")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testListarComRoleAnalista() {
        given()
            .when()
            .get("/clientes")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "cliente", roles = "cliente")
    void testListarComRoleClienteNegado() {
        given()
            .when()
            .get("/clientes")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testCriarComRoleAnalistaNegado() {
        String clienteJson = """
        {
            "nome": "Test Cliente",
            "email": "test@example.com",
            "perfilId": 1,
            "totalInvestido": 10000.0,
            "frequenciaInvestimento": "MENSAL",
            "preferenciaInvestimento": "CONSERVADOR"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(clienteJson)
            .when()
            .post("/clientes")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "cliente", roles = "cliente")
    void testCriarComRoleClienteNegado() {
        String clienteJson = """
        {
            "nome": "Test Cliente",
            "email": "test@example.com",
            "perfilId": 1,
            "totalInvestido": 10000.0,
            "frequenciaInvestimento": "MENSAL",
            "preferenciaInvestimento": "CONSERVADOR"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(clienteJson)
            .when()
            .post("/clientes")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testDeletarComRoleAnalistaNegado() {
        given()
            .when()
            .delete("/clientes/1")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "cliente", roles = "cliente")
    void testBuscarComRoleCliente() {
        given()
            .when()
            .get("/clientes/1")
            .then()
            .statusCode(anyOf(is(200), is(404)));
    }
}

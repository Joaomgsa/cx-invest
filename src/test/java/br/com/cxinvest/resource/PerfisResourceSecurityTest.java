package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class PerfisResourceSecurityTest {

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void testListarComRoleAdmin() {
        given()
            .when()
            .get("/perfis")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testListarComRoleAnalista() {
        given()
            .when()
            .get("/perfis")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "cliente", roles = "cliente")
    void testListarComRoleCliente() {
        given()
            .when()
            .get("/perfis")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void testCriarComRoleAdmin() {
        String perfilJson = """
        {
            "nome": "Perfil Test",
            "descricao": "Descricao"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(perfilJson)
            .when()
            .post("/perfis")
            .then()
            .statusCode(anyOf(is(200), is(201), is(500)));
    }

    @Test
    @TestSecurity(user = "analista", roles = "analista")
    void testCriarComRoleAnalistaNegado() {
        String perfilJson = """
        {
            "nome": "Perfil Test",
            "descricao": "Descricao"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(perfilJson)
            .when()
            .post("/perfis")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "cliente", roles = "cliente")
    void testCriarComRoleClienteNegado() {
        String perfilJson = """
        {
            "nome": "Perfil Test",
            "descricao": "Descricao"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(perfilJson)
            .when()
            .post("/perfis")
            .then()
            .statusCode(403);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "analista", roles = "analista")
    void testDeletarComRoleAnalistaNegado(int perfilId) {
        given()
            .when()
            .delete("/perfis/" + perfilId)
            .then()
            .statusCode(403);
    }
}

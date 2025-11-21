package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class SimulacaoResourceSecurityTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "admin", roles = "admin")
    void testSimularComRoleAdmin(int clienteId) {
        String simJson = String.format("""
        {
            "clienteId": %d,
            "valor": 1000.0,
            "prazoMeses": 12,
            "tipoProduto": "CDB"
        }
        """, clienteId);

        given()
            .contentType(ContentType.JSON)
            .body(simJson)
            .when()
            .post("/simulacoes/simular-investimento")
            .then()
            .statusCode(anyOf(is(200), is(400)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "analista", roles = "analista")
    void testSimularComRoleAnalista(int clienteId) {
        String simJson = String.format("""
        {
            "clienteId": %d,
            "valor": 1000.0,
            "prazoMeses": 12,
            "tipoProduto": "CDB"
        }
        """, clienteId);

        given()
            .contentType(ContentType.JSON)
            .body(simJson)
            .when()
            .post("/simulacoes/simular-investimento")
            .then()
            .statusCode(anyOf(is(200), is(400)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "cliente", roles = "cliente")
    void testSimularComRoleCliente(int clienteId) {
        String simJson = String.format("""
        {
            "clienteId": %d,
            "valor": 1000.0,
            "prazoMeses": 12,
            "tipoProduto": "CDB"
        }
        """, clienteId);

        given()
            .contentType(ContentType.JSON)
            .body(simJson)
            .when()
            .post("/simulacoes/simular-investimento")
            .then()
            .statusCode(anyOf(is(200), is(400)));
    }
}

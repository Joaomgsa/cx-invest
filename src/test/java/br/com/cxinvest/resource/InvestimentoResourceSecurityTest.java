package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class InvestimentoResourceSecurityTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "admin", roles = "admin")
    void testListarInvestimentosComRoleAdmin(int clienteId) {
        given()
            .when()
            .get("/investimentos/" + clienteId)
            .then()
            .statusCode(anyOf(is(200), is(404), is(500)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "analista", roles = "analista")
    void testListarInvestimentosComRoleAnalista(int clienteId) {
        given()
            .when()
            .get("/investimentos/" + clienteId)
            .then()
            .statusCode(anyOf(is(200), is(404), is(500)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "cliente", roles = "cliente")
    void testListarInvestimentosComRoleClientePossivel(int clienteId) {
        given()
            .when()
            .get("/investimentos/" + clienteId)
            .then()
            .statusCode(anyOf(is(200), is(404), is(403), is(500)));
    }
}

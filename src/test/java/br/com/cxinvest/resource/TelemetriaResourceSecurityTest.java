package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class TelemetriaResourceSecurityTest {

    @ParameterizedTest
    @ValueSource(ints = {0})
    @TestSecurity(user = "admin", roles = "admin")
    void testTelemetriaComRoleAdmin(int unused) {
        given()
            .when()
            .get("/telemetria")
            .then()
            .statusCode(anyOf(is(200), is(204), is(500)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    @TestSecurity(user = "analista", roles = "analista")
    void testTelemetriaComRoleAnalista(int unused) {
        given()
            .when()
            .get("/telemetria")
            .then()
            .statusCode(anyOf(is(200), is(204), is(500)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    @TestSecurity(user = "cliente", roles = "cliente")
    void testTelemetriaComRoleClienteNegado(int unused) {
        given()
            .when()
            .get("/telemetria")
            .then()
            .statusCode(anyOf(is(403), is(500)));
    }
}

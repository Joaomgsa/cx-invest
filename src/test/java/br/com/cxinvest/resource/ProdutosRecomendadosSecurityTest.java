package br.com.cxinvest.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class ProdutosRecomendadosSecurityTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "admin", roles = "admin")
    void testProdutosRecomendadosComRoleAdmin(int perfilId) {
        given()
            .when()
            .get("/produtos-recomendados/" + perfilId)
            .then()
            .statusCode(anyOf(is(200), is(404)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "analista", roles = "analista")
    void testProdutosRecomendadosComRoleAnalista(int perfilId) {
        given()
            .when()
            .get("/produtos-recomendados/" + perfilId)
            .then()
            .statusCode(anyOf(is(200), is(404)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @TestSecurity(user = "cliente", roles = "cliente")
    void testProdutosRecomendadosComRoleCliente(int perfilId) {
        given()
            .when()
            .get("/produtos-recomendados/" + perfilId)
            .then()
            .statusCode(anyOf(is(200), is(404)));
    }
}

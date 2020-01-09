package org.acme.rest.json;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FruitResourceTest {

    @Test
    public void testList() {
        given()
          .when().get("/fruits")
          .then()
             .statusCode(200)
             .body("$.size()", is(2),
                     "name", containsInAnyOrder("Apple", "Pineapple"),
                     "x", containsInAnyOrder(0, 1),
                     "y", containsInAnyOrder(1, 0));
    }
}

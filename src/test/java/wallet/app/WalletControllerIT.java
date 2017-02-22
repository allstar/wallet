package wallet.app;

import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class WalletControllerIT extends IntegrationTest {
    private long id;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        id = Long.parseLong(given()
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("balance", equalTo(0))
                .extract()
                .header("Location"));
    }

    @Test
    public void canGetWallet() throws Exception {
        given()
                .when()
                .get("/" + id)
                .then()
                .statusCode(200)
                .body("balance", equalTo(new BigDecimal("0.0000")));

    }

    @Test
    public void createTransaction() throws Exception {
        given()
                .contentType(ContentType.JSON)
                .body("{\"amount\": 200}")
                .when()
                .post("/" + id + "/transactions")
                .then()
                .statusCode(201)
                .body("balance", equalTo(new BigDecimal("200.0000")));
    }
}
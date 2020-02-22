package com.kkot.moneytransfer.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.inject.Inject;

import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.Transfer;

@QuarkusTest
public class TransferResourceTest {

	@Inject
	private Bank bank;

	@Test
	void shouldReturn400_whenAndReasonWhenSourceAccountDoesNotExist() {
		TransferDto transfer = new TransferDto("1", "2", 50);
		given()
				.contentType(ContentType.JSON)
				.body(transfer)
				.post("/transfer")
				.then()
				.statusCode(400)
				.body(is("{\"code\":1,\"message\":\"xxx\"}"));
	}

	//@Test
	public void testHelloEndpoint() {
		given()
				.when().get("/transfer")
				.then()
				.statusCode(200)
				.body(is("hello"));
	}

}

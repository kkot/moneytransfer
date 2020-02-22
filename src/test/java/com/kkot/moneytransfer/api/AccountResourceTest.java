package com.kkot.moneytransfer.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.inject.Inject;

import com.kkot.moneytransfer.domain.Bank;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AccountResourceTest {

	@Inject
	private Bank bank;

	//@Test
	void shouldReturn0_whenBalanceIsChecked() {

		given()
				.when().get("/account/0/balance")
				.then()
				.statusCode(200)
				.body(is("123"));
	}
}

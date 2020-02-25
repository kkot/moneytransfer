package com.kkot.moneytransfer.api;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.kkot.moneytransfer.domain.valueobject.AccountId;
import com.kkot.moneytransfer.domain.Bank;

@Path("/account/{id}")
public class AccountResource {

	@Inject
	Bank bank;

	@GET
	@Path("/balance")
	@Produces(MediaType.APPLICATION_JSON)
	public String balance(@PathParam("id") String id) {
		return String.valueOf(bank.getBalance(AccountId.of(id)));
	}
}

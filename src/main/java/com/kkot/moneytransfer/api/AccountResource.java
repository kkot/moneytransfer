package com.kkot.moneytransfer.api;

import com.kkot.moneytransfer.api.dto.AccountDto;
import com.kkot.moneytransfer.api.service.AccountApiService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account/{id}")
public class AccountResource {
    @Inject
    private AccountApiService accountApiService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        return accountApiService.getAccount(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(@PathParam("id") String id, AccountDto accountDto) {
        return accountApiService.createAccount(id, accountDto);
    }
}

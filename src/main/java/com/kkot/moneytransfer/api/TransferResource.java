package com.kkot.moneytransfer.api;

import com.kkot.moneytransfer.api.dto.TransferDto;
import com.kkot.moneytransfer.api.service.TransferApiService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferResource {
    private final TransferApiService service;

    public TransferResource(final TransferApiService service) {
        this.service = service;
    }

    @POST
    public Response performTransfer(TransferDto transfer) {
        return service.transfer(transfer);
    }
}

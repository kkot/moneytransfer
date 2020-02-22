package com.kkot.moneytransfer.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kkot.moneytransfer.domain.Transfer;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferResource {

    @Inject
    private TransferService service;

    @POST
    public Response newTransfer(TransferDto transfer) {
        return service.transfer(transfer);
    }
}

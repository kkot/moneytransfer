package com.kkot.moneytransfer.api;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class TransferService {

    public Response transfer(final TransferDto transfer) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDto(1, "xxx"))
                .type( MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}

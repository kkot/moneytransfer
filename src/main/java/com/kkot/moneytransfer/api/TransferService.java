package com.kkot.moneytransfer.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kkot.moneytransfer.domain.Bank;

@ApplicationScoped
public class TransferService {

    @Inject
    private Bank bank;

    public Response transfer(final TransferDto transfer) {
        bank.transfer(transfer.toTransfer());
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new TransferErrorDto(TransferErrorType.ACCOUNT_ID_MISSING, "1"))
                .type( MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}

package com.kkot.moneytransfer.api.service;

import com.kkot.moneytransfer.api.TransferErrorType;
import com.kkot.moneytransfer.api.dto.TransferDto;
import com.kkot.moneytransfer.api.dto.TransferErrorDto;
import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.status.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class TransferApiService {
    private static final Logger log = LoggerFactory.getLogger(TransferApiService.class);

    private final Bank bank;

    @Inject
    public TransferApiService(final Bank bank) {
        this.bank = bank;
    }

    public Response transfer(final TransferDto transfer) {
        log.debug("making transfer {}", transfer);
        OperationStatus status = bank.transfer(transfer.toTransfer());
        log.debug("transfer result {}", status);
        if (status instanceof OkStatus) {
            return Response.ok().build();
        }
        if (status instanceof AccountIsMissingStatus) {
            var accountNotExistStatus = (AccountIsMissingStatus) status;
            var errorDto = new TransferErrorDto(TransferErrorType.ACCOUNT_ID_MISSING,
                    accountNotExistStatus.getAccountId());
            return createBadRequest(errorDto);
        }
        if (status instanceof AmountIsNotPositiveNumberStatus) {
            var errorDto = new TransferErrorDto(TransferErrorType.NOT_POSITIVE_AMOUNT);
            return createBadRequest(errorDto);
        }
        if (status instanceof InsufficientBalanceStatus) {
            var insufficientBalanceStatus = (InsufficientBalanceStatus) status;
            var errorDto = new TransferErrorDto(TransferErrorType.INSUFFICIENT_BALANCE,
                    insufficientBalanceStatus.getAccountId());
            return createBadRequest(errorDto);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response createBadRequest(final TransferErrorDto errorDto) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorDto)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}

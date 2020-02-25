package com.kkot.moneytransfer.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kkot.moneytransfer.domain.Bank;
import com.kkot.moneytransfer.domain.status.AccountIsMissingStatus;
import com.kkot.moneytransfer.domain.status.InsufficientBalanceStatus;
import com.kkot.moneytransfer.domain.status.OkStatus;
import com.kkot.moneytransfer.domain.status.OperationStatus;

@ApplicationScoped
public class TransferApiService {
	private final Bank bank;

	@Inject
	public TransferApiService(final Bank bank) {
		this.bank = bank;
	}

	public Response transfer(final TransferDto transfer) {
		OperationStatus status = bank.transfer(transfer.toTransfer());
		if(status instanceof OkStatus) {
			return Response.ok().build();
		}
		if(status instanceof AccountIsMissingStatus) {
			var accountNotExistStatus = (AccountIsMissingStatus) status;
			var errorDto = new TransferErrorDto(TransferErrorType.ACCOUNT_ID_MISSING,
					accountNotExistStatus.getAccountId());
			return createBadRequest(errorDto);
		}
		if(status instanceof InsufficientBalanceStatus) {
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

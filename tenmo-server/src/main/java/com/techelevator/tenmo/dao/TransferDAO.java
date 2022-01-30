package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {

    public List<Transfer> getAllTransfers (long userId);
    public Transfer getTransferWithId (long transferId);
    public String sendTransfer (long userFrom, long userTo, BigDecimal transferAmount);
    public String requestTransfer (long userFrom, long userTo, BigDecimal transferAmount);
    public List<Transfer> getPendingRequests (long userId);
    public String updateTransferRequest (Transfer transfer, long statusId);
}

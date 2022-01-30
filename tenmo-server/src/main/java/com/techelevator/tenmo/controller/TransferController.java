package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    private TransferDAO transferDAO;

    public TransferController(TransferDAO transferDAO) {
        this.transferDAO = transferDAO;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "account/transfers/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllMyTransfers(@PathVariable int id) {
        List<Transfer> allTransfers = transferDAO.getAllTransfers(id);
        return allTransfers;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public Transfer getSelectedTransfer(@PathVariable int id) {
        Transfer transfer = transferDAO.getTransferWithId(id);
        return transfer;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public String sendTransferRequest(@RequestBody Transfer transfer) {
        String request = transferDAO.sendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return request;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(path = "request", method = RequestMethod.POST)
    public String requestTransferRequest(@RequestBody Transfer transfer) {
        String request = transferDAO.requestTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return request;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "request/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllTransferRequests(@PathVariable int id) {
        List<Transfer> allTransfers = transferDAO.getPendingRequests(id);
        return allTransfers;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(path = "transfer/status/{statusId}", method = RequestMethod.PUT)
    public String updateRequest(@RequestBody Transfer transfer, @PathVariable int statusId) {
        String request = transferDAO.updateTransferRequest(transfer, statusId);
        return request;
    }
}
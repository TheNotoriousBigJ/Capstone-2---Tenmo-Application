package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDAO {

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    private AccountDAO accountDAO;


    @Override
    public List<Transfer> getAllTransfers(long userId) {
        ArrayList<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfers.*, f.username AS userFrom, t.username AS userTo " +
                     "FROM transfers " +
                     "JOIN accounts a ON transfers.account_from = a.account_id " +
                     "JOIN accounts b ON transfers.account_to = b.account_id " +
                     "JOIN users f ON a.user_id = f.user_id " +
                     "JOIN users t ON b.user_id = t.user_id " +
                     "WHERE a.user_id = ? OR b.user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (result.next()) {
            transfers.add(mapRowToTransfer(result));
        }

        return transfers;
    }

    @Override
    public Transfer getTransferWithId(long transferId) {
        Transfer transfer = new Transfer();

        String sql = "SELECT transfers.*, f.username AS userFrom, t.username AS userTo, transfer_status.transfer_status_desc, transfer_type.transfer_type_desc " +
                     "FROM transfers " +
                     "JOIN accounts a ON transfers.account_from = a.account_id " +
                     "JOIN accounts b ON transfers.account_to = b.account_id " +
                     "JOIN users f ON a.user_id = f.user_id " +
                     "JOIN users t ON b.user_id = t.user_id " +
                     "JOIN transfer_statuses transfer_status ON transfers.transfer_status_id = transfer_status.transfer_status_id " +
                     "JOIN transfer_types transfer_type ON transfers.transfer_type_id = transfer_type.transfer_type_id " +
                     "WHERE transfers.transfer_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);
        while (result.next()) {
            transfer = mapRowToTransfer(result);
        }
        return transfer;
    }

    @Override
    public String sendTransfer(long userFrom, long userTo, BigDecimal transferAmount) {
        if (userFrom == userTo) {
            return "Cannot send money to yourself, nice try!";
        }
        if (transferAmount.compareTo(new BigDecimal(0)) == 0) {
            return "Cannot send an empty transfer.";
        }
        if (transferAmount.compareTo(accountDAO.getBalance(userFrom)) == -1) {
            String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                         "VALUES (2,2,?,?,?);";
            jdbcTemplate.update(sql, accountDAO.findUserWithId(userFrom).getAccountId(),
                                accountDAO.findUserWithId(userTo).getAccountId(), transferAmount);
            accountDAO.subtractBalance(transferAmount,accountDAO.findUserWithId(userFrom).getAccountId());
            accountDAO.addBalance(transferAmount,accountDAO.findUserWithId(userTo).getAccountId());
            return "Transfer complete!";
        } else
        return "Cannot complete transfer, check transfer amount and receiving user.";
    }

    @Override
    public String requestTransfer(long userFrom, long userTo, BigDecimal transferAmount) {
        if (userFrom == userTo) {
            return "Cannot request money from yourself, nice try!";
        }
        if (transferAmount.compareTo(accountDAO.getBalance(userFrom)) == -1 ||
                (transferAmount.compareTo(accountDAO.getBalance(userFrom))) == 0) {
            String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (1,1,?,?,?);";
            jdbcTemplate.update(sql,accountDAO.findUserWithId(userFrom).getAccountId()
                               ,accountDAO.findUserWithId(userTo).getAccountId(),transferAmount);
            return "Transfer requested, awaiting approval.";
        } else
        return "Cannot complete transfer, check transfer amount and requested user.";
    }

    @Override
    public List<Transfer> getPendingRequests(long userId) {
        ArrayList<Transfer> pendingRequests = new ArrayList<>();

        String sql = "SELECT transfers.*, u.username AS userFrom, v.username AS userTo FROM transfers " +
                "JOIN accounts a ON transfers.account_from = a.account_id " +
                "JOIN accounts b ON transfers.account_to = b.account_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN users v ON b.user_id = v.user_id " +
                "WHERE transfer_status_id = 1 AND (account_from = ? OR account_to = ?);";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountDAO.findUserWithId(userId).getAccountId()
                                                    ,accountDAO.findUserWithId(userId).getAccountId());
        while (result.next()) {
            Transfer transfer = mapRowToTransfer(result);
            pendingRequests.add(transfer);
        }
        return pendingRequests;
    }

    @Override
    public String updateTransferRequest(Transfer transfer, long statusId) {
        if (statusId == 3) {
            String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?;";
            jdbcTemplate.update(sql, statusId, transfer.getTransferId());
            return "Transfer rejected";
        }
        if (!(accountDAO.findAccountWithId(transfer.getAccountFrom()).getBalance().compareTo(transfer.getAmount()) == -1)) {
            String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?;";
            jdbcTemplate.update(sql, statusId, transfer.getTransferId());
            accountDAO.addBalance(transfer.getAmount(), transfer.getAccountTo());
            accountDAO.subtractBalance(transfer.getAmount(), transfer.getAccountFrom());
            return "Transfer approved";
        } else {
            return "Insufficient funds";
        }
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getLong("transfer_type_id"));
        transfer.setTransferStatusId(results.getLong("transfer_status_id"));
        transfer.setAccountFrom(results.getLong("account_From"));
        transfer.setAccountTo(results.getLong("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        try {
            transfer.setUserFrom(results.getString("userFrom"));
            transfer.setUserTo(results.getString("userTo"));
        } catch (Exception e) {
        }
        try {
            transfer.setTransferTypeDescription(results.getString("transfer_type_desc"));
            transfer.setTransferStatusDescription(results.getString("transfer_status_desc"));
        } catch (Exception e) {
        }
        return transfer;
    }
}

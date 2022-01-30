package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDAO {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public BigDecimal getBalance(long userId) {
        BigDecimal thisBalance = null;
        try {
            String getBalanceById = "SELECT balance " +
                                    "FROM accounts " +
                                    "WHERE user_id = ?;";
            SqlRowSet sql = jdbcTemplate.queryForRowSet(getBalanceById, userId);

            while (sql.next()) {
                thisBalance = sql.getBigDecimal("balance");
            }
        } catch (DataAccessException e) {
            System.out.println("Cannot Access Data");
            throw new RuntimeException(e);
        }
        return thisBalance;
    }

    public BigDecimal addBalance(BigDecimal amountToAdd, long accountId) {
        Account account = findAccountWithId(accountId);
        BigDecimal newBalance = account.getBalance().add(amountToAdd);
        System.out.println(newBalance);

        String sql = "UPDATE accounts " +
                     "SET balance = ? " +
                     "WHERE account_id = ?;";
        try {
            jdbcTemplate.update(sql, newBalance, accountId);
        } catch (DataAccessException e) {
            System.out.println("Cannot Access Data");
        }
        return account.getBalance();
    }

    @Override
    public BigDecimal subtractBalance(BigDecimal amountToSubtract, long accountId) {
        Account account = findAccountWithId(accountId);
        BigDecimal newBalance = account.getBalance().subtract(amountToSubtract);
        System.out.println(newBalance);

        String sql = "UPDATE accounts " +
                     "SET balance = ? " +
                     "WHERE account_id = ?;";
        try {
            jdbcTemplate.update(sql, newBalance, accountId);
        } catch (DataAccessException e) {
            System.out.println("Cannot Access Data");
        }
        return account.getBalance();
    }

    @Override
    public Account findUserWithId(long userId) {
        Account account = new Account();

        String getAccountFromUserId = "SELECT * " +
                                      "FROM accounts " +
                                      "WHERE user_id = ?";
        System.out.println("findUserWithId: userId = " + userId);
        try {
            SqlRowSet sql = jdbcTemplate.queryForRowSet(getAccountFromUserId, userId);
            while (sql.next()) {
                account = mapRowToAccount(sql);
            }
        } catch (DataAccessException e) {
            System.out.println("Cannot Access Data");
        }
        return account;
    }

    @Override
    public Account findAccountWithId(long accountId) {
        Account account = new Account();

        String getAccountFromAccountId = "SELECT balance, account_id, user_id " +
                                      "FROM accounts " +
                                      "WHERE account_id = ?;";

        System.out.println("findAccountWithId: accountId = " + accountId);
        try {
            SqlRowSet sql = jdbcTemplate.queryForRowSet(getAccountFromAccountId, accountId);
            if (sql.next()) {
                account = mapRowToAccount(sql);
            }
        } catch (DataAccessException e) {
            System.out.println("Cannot Access Data");
        }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet sql){
        try {
            Account account = new Account();
            account.setBalance(sql.getBigDecimal("balance"));
            account.setAccountId(sql.getLong("account_id"));
            account.setUserId(sql.getLong("user_id"));
            return account;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDAO {

    BigDecimal getBalance (long userId);
    BigDecimal addBalance (BigDecimal amountToAdd, long accountId);
    BigDecimal subtractBalance (BigDecimal amountToSubtract, long accountId);
    Account findUserWithId (long userId);
    public Account findAccountWithId (long accountId);
}

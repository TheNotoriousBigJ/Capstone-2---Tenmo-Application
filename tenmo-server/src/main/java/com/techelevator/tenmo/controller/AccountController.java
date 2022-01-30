package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private UserDao userDao;

    public AccountController(AccountDAO accountDAO, UserDao userDao) {
        this.accountDAO = accountDAO;
        this.userDao = userDao;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(path = "balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        BigDecimal balance = accountDAO.getBalance(id);
        return balance;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(path = "findall", method = RequestMethod.GET)
    public List<User> findAll() {
        List<User> allUsers = userDao.findAll();
        return allUsers;
    }
}

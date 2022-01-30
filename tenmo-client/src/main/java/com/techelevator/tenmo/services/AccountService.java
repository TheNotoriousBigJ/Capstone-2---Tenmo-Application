package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public AccountService (AuthenticatedUser currentUser){
        this.currentUser = currentUser;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0);

        try {
            balance = restTemplate.exchange(API_BASE_URL + "balance/" + currentUser.getUser().getId(),
                      HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
            System.out.println("Users current balance is: $ " + balance);
        } catch (RestClientException e) {
            System.out.println("Could not retrieve current balance");
        }
        return balance;
    }
}

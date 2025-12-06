package com.mammer.botwithiatest.infrastructure.mt5;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "mt5")
public class Mt5Properties {

    private String server;
    private String login;
    private String password;
    private Mt5AccountType accountType = Mt5AccountType.DEMO;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Mt5AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(Mt5AccountType accountType) {
        this.accountType = accountType;
    }

    public boolean isFullyConfigured() {
        return StringUtils.hasText(server) && StringUtils.hasText(login) && StringUtils.hasText(password);
    }
}

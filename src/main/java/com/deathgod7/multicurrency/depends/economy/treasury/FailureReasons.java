package com.deathgod7.multicurrency.depends.economy.treasury;

import me.lokka30.treasury.api.common.response.FailureReason;
import org.jetbrains.annotations.NotNull;

public enum  FailureReasons implements FailureReason {
    FEATURE_NOT_SUPPORTED("Feature is not supported at the moment!"),
    INVALID_VALUE("Invalid value inputted!"),
    INVALID_CURRENCY("Invalid currency inputted!"),
    UPDATE_FAILED("Updating player currency failed!"),
    NEGATIVE_BALANCES_NOT_SUPPORTED("Negative value in currency is not supported!"),
    ACCOUNT_CREATE_FAILURE("Account could not be created!"),
    ACCOUNTS_RETRIEVE_FAILURE("Account could not be retrieved!"),
    ACCOUNT_NOT_FOUND("Account could not be found!");
    private final String description;

    FailureReasons(String description) {
        this.description = description;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }
}

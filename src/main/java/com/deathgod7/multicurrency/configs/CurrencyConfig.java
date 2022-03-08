package com.deathgod7.multicurrency.configs;

import com.deathgod7.multicurrency.depends.economy.CurrencyTypes;
import redempt.redlib.config.annotations.Comment;
import redempt.redlib.config.annotations.ConfigMappable;

@ConfigMappable
public class CurrencyConfig{
    @Comment("#############################################################################")
    @Comment("||                                                                         ||")
    @Comment("||                   Multi Currency - by Death GOD 7                       ||")
    @Comment("||                            - A solution to your custom needs            ||")
    @Comment("||                                                                         ||")
    @Comment("||               [ Github : https://github.com/DeathGOD7 ]                 ||")
    @Comment("||       [ Wiki : https://github.com/DeathGOD7/MultiCurrency/wiki ]        ||")
    @Comment("||                                                                         ||")
    @Comment("#############################################################################")
    @Comment("")
    @Comment("Currency File Configs")
    @Comment("")
    public CurrencyTypes currency = new CurrencyTypes();

}

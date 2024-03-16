package com.studentportal.helpbot.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;




@Configuration
@Data
@NoArgsConstructor
@PropertySource("classpath:application.properties")
public class HelpbotConfig {
    @Value("${telegram.help.bot.username}")
    private  String username;

    @Value("${telegram.help.bot.token}")
    private  String token;

    @Value("${telegram.help.bot.tokenpay}")
    private  String tokenPay;
}

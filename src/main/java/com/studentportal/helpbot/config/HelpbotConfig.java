package com.studentportal.helpbot.config;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;



@Configuration
@EnableScheduling
@Data
@PropertySource("classpath:application.properties")
public class HelpbotConfig {
    @Value("${telegram.help.bot.username}")
    private final String username;

    @Value("${telegram.help.bot.token}")
    private final String token;

    @Value("${telegram.help.bot.tokenpay}")
    private final String tokenPay;


}

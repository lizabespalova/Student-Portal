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
public class HelpbotConfig {
    @Value("${BOT_NAME}")
    private final String username;

    @Value("${TOKEN}")
    private final String token;

    @Value("${TOKEN_PAY}")
    private final String tokenPay;

    public HelpbotConfig(
            @Value("${BOT_NAME}") String username,
            @Value("${TOKEN}") String token,
            @Value("${TOKEN_PAY}") String tokenPay
    ) {
        this.username = username;
        this.token = token;
        this.tokenPay = tokenPay;
    }
}

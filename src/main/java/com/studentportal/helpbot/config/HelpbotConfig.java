package com.studentportal.helpbot.config;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;




@Configuration
@PropertySource("classpath:application.properties")
public class HelpbotConfig {
    @Value("${telegram.help.bot.username}")
    private  String username;

    @Value("${telegram.help.bot.token}")
    private  String token;

    @Value("${telegram.help.bot.tokenpay}")
    private  String tokenPay;


}

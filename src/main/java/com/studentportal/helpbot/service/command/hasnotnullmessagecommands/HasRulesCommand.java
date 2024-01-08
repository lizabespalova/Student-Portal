package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HasRulesCommand extends HasNotNullMessageCommands{
    public HasRulesCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        helpbot.rules_buttons(String.valueOf(update.getMessage().getChatId()));
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        String user_sms = message.getText();
        return user_sms.equals(EmojiParser.parseToUnicode("Правила користування ботом"+":clipboard:"));
    }
}

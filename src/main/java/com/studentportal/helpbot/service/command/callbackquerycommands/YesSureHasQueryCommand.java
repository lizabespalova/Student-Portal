package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.repository.CustomerRepository;
import com.studentportal.helpbot.repository.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class YesSureHasQueryCommand extends QueryCommands {
    public YesSureHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        generateNewLink(update);
    }

    @Override
    public boolean apply(Update update) {
        if(update.hasCallbackQuery()) {
            var messagetext = update.getCallbackQuery().getData();
            return messagetext.equals("YESSURE");
        }
        return false;
    }
}

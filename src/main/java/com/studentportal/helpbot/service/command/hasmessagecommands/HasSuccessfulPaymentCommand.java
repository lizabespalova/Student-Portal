package com.studentportal.helpbot.service.command.hasmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class HasSuccessfulPaymentCommand extends HasMessageCommands{
    HasSuccessfulPaymentCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        servePayment(update.getMessage().getSuccessfulPayment());
        blockPayment(update.getMessage().getSuccessfulPayment());
    }

    @Override
    public boolean apply(Update update) {
        return update.getMessage().hasSuccessfulPayment();
    }
}

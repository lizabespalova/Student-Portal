package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HasForwardMessage extends HasNotNullMessageCommands {
    HasForwardMessage(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        if(customerRepository.findById(message.getFrom().getId()).get().getThiefListState()==50)
            checkThiefFromCustomer(message);
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        return message.getForwardFrom()!=null;
    }

}

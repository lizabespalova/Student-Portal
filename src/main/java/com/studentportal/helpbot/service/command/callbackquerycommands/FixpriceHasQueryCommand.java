package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Quiz;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class FixpriceHasQueryCommand extends QueryCommands {
    public FixpriceHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }
    @Override
    public void resolve(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        set_fix_price_menu(String.valueOf(chatId),update.getCallbackQuery().getMessage());
    }

    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals(Quiz.FIXPRICE.toString())
                && customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getState().equals(Quiz.FILEDESCRIPTION.toString());
    }


}

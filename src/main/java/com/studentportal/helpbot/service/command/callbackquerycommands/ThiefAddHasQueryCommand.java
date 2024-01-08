package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Component
public class ThiefAddHasQueryCommand extends QueryCommands {
    public ThiefAddHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        setAdminToAddThief(update);
    }

    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals("THIEFADD");
    }
    public void setAdminToAddThief(Update update){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText(Text.setMe);
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

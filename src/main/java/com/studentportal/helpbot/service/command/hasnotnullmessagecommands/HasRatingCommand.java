package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.Performer;
import com.studentportal.helpbot.repository.CustomerRepository;
import com.studentportal.helpbot.repository.PerformerRepository;
import com.studentportal.helpbot.repository.RoomsRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HasRatingCommand extends HasNotNullMessageCommands{
    @Autowired
    PerformerRepository performerRepository;
    public HasRatingCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {

        if( !performerRepository.findById(update.getMessage().getFrom().getId()).isEmpty()){
            Performer performer = performerRepository.findById(update.getMessage().getFrom().getId()).get();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("Ваш рейтинг: " + performer.getRating());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else{
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("У вас нема рейтингу");
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        String user_sms = message.getText();
        return user_sms.equals(Text.rating);
    }
}

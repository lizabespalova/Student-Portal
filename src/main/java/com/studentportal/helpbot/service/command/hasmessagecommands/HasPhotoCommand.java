package com.studentportal.helpbot.service.command.hasmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Quiz;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class HasPhotoCommand extends HasMessageCommands{
    HasPhotoCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        get_photo_description(update);
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        return update.hasMessage() && update.getMessage().hasPhoto() && customerRepository.findById(message.getChatId()).get().getState().equals(Quiz.TEXTDESCRIPTION.toString());
    }
}

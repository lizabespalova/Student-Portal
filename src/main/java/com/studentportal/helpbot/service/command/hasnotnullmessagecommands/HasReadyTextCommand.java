package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Quiz;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
@Component
public class HasReadyTextCommand extends HasNotNullMessageCommands{
    public HasReadyTextCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        end_stop_menu(String.valueOf(message.getChatId()));

        try {
            if (customerRepository.findById(message.getChatId()).get().getPhotoLink() != null)
                set_photo_to_channel_and_return_link(message);
            if (customerRepository.findById(message.getChatId()).get().getFileLink() != null)
                set_file_to_channel_and_return_link(message);
            if (customerRepository.findById(message.getChatId()).get().getPhotoLink() == null)
                customerRepository.findById(message.getChatId()).get().setPhotoLink(null);
            if (customerRepository.findById(message.getChatId()).get().getFileLink() == null)
                customerRepository.findById(message.getChatId()).get().setFileLink(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!customerRepository.findById(message.getChatId()).get().isCheck_state()) {
            set_post(String.valueOf(message.getChatId()), 0, message.getChatId(), message);
            set_last_buttons(String.valueOf(message.getChatId()));
        } else set_price_description(String.valueOf(message.getChatId()), message);
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        String user_sms = message.getText();
        return user_sms.equals(Text.ready_text) && customerRepository.findById(message.getChatId()).get().getState().equals(Quiz.TEXTDESCRIPTION.toString());
    }
}

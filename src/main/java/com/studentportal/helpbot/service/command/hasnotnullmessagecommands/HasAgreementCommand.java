package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class HasAgreementCommand extends HasNotNullMessageCommands{
    public HasAgreementCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        end_stop_menu(String.valueOf(message.getChatId()));
        if (!customerRepository.findById(message.getChatId()).get().isCheck_state()) {
            set_post(String.valueOf(message.getChatId()), 0, update.getCallbackQuery().getMessage().getMessageId(), message);
            set_last_buttons(String.valueOf(message.getChatId()));
        } else set_subject_menu(String.valueOf(message.getChatId()), message);
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        String user_sms = message.getText();
        return user_sms.equals(Text.agreement_text);
    }
}

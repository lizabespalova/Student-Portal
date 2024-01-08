package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class HasChangePhotoOrFileCommand extends HasNotNullMessageCommands{
    public HasChangePhotoOrFileCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setPhotoLink(null);
        customer.setFileLink(null);
        customerRepository.save(customer);
        set_file_description(message.getChatId(), message);
        set_ready_button(String.valueOf(message.getChatId()));
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        String user_sms = message.getText();
        return user_sms.equals(Text.change_photo_or_file);
    }
}

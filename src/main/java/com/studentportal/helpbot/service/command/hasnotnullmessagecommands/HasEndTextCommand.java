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
public class HasEndTextCommand extends HasNotNullMessageCommands {
    public HasEndTextCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        helpbot.set_main_menu(String.valueOf(message.getChatId()), message);
        //редагування коду
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setCheck_state(true);
        customer.setPriceFlag(1);
        customer.setPhotoLink(null);
        customer.setFileLink(null);
        customerRepository.save(customer);
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        String user_sms = message.getText();
        return user_sms.equals(Text.end_text);
    }
}

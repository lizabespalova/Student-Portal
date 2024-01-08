package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Buttons;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ICustomerHasQueryCommand extends QueryCommands{
    ICustomerHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
     setForCustomer(update.getCallbackQuery().getMessage());
    }

    @Override
    public boolean apply(Update update) {
        return update.getCallbackQuery().getData().equals(Buttons.CUSTOMER.toString());
    }
    public void setForCustomer(Message message){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(message.getChatId());
        main_menu_sms.setText("Ви можете зробити оголошення прямо в цьому боті. Якщо не знаєте як, то радимо вам скористатися інструкцією.");
        try{
            helpbot.execute(main_menu_sms);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
}

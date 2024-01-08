package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class HasNumberCommand extends HasNotNullMessageCommands{
    public HasNumberCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        String user_sms = message.getText();
        int estimate = Integer.parseInt(user_sms);

        long customerID=0;
        for(int i=0; i<roomsRepository.count(); i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId())){
                customerID = roomsRepository.findById(i+1).get().getCustomerID();
                if(roomsRepository.findById(i+1).get().getFollowing()==1){
                    checkCustomerEstimate(customerID, message,estimate);
                }
                break;
            }
        }
    }

    @Override
    public boolean apply(Update update) {

        Message message = update.getMessage();
        String user_sms = message.getText();
        if(user_sms.matches("^-?\\d+$")){
            for(int i=0; i<roomsRepository.count(); i++){
                if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}

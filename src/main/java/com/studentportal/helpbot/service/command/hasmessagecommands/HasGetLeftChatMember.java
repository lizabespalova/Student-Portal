package com.studentportal.helpbot.service.command.hasmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.PurchaseRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
@Component
public class HasGetLeftChatMember extends HasMessageCommands{
    @Autowired
    private PurchaseRepository purchaseRepository;
    HasGetLeftChatMember(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        String payLoad="";
        for(int i=0; i<roomsRepository.count();i++){
            if(update.getMessage().getChat().getId().equals(roomsRepository.findById(i+1).get().getRoomID())){
                payLoad = roomsRepository.findById(i+1).get().getPayload();
                break;
            }
        }
        if(payLoad==null||!purchaseRepository.findById(payLoad).get().isFlag()){
            try {
                deleteChatMember(update);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if(payLoad!=null&&purchaseRepository.findById(payLoad).get().isFlag()){
            for(int i=0; i<roomsRepository.count();i++){
                if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId())){
                    if(roomsRepository.findById(i+1).get().getFollowing()==1)
                        setWarningRoguery(update);
                    break;
                }
            }

        }
    }

    @Override
    public boolean apply(Update update) {
        return update.getMessage().getLeftChatMember()!=null;
    }
}

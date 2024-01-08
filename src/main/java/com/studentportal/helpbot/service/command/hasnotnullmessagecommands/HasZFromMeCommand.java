package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class HasZFromMeCommand extends HasNotNullMessageCommands{
    public HasZFromMeCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        Message message = update.getMessage();
        if(update.getMessage().getText().equals(Text.deleteThief)){
            setThiefIDtoAdmin(message);
        }else if(customerRepository.findById(782340442L).get().getThiefListState()==1){
            getThiefID(message);
            setThiefSurname(message);
            menuDeleteThief(message);
        }
        else if(customerRepository.findById(782340442L).get().getThiefListState()==2){
            getThiefSurname(message);
            setThiefName(message);
            menuDeleteThief(message);
        }
        else if(customerRepository.findById(782340442L).get().getThiefListState()==3){
            getThiefName(message);
            setThiefNick(message);
            menuDeleteThief(message);
        }
        else if(customerRepository.findById(782340442L).get().getThiefListState()==4){
            long thiefId=getThiefNick(message);
            setThiefToFile(thiefId, message);
        }
        else if(customerRepository.findById(782340442L).get().getThiefListState()==10){
            deleteThiefRow(message);
        }
    }

    @Override
    public boolean apply(Update update) {
        Message message = update.getMessage();
        return message.getFrom().getId()==782340442&&!customerRepository.findById(782340442L).isEmpty();
    }
}

package com.studentportal.helpbot.service.command.callbackquerycommands;


import com.studentportal.helpbot.model.Performer;
import com.studentportal.helpbot.repository.CustomerRepository;
import com.studentportal.helpbot.repository.PerformerRepository;
import com.studentportal.helpbot.repository.RoomsRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SavePerformersRate  extends QueryCommands{
    @Autowired
    PerformerRepository performerRepository;
    SavePerformersRate(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        String rating = callbackData.substring(callbackData.indexOf("_") + 1, callbackData.indexOf(":"));
        long performerId = Long.parseLong(callbackData.split(":")[1]);
        calculate_and_save_rating(performerId, rating);
        delete_rate_menu(update.getCallbackQuery());

    }

    public void delete_rate_menu(CallbackQuery callbackQuery){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setText("Дякую за оцінку");

        try {
            helpbot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void calculate_and_save_rating(long performerID, String rating){
        Performer performer = performerRepository.findById(performerID).get();
        performer.setBargain_amount(performer.getBargain_amount()+1);
        if(performerRepository.findById(performerID).get().getRating().equals(Text.first_performer)){
            performer.setRating(rating);
        }else{
            float averageEstimate = Float.parseFloat(performerRepository.findById(performerID).get().getRating());
            averageEstimate+=Float.parseFloat(rating);
            averageEstimate/=2;
            performer.setRating(String.valueOf(averageEstimate));
        }
        performerRepository.save(performer);
    }
    @Override
    public boolean apply(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        if (callbackData.startsWith("rate_")) {
            return true;
        }
        return false;
    }
}

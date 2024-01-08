package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.Performer;
import com.studentportal.helpbot.model.PerformerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Component
public class PerformerRegistwrHasQueryCommand extends QueryCommands {
    @Autowired
    private PerformerRepository performerRepository;
    public PerformerRegistwrHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository,roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        register_to_data_base_performer(update.getCallbackQuery().getFrom(), String.valueOf(chatId));
    }

    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals(Subjects.PERFORMER_REGISTER.toString());
    }
    public void register_to_data_base_performer(User user, String chatid){
        if(performerRepository.findById(user.getId()).isEmpty()) {
            var chatID = user.getId();
            Performer performer = new Performer();
            performer.setChatID(chatID);
            performer.setName(user.getFirstName());
            performer.setSurname(user.getLastName());
            performer.setUser_nick(user.getUserName());
            performer.setBargain_amount(0);
            performer.setRating(Text.first_performer);
            performerRepository.save(performer);
            /*performers_id = chatID;*/
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatid);
            sendMessage.setText("Вас було зареєйстровано як виконавець та видано рейтинг. Поки що він порожній");
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else{
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatid);
            sendMessage.setText("Ви вже зареєстровані, як виконавець");
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}

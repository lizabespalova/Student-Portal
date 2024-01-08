package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.PerformerRepository;
import com.studentportal.helpbot.model.PostRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CustomerActions;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Component
public class GetAdvertismentHasQueryCommand extends QueryCommands {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PerformerRepository performerRepository;
    public GetAdvertismentHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository,roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        performer_check(update.getCallbackQuery().getFrom(),update.getCallbackQuery().getId(), update.getCallbackQuery().getMessage().getMessageId());
        set_information_about_performer(update.getCallbackQuery().getFrom(), postRepository.findById(update.getCallbackQuery().getMessage().getMessageId()).get().getCustomer_id(), update, update.getCallbackQuery().getMessage().getMessageId());
    }

    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals("Візьму");
    }
    public void performer_check(User user, String chatID, int messageID){
        long customerID = postRepository.findById(messageID).get().getCustomer_id();
        if(performerRepository.findById(user.getId()).isEmpty()) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(chatID);
            answerCallbackQuery.setText("Ви не зареєстровані, як виконавець. Зареєструйтеся спочатку у боті. Посилання на нього в описі");
            answerCallbackQuery.setShowAlert(true);
            try {
                // Send the message
                helpbot.execute(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if(customerID==user.getId()){
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(chatID);
            answerCallbackQuery.setText("Ви не можете виконати своє ж завдання");
            answerCallbackQuery.setShowAlert(true);
            try {
                // Send the message
                helpbot.execute(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else{
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(chatID);
            answerCallbackQuery.setText("Ваша заявка була відправлена користувачу на розгляд");
            answerCallbackQuery.setShowAlert(true);
            try {
                // Send the message
                helpbot.execute(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
    public void set_information_about_performer(User user, Long chatID, Update update, int messageID)  {
        long customerID = postRepository.findById(messageID).get().getCustomer_id();
        if(!performerRepository.findById(user.getId()).isEmpty()&&customerID!=user.getId()){
            CustomerActions customerActions = new CustomerActions(customerRepository);
            String post = customerActions.get_customer_post_link_tostr(update,postRepository);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatID);
            String userName = "";
            String userSurname = "";
            if(performerRepository.findById(user.getId()).get().getName()!=null){
                userName =performerRepository.findById(user.getId()).get().getName();
            }if(performerRepository.findById(user.getId()).get().getSurname()!=null){
                userSurname = performerRepository.findById(user.getId()).get().getSurname();
            }
            String performersInfo = customerActions.getPerformersInformation(userName,userSurname,post,performerRepository.findById(user.getId()).get().getRating(),performerRepository.findById(user.getId()).get().getBargain_amount());
            sendMessage.setText(performersInfo);
            //sendMessage.setText("Користувач " + userName + " " +  userSurname + ", з рейтингом: " + performerRepository.findById(user.getId()).get().getRating() + " та кількістю угод: " + performerRepository.findById(user.getId()).get().getBargain_amount() + ", готовий/а взятися за ваше завдання" + "\n" + post);
            InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

            List<InlineKeyboardButton> row_inline=new ArrayList<>();
            var agree_Button = new InlineKeyboardButton();
            agree_Button.setText(Text.agree_text);
            agree_Button.setCallbackData(update.getCallbackQuery().getMessage().getMessageId()+","+update.getCallbackQuery().getFrom().getId());/*subjects.AGREE.toString()*/
            var cancel_Button = new InlineKeyboardButton();
            cancel_Button.setText(Text.cancel_text);
            cancel_Button.setCallbackData(Subjects.CANCEL.toString());
            row_inline.add(agree_Button);
            row_inline.add(cancel_Button);
            rows_inline.add(row_inline);
            inline_keybord.setKeyboard(rows_inline);
            sendMessage.setReplyMarkup(inline_keybord);
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}

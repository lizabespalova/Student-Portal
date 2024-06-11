package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.repository.CustomerRepository;
import com.studentportal.helpbot.repository.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Component
public class SendPerformerEstimate extends QueryCommands{
    SendPerformerEstimate(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        String callbackData = update.getCallbackQuery().getData();

        long performerId = Long.parseLong(callbackData.substring(callbackData.indexOf("_") + 1, callbackData.indexOf("-")));
        long postId = Long.parseLong(callbackData.split("-")[1]);
        sendEstimationMenu(update.getCallbackQuery().getMessage(), performerId, postId);
    }

    @Override
    public boolean apply(Update update) {
        if(update.hasCallbackQuery()) {
            var messagetext = update.getCallbackQuery().getData();
            return messagetext.startsWith("CUSTOMERSNOTE");
        }
        return false;
    }
    public void sendEstimationMenu(Message message, long performerId, long postId){
        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setMessageId(message.getMessageId());
        sendMessage.setText("Оберіть оцінку:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(i + " " + getStars(i));
            button.setCallbackData("rate_" + i + ":" + performerId +  "-" + postId);
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String getStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("⭐");
        }
        return stars.toString();
    }
}

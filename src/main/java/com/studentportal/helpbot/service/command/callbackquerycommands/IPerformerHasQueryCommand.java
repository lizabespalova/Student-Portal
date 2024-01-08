package com.studentportal.helpbot.service.command.callbackquerycommands;



import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Buttons;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Component
public class IPerformerHasQueryCommand extends QueryCommands{
    IPerformerHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        setMenuChoice(update.getCallbackQuery().getMessage());
    }

    @Override
    public boolean apply(Update update) {
        return update.getCallbackQuery().getData().equals(Buttons.PERFOMER.toString());
    }
        public void setMenuChoice(Message message){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(message.getChatId());
        main_menu_sms.setText("Обери галузь, в якій ви добре знаєтесь. Зареєструйся як виконавець та можешь брати завдання на виконання:");
         String programming_text= EmojiParser.parseToUnicode("Програмування" + ":computer:");
         String matem_text =   EmojiParser.parseToUnicode("Математика" + ":1234:");
         String medicine_text=EmojiParser.parseToUnicode("Медицина" + ":woman_health_worker:");
         String phylosophy_text=EmojiParser.parseToUnicode("Філософія" + ":book:");
         String languages_text= EmojiParser.parseToUnicode("Мови" + ":globe_with_meridians:");
         String chemistry_text= EmojiParser.parseToUnicode("Хімія" + ":alembic: ");
         String geographe_text =EmojiParser.parseToUnicode("Географія" + ":earth_americas:");
         String another_text = "Інше...";
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var programming_Button = new InlineKeyboardButton();
        programming_Button.setText(programming_text);
        programming_Button.setCallbackData("PROGRAMMING");
        programming_Button.setUrl("https://t.me/program_vedmedyk/");
        var math_Button = new InlineKeyboardButton();
        math_Button.setText(matem_text);
        math_Button.setCallbackData("MATH");
        math_Button.setUrl("https://t.me/matem_vedmedyk/");
        row_inline.add(programming_Button);
        row_inline.add(math_Button);


        List<InlineKeyboardButton> row1_inline=new ArrayList<>();
        var medicine_button = new InlineKeyboardButton();
        medicine_button.setText(medicine_text);
        medicine_button.setCallbackData("MEDICINE");
        medicine_button.setUrl("https://t.me/medicine_vedmedyk/");
        var chemistry_button = new InlineKeyboardButton();
        chemistry_button.setText(chemistry_text);
        chemistry_button.setCallbackData("CHEMISTRY");
        chemistry_button.setUrl("https://t.me/chemistry_vedmedyk/");
        row1_inline.add(medicine_button);
        row1_inline.add(chemistry_button);

        List<InlineKeyboardButton> row2_inline=new ArrayList<>();
        var phylosophy_button = new InlineKeyboardButton();
        phylosophy_button.setText(phylosophy_text);
        phylosophy_button.setCallbackData("PHYLOSOPHY");
        phylosophy_button.setUrl("https://t.me/phylosophy_vedmedyk/");
        var language_button = new InlineKeyboardButton();
        language_button.setText(languages_text);
        language_button.setCallbackData("LANGUAGES");
        language_button.setUrl("https://t.me/languages_vedmedyk/");
        row2_inline.add(phylosophy_button);
        row2_inline.add(language_button);


        List<InlineKeyboardButton> row3_inline=new ArrayList<>();
        var geography_button = new InlineKeyboardButton();
        geography_button.setText(geographe_text);
        geography_button.setCallbackData("GEOGRAPHY");
        geography_button.setUrl("https://t.me/geogtaphy_vedmedyk/");
        var another_button = new InlineKeyboardButton();
        another_button.setText(another_text);
        another_button.setCallbackData("ANOTHER");
        another_button.setUrl("https://t.me/main_vedmedyk/");
        row3_inline.add(geography_button);
        row3_inline.add(another_button);

        rows_inline.add(row_inline);
        rows_inline.add(row1_inline);
        rows_inline.add(row2_inline);
        rows_inline.add(row3_inline);
        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);
        try{
            helpbot.execute(main_menu_sms);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
}

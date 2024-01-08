package com.studentportal.helpbot.service.command;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.Rooms;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Quiz;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CustomerActions;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class Commands {
    protected final Helpbot helpbot;
    protected final CustomerRepository customerRepository;
    protected final RoomsRepository roomsRepository;
    public void set_text_description(long chatId, Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Опишіть своє завдання (без фото або файлів)");
        sendMessage.setChatId(chatId);
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setCheckDescriptionState(1);
        customer.setState(Quiz.SUBJECTMENU.toString());
        customerRepository.save(customer);
        try{
            helpbot.execute(sendMessage);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void set_last_buttons(String chatId){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText("Якщо хочете публікувати пост до каналу, натисніть <Публікувати>, якщо щось змінити, натисніть <Змінити>");
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var change_Button = new InlineKeyboardButton();
        change_Button.setText(Text.change_text);
        change_Button.setCallbackData(Subjects.CHANGE.toString());
        var publish_Button = new InlineKeyboardButton();
        publish_Button.setText(Text.public_text);
        publish_Button.setCallbackData(Subjects.PUBLIC.toString());
        row_inline.add(change_Button);
        row_inline.add(publish_Button);

        rows_inline.add(row_inline);

        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);
        try{
            helpbot.execute(main_menu_sms);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void set_post(String chatId, int check, long messageID, Message message){
        CustomerActions customerActions = new CustomerActions(customerRepository);
        String post;
        if(check == 0) {
            post = customerActions.post_tostr(0, message);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setParseMode("HTML");
            sendMessage.setChatId(chatId);
            sendMessage.setText(post);
            Customer customer = customerRepository.findById(message.getChatId()).get();
            customer.setAgreementsState(true);
            customer.setCheck_state(false);
            customer.setPriceFlag(1);
            customerRepository.save(customer);
            try {
                // Send the message
                helpbot.execute(sendMessage);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }else {
            post = customerActions.post_tostr(1, message);
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId((int) messageID);
            editMessageText.setParseMode("HTML");
            editMessageText.setText(post);
            Customer customer = customerRepository.findById(message.getChatId()).get();
            customer.setAgreementsState(true);
            customer.setCheck_state(true);
            customer.setFileLink(null);
            customer.setPhotoLink(null);
            customer.setPriceFlag(1);
            customerRepository.save(customer);
            try {
                // Send the message
                helpbot.execute(editMessageText);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
    public void set_fix_price_menu(String chatId, Message message){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        var text = EmojiParser.parseToUnicode("Напишіть вартість цифрою, без копійок:");
        main_menu_sms.setText(text);
        save(message, Quiz.FIXPRICE.toString());
        try {
            // Send the message
            helpbot.execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
     public void save(Message message, String state){
        if(!customerRepository.findById(message.getChatId()).isEmpty()) {
            Customer customer = customerRepository.findById(message.getChatId()).get();
            customer.setAgreementsState(true);
            customer.setState(state);
            customer.setPriceFlag(0);
            customer.setCheck_state(true);
            customerRepository.save(customer);
        }else{
            Customer customer = new Customer();
            customer.setChatID(message.getChatId());
            customer.setSurname(message.getFrom().getLastName());
            customer.setName(message.getFrom().getFirstName());
            customer.setUser_nick(message.getFrom().getUserName());
            customer.setCheckDescriptionState(0);
            customer.setCheck_state(true);
            customer.setState(state);
            customerRepository.save(customer);
        }
    }
    public boolean check_customers_price(String user_price, Message message) throws IOException {
        for(int i=0; i<roomsRepository.count(); i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId()) && !message.getFrom().getId().equals(roomsRepository.findById(i+1).get().getCustomerID())/*&&customerRepository.findById(message.getFrom().getId()).get().getPriceFlag() != 1*/){
                String post = ("Ми просимо відправити смс саме користувача (людині, яка робила пост в канал)");
                String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
                String chatId = String.valueOf(roomsRepository.findById(i+1).get().getRoomID());
                post = URLEncoder.encode(post, "UTF-8");
                urlString = String.format(urlString, helpbot.getBotToken(), chatId, post);
                URL newurl = new URL(urlString);
                URLConnection conn = newurl.openConnection();
                StringBuilder sb = new StringBuilder();
                InputStream is = new BufferedInputStream(conn.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                fix_finish_text_price_customer(message);
                return false;
            }
        }
        try {
            int intParse =  Integer.parseInt(user_price);
            sms_to_performer(intParse, message);
            for (int i = 0; i < roomsRepository.count(); i++) {
                if (message.getChat().getId().equals(roomsRepository.findById(i + 1).get().getRoomID())) {
                    Rooms rooms = roomsRepository.findById(i + 1).get();
                    rooms.setPrice(intParse);
                    roomsRepository.save(rooms);
                    break;
                }
            }
        } catch (NumberFormatException nfe) {
            for(int i=0; i<roomsRepository.count(); i++){
                if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId())){
                    String post = ("Ви відправили не число");
                    String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
                    String chatId = String.valueOf(roomsRepository.findById(i+1).get().getRoomID());
                    post = URLEncoder.encode(post, "UTF-8");
                    urlString = String.format(urlString, helpbot.getBotToken(), chatId, post);
                    URL newurl = new URL(urlString);
                    URLConnection conn = newurl.openConnection();
                    StringBuilder sb = new StringBuilder();
                    InputStream is = new BufferedInputStream(conn.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String inputLine = "";
                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    Rooms rooms = roomsRepository.findById(i+1).get();
                    rooms.setStateInChat(null);
                    roomsRepository.save(rooms);
                    return false;
                }
            }
        }
        return true;
    }
    public void deleteMember(long roomID, long MemberId) throws IOException {
        String urlString = "https://api.telegram.org/bot%s/unbanChatMember?chat_id=%s&user_id=%s";
        urlString = String.format(urlString, helpbot.getBotToken(), roomID, MemberId);
        URL newurl = new URL(urlString);
        URLConnection conn = newurl.openConnection();
        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
    }
    public void setWarningToCleanRoom(Message message) throws IOException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Єлизавето, прибиральнице, видрій кімнату, сцуко!  @vasssabiiiiii");
        sendMessage.setChatId(message.getChat().getId());
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        for(int i=0; i<roomsRepository.count();i++){
            if (message.getChat().getId().equals(roomsRepository.findById(i + 1).get().getRoomID())) {
                String newChatLink = "";
                long chanels = roomsRepository.findById(i+1).get().getRoomID();
                ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink();
                exportChatInviteLink.setChatId(chanels);
                try {
                    // Send the message
                    newChatLink = helpbot.execute(exportChatInviteLink);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                Rooms rooms = roomsRepository.findById(i + 1).get();
                rooms.setIsFree(false);
                rooms.setDate("Тайм зробити клінінг");
                rooms.setChatLink(newChatLink);
                roomsRepository.save(rooms);
                break;
            }
        }

    }
    public void sendCard(long roomID){
        for(int i=0; i<roomsRepository.count();i++){
            if(roomID==roomsRepository.findById(i+1).get().getRoomID()){
                roomsRepository.findById(i+1).get().setFollowing(2);
                break;
            }
        }
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setText("Виконавець, надішліть, будь ласка, вашу картку");
        sendMessage1.setChatId(roomID);
        try {
            // Send the message
            helpbot.execute(sendMessage1);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void fix_finish_text_price_customer(Message message) throws IOException {
        int roomId = 0;
        for(int i=0; i<roomsRepository.count(); i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId())){
                roomId = i+1;
                Rooms rooms=roomsRepository.findById(i+1).get();
                rooms.setFollowing(2);
                rooms.setStateInChat(0);
                roomsRepository.save(rooms);
                break;
            }
        }
        String post = "Користувач, напишіть ціну цифрою, без копійок";
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        long chatId = roomsRepository.findById(roomId).get().getRoomID();
        post = URLEncoder.encode(post,"UTF-8");
        urlString = String.format(urlString, helpbot.getBotToken(), chatId, post);
        URL newurl = new URL(urlString);
        URLConnection conn = newurl.openConnection();
        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
    }
    public void sms_to_performer(int user_price, Message message) throws IOException {
        if (user_price < 50 || user_price > 10000) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(Text.littleMoney);
            sendMessage.setChatId(message.getChat().getId());
            try {
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else {
            String post = ("Ціну зафіксовано, вона дорівнює: " + user_price + " грн, виконавець, ви згодні з цією ціною?");
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&reply_markup=%s&text=%s";
            String chatId = String.valueOf(message.getChat().getId());
            String reply = URLEncoder.encode("{\"inline_keyboard\":[[{\"text\":\"" + "Так" + "\",\"callback_data\":\"YES\"},{\"text\":\"" + "Ні" + "\",\"callback_data\":\"NO\"}]]}", "UTF-8");
            post = URLEncoder.encode(post, "UTF-8");
            urlString = String.format(urlString, helpbot.getBotToken(), chatId, reply, post);
            URL newurl = new URL(urlString);
            URLConnection conn = newurl.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            for(int i=0; i<roomsRepository.count();i++){
                if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChatId())){
                    Rooms rooms = roomsRepository.findById(i+1).get();
                    rooms.setStateInChat(1);
                    roomsRepository.save(rooms);
                    break;
                }
            }
        }
    }
    public void set_subject_menu(String chatId, Message message){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText("Обери галузь:");
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var programming_Button = new InlineKeyboardButton();
        programming_Button.setText(Text.programming_text);
        programming_Button.setCallbackData(Subjects.PROGRAMMING.toString());
        var math_Button = new InlineKeyboardButton();
        math_Button.setText(Text.matem_text);
        math_Button.setCallbackData(Subjects.MATH.toString());
        row_inline.add(programming_Button);
        row_inline.add(math_Button);


        List<InlineKeyboardButton> row1_inline=new ArrayList<>();
        var medicine_button = new InlineKeyboardButton();
        medicine_button.setText(Text.medicine_text);
        medicine_button.setCallbackData(Subjects.MEDICINE.toString());
        var chemistry_button = new InlineKeyboardButton();
        chemistry_button.setText(Text.chemistry_text);
        chemistry_button.setCallbackData(Subjects.CHEMISTRY.toString());
        row1_inline.add(medicine_button);
        row1_inline.add(chemistry_button);

        List<InlineKeyboardButton> row2_inline=new ArrayList<>();
        var phylosophy_button = new InlineKeyboardButton();
        phylosophy_button.setText(Text.phylosophy_text);
        phylosophy_button.setCallbackData(Subjects.PHYLOSOPHY.toString());
        var language_button = new InlineKeyboardButton();
        language_button.setText(Text.languages_text);
        language_button.setCallbackData(Subjects.LANGUAGES.toString());
        row2_inline.add(phylosophy_button);
        row2_inline.add(language_button);


        List<InlineKeyboardButton> row3_inline=new ArrayList<>();
        var geography_button = new InlineKeyboardButton();
        geography_button.setText(Text.geographe_text);
        geography_button.setCallbackData(Subjects.GEOGRAPHY.toString());
        var another_button = new InlineKeyboardButton();
        another_button.setText(Text.another_text);
        another_button.setCallbackData(Subjects.ANOTHER.toString());
        row3_inline.add(geography_button);
        row3_inline.add(another_button);

        rows_inline.add(row_inline);
        rows_inline.add(row1_inline);
        rows_inline.add(row2_inline);
        rows_inline.add(row3_inline);
        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setState(Quiz.MAINMENU.toString());
        customer.setPriceFlag(0);
        customerRepository.save(customer);
        try{
            helpbot.execute(main_menu_sms);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void end_stop_menu(String chatId){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText(EmojiParser.parseToUnicode(":+1:"));
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> menu = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(EmojiParser.parseToUnicode(":back:"+"Назад"));
        row1.add(EmojiParser.parseToUnicode("Відмінити"+":x:"));
        menu.add(row1);
        keyboard.setKeyboard(menu);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setSelective(true);
        main_menu_sms.setReplyMarkup(keyboard);
        try {
            // Send the message
            helpbot.execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void menuDeleteThief(Message message){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setText("Або - ,якщо нема, але не випадком з id)");
        main_menu_sms.setChatId(message.getChatId());
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> menu = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Text.deleteThief);
        menu.add(row1);
        keyboard.setKeyboard(menu);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setSelective(true);
        main_menu_sms.setReplyMarkup(keyboard);
        try {
            // Send the message
            helpbot.execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void set_file_description(long chatId, Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Якщо є фото або файл, то відправте");
        sendMessage.setChatId(chatId);
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setState(Quiz.TEXTDESCRIPTION.toString());
        customerRepository.save(customer);
        try{
            helpbot.execute(sendMessage);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }

    }
    public void set_ready_button(String chatId){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText("Коли відправите всі файли/фото, то натисніть <Готово>");
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> menu = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Text.ready_text);
        menu.add(row1);
        keyboard.setKeyboard(menu);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setSelective(true);
        main_menu_sms.setReplyMarkup(keyboard);
        try {
            // Send the message
            helpbot.execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void set_price_description(String chatId, Message message){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText("Укажіть як ви хочете зробити угоду - з фіксованою ціною або домовитися з виконавцем:");
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var fix_Button = new InlineKeyboardButton();
        fix_Button.setText(Text.fix_price_text);
        fix_Button.setCallbackData(Quiz.FIXPRICE.toString());
        var agreement_price_Button = new InlineKeyboardButton();
        agreement_price_Button.setText(Text.agreement_price_text);
        agreement_price_Button.setCallbackData(Quiz.AGREEMENTPRICE.toString());
        row_inline.add(fix_Button);
        row_inline.add(agreement_price_Button);
        rows_inline.add(row_inline);
        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);
        /*quiz=quiz.FILEDESCRIPTION;*/
        save(message, Quiz.FILEDESCRIPTION.toString());
        try{
            helpbot.execute(main_menu_sms);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void generateNewLink(Update update){
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        long customerID=0;
        long performerID=0;
        for(int i=0; i<roomsRepository.count();i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(update.getCallbackQuery().getMessage().getChat().getId())){
                customerID = roomsRepository.findById(i+1).get().getCustomerID();
                performerID = roomsRepository.findById(i+1).get().getPerformerID();
            }
        }

        try {
            //Генерувати нове посилання на кімнату
            deleteMember(update.getCallbackQuery().getMessage().getChat().getId(), customerID);
            deleteMember(update.getCallbackQuery().getMessage().getChat().getId(), performerID);
            for(int i=0; i<roomsRepository.count();i++){
                String inviteLink = "";
                if(roomsRepository.findById(i+1).get().getRoomID().equals(update.getCallbackQuery().getMessage().getChat().getId())){
                    String newChatLink = "";
                    long chanels = roomsRepository.findById(i+1).get().getRoomID();
                    ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink();
                    exportChatInviteLink.setChatId(chanels);
                    try {
                        // Send the message
                        newChatLink = helpbot.execute(exportChatInviteLink);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    Rooms rooms = roomsRepository.findById(i+1).get();
                    rooms.setChatLink(newChatLink);
                    rooms.setIsFree(true);
                    rooms.setStateInChat(0);
                    rooms.setFollowing(0);
                    roomsRepository.save(rooms);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

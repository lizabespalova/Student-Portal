package com.studentportal.helpbot.service.command.hasnotnullmessagecommands;

import com.studentportal.helpbot.model.*;
import com.studentportal.helpbot.service.command.Commands;
import com.studentportal.helpbot.service.consts.Quiz;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class HasNotNullMessageCommands extends Commands implements BotHasNotNullMessageCommand {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ThiefRepository thiefRepository;
    @Autowired
    private PerformerRepository performerRepository;
    public HasNotNullMessageCommands(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }
    public void setThiefList(Update update){
        if(update.getMessage().getChatId()==782340442){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(Text.thiefId);
            Customer customer = customerRepository.findById(update.getMessage().getChatId()).get();
            customer.setThiefListState(1);
            customerRepository.save(customer);
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            menuDeleteThief(update.getMessage());
        }
        else{
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChat().getId());
            sendMessage.setText(Text.thiefMenu);
            InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

            var thiefListButton = new InlineKeyboardButton();
            thiefListButton.setText(Text.thiefList);
            thiefListButton.setCallbackData("THIEFLIST");
            List<InlineKeyboardButton> row_inline=new ArrayList<>();
            var thiefAddButton = new InlineKeyboardButton();
            thiefAddButton.setText(Text.thiefAdd);
            thiefAddButton.setCallbackData("THIEFADD");
            var checkToThiefButton = new InlineKeyboardButton();
            checkToThiefButton.setText(Text.checkThief);
            checkToThiefButton.setCallbackData("THIEFCHECK");

            row_inline.add(thiefListButton);
            row_inline.add(thiefAddButton);
            row_inline.add(checkToThiefButton);
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
    public void set_file_to_channel_and_return_link(Message message)throws IOException {
        List file_url_list = new ArrayList();
        for (int j=0; j<customerRepository.findById(message.getChatId()).get().getFileLink().size();j++) {
            URL newurl = new URL((String) customerRepository.findById(message.getChatId()).get().getFileLink().get(j));
            URLConnection conn = newurl.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            String digits = "";
            int check = 0;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
                for (int i = 0; i < inputLine.length(); i++) {
                    char chrs = inputLine.charAt(i);
                    if (Character.isDigit(chrs)) {
                        digits = digits + chrs;

                    }
                    if (chrs == ',') {
                        check++;
                    }
                    if (check == 2) {
                        break;
                    }
                }
                if (check == 2) {
                    break;
                }
            }

            String url = "https://t.me/vedmedik_base/" + digits;
            file_url_list.add(url);
        }
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setFileLink(file_url_list);
//        customer.setState(Quiz.PRICE.toString());
//        customerRepository.saveAll(file_url_list);
        customerRepository.save(customer);
    }
    public void set_photo_to_channel_and_return_link(Message message)throws IOException{
        List photo_url_list = new ArrayList();
        for (int j=0; j<customerRepository.findById(message.getChatId()).get().getPhotoLink().size();j++) {
            URL newurl = new URL((String) customerRepository.findById(message.getChatId()).get().getPhotoLink().get(j));
            URLConnection conn = newurl.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            String digits = "";
            int check = 0;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
                for (int i = 0; i < inputLine.length(); i++) {
                    char chrs = inputLine.charAt(i);
                    if (Character.isDigit(chrs)) {
                        digits = digits + chrs;

                    }
                    if (chrs == ',') {
                        check++;
                    }
                    if (check == 2) {
                        break;
                    }
                }
                if (check == 2) {
                    break;
                }
            }
            String url = "https://t.me/vedmedik_base/"+ digits;
            photo_url_list.add(url);

        }
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setPhotoLink(photo_url_list);
        customerRepository.save(customer);
    }
    public void set_button_register_performer(String ChatId){
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(ChatId);
        main_menu_sms.setText("Щоб зареєструватися, натисніть на кнопку \"Хочу зареєструватися\":");
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var registrate_Button = new InlineKeyboardButton();
        registrate_Button.setText(Text.want_registrate);
        registrate_Button.setCallbackData(Subjects.PERFORMER_REGISTER.toString());
        row_inline.add(registrate_Button);
        rows_inline.add(row_inline);
        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);
        try{
            helpbot.execute(main_menu_sms);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void cleanRoom(Update update, boolean deletePost) throws IOException {
        long postID = 0;
        long chanels = 0;
        Long id = update.getMessage().getChat().getId();
        for(int i = 0; i<roomsRepository.count(); i++){
            if (id.equals(roomsRepository.findById(i + 1).get().getRoomID())) {
                postID = roomsRepository.findById(i+1).get().getPostId();
                chanels = roomsRepository.findById(i+1).get().getRoomID();
                break;
            }
        }
//        chanels = postRepository.findById((int) postID).get().getChanel();
        int messageID = update.getMessage().getMessageId();

        String urlString = "https://api.telegram.org/bot%s/deleteMessage?chat_id=%s&message_id=%s";
        urlString = String.format(urlString, helpbot.getBotToken(), chanels, messageID);
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
            if (id.equals(roomsRepository.findById(i + 1).get().getRoomID())) {
                Rooms rooms =roomsRepository.findById(i+1).get();
                rooms.setIsFree(true);
                rooms.setPayload(null);
                rooms.setCustomerID(null);
                rooms.setDate(null);
                rooms.setPerformerID(null);
                rooms.setPostId(null);
                rooms.setPrice(null);
                roomsRepository.save(rooms);
                break;
            }
        }
        if(deletePost) {
            if (!postRepository.findById((int) postID).isEmpty() && !postRepository.findById((int) postID).get().isActive()) {
                Post post = postRepository.findById((int) postID).orElseThrow();
                postRepository.delete(post);
            }
        }
    }
    public void get_text_description(Message message){

        String user_text_description = message.getText();
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setAgreementsState(true);
        customer.setDescription(user_text_description);
        customer.setCheckDescriptionState(0);
        customerRepository.save(customer);
    }
    public void checkCard(Message message){
        String payLoad="";
        for(int i=0;i<roomsRepository.count();i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId())){
                payLoad = roomsRepository.findById(i+1).get().getPayload();
                break;
            }
        }
        long card = purchaseRepository.findById(payLoad).get().getPerformerCard();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChat().getId());
        sendMessage.setText(Text.areYouSure+"\nКартка, яку ми зафіксували: "+card);
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Так");
        yesButton.setCallbackData("YESF");
        var noButton = new InlineKeyboardButton();
        noButton.setText("Ні");
        noButton.setCallbackData("NOF");
        row_inline.add(yesButton);
        row_inline.add(noButton);
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
    public boolean debug_fix_price(String user_price, long chatId, Message message){
        try {
            int check_num = Integer.parseInt(user_price);
            if(check_num<10||check_num>10000){
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(Text.littleMoney);
                sendMessage.setChatId(chatId);
                try {
                    // Send the message
                    helpbot.execute(sendMessage);
                    return false;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Ціну зафіксовано, вона дорівнює: " + check_num);
                sendMessage.setChatId(chatId);
                /* customerData.setPrice(check_num);*/
                Customer customer = customerRepository.findById(message.getChatId()).get();
                customer.setAgreementsState(true);
                customer.setPrice(String.valueOf(check_num));
                customer.setCheck_state(true);
                customer.setState(String.valueOf(Quiz.FIXPRICE));
                customerRepository.save(customer);
                try {
                    // Send the message
                    helpbot.execute(sendMessage);
                    return true;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }catch(NumberFormatException nfe){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Ви відправили не число");
            sendMessage.setChatId(chatId);
            try {
                // Send the message
                helpbot.execute(sendMessage);
                return false;

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public void setThiefIDtoAdmin(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Text.deleteThiefText);
        sendMessage.setChatId(message.getChatId());
        Customer customer = customerRepository.findById(message.getFrom().getId()).get();
        customer.setThiefListState(10);
        customerRepository.save(customer);
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void deleteThiefRow(Message message){
        try {
            Thief thief = thiefRepository.findById(Long.valueOf(message.getText())).orElseThrow();
            thiefRepository.delete(thief);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Шахрая видалено");
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }catch(Exception exception){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Шахрая не було видалено");
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        removeLineFromFile(message);
    }
    public void getThiefID (Message message){
        if(!thiefRepository.findById(Long.valueOf(message.getText())).isEmpty()){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Цього шахрая вже зареєстровано");
            sendMessage.setChatId(message.getChat().getId());
            Customer customer = customerRepository.findById(message.getFrom().getId()).get();
            customer.setThiefListState(0);
            customerRepository.save(customer);
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }else {
            Thief thief = new Thief();
            thief.setThiefID(Long.valueOf(message.getText()));
            thiefRepository.save(thief);
            try {
                String projectPath = System.getProperty("user.dir");
                String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ForThief";
                String absolutePath = projectPath + File.separator + relativePath;
                FileWriter fileWriter = new FileWriter(absolutePath, StandardCharsets.UTF_8, false);
                fileWriter.write(message.getText());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Помилка при записуванні ID");
                sendMessage.setChatId(message.getChat().getId());
                try {
                    // Send the message
                    helpbot.execute(sendMessage);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
                throw new RuntimeException(e);
            }
        }
    }
    public void setThiefSurname(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Text.addThiefSurname);
        sendMessage.setChatId(message.getChat().getId());
        Customer customer = customerRepository.findById(message.getFrom().getId()).get();
        customer.setThiefListState(2);
        customerRepository.save(customer);
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void getThiefSurname(Message message){
        String thiefID="";
        try {
            String projectPath = System.getProperty("user.dir");
            String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ForThief";
            String absolutePath = projectPath + File.separator + relativePath;
            FileReader fileReader = new FileReader(absolutePath, StandardCharsets.UTF_8);
            int count=0;
            while((count=fileReader.read())!=-1){
                thiefID += (char)count;
            }
            fileReader.close();
        } catch (IOException e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Помилка при зчитуванні ID");
            sendMessage.setChatId(message.getChat().getId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e);
        }
        Thief thief = thiefRepository.findById(Long.valueOf(thiefID)).get();
        thief.setSurname(message.getText());
        thiefRepository.save(thief);
    }
    public void setThiefName(Message message){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Text.addThiefName);
        sendMessage.setChatId(message.getChat().getId());
        Customer customer = customerRepository.findById(message.getFrom().getId()).get();
        customer.setThiefListState(3);
        customerRepository.save(customer);
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void getThiefName(Message message){
        String thiefID="";
        try {
            String projectPath = System.getProperty("user.dir");
            String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ForThief";
            String absolutePath = projectPath + File.separator + relativePath;
            FileReader fileReader = new FileReader(absolutePath, StandardCharsets.UTF_8);
            int count=0;
            while((count=fileReader.read())!=-1){
                thiefID += (char)count;
            }
            fileReader.close();
        } catch (IOException e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Помилка при зчитуванні ID");
            sendMessage.setChatId(message.getChat().getId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e);
        }
        Thief thief = thiefRepository.findById(Long.valueOf(thiefID)).get();
        thief.setName(message.getText());
        thiefRepository.save(thief);
    }
    public void setThiefNick(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Text.addThiefNick);
        sendMessage.setChatId(message.getChat().getId());
        Customer customer = customerRepository.findById(message.getFrom().getId()).get();
        customer.setThiefListState(4);
        customerRepository.save(customer);
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public long getThiefNick(Message message){
        String thiefID="";
        try {
            String projectPath = System.getProperty("user.dir");
            String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ForThief";
            String absolutePath = projectPath + File.separator + relativePath;
            FileReader fileReader = new FileReader(absolutePath, StandardCharsets.UTF_8);
            int count=0;
            while((count=fileReader.read())!=-1){
                thiefID += (char)count;
            }
            fileReader.close();
        } catch (IOException e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Помилка при зчитуванні ID");
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e);
        }
        Thief thief = thiefRepository.findById(Long.valueOf(thiefID)).get();
        thief.setNick(message.getText()+",");
        thiefRepository.save(thief);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Text.readyThief);
        sendMessage.setChatId(message.getChatId());
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        Customer customer = customerRepository.findById(message.getFrom().getId()).get();
        customer.setThiefListState(0);
        customerRepository.save(customer);
        return Long.parseLong(thiefID);
    }
    public void setThiefToFile (long thiefId, Message message){
        Thief thief = thiefRepository.findById(thiefId).get();
        String resultString = thief.toString()+"\n";
        try {
            String projectPath = System.getProperty("user.dir");
            String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ThiefDataTable";
            String absolutePath = projectPath + File.separator + relativePath;
            FileWriter fileWriter = new FileWriter(absolutePath, StandardCharsets.UTF_8, true);
            fileWriter.write(resultString);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Помилка при записуванні шахрая до файла");
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e);
        }
    }
    public void removeLineFromFile(Message message){
        String fileName = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ThiefDataTable";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8,false))) {
            List<Thief> entities = (List<Thief>) thiefRepository.findAll();
            for (Thief entity : entities) {
                // Получите данные из объекта и запишите их в файл
                String data = entity.toString();
                writer.write(data);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Customer customer = customerRepository.findById(782340442L).get();
        customer.setThiefListState(0);
        customerRepository.save(customer);
//        String fileName = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ThiefDataTable";
//
//        // строка, которую нужно удалить
//        String thiefId = message.getText();
//        String lineToRemove="";
//        try {
//            lineToRemove = thiefRepository.findById(Long.valueOf(thiefId)).get().toString();
//        }catch(Exception ex){
//            SendMessage sendMessage= new SendMessage();
//            sendMessage.setText("Не знайдено такого ID");
//            sendMessage.setChatId(message.getChatId());
//            try {
//                // Send the message
//                helpbot.execute(sendMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//            ex.printStackTrace();
//        }
//        try {
//            // создаем временный файл
//            File tempFile = new File("Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/temp.txt");
//            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
//            // читаем исходный файл
//            BufferedReader reader = new BufferedReader(new FileReader(fileName));
//            String currentLine;
//            while ((currentLine = reader.readLine()) != null) {
//                // если текущая строка не равна удаляемой
//                // записываем ее во временный файл
//                if (!currentLine.equals(lineToRemove)) {
//                    writer.write(currentLine + System.getProperty("line.separator"));
//                }
//            }
//            // закрываем ридер и писатель
//            reader.close();
//            writer.close();
//            // удаляем исходный файл
//            File oldFile = new File(fileName);
//            oldFile.delete();
//            // переименовываем временный файл в исходное имя файла
//            tempFile.renameTo(oldFile);
//            SendMessage sendMessage= new SendMessage();
//            sendMessage.setText("З файлу теж все видалилось");
//            sendMessage.setChatId(message.getChatId());
//            try {
//                // Send the message
//                helpbot.execute(sendMessage);
//            } catch (TelegramApiException ex) {
//                ex.printStackTrace();
//            }
//        } catch (IOException ex) {
//            SendMessage sendMessage= new SendMessage();
//            sendMessage.setText("Помилка з видаленням з файлу!!!!");
//            sendMessage.setChatId(message.getChatId());
//            try {
//                // Send the message
//                helpbot.execute(sendMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//        Customer customer = customerRepository.findById(782340442L).get();
//        customer.setThiefListState(0);
//        customerRepository.save(customer);
    }
    public void checkCustomerEstimate(long customerID, Message message, int estimate) {
        if (customerID != message.getFrom().getId()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Ми просимо відповісти самe користувача");
            sendMessage.setChatId(message.getChat().getId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (estimate > 5 || estimate < 0) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Ви переоцінили або недооцінили виконавця");
            sendMessage.setChatId(message.getChat().getId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(Text.byeTxt);
            sendMessage.setChatId(customerID);
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                deleteMember(message.getChat().getId(),customerID);
            } catch (IOException e) {throw new RuntimeException(e);}
            sendCard(message.getChat().getId());
            for(int i=0; i<roomsRepository.count();i++){
                if(roomsRepository.findById(i+1).get().getRoomID().equals(message.getChat().getId())){
                    long performerID = roomsRepository.findById(i+1).get().getPerformerID();
                    Rooms rooms = roomsRepository.findById(i+1).get();
                    rooms.setFollowing(2);
                    roomsRepository.save(rooms);
                    Performer performer = performerRepository.findById(performerID).get();
                    performer.setBargain_amount(performer.getBargain_amount()+1);
                    if(performerRepository.findById(performerID).get().getRating().equals(Text.first_performer)){
                        performer.setRating(message.getText());
                    }else{
                        float averageEstimate = Float.parseFloat(performerRepository.findById(performerID).get().getRating());
                        averageEstimate+=Float.parseFloat(message.getText());
                        averageEstimate/=2;
                        performer.setRating(String.valueOf(averageEstimate));
                    }
                    performerRepository.save(performer);
                    break;
                }
            }
        }
    }
    public void checkThiefFromCustomer(Message message){
        if(!thiefRepository.findById(message.getForwardFrom().getId()).isEmpty()){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(Text.thiefExists);
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else{
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(Text.thiefDoesntExists);
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}

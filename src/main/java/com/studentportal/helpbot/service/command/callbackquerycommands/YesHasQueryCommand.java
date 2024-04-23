package com.studentportal.helpbot.service.command.callbackquerycommands;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import com.studentportal.helpbot.model.*;
import com.studentportal.helpbot.repository.CustomerRepository;
import com.studentportal.helpbot.repository.PurchaseRepository;
import com.studentportal.helpbot.repository.RoomsRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CustomerActions;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Component
public class YesHasQueryCommand extends QueryCommands {
    @Autowired
    private PurchaseRepository purchaseRepository;
    public YesHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {

        String Payload = "";
        long performerID=0;
        for(int i=0; i<roomsRepository.count();i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(update.getCallbackQuery().getMessage().getChat().getId())){
                Payload = roomsRepository.findById(i+1).get().getPayload();
                performerID = roomsRepository.findById(i+1).get().getPerformerID();
                break;
            }
        }
        if(performerID!=update.getCallbackQuery().getFrom().getId()){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
            sendMessage.setText("Ми просимо відповісти саме виконавця");
            try{
                helpbot.execute(sendMessage);
            }catch(TelegramApiException e){e.printStackTrace();}
        }else if(Payload==null||purchaseRepository.findById(Payload).get().getPriceToPerformer()==0) {
            try {
                sendPayment(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            blockActionsWhilePayed(update);
        }
    }
    public void sendPayment(Update update) throws TelegramApiException {
        CustomerActions customerActions = new CustomerActions(customerRepository);
        int price = 0;
        int roomId=0;
        String payLoad="";
        for(int i=0; i<roomsRepository.count();i++) {
            if (roomsRepository.findById(i+1).get().getRoomID().equals(update.getCallbackQuery().getMessage().getChatId())){
                roomId = i+1;
                if(roomsRepository.findById(i+1).get().getPayload()==null){
                    char[] sAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
                    int sLength = sAlphabet.length;
                    Random sRandom = new Random();
                    boolean flag = true;
                    int k=0;

                    while(flag){
                        payLoad+=sAlphabet[sRandom.nextInt(sLength)];
                        if(payLoad.length()==15&&!purchaseRepository.findById(payLoad).isEmpty()){
                            k=0;
                            flag = true;
                            payLoad = "";
                        } else if (payLoad.length()==15&&purchaseRepository.findById(payLoad).isEmpty()) {
                            break;
                        }
                        k++;
                    }
                    long customerId = 0;
                    for(int j=0; j<roomsRepository.count();j++) {
                        if (roomsRepository.findById(j + 1).get().getRoomID().equals(update.getCallbackQuery().getMessage().getChatId())) {
                            customerId = roomsRepository.findById(j + 1).get().getCustomerID();
                        }
                    }
                    Purchase purchase = new Purchase();
                    purchase.setPayloadID(payLoad);
                    purchase.setRoomID(update.getCallbackQuery().getMessage().getChatId());
                    purchase.setCustomerID(customerId);
                    purchase.setPerformerID(update.getCallbackQuery().getFrom().getId());
                    purchase.setSuccessfulBargain(false);
                    purchase.setPriceToPerformer(0);
                    purchase.setFlag(false);
                    purchaseRepository.save(purchase);
                }
                else{payLoad = roomsRepository.findById(i+1).get().getPayload();break;}
            }
        }
        for(int j=0; j<roomsRepository.count();j++){
            Rooms rooms1 = roomsRepository.findById(j + 1).get();
            if(rooms1.getRoomID().equals(update.getCallbackQuery().getMessage().getChatId())){
                price = rooms1.getPrice();
                Rooms rooms = rooms1;
                rooms.setPayload(payLoad);
                roomsRepository.save(rooms);
                break;
            }
        }
        int finishPrice = customerActions.finish_price_for_customer(price);
//        Rooms room = roomsRepository.findById(roomId).get();
//        CreateInvoiceLink createInvoiceLink = new CreateInvoiceLink(Text.title, Text.formDescription, room.getPayload(), helpbot.getBotTokenPay(), "UAH",
//         List.of(new LabeledPrice("Вартість", finishPrice * 100)));
        String invoiceLink = "";/*helpbot.execute(createInvoiceLink);*/
        String public_key = helpbot.getBotTokenPay();
        String secret_key = helpbot.getBotSecretTokenPay();
        String url = "https://merchant.betatransfer.io/api/payment?token=" + public_key;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            // Set headers
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Construct data
            Map<String, String> data = new HashMap<>();
            data.put("amount", String.valueOf(finishPrice));
            data.put("currency", "UAH");
            data.put("orderId", payLoad);
            String sign = md5(dataToString(data) + secret_key);
            data.put("sign", sign);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(dataToString(data), headers);

            // Send request
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            invoiceLink = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //change

        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(update.getCallbackQuery().getMessage().getChat().getId());
        main_menu_sms.setText(Text.invocieDescription+finishPrice);
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var payButton = new InlineKeyboardButton();
        payButton.setText(Text.pay);

        payButton.setUrl(/*"https://prt.mn/a-sAWPZats"*/  invoiceLink);
        row_inline.add(payButton);
        rows_inline.add(row_inline);
        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);

        try{
            Message message = helpbot.execute(main_menu_sms);
            Purchase purchase = purchaseRepository.findById(payLoad).get();
            purchase.setMessageID(message.getMessageId());
            purchase.setPriceToPerformer(0);
            purchase.setSuccessfulBargain(false);
            purchaseRepository.save(purchase);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public static String dataToString(Map<String, String> data) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result.append(entry.getKey()).append(entry.getValue());
        }
        return result.toString();
    }

    // Calculate MD5 hash
    public static String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean apply(Update update) {
        if(update.hasCallbackQuery()) {
            var messagetext = update.getCallbackQuery().getData();
            return messagetext.equals("YES");
        }
        return false;
    }
}

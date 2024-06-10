//package com.studentportal.helpbot.controller;
//
//import com.studentportal.helpbot.model.Purchase;
//import com.studentportal.helpbot.model.Rooms;
//import com.studentportal.helpbot.repository.CustomerRepository;
//import com.studentportal.helpbot.repository.PurchaseRepository;
//import com.studentportal.helpbot.repository.RoomsRepository;
//import com.studentportal.helpbot.service.command.hasmessagecommands.HasMessageCommands;
//import com.studentportal.helpbot.service.consts.Text;
//import com.studentportal.helpbot.service.dopclasses.CustomerActions;
//import com.studentportal.helpbot.service.mainclasses.Helpbot;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//@RestController
//public class PaymentController {
//
//    @Autowired
//    private Helpbot helpbot;
//    @Autowired
//    private PurchaseRepository purchaseRepository;
//    @Autowired
//    private RoomsRepository roomsRepository;
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @PostMapping("/payment/callback")
//    public String handlePaymentCallback(@RequestParam String sign,
//                                        @RequestParam double amount,
//                                        @RequestParam String orderId,
//                                        @RequestParam String status) {
//        final String SECRET_KEY = helpbot.getBotSecretTokenPay();
//        String calculatedSign = md5(amount + orderId + SECRET_KEY);
//
//        if (calculatedSign.equals(sign)) {
//            // Подпись верна, обработка успешной оплаты
//            if ("success".equalsIgnoreCase(status)) {
//                servePayment(orderId);
//                blockPayment(orderId);
//                return "OK"; // Возвращаем "OK" при успешной обработке
//            } else if ("failure".equalsIgnoreCase(status)) {
//                return "Payment failed processed";
//            } else {
//                return "Invalid payment status";
//            }
//        } else {
//            // Неверная подпись, возможно атака, верните ошибку
//            return "Error: Invalid signature";
//        }
//    }
//
//    // Обработка успешного платежа
//    public void servePayment(String payload) {
//        long roomID = 0;
//        int price = 0;
//        if (purchaseRepository.findById(payload).isPresent()) {
//            roomID = purchaseRepository.findById(payload).get().getRoomID();
//        }
//        for (int i = 0; i < roomsRepository.count(); i++) {
//            if (roomsRepository.findById(i + 1).get().getRoomID() == roomID) {
//                price = roomsRepository.findById(i + 1).get().getPrice();
//                break;
//            }
//        }
//        CustomerActions customerActions = new CustomerActions(customerRepository);
//        int finishPrice = customerActions.finish_price_for_performer(price);
//        Purchase purchase = purchaseRepository.findById(payload).get();
//        purchase.setFlag(true);
//        purchase.setPriceToPerformer(finishPrice);
//        purchaseRepository.save(purchase);
//        for (int i = 0; i < roomsRepository.count(); i++) {
//            if (roomsRepository.findById(i + 1).get().getPayload() != null) {
//                if (roomsRepository.findById(i + 1).get().getPayload().equals(payload)) {
//                    Rooms rooms = roomsRepository.findById(i + 1).get();
//                    rooms.setStateInChat(1);
//                    rooms.setFollowing(1);
//                    roomsRepository.save(rooms);
//                    break;
//                }
//            }
//        }
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(roomID);
//        sendMessage.setText(Text.msgPayYes);
//        try {
//            helpbot.execute(sendMessage);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Обработка неуспешного платежа
//    public void blockPayment(@NotNull String payload) {
//        int messageId = 0;
//        long chatId = 0;
//        if (purchaseRepository.findById(payload).isPresent()) {
//            messageId = purchaseRepository.findById(payload).get().getMessageID();
//            chatId = purchaseRepository.findById(payload).get().getRoomID();
//        }
//        EditMessageText editMessageText = new EditMessageText();
//        editMessageText.setChatId(chatId);
//        editMessageText.setMessageId(messageId);
//        editMessageText.setText(Text.isPaid);
//
//        try {
//            helpbot.execute(editMessageText);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Вычисление хэша MD5
//    public static String md5(String input) {
//        try {
//            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
//            byte[] array = md.digest(input.getBytes());
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < array.length; ++i) {
//                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
//            }
//            return sb.toString();
//        } catch (java.security.NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}

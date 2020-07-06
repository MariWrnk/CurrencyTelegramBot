import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    public static void main(String[] args) {
        logger.info("Bot initialization");
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try{
            telegramBotsApi.registerBot(new Bot());
        }catch (TelegramApiException e){
            logger.error("TelegramApiException was catched.", e);
        }
    }

    public void sendMsg(Message message, String text){
        logger.debug("sendMsg method is starting.");
        logger.info("Create SendMessage instance.");
        SendMessage sendMessage = new SendMessage();
        logger.info("Set message parameters.");
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try{
            logger.info("Sending message to user");
            execute(sendMessage);
        }catch (TelegramApiException e){
            logger.error("TelegramApiException was catched.", e);
        }
    }

    public void onUpdateReceived(Update update) {
        logger.debug("onUpdateRecievd method is starting.");
        logger.info("Message update.");
        Message message = update.getMessage();
        logger.info("Checking if message is empty or does not contain text.");
        if(message == null || !message.hasText()){
            logger.debug("Checking if the chat is private.");
            if(message.getChat().isUserChat()){
                logger.info("Sending answer to user. Bot does not understand the message.");
                sendMsg(message, "Слишком сложно. Может, лучше спросите что-то о курсах валют?");
            }
        }
        if(message != null && message.hasText()){
            logger.debug("Taking text from message.");
            String messageText = message.getText();
            logger.debug("Processing message text.");
            switch (messageText){
                case "/start":
                    logger.info("Sending answer to /start request.");
                    sendMsg(message, getHelp());
                    break;
                case "/help":
                    logger.info("Sending answer to /help request.");
                    sendMsg(message, getHelp());
                    break;
                default:
                    logger.info("Creating  MessageParser");
                    MessageParser mp = new MessageParser(messageText);
                    logger.info("Getting string answer from parser.");
                    String answer = mp.parser();
                    logger.debug("Checking if answer can be send to a group.");
                    if(mp.isPrivateOnly()){
                        logger.debug("Checking if the chat is private.");
                        if(message.getChat().isUserChat()){
                            logger.info("Sending answer.");
                            sendMsg(message, answer);
                        }
                    }else{
                        logger.info("Sending answer.");
                        sendMsg(message, answer);
                    }
            }
        }

    }

    public String getBotUsername() {
        logger.debug("Getting bot username.");
        return "CurrencyExchangeTrackerBot";
    }

    public String getBotToken() {
        logger.debug("Getting bot token.");
        return "1370035484:AAEHWrwuGU4Bu8bixRbHm6crfw_opLLDSJQ";
    }

    public String getHelp(){
        logger.debug("Getting bot string for help.");
        return "Введите /help, чтобы получить справку.\nВведите валюту и город, чтобы узнать курс в регионе.\nВведите цб и дату в формате чч.мм.гггг через пробел, чтобы узнать курс Центро Банка на эту дату.";

    }
}

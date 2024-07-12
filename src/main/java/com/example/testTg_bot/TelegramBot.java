package com.example.testTg_bot;

import com.example.testTg_bot.config.BotConfig;
import com.example.testTg_bot.moodle.Entity.AdsEntity;
import com.example.testTg_bot.moodle.Entity.UserEntity;
import com.example.testTg_bot.moodle.Repository.AdsRepository;
import com.example.testTg_bot.moodle.Repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdsRepository adsRepository;
    final BotConfig config;

    static final String HELP_TEXT = "This bot is to created to Spring capabilities.\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n" +
            "Type /start to see a welcome massage\n\n" +
//            "Type /mydata to see data stored about yourself\n\n" +
//            "Type /delete ...\n\n" +
            "Type /help ...";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome massage"));
        listOfCommands.add(new BotCommand("/register", "..."));
        listOfCommands.add(new BotCommand("send", "..."));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        //        listOfCommands.add(new BotCommand("/", "..."));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error settings bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) // start, help, register, send
    {
        log.info("Update data");
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId){
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (UserEntity user: users) {
                    SendMessage message = new SendMessage();
                    executeSendMessage(message, user.getId(), textToSend);
                }
            } else {

                String memberName = update.getMessage().getFrom().getFirstName();

                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startBox(chatId, memberName);
                        break;
                    case "/help":
                        SendMessage message = new SendMessage();
                        executeSendMessage(message, chatId, HELP_TEXT);
                        break;
                    case "/register":
                        register(chatId);
                        break;
                    default:
                        message = new SendMessage();
                        executeSendMessage(message, chatId, "Unexpected message");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            EditMessageText message = new EditMessageText();
            String text = null;

            if (callBackData.equals(YES_BUTTON)){
                text = "You pressed YES button";
            } else if (callBackData.equals(NO_BUTTON)){
                text = "You pressed NO button";
            }

            message.setMessageId((int) messageId);
            executeEditMessageText(message, chatId, text);
        }

    }

    private void register(long chatId) {
        SendMessage message = new SendMessage();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        var yesButton = newButton("Yes", "YES_BUTTON");
        var noButton = newButton("No", "NO_BUTTON");

        row.add(yesButton);
        row.add(noButton);
        rowsInLine.add(row);

        markup.setKeyboard(rowsInLine);
        message.setReplyMarkup(markup);

        executeSendMessage(message, chatId, "Do you really want to register");

    }

    private void startBox(long chatId, String userName) {
        String text = EmojiParser.parseToUnicode("Hello, " + userName + "! I'm a Telegram bot." + ":blush:");
        log.info("Replied to user " + userName);
        SendMessage message = new SendMessage();
        executeSendMessage(message, chatId, text);
    }

    private void registerUser(Message message) {
        var chatId = message.getChatId();

        if (userRepository.findById(chatId).isEmpty()){
            try {

                var chat = message.getChat();

                UserEntity user = new UserEntity();
                user.setId(chatId);
                user.setFirstName(chat.getFirstName());
                user.setLastName(chat.getLastName());
                user.setUserName(chat.getUserName());
                user.setRegisteredAt(LocalDateTime.now());

                userRepository.save(user);
                log.info("user saved: " + user);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
    }

    private void executeSendMessage(SendMessage message, long chatId, String text){
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeEditMessageText(EditMessageText message, long chatId, String text){
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private InlineKeyboardButton newButton(String text, String constanta){
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(constanta);
        return button;
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds(){
        var ads = adsRepository.findAll();
        var users = userRepository.findAll();

        for (AdsEntity ad: ads){
            for (UserEntity user: users){
                SendMessage message = new SendMessage();
                executeSendMessage(message, user.getId(), ad.getAd());
            }
        }

    }
}
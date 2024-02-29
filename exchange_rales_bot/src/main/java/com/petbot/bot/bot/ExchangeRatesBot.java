package com.petbot.bot.bot;

import com.petbot.bot.exception.ServiceException;
import com.petbot.bot.service.ExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String RUB = "/bel";
    private static final String HELP = "/help";




    private static final String helpText = """
            Справочная информация по боту
            Для получения текущих курсов валют воспользуйтесь командами:
            /usd - курс доллара
            /eur - курс евро
            /bel - курс Белорусского рубля""";

    private static final String unknownText = "Чирик чирик, не понятно, что ты говоришь";
    private static final String usdText = "Курс доллара на %s составляет %s рублей";
    private static final String eurText = "Курс евро на %s составляет %s рублей";
    private static final String rubText = "Курс Белорусского рубля на %s составляет %s рублей";
    private static final String errText = "Не удалось получить текущий курс. Попробуйте позже.";

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return "petProgectTestName_PPTN_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        String userName = update.getMessage().getChat().getUserName();
        long chatId = update.getMessage().getChatId();
        parseArgs(message, chatId, userName);
    }

    private void parseArgs(String message, Long chatId, String userName) {
        switch (message) {
            case START -> StartCommand(chatId, userName);
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case RUB -> rubCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }

    private void StartCommand(Long chatId, String userName) {
        String commandText = """
                Добро пожаловать в бот, %s!
                Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                Для этого воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                /bel     - курс Белорусского рубля
                Дополнительные команды:
                /help - получение справки""";
        String resCommandText = String.format(commandText, userName);
        sendMassage(chatId, resCommandText);
    }
    private void usdCommand(Long chatId) {
        String resultText;
        try {
            String course = exchangeRatesService.getUSDExchangeRate();
            resultText = String.format(usdText, LocalDate.now(), course);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса доллара", e);
            resultText = errText;
        }
        sendMassage(chatId, resultText);
    }
    private void eurCommand(Long chatId) {
        String resultText;
        try {
            String course = exchangeRatesService.getEURExchangeRate();
            resultText = String.format(eurText, LocalDate.now(), course);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса евро", e);
            resultText = errText;
        }
        sendMassage(chatId, resultText);
    }
    private void rubCommand(Long chatId) {
        String resultText;
        try {
            String course = exchangeRatesService.getBELExchangeRate();
            resultText = String.format(rubText, LocalDate.now(), course);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса Белорусского рубля", e);
            resultText = errText;
        }
        sendMassage(chatId, resultText);
    }
    private void helpCommand(Long chatId) {
        sendMassage(chatId, helpText);
    }
    private void unknownCommand(Long chatId) {
        sendMassage(chatId, unknownText);
    }
    private void sendMassage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки сообщения", e);
        }
    }
}

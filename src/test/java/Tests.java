import org.junit.*;
import static org.junit.Assert.*;

public class Tests {
    private MessageParser mp;
    private CurrencyForRegions cfreg = new CurrencyForRegions();
    private CurrencyForCenterBank cfcb = new CurrencyForCenterBank();
    private Bot bot = new Bot();

    @Test
    public void parserRandomMessageTest(){
        mp = new MessageParser("Привет!!!");
        String expResult = "Я озадачен...\nЕсли что, /help - вызов справки.";
        String result = mp.parser();
        assertEquals(expResult, result);
    }

    @Test
    public void parserCurrencyOnlyTest(){
        mp = new MessageParser("покажи курс usd в Сан-Франциско");
        String expResult = "Не знаю такого города. Введите запрос заново.";
        String result = mp.parser();
        assertEquals(expResult, result);
    }

    @Test
    public void parserCityOnlyTest(){
        mp = new MessageParser("курс долара в москве");
        String expResult = "Не понимаю валюту. Введите запрос заново.";
        String result = mp.parser();
        assertEquals(expResult, result);
    }

    @Test
    public void parserDateNotExistsTest(){
        mp = new MessageParser("цб 28.28.2004");
        String expResult = "Запрашиваемой даты не существует!";
        String result = mp.parser();
        assertEquals(expResult, result);
    }

    @Test
    public void parserFutureDateTest(){
        mp = new MessageParser("цб 01.04.2021");
        String expResult = "Запрашиваемая дата еще не наступила!";
        String result = mp.parser();
        assertEquals(expResult, result);
    }

    @Test
    public void regionsResultTest(){
        String expResult = "Курс USD в городе Москва:";
        String result = cfreg.run("https://mainfin.ru/currency/usd/moskva", "Москва", "usd").substring(0,25);
        assertEquals(expResult, result);
    }

    @Test
    public void regionsValueTest(){
        double expResult = 71.03;
        double result = Double.parseDouble(cfreg.run("https://mainfin.ru/currency/usd/moskva", "Москва", "usd").substring(63,68));
        assertEquals(expResult, result, 0.5);
    }

    @Test
    public void cbOutputTest(){
        String expResult = "Курс валют ЦБ на 06.07.2020:";
        String result = cfcb.run("http://www.cbr.ru/currency_base/daily/?UniDbQuery.Posted=True&UniDbQuery.To=06.07.2020", "06.07.2020").substring(0,28);
        assertEquals(expResult, result);
    }

    @Test
    public void cbValueUsdTest(){
        int expResult = 70;
        int result = Integer.parseInt(cfcb.run("http://www.cbr.ru/currency_base/daily/?UniDbQuery.Posted=True&UniDbQuery.To=06.07.2020", "06.07.2020").substring(46,48));
        assertEquals(expResult, result, 0);
    }

    @Test
    public void botNameTest(){
        String expResult = "CurrencyExchangeTrackerBot";
        String result = bot.getBotUsername();
        assertEquals(expResult, result);
    }

    @Test
    public void botTokenTest(){
        String expResult = "1370035484:AAEHWrwuGU4Bu8bixRbHm6crfw_opLLDSJQ";
        String result = bot.getBotToken();
        assertEquals(expResult, result);
    }

}

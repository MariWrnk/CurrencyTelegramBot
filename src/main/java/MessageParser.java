import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageParser {
    private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

    private String message;
    private String[] currencyEng = {"usd", "eur", "gbp", "jpy", "cny"};
    private String[] currencyRus = {"доллар", "евро", "фунт", "йен", "юан"};
    private String cityFullName = null;
    private String cityUrlName = null;
    private String currency = null;
    private String urlForRegions = "https://mainfin.ru/currency/";
    private String urlForCenterBank = "https://www.cbr.ru/currency_base/daily/?UniDbQuery.Posted=True&UniDbQuery.To=";
    private boolean privateOnly = false;
    public boolean isPrivateOnly() {
        return privateOnly;
    }

    public MessageParser(String message){
        this.message = message;
        this.message = this.message.toLowerCase();
    }

    public String parser() {
        logger.debug("parser method is starting.");
        logger.info("Searching if message contains currency.");
        for(int i = 0; i < currencyEng.length; i++){
            if(message.contains(currencyEng[i])){
                logger.info("Found currency {}.", currencyEng[i]);
                currency = currencyEng[i];
                break;
            }
        }
        if(currency == null){
            for(int i = 0; i < currencyRus.length; i++){
                if(message.contains(currencyRus[i])){
                    logger.info("Found currency {}.", currencyEng[i]);
                    currency = currencyEng[i];
                    break;
                }
            }
        }
        logger.info("Searching if message contains a city name.");
        try{
            Scanner sc = new Scanner(new File("src/main/resources/cities.txt"));
            logger.info("Scan a list of city names.");
            String line;
            while(sc.hasNextLine()){
                logger.debug("Searching in next line.");
                line = sc.nextLine();
                if(message.contains(line)){
                    logger.info("Found a city name {}.", line);
                    cityFullName = line;
                    break;
                }
            }
        }catch (FileNotFoundException e){
            logger.error("File with list of city names was not found.", e);
        }
        if(cityFullName != null){
            logger.info("Getting city info.");
            logger.info("Create JSONParser.");
            JSONParser parser = new JSONParser();
            try {
                logger.info("Getting city parameters from data.json.");
                Object obj = parser.parse(new FileReader("src/main/resources/data.json"));
                JSONObject jsonObj = (JSONObject) obj;
                JSONObject jo = (JSONObject) jsonObj.get(cityFullName);
                cityFullName = (String) jo.get("fullName");
                cityUrlName = (String) jo.get("urlName");
            } catch (IOException e) {
                logger.error("Catched IOException.", e);
            } catch (ParseException e) {
                logger.error("Catched ParseException.", e);
            }
        }

        logger.info("Checking if we have currency and city.");
        if(cityFullName != null && currency == null){
            logger.info("Sending answer. Currency is missing.");
            return "Не понимаю валюту. Введите запрос заново.";
        }else if(cityFullName == null && currency != null){
            logger.info("Sending answer. City is missing.");
            return "Не знаю такого города. Введите запрос заново.";
        }else if(cityFullName != null  && currency != null){
            logger.info("Creating an url for parameters.");
            urlForRegions = urlForRegions + currency + "/" + cityUrlName;
            logger.debug("Creating CurrencyForRegions");
            CurrencyForRegions cfr = new CurrencyForRegions();
            logger.info("Sending answer from CurrencyForRegions.");
            return cfr.run(urlForRegions, cityFullName, currency);
        }

        logger.info("Looking for data for center bank.");
        String[] words = message.split(" ");
        if(words[0].equals("цб")){
            logger.info("Looking for a date.");
            String day = words[1];
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setLenient(false);
            Date inputDate = null;
            try {
                logger.info("Checking date format.");
                inputDate = dateFormat.parse(day);
            } catch (java.text.ParseException e) {
                logger.info("Sending answer. Wrong date format.");
                return "Запрашиваемой даты не существует!";
            }
            Date today = new Date();
            logger.info("Comparing input date with today.");
            if(today.compareTo(inputDate) < 0){
                logger.info("sending answer. It is future date.");
                return "Запрашиваемая дата еще не наступила!";
            }
            logger.info("Sending answer from CurrencyForCenterBank.");
            urlForCenterBank = urlForCenterBank + day;
            logger.debug("Creating CurrencyForCenterBank.");
            CurrencyForCenterBank cfcb = new CurrencyForCenterBank();
            logger.info("Sending answer from CurrencyForCenterBank.");
            return cfcb.run(urlForCenterBank, day);
        }
        privateOnly = true;
        logger.info("Sending answer. No options for given request.");
        return "Я озадачен...\nЕсли что, /help - вызов справки.";
    }

}

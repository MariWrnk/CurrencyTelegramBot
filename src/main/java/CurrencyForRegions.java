import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyForRegions {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyForRegions.class);

    public static String run(String url, String cityFullName, String currency) {
        logger.debug("run method is starting.");
        String answer = "Курс " + currency.toUpperCase() + " в городе " + cityFullName + ":\n";
        try {
            logger.info("Connecting to url.");
            Document doc = Jsoup.connect(url).get();
            logger.debug("Searching for elements with data-key attribute.");
            Elements table = doc.getElementsByAttribute("data-key");
            int banksCount = table.size();
            if(banksCount == 0){
                logger.info("Sending answer. No info for given city.");
                return "В городе " + cityFullName + " нет такой валюты";
            }
            if(banksCount > 5){
                banksCount = 5;
            }
            String bankName;
            String purchase;
            String sale;
            logger.info("Reading data about currency values.");
            for(int i = 0; i < banksCount; i++){
                logger.debug("Next element.");
                Element currElement = table.get(i);
                Element td = currElement.child(0);
                Element a = td.child(1);
                bankName = a.text();
                purchase = currElement.child(1).child(0).text();
                logger.info("Got currency purchase value {}.", purchase);
                sale = currElement.child(2).child(0).text();
                logger.info("Got currency sale value {}.", sale);
                logger.info("Adding results to answer string.");
                answer = answer + bankName + "\nПокупка: " + purchase + "\nПродажа:" + sale + "\n";
            }
        } catch (IOException e) {
            logger.error("Catched IOException.", e);
        }
        logger.info("Returning answer.");
        return answer;
    }

}

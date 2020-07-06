import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyForCenterBank {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyForRegions.class);

    public static String run(String url, String date){
        logger.debug("run method is starting.");
        String answer = "Курс валют ЦБ на " + date + ":\n";
        try{
            logger.info("Connecting to url.");
            Document doc = Jsoup.connect(url).get();
            Elements table = doc.getElementsByTag("tbody");
            Element wantedTable = table.get(0);
            logger.info("Checking elements from a table.");
            for(int i = 0; i < wantedTable.childrenSize(); i++){
                Element elm = wantedTable.child(i);
                if(elm.child(1).text().equals("USD") || elm.child(1).text().equals("EUR") || elm.child(1).text().equals("GBP") || elm.child(1).text().equals("JPY") || elm.child(1).text().equals("CNY")){
                    logger.info("Found needed element. Adding data to answer string.");
                    answer = answer + elm.child(3).text() + "\nКурс: " + elm.child(4).text() + "\n";
                }
            }
            logger.info("Returning answer.");
            return answer;
        }catch (IOException e){
            logger.info("Returning answer. No data for given date.");
            return "Отсутствуют данные на эту дату.";
        }
    }
}

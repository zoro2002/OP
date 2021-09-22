package com.op.backend.scrape;

import com.op.backend.model.ScrapeInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;


public class WebScrape {

    Map<String,ScrapeInfo> listData = new HashMap<>();
    Date currentDate = new Date();
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    //Added current Year in URL
    final String url = "https://claystage.com/one-piece-chapter-release-schedule-for-" + yearFormat.format(currentDate);

    public ScrapeInfo getDateChapter(){
        ScrapeInfo weekInfo;
        this.getDate();
        weekInfo = listData.get(this.getCurrentWeek());

        return  weekInfo;
    }

    private String getCurrentWeek(){
        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
    }

    private void getDate(){
        try {
            final Document document = Jsoup.connect(url).get();

            //Selected Body of the Table
            Elements body = document.select("body");

            //Getting Information from Body
            for (Element e : body.select("tr")){

                String keyWeek = removeText(e.select("td.col-1.odd").text(), "Week ");


                ScrapeInfo scrapeInfo = new ScrapeInfo();

                scrapeInfo.Chapter = e.select("td.col-2.even").text();
                scrapeInfo.Leak =  removeText(e.select("td.col-3.odd").text(), ", " + yearFormat.format(currentDate));
                scrapeInfo.Official = removeText(e.select("td.col-5.odd").text(), ", " + yearFormat.format(currentDate));

                listData.put(keyWeek, scrapeInfo);
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        };
    }

    private String removeText(String text, String remove){
        String target = text.copyValueOf(remove.toCharArray());
        return text.replace(target, "");
    }

}

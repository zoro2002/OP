package com.op.backend.scrape;

import com.op.backend.model.ScrapeInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebScrape {
    //If Chapter is Today
    boolean isChapterToday = false;

    //For URL
    DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
    LocalDate currentDate = LocalDate.now();

    //Added current Year in URL
    final String url = "https://claystage.com/one-piece-chapter-release-schedule-for-" + yearFormat.format(currentDate);

    //Regex to check Date from Site
    String regex = "([a-z A-Z])\\w+.[0-9]{1,2},.[0-9]{4}";
    Pattern pattern = Pattern.compile(regex);

    //All Leaked Dates
    SortedSet<LocalDate> listDate = new TreeSet<>();
    //All Weeks (key) with all Leaked Dates (value)
    HashMap<String, LocalDate> mapLeakedDates = new HashMap<>();
    //All Weeks (key) with all Chapters (value)
    HashMap<String,String> mapChapters = new HashMap<>();

    public ScrapeInfo getDateChapter(){
        //Get Data from URL
        this.getDateFromUrl();
        //Get difference between today and next chapters Date
        this.diffDate();

        return getScrapeInfo();
    }

    private void getDateFromUrl(){
        try {
            final Document document = Jsoup.connect(url).get();

            //Selected Body of the Table
            Elements body = document.select("body");

            //Getting Information from Body
            for (Element e : body.select("tr")){
                String keyWeek = this.removeText(e.select("td.col-1.odd").text(), "Week ");
                String Chapter = e.select("td.col-2.even").text();
                String Leak = e.select("td.col-3.odd").text();

                this.addToMap(keyWeek, Leak);
                mapChapters.put(keyWeek, Chapter);
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

    private void addToMap(String keyWeek, String Leak){
        //Check if Leak is a Date
        Matcher mt = pattern.matcher(Leak);

        if (mt.matches()){
            mapLeakedDates.put(keyWeek, this.convertStringToLocalDate(Leak));
        }
        else{
            //If not add local date instead of null
            LocalDate date = LocalDate.of(2020, 1, 8);
            mapLeakedDates.put(keyWeek, date);
        }
    }

    private LocalDate convertStringToLocalDate(String stringDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        return LocalDate.parse(stringDate, formatter);
    }

    private void diffDate(){
        LocalDate date2;
        for (Map.Entry me : mapLeakedDates.entrySet()) {
            date2 = (LocalDate) me.getValue();
            if(currentDate.isBefore(date2)){
                listDate.add(date2);
            }
            if (currentDate.equals(date2)){
                isChapterToday = true;
                listDate.add(date2);
            }
        }
    }

    private ScrapeInfo getScrapeInfo(){
        ScrapeInfo scrapeInfo = new ScrapeInfo();

        for (Map.Entry me : mapLeakedDates.entrySet()) {
            if (me.getValue() == listDate.first()){
                scrapeInfo.Chapter = mapChapters.get(me.getKey());
            }
        }
        scrapeInfo.ChapterInDay = whenIsChapter();

        return scrapeInfo;
    }

    private String whenIsChapter(){
        String day;
        if(isChapterToday){
            day = "Today";
        }
        else {
            day = this.chapterInDays();
        }
        return day;
    }

    private String chapterInDays(){
        Period period = Period.between(currentDate, listDate.first());
        if (period.getDays() == 1){
           return period.getDays() + " day";
        }
        return period.getDays() + " days";
    }
}
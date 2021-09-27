package com.op.backend.scrape;

import com.op.backend.model.ChapterInfo;
import org.jsoup.Connection;
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


public class ChapterScrape {
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

    //All Dates from Today and the Future
    SortedSet<LocalDate> listDateFuture = new TreeSet<>();
    //All Weeks (key) with all Leaked Dates (value)
    HashMap<String, LocalDate> mapLeakedDates = new HashMap<>();
    //All Weeks (key) with all Chapters (value)
    HashMap<String,String> mapChapters = new HashMap<>();

    public ChapterInfo getDateChapter(){
        //Get Data from URL
        this.getDateFromUrl();
        //Get difference between today and next chapters Date
        this.diffDate();

        return getScrapeInfo();
    }

    private void getDateFromUrl(){
        try {
            // https://stackoverflow.com/questions/34242924/jsoup-not-working-properly-on-specified-website
            Connection.Response res = Jsoup.connect(url)
                    .followRedirects(false)
                    .timeout(0)
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0")
                    .execute();

            String location = res.header("Location");

            res = Jsoup.connect(url)
                    .timeout(0)
                    .data("is_check", "1")
                    .method(Connection.Method.POST)
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Referer", location)
                    .execute();

            Document doc = res.parse();
            //Get Date from Table
            Elements body = doc.select("tbody");

            //Getting Information from Body
            for (Element e : body.select("tr")){
                String keyWeek = this.removeText(e.select("td.col-1.odd").text(), "Week ");
                String Chapter = e.select("td.col-2.even").text();
                String Leak = e.select("td.col-3.odd").text();

                this.addToMapLeaked(keyWeek, Leak);
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

    private void addToMapLeaked(String keyWeek, String Leak){
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
                listDateFuture.add(date2);
            }
            if (currentDate.equals(date2)){
                isChapterToday = true;
                listDateFuture.add(date2);
            }
        }
    }

    private ChapterInfo getScrapeInfo(){
        ChapterInfo chapterInfo = new ChapterInfo();

        for (Map.Entry me : mapLeakedDates.entrySet()) {
            if (me.getValue() == listDateFuture.first()){
                chapterInfo.Chapter = mapChapters.get(me.getKey());
            }
        }
        chapterInfo.ChapterInDay = whenIsChapter();

        return chapterInfo;
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
        Period period = Period.between(currentDate, listDateFuture.first());
        if (period.getDays() == 1){
           return period.getDays() + " day";
        }
        return period.getDays() + " days";
    }
}
package com.op.backend.controller;

import com.op.backend.model.ScrapeInfo;
import com.op.backend.scrape.WebScrape;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpController {

    @CrossOrigin("http://localhost:4200/")
    @GetMapping("/getDateAndChapter")
    public ScrapeInfo getDateChapter(){
        WebScrape webScrape = new WebScrape();
        ScrapeInfo scrapeInfo;
        scrapeInfo = webScrape.getDateChapter();

        return scrapeInfo;
    }
}

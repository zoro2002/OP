package com.op.backend.controller;

import com.op.backend.model.ChapterInfo;
import com.op.backend.scrape.ChapterScrape;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpController {

    @CrossOrigin("http://localhost:4200/")
    @GetMapping("/getDateAndChapter")
    public ChapterInfo getDateChapter(){
        ChapterScrape chapterScrape = new ChapterScrape();
        ChapterInfo chapterInfo;
        chapterInfo = chapterScrape.getDateChapter();

        return chapterInfo;
    }
}

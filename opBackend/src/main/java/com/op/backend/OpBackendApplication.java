package com.op.backend;

import com.op.backend.repository.OpRepository;
import com.op.backend.scrape.WebScrape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpBackendApplication.class, args);
        WebScrape scrape = new WebScrape();
        scrape.getDateChapter();
    }

}

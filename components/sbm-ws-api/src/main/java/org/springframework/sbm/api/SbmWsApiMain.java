package org.springframework.sbm.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This is a library, but the SpringBootApplication is needed to autoscan the cont4ext.
@SpringBootApplication
public class SbmWsApiMain {
    public static void main(String[] args) {
        SpringApplication.run(SbmWsApiMain.class, args);
    }
}

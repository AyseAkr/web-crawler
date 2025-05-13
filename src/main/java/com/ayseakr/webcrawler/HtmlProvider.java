package com.ayseakr.webcrawler;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HtmlProvider {
    public String getHtml(String url) throws IOException {
        return Jsoup.connect(url).get().html();
    }
}

package com.ayseakr.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Service
public class WebCrawlerService {
    private final HtmlProvider provider;

    public WebCrawlerService(HtmlProvider provider) {
        this.provider = provider;
    }

    public PagesResponse findLinks(String target) {
        if (target == null || target.isEmpty()) {
            return new PagesResponse(target, Set.of());
        }
        Set<String> linkList = new HashSet<>();
        crawlAllPages(target, linkList);
        return new PagesResponse(target, linkList);

        }


      private void crawlAllPages(String url, Set<String> linkList) {
        if(linkList.contains(url)){
            return;
        }
        linkList.add(url);
        try {
            String html = provider.getHtml(url);
            Document document = Jsoup.parse(html);
            Elements links = document.select("a");
            String baseUrl = url.replaceFirst("/+$", "");


            for (Element link : links) {
                String href = link.attr("href");
                if (href.isBlank() || href.startsWith("#") || href.startsWith("mailto:") || href.startsWith("javascript:")) {
                    continue;
                }
                String absoluteUrl;
                if (isAbsolute(href)) {
                    absoluteUrl = href;
                } else {
                    href = href.replaceFirst("^/", "");
                    absoluteUrl = baseUrl + "/" + href;
                }
                if (absoluteUrl.startsWith(baseUrl)) {
                    crawlAllPages(absoluteUrl, linkList);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

      }

      public boolean isAbsolute(String link){
        try {
            URL url = new URL(link);
            return url.getProtocol() != null && url.getHost() != null;
        } catch (MalformedURLException e) {
            return false;
        }
      }

}

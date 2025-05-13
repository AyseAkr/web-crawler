package com.ayseakr.webcrawler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
class WebCrawlerServiceIntegrationTest {
    @Autowired
    private WebCrawlerService webCrawlerService;

    @Test
    void testCrawl_returnsExpectedJson() {
        String targetUrl = "https://example.com/";
        PagesResponse expected = new PagesResponse("https://example.com/", Set.of(
                "https://example.com/")
        );


        PagesResponse actual = webCrawlerService.findLinks(targetUrl);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testFindLinks_withInvalidUrl_returnsEmptyLinks() {
        String invalidUrl = "https://invaliddasdadsa-url.com";

        PagesResponse actual = webCrawlerService.findLinks(invalidUrl);

        Assertions.assertEquals(invalidUrl, actual.domain());
        Assertions.assertEquals(actual.pages(), Set.of(invalidUrl));
    }
}
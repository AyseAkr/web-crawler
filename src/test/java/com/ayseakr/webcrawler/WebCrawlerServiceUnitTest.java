package com.ayseakr.webcrawler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerServiceUnitTest {
    private WebCrawlerService webCrawlerService;
    private HtmlProvider htmlProvider;

    @BeforeEach
    void setUp() {
        htmlProvider = Mockito.mock(HtmlProvider.class);
        webCrawlerService = new WebCrawlerService(htmlProvider);
    }

    @Test
    void testFindLinks_withValidHtml_returnsLinks() throws Exception {
        String url = "https://example.com";
        String html = """
        <html>
          <body>
            <a href="/about.html">About</a>
            <a href="https://example.com/contact.html">Contact</a>
          </body>
        </html>
        """;

        Mockito.when(htmlProvider.getHtml(url)).thenReturn(html);
        Mockito.when(htmlProvider.getHtml("https://example.com/about.html")).thenReturn("");
        Mockito.when(htmlProvider.getHtml("https://example.com/contact.html")).thenReturn("");

        PagesResponse result = webCrawlerService.findLinks(url);

        assertEquals(url, result.domain());
        assertEquals(
                Set.of(
                        "https://example.com",
                        "https://example.com/about.html",
                        "https://example.com/contact.html"
                ),
                result.pages()
        );
    }

    @Test
    void testFindLinks_withRecursiveLink_doesNotLoopInfinitely() throws Exception {
        String url = "https://example.com";
        String html = """
        <html>
          <body>
            <a href="https://example.com">Home</a>
          </body>
        </html>
        """;

        Mockito.when(htmlProvider.getHtml(url)).thenReturn(html);

        PagesResponse result = webCrawlerService.findLinks(url);

        assertEquals(Set.of("https://example.com"), result.pages());
    }

    @Test
    void testFindLinks_withInvalidLinks_shouldSkipThem() throws Exception {
        String url = "https://example.com";
        String html = """
        <html>
          <body>
            <a href="mailto:test@example.com">Mail</a>
            <a href="#section1">Section</a>
            <a href="/valid.html">Valid</a>
          </body>
        </html>
        """;

        Mockito.when(htmlProvider.getHtml(url)).thenReturn(html);
        Mockito.when(htmlProvider.getHtml("https://example.com/valid.html")).thenReturn("");

        PagesResponse result = webCrawlerService.findLinks(url);

        assertEquals(
                Set.of(
                        "https://example.com",
                        "https://example.com/valid.html"
                ),
                result.pages()
        );
    }

    @Test
    void testFindLinks_whenHtmlProviderThrows_shouldHandleGracefully() throws Exception {
        String url = "https://fail.com";

        Mockito.when(htmlProvider.getHtml(url)).thenThrow(new IOException("network error"));

        PagesResponse result = webCrawlerService.findLinks(url);

        assertEquals(url, result.domain());
        assertEquals(Set.of(url), result.pages());
    }

    @Test
    void testFindLinks_withDuplicateLinks_shouldAddOnceOnly() throws Exception {
        String url = "https://example.com";
        String html = """
        <html>
          <body>
            <a href="/about.html">About</a>
            <a href="/about.html">About Again</a>
          </body>
        </html>
        """;

        Mockito.when(htmlProvider.getHtml(url)).thenReturn(html);
        Mockito.when(htmlProvider.getHtml("https://example.com/about.html")).thenReturn("");

        PagesResponse result = webCrawlerService.findLinks(url);

        assertEquals(Set.of("https://example.com", "https://example.com/about.html"), result.pages());
    }
    @Test
    void testFindLinks_shouldNotFollowExternalLinks() throws Exception {
        String url = "https://example.com";
        String html = """
        <html>
          <body>
            <a href="https://other.com/page.html">External</a>
            <a href="/internal.html">Internal</a>
          </body>
        </html>
        """;

        Mockito.when(htmlProvider.getHtml(url)).thenReturn(html);
        Mockito.when(htmlProvider.getHtml("https://example.com/internal.html")).thenReturn("");

        PagesResponse result = webCrawlerService.findLinks(url);

        assertEquals(Set.of("https://example.com", "https://example.com/internal.html"), result.pages());
    }

    @Test
    void testFindLinks_withEmptyUrl_returnsEmpty() {
        String emptyUrl = "";

        PagesResponse result = webCrawlerService.findLinks(emptyUrl);

        assertEquals(emptyUrl, result.domain());
        assertTrue(result.pages().isEmpty());
    }

    @Test
    void testFindLinks_withNullUrl_shouldNotThrowException() {
        String nullUrl = null;

        PagesResponse result = webCrawlerService.findLinks(nullUrl);

        assertNull(result.domain());
        assertTrue(result.pages().isEmpty());
    }

    @Test
    void testIsAbsolute_withAbsoluteUrl_returnsTrue() {
        String absoluteUrl = "https://example.com";

        boolean result = webCrawlerService.isAbsolute(absoluteUrl);

        Assertions.assertTrue(result);
    }

    @Test
    void testIsAbsolute_withRelativeUrl_returnsFalse() {
        String relativeUrl = "/relative/path";

        boolean result = webCrawlerService.isAbsolute(relativeUrl);

        Assertions.assertFalse(result);
    }

}
package com.ayseakr.webcrawler;

import java.util.List;
import java.util.Set;

public record PagesResponse(String domain, Set<String> pages) {
}

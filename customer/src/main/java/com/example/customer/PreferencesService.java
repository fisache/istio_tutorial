package com.example.customer;

import feign.RequestLine;

public interface PreferencesService {
    @RequestLine("GET /")
    String getPreferences();
}
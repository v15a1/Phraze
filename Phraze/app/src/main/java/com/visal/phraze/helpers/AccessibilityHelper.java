package com.visal.phraze.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;

public class AccessibilityHelper {
    private static final String API_KEY = "lHcLKmDes4gSkHDkbisAQPjoVgXkPZXZUieen1mPNEA_";
    private static final String URI = "https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/a27469d0-da65-41f4-8c2a-1db04597da4e";
    private LanguageTranslator service;

    public AccessibilityHelper() {
        this.service = initLanguageTranslatorService();
    }

    private LanguageTranslator initLanguageTranslatorService(){
        Authenticator authenticator = new IamAuthenticator(API_KEY);
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);
        service.setServiceUrl(URI);
        return service;
    }

    public LanguageTranslator getService() {
        return this.service;
    }
}

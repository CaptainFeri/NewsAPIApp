package com.example.xnewsapiapp;

import java.util.ArrayList;
import java.util.List;

public class ObjectRequest {
    String status;
    ArrayList<Article> articles;

    public ObjectRequest() {
    }

    public ObjectRequest(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public ObjectRequest(String status, ArrayList<Article> articles) {
        this.status = status;
        this.articles = articles;
    }
}

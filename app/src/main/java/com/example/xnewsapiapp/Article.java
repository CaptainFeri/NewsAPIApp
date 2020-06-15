package com.example.xnewsapiapp;

public class Article {
    String name;
    String author;
    String title;
    String url;
    String Image_url;
    String publishedAt;
    String content;

    public Article(String name, String author, String title, String url, String image_url, String publishedAt, String content) {
        this.name = name;
        this.author = author;
        this.title = title;
        this.Image_url = image_url;
        this.url = url;
        this.publishedAt = publishedAt;
        this.content = content;
    }
}

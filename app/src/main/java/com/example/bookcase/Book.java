package com.example.bookcase;

public class Book {
    private int id;
    private String title;
    private String author;
    private int published;
    private String coverURL;

    public Book(int id, String title, String author, int published, String coverURL) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.published = published;
        this.coverURL = coverURL;
    }
}

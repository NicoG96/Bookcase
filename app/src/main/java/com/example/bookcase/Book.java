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

    public Book() {}

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public void setPublished(int published) {
        this.published = published;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Book ID:\t" + id + "\n" +
                "Title:\t" + title + "\n" +
                "Author:\t" + author + "\n" +
                "Published:\t" + published + "\n" +
                "coverURL:\t" + coverURL + "\n";
    }
}
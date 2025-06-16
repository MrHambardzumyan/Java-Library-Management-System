package com.library.model;

public class Book {
    private String isbn, title, author;
    private boolean isAvailable;

    public Book(String isbn, String title, String author, boolean isAvailable) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
    }
    //get setters 
    public String getIsbn()      { return isbn; }
    public String getTitle()     { return title; }
    public String getAuthor()    { return author; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean av) { isAvailable = av; }


    //summary of book availability-used array list instead of linked list so boolean is attached on each book. all stored in sql
    @Override
    public String toString() {
        //format: isbn, "title", author, boolean(availability)
        return String.format("[%s] \"%s\" by %s — %s",
            isbn, title, author,
            isAvailable ? "Available" : "Checked out");
    }
}

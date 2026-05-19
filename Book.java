public class Book {
    public int bookId;
    public String title;
    public String author;
    public boolean available;

    public Book(int bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return available; }
    public void setAvailability(boolean status) { this.available = status; }

    public void displayBookDetails() {
        System.out.println("ID: " + bookId + " | Title: " + title + " | Author: " + author + " | Status: " + (available ? "Available" : "Borrowed"));
    }
}
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BorrowRecord {
    public Book book;
    public int studentId;
    public Date borrowDate;
    public Date dueDate;
    public Date returnDate;
    public FineCalculator fineCalculator;

    public BorrowRecord(Book book, int studentId, Date borrowDate, Date dueDate) {
        this.book = book;
        this.studentId = studentId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate; 
        this.fineCalculator = new LibraryFineCalculator();
    }
    
    public Book getBook() {
        return this.book;
    }

    public void setReturnDate(Date returnDate) { 
        this.returnDate = returnDate; 
    }

    
    public boolean isOverdue() {
        if (returnDate == null) return new Date().after(dueDate);
        return returnDate.after(dueDate);
    }

   
    public int calculateDaysLate() {
        if (!isOverdue()) return 0;
        long diffInMillis = returnDate.getTime() - dueDate.getTime();
        return (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public double calculateFine() {
        return fineCalculator.calculateFine(calculateDaysLate());
    }

    public void displayRecord() {
        System.out.println("Book: " + book.getTitle());
        System.out.println("Due Date: " + dueDate);
        System.out.println("Return Date: " + (returnDate != null ? returnDate : "Not Returned Yet"));
        System.out.println("Fine: Rs. " + calculateFine());
    }
}
import java.util.ArrayList;
import java.util.Date;

public class Library {
    public ArrayList<Book> bookList = new ArrayList<>();
    public ArrayList<Student> studentList = new ArrayList<>();
    public ArrayList<BorrowRecord> activeRecords = new ArrayList<>();

    public void addBook(Book book) { bookList.add(book); }
    public void addStudent(Student student) { studentList.add(student); }

    public String issueBook(int studentId, int bookId) {
        Book book = findBook(bookId);
        Student student = findStudent(studentId);
    
        if (book == null) return "Error: Book not found.";
        if (student == null) return "Error: Student not found.";
        if (!book.isAvailable()) return "Error: Book is already borrowed.";

        book.setAvailability(false);
        // Setting a due date (7 days from now)
        Date dueDate = new Date(System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000));
        BorrowRecord record = new BorrowRecord(book, studentId, new Date(), dueDate);
    
        activeRecords.add(record);
        student.addBorrowRecord(record);
    
        return "Success: Book issued to " + student.getName();
    }

    public String returnBook(int bookId) {
        BorrowRecord recordToRem = null;

        // Search for the active record
        for (BorrowRecord record : activeRecords) {
            if (record.getBook().getBookId() == bookId) {
                recordToRem = record;
                break;
            }
        }

        if (recordToRem != null) {
            // 1. Allocate the return date to a variable
            Date currentDate = new Date(); 
        
            // 2. Set the return date in the record
            recordToRem.setReturnDate(currentDate);

            // 3. Logic check: Is it on time or late?
            // Note: recordToRem.calculateFine() should compare currentDate with the record's dueDate
            double fine = recordToRem.calculateFine();

            // 4. Update status and clean up
            recordToRem.getBook().setAvailability(true);
            activeRecords.remove(recordToRem);

            // 5. Return status message based on fine
            if (fine > 0) {
                return "Returned Late. Fine: ₹" + fine;
            } else {
                return "Success: Returned on time.";
            }
        }
    
        return "Error: No active record found for this Book ID.";
    }
    
    private Book findBook(int id) {
        return bookList.stream().filter(b -> b.bookId == id).findFirst().orElse(null);
    }

    private Student findStudent(int id) {
        return studentList.stream().filter(s -> s.studentId == id).findFirst().orElse(null);
    }
    public void displayStudentHistory(int studentId) {
        Student student = findStudent(studentId); // Reuses the private findStudent method
        if (student != null) {
            System.out.println("\n--- History for " + student.getName() + " (ID: " + studentId + ") ---");
            student.viewBorrowedBooks();
        } else {
            System.out.println("Student with ID " + studentId + " not found.");
        }
    }
}
import java.util.ArrayList;

public class Student {
    public int studentId;
    public String name;
    public ArrayList<BorrowRecord> records;

    public Student(int studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.records = new ArrayList<>();
    }

    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    
    public void addBorrowRecord(BorrowRecord record) {
        records.add(record);
    }

    public void viewBorrowedBooks() {
        if (records.isEmpty()) {
            System.out.println("No borrowing history found for this student.");
        } else {
            for (BorrowRecord record : records) {
                record.displayRecord();
                System.out.println("-------------------------");
            }
        }
    }
    
}
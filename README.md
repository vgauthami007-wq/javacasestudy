# 📚 Amrita Library Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swing-GUI-4A90D9?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/OOP-Case_Study-2ECC71?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge"/>
</p>

<p align="center">
  A fully-featured <strong>Library Management System</strong> built with core Java and Swing, demonstrating Object-Oriented Programming principles through a real-world academic use case.
</p>

---

## ✨ Features

| Feature | Description |
|---|---|
| 📖 **Book Management** | Add books, track availability, and search/filter by ID, title, or author |
| 🧑‍🎓 **Student Management** | Register students and view their complete borrowing history |
| 🔄 **Issue & Return** | Issue books with automatic 7-day due dates; process returns with instant feedback |
| 💰 **Fine Calculation** | Auto-calculates overdue fines at ₹10/day; real-time preview on the return screen |
| 📊 **Live Dashboard** | Stat cards showing total books, students, borrowed, overdue counts, and accumulated fines |
| 🕒 **Borrow History** | Full audit trail per student — including days late, fines paid, and return status |
| 🔍 **Fine Calculator Panel** | Manual fine calculator + active-record lookup by Book ID |

---

## 🏗️ Architecture

The project follows a clean **layered OOP design** with a clear separation of concerns:

```
javacasestudy/
│
├── Book.java           # Entity — book data and availability flag
├── Student.java        # Entity — student data and personal borrow history
├── BorrowRecord.java   # Entity — links a student to a book; tracks dates & fines
├── FineCalculator.java # Utility — fine computation logic
├── Library.java        # Service layer — issue, return, search, and history operations
└── LibraryGUI.java     # Presentation layer — full Swing GUI (360+ lines)
```

### Class Diagram (simplified)

```
Library
 ├── ArrayList<Book>
 ├── ArrayList<Student>
 └── ArrayList<BorrowRecord>
         │
         ├── Book        (bookId, title, author, isAvailable)
         ├── Student     (studentId, name, records[])
         └── BorrowRecord(book, studentId, borrowDate, dueDate, returnDate)
                              └── calculateFine() / isOverdue() / calculateDaysLate()
```

---

## 🖥️ GUI Panels

The Swing interface is organized around a collapsible sidebar and a `CardLayout` content area:

- **Dashboard** — live stat cards + active borrow table with colour-coded overdue rows  
- **Issue Book** — issue by Student ID + Book ID; shows computed due date on success  
- **Return Book** — live fine preview as you type the Book ID; one-click confirm  
- **All Books** — searchable table with colour-coded availability status  
- **Add Book** — validated form with duplicate-ID detection  
- **All Students** — searchable table showing borrow count, active loans, and fines paid  
- **Add Student** — validated form with duplicate-ID detection  
- **Borrow History** — filterable by student ID; status column colour-coded (Returned / Late / Overdue / Active)  
- **Fine Calculator** — manual days × rate calculator *plus* real-time lookup by active Book ID  

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or later (`java -version`)
- No external libraries — pure Java SE + Swing

### Clone & Run

```bash
# 1. Clone the repository
git clone https://github.com/vgauthami007-wq/javacasestudy.git
cd javacasestudy

# 2. Compile all source files
javac *.java

# 3. Launch the GUI
java LibraryGUI
```

> The application seeds 4 sample books and 3 sample students on startup so you can explore all features immediately.

---

## 🧪 Sample Data (pre-loaded)

| ID | Book | Author |
|----|------|--------|
| 101 | OOP | Amrita Faculty |
| 102 | Data Structures | Mark Weiss |
| 103 | Design Patterns | Gang of Four |
| 104 | Clean Code | R.C. Martin |

| ID | Student |
|----|---------|
| 25008 | Abraham Harish |
| 25009 | Fida Fathima |
| 25010 | Rahul Menon |

---

## 💡 OOP Concepts Demonstrated

- **Encapsulation** — each class manages its own state via getters/setters
- **Abstraction** — `Library` exposes high-level operations (`issueBook`, `returnBook`) hiding internal list logic
- **Composition** — `BorrowRecord` composes `Book` and references a `Student` by ID
- **Stream API** — used throughout for filtering and aggregating collections
- **Event-Driven Programming** — all GUI interactions handled via Swing listeners

---

## 📐 Fine Policy

| Condition | Fine |
|-----------|------|
| Returned on or before due date | ₹0.00 |
| Returned after due date | ₹10.00 × days overdue |

Due date is set to **7 days** from the borrow date.

---

## 👩‍💻 Author

**Gauthami V** — [@vgauthami007-wq](https://github.com/vgauthami007-wq)  
Built as a Java OOP Case Study at Amrita School of Engineering.

---

<p align="center">Made with ☕ Java and a lot of <code>ArrayList</code>s</p>

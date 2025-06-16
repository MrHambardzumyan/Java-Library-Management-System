import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

class Library {
    static List<Book> books = new ArrayList<>();
    static List<User> users = new ArrayList<>();

    // Book
    class Book {
        String title;
        String author;
        String ISBN;
        Boolean available = true;

        Book(String title, String author, String ISBN) {
            this.title = title;
            this.author = author;
            this.ISBN = ISBN;
        }

        void updateAvailability() {
            this.available = !(this.available);
        }

        Boolean getAvailability() {
            return this.available;
        }

        // Getter functions

        String getTitle() {
            return this.title;
        }

        String getAuthor() {
            return this.author;
        }
    }

    // User
    class User {
        String name;
        String email;
        float fine;
        List<Borrow> borrows = new ArrayList<>();
        List<Reservation> reservations = new ArrayList<>();

        User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        Book searchBooks(String title, String author) {
            for(int i = 0; i < Library.books.size(); i++) {
                Book currentBook = Library.books.get(i);
                String currentTitle = currentBook.getTitle();
                String currentAuthor = currentBook.getAuthor();

                if(currentTitle.equals(title) && currentAuthor.equals(author)) {
                    return currentBook;
                }
            }

            // If not found
            return null;
        }

        void borrowBook(Book book) {
            // If not available
            if(!(book.getAvailability())) {
                System.out.println("Book not available!");
                return;
            }
            
            book.updateAvailability();
            // Default return date is two weeks later
            Borrow borrow = new Borrow();

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14); 
            borrow.createBorrowRecord(book, calendar.getTime()); 

            this.borrows.add(borrow);
        }

        void returnBook(Book book) {
            book.updateAvailability();

            // Removes from borrows
            for (int i = 0; i < this.borrows.size(); i++) {
                Borrow currentBorrow = this.borrows.get(i);

                if(currentBorrow.getBook() == book) {
                    long currentTime = new Date().getTime();
                    long dueTime = currentBorrow.getReturnDate().getTime();

                    // If passed return day, adds fines
                    if (currentTime > dueTime) {
                        float finePerDayLate = 1.0f; // Fine per day late
                        
                        long lateDays = (currentTime - dueTime) / (1000 * 60 * 60 * 24);
                        float fineAmount = lateDays * finePerDayLate; 

                        this.fine += fineAmount; 
                    }

                    this.borrows.remove(i);
                    break;
                }
            }   
        }

        // Getter functions
        String getName() {
            return this.name;
        }

        String getEmail() {
            return this.email;
        }

        List<Reservation> getReservations() {
            return this.reservations;
        }

        List<Borrow> getBorrows() {
            return this.borrows;
        }

        float getFines() {
            return this.fine;
        }
    }

    // Admin
    class Admin extends User {
        Admin(String name, String email) {
            super(name, email);
        }

        void addBook(Book book) {
            Library.books.add(book);
        }

        void removeBook(Book book) {
            Library.books.remove(book);
        }

        void viewMemberDetails(Member member) {
            System.out.println("Viewing details for Member: " + member.getName());
            System.out.println("Email: " + member.getEmail());
            System.out.println("Current Borrows: " + member.getBorrows().size());
            System.out.println("Current Reservations: " + member.getReservations().size());
            System.out.println("Current Fines: $" + member.getFines());
        }
    }

    class Member extends User {
        Member(String name, String email) {
            super(name, email);
        }

        void register() {
            Library.users.add(this);
        }

        void payFine(float amount) {
            if(this.fine > 0) {
                this.fine -= amount;
            } else {
                System.out.println("You have no fines!");
            }
        }
    }

    class Borrow {
        Book book;
        Date borrowDate;
        Date returnDate;

        Borrow() {
            this.borrowDate = new Date();
        }

        void createBorrowRecord(Book book, Date returnDate) {
            this.book = book;
            this.returnDate = returnDate;
        }

        Book getBook() {
            return this.book;
        }

        Date getBorrowDate() {
            return this.borrowDate;
        }

        Date getReturnDate() {
            return this.returnDate;
        }
    }

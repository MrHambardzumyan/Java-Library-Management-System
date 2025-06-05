import java.io.*;
import java.util.*;

// Loading a book to program (book data sets are in csv format)
public class BookLoader {
    public static List<Library.Book> loadBooks(String filePath, Library library) throws IOException {
        List<Library.Book> bookList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // skip header (Current file has header with title, author, and ISBN)

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 3) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                String isbn = parts[2].trim();

                Library.Book book = library.new Book(title, author, isbn);
                bookList.add(book);
            }
        }
        reader.close();
        return bookList;
    }
}

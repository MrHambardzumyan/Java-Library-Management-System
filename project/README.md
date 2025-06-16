# Library Management System

A simple Java/Swing application backed by SQLite for managing a library.  
Features:
- **Login/Registration** with SHA-256 password hashing
- **Admin interface**: search/add books, view book list, add/delete users
- **Member interface**: search/borrow/return books, view book list, pay fines
- **Bulk CSV import** of books (Title,Author,ISBN)

---

## Prerequisites

1. **Java Development Kit** (JDK 8 or later)  
   Available at https://adoptopenjdk.net/ or https://jdk.java.net/

2. **SQLite JDBC driver** JAR  
   Download e.g. `sqlite-jdbc-3.49.1.0.jar` from  
   https://github.com/xerial/sqlite-jdbc/releases  
   Place it in your project’s `lib/` folder.



##To run code
Compile all src code by running in terminal:

mkdir bin
javac -cp "lib\sqlite-jdbc-3.49.1.0.jar" -d bin `
src\com\library\dao\DatabaseManager.java `
src\com\library\model\*.java `
src\com\library\service\*.java `
src\com\library\ui\*.java `
src\com\library\util\ImportCsv.java


Run <Main> in LoginFrame.java or use command:
java -cp "bin:lib/sqlite-jdbc-3.49.1.0.jar" com.library.ui.LoginFrame

-replace : with ; for windows users

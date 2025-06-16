// src/com/library/util/ImportCsv.java
package com.library.util;

import com.library.dao.DatabaseManager;
import com.library.service.LibraryService;

public class ImportCsv {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: ImportCsv <path-to-csv>");
            System.exit(1);
        }

        String csvPath = args[0];
        try {
            DatabaseManager.connect();

            // Import csv file for book list initially
            int added = LibraryService.importBooksFromCSV(csvPath);
            System.out.println("Imported " + added + " books from " + csvPath);

        } catch (Exception e) {
            System.err.println("Import failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}

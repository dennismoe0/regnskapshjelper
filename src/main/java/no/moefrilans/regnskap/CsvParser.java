package no.moefrilans.regnskap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvParser {

    public static void main(String[] args) {
        // Should be changed so input decides path
        String filePath = "eksempel.csv";
        String line;
        String delimiter = ";";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Stops if empty line, probably needs to handle edge cases (sudden empty line)
            while((line = br.readLine()) != null) {
                String[] values = line.split(delimiter); // Splits by delimiter
                // print for visual testing without test class atm.
                for (String value : values) {
                    System.out.println(value + " ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}


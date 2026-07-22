package no.moefrilans.regnskap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

public class CsvParser {

  private static final Locale NO_LOCALE = Locale.of("nb", "NO");

  /**
   * Takes in a String in NO date format "25.06.2001" and formats it to a LocalDate object. In
   * nb/NO.
   *
   * @param stringToParse A string, from a CSV row, e.g. "25.06.2001", to split up and parse.
   * @return LocalDate variable in correct Norwegian format.
   */
  public LocalDate LocalDateFromNorwegianString(String stringToParse) {
    String[] dateParts = stringToParse.split("\\.");
    int day = Integer.parseInt(dateParts[0]);
    int month = Integer.parseInt(dateParts[1]);
    int year = Integer.parseInt(dateParts[2]);

    return LocalDate.of(year, month, day);
  }

  /**
   * Takes in a string and parses it into a standardized BigDecimal.
   *
   * @param stringToParse E.g. "2.001,23", specifically from a DNB csv.
   * @return BigDecimal to be used in value operations. E.g. "2001.23"
   */
  public BigDecimal BigDecimalFromStringDNB(String stringToParse) {
    // Need to take in the string, use "." as thousands splitter, and "," as decimal.
    // E.g. => "2.010,56", but also just "100,21" or "0,23"
    // BigDecimal value = new BigDecimal("2000.23");
    // Needs to split by . making 2.000 => 2000 .
    // Then needs to split by , and adding it onto it with a period.
    // 2000 + decimal e.g. 23 => 2000.23 which can be used
    // STRING! If we use a double, double value = 2003.23.
    // =>  BigDecimal value = BigDecimal.valueOf(2000.23);

    // Need to handle edge cases

    // Split by period and comma
    String[] numberParts = stringToParse.split("[.,]");
    // E.g. "2.000,23"
    String parsed;
    if (numberParts.length == 3) {
      parsed = numberParts[0] + numberParts[1] + "." + numberParts[2];
    } else if (numberParts.length == 2) {
      parsed = numberParts[0] + "." + numberParts[1];
    } else {
      throw new IllegalArgumentException("Ugyldig beløpsformat: " + stringToParse);
    }
    return new BigDecimal(parsed);
  }

  public static void main(String[] args) {
    // Should be changed so input decides path
    String filePath = "data/guttasas_ekte_konto_utskrift.txt";
    String line;
    String delimiter = ";";

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      // Stops if empty line, probably needs to handle edge cases (sudden empty line)
      while ((line = br.readLine()) != null) {
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


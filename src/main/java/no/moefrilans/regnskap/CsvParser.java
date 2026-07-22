package no.moefrilans.regnskap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
   * Takes in a string and parses it into a standardized BigDecimal. Should have more edge case
   * coverage, but that's for later.
   *
   * @param stringToParse E.g. "2.001,23", specifically from a DNB csv.
   * @return BigDecimal to be used in value operations. E.g. "2001.23"
   */
  public BigDecimal BigDecimalFromStringDNB(String stringToParse) {
    if (stringToParse.isEmpty()) {
      return new BigDecimal("0");
    }
    String string1 = stringToParse.replace(".", "");
    String result = string1.replace(",", ".");
    System.out.print(result);
    return new BigDecimal(result);
  }

//  public Transaction ParsedCsv(String csvPath) {
//  }

  public List<String> readLines(String csvPath) throws IOException {
    List<String> lines;
    lines = Files.readAllLines(Path.of(csvPath), StandardCharsets.ISO_8859_1);
    if (lines.isEmpty()) {
      System.out.println("Read found no lines, empty list returned.");
      return lines;
    }
    System.out.println("File read completed.");
    return lines;
  }

  public DnbSections dnbSectionSplitter(List<String> lines) {
    return new DnbSections(Collections.singletonList(""), Collections.singletonList(""));
  }

  /**
   * Finds the index of the transaction header row in a DNB CSV export.
   *
   * <p>The header row is identified by the marker "Arkivref", which appears only in that row.
   * Everything above it belongs to the account summary, everything below is transactions.
   *
   * @param lines all lines from the CSV file
   * @return the zero-based index of the transaction header row
   * @throws IllegalArgumentException if no header row is found
   */
  public int findTransactionHeaderIndex(List<String> lines) throws IllegalArgumentException {
    // Go through entire list
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).contains("Arkivref")) {
        return i;
      }
    }
    throw new IllegalArgumentException("Fant ingen transaksjonsheader i fila");
  }

  // MUST STRIP AWAY QUOTATION
  public static void main(String[] args) {
    // Should be changed so input decides path
    String filePath = "data/guttasas_ekte_konto_utskrift.txt";
    String line;
    String delimiter = ";";

    try (BufferedReader br = new BufferedReader(
        new FileReader(filePath, StandardCharsets.ISO_8859_1))) {
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


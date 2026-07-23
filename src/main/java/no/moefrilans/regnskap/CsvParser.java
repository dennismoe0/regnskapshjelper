package no.moefrilans.regnskap;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
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
    String string1 = stringToParse.replace("-", "");
    String string2 = string1.replace(".", "");
    String result = string2.replace(",", ".");
    return new BigDecimal(result);
  }

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

  /**
   * Finds the index of the transaction header row in a DNB CSV export.
   *
   * <p>The header row is identified by the marker "Arkivref", which appears only in that row.
   * Everything above it belongs to the account summary, everything below is transactions.
   *
   * @param lines  all lines from the CSV file
   * @param marker header string to stop at
   * @return the zero-based index of the transaction header row
   * @throws IllegalArgumentException if no header row is found
   */
  public int findTransactionHeaderIndex(List<String> lines, String marker)
      throws IllegalArgumentException {
    // Go through entire list
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).contains(marker)) {
        return i;
      }
    }
    throw new IllegalArgumentException("Fant ingen transaksjonsheader i fila");
  }

  /**
   * Splits a DNB export list into its two sections: the account summary and the transactions by
   * index.
   *
   * <p>The transaction header row marks the boundary. Lines above it are the summary, lines below
   * it are transaction rows. The header row itself is excluded from both.
   *
   * @param lines all lines from the CSV file
   * @return the two sections as raw, unparsed lines
   * @throws IllegalArgumentException if the file has no transaction header row
   */
  public DnbSections dnbSectionSplitter(List<String> lines) {
    int index = findTransactionHeaderIndex(lines, "\"Arkivref.\"");

    List<String> overviewSection = lines.subList(0, index);
    List<String> transactionSection = lines.subList(index + 1, lines.size());
    return new DnbSections(overviewSection, transactionSection);
  }

  /**
   * Removes quotation marks '"' and splits lines into individual strings by the delimiter ';'.
   *
   * @param line String to be stripped and split.
   * @return An array containing the new Strings.
   */
  public String[] splitAndCleanSectionLines(String line) {
    // Each line from e.g. the Transaction section is a string containing an entire transaction
    // Delimiter = ";" , we need to split by that AND remove the quotations in front and back.
    return (line.replace("\"", "")).split(
        ";", -1); // Should remove all quotation marks and split into array
  }

  public Transaction parseTransactionLine(String[] transaction) {
    // Create the transactions, parseTransactions does the removal of quotations and splits into fields
    // => Everything should be ready as strings to be parsed (BigDecimal etc) and inputted into a Transaction
    LocalDate dateRegistered = LocalDateFromNorwegianString(transaction[0]);
    String explanatoryText = transaction[1];
    String status = transaction[2];
    String transactionType = transaction[3];
    LocalDate interestDate = LocalDateFromNorwegianString(transaction[4]);
    BigDecimal moneyOut = BigDecimalFromStringDNB(transaction[5]);
    BigDecimal moneyIn = BigDecimalFromStringDNB(transaction[6]);
    String referenceTextArchive = transaction[7];
    String referenceText = transaction[8];
    return new Transaction(dateRegistered, explanatoryText, status, transactionType, interestDate,
        moneyOut,
        moneyIn, referenceTextArchive, referenceText);
  }

  public List<Transaction> parseTransactions(List<String> transactionLines) {
    List<Transaction> result = new ArrayList<>();
    for (String line : transactionLines) {
      String[] fields = splitAndCleanSectionLines(line);
      result.add(parseTransactionLine(fields));
    }
    return result;
  }

  public Summary parseSummary(List<String> summaryLines) {
    String[] accountRow = splitAndCleanSectionLines(summaryLines.get(1));
    String[] totalsRow = splitAndCleanSectionLines(summaryLines.get(3));

    String accountNumber = accountRow[0];
    String accountName = accountRow[1];
    BigDecimal openingBalance = BigDecimalFromStringDNB(totalsRow[0]);
    BigDecimal moneyInTotal = BigDecimalFromStringDNB(totalsRow[1]);
    BigDecimal moneyOutTotal = BigDecimalFromStringDNB(totalsRow[2]);
    BigDecimal available = BigDecimalFromStringDNB(totalsRow[3]);

    return new Summary(accountNumber, accountName, openingBalance,
        moneyInTotal, moneyOutTotal, available);
  }


  public static void main(String[] args) throws IOException {

    //String path = args[0]; // Filepath
    String path = "data/guttasas_ekte_konto_utskrift.txt"; // Filepath

    CsvParser parser = new CsvParser();

    List<String> lines = parser.readLines(path);
    DnbSections sections = parser.dnbSectionSplitter(lines);
    Summary summary = parser.parseSummary(sections.summary());
    List<Transaction> transactions = parser.parseTransactions(sections.transactions());
    System.out.println(summary);
    System.out.println(transactions);
  }
}


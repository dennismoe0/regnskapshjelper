package no.moefrilans.regnskap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CsvParserTest {

  private CsvParser parser;
  private List<String> lines;
  private DnbSections sections;
  private String marker;


  @BeforeEach
  void setUp() {
    // Arrange
    parser = new CsvParser();
    lines = List.of(
        "\"Konto\";\"Kontonavn\"",
        "\"1234.56.78903\";\"EKSEMPEL AS\"",
        "\"Bokfort dato\";\"Status\";\"Arkivref.\";\"Referanse\"",
        "\"02.01.2026\";\"B\";\"111111111\";\"222222\"");
    sections = parser.dnbSectionSplitter(lines);
    marker = "\"Arkivref.\"";
  }

  @Test
  public void testLocalDateFromNorwegianString() {
    LocalDate result = parser.LocalDateFromNorwegianString("25.06.2001");

    assertEquals(LocalDate.of(2001, 6, 25), result);
  }

  @Test
  public void testNormalDnbNumber() {
    // Arrange done
    // BigDecimal expected = new BigDecimal("2000.20");
    // Act
    BigDecimal result = parser.BigDecimalFromStringDNB("2.000,20");
    // Assert
    assertEquals("2000.20", result.toPlainString());
  }

  @Test
  public void testShortDnbNumber() {
    // Arrange done
    // Act
    BigDecimal result = parser.BigDecimalFromStringDNB("200,20");
    // Assert
    assertEquals("200.20", result.toPlainString());
  }

  @Test
  public void testLongDnbNumber() {
    // Arrange
    // Act
    BigDecimal result = parser.BigDecimalFromStringDNB("12.500.000,21");
    // Assert
    assertEquals("12500000.21", result.toPlainString());
  }

  @Test
  public void testFileReadDnb() throws IOException {
    List<String> result = parser.readLines("src/test/resources/test.csv");
    assertEquals(2, result.size());
    assertTrue(result.get(0).startsWith("\"Bokf\u00F8rt dato\""));
    assertTrue(result.get(1).contains("\"65,34"));
  }

  @Test
  void findsDnbTransactionHeaderIndex() {

    assertEquals(2, parser.findTransactionHeaderIndex(lines, marker));
  }

  @Test
  void throwsWhenNoDnbTransactionHeader() {
    List<String> linesShort = List.of("\"Konto\";\"Kontonavn\"");

    assertThrows(IllegalArgumentException.class,
        () -> parser.findTransactionHeaderIndex(linesShort, marker));
  }

  @Test
  void sectionSplitterDnb() {
    // Record with two different items
    DnbSections sections = parser.dnbSectionSplitter(lines);

    assertTrue(sections.summary().get(0).contains("Konto"));
    assertTrue(sections.transactions().get(0).startsWith("\"02.01.2026\""));
  }

  @Test
  void splitAndCleanSectionLinesTest() {

  }
}
package no.moefrilans.regnskap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class CsvParserTest {

  @Test
  public void testLocalDateFromNorwegianString() {

  }

  // This doesnt cover all types
  @Test
  public void testBigDecimalFromStringDNB() {
    // Arrange
    CsvParser parser = new CsvParser();
    // BigDecimal expected = new BigDecimal("2000.20");
    // Act
    BigDecimal result = parser.BigDecimalFromStringDNB("2.000,20");
    // Assert
    assertEquals("2000.20", result.toPlainString());
  }
}
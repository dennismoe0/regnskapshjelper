package no.moefrilans.regnskap;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Record for the DNB csv export rows.
 *
 * @param dateRegistered       Date payment was registered on the account. E.g. "25.06.2001"
 * @param explanatoryText      Usually the origin, e.g. "Vipps Mobilepay AS", but can be a
 *                             description.
 * @param status               Status of payment. Not completely sure, but e.g. "B" => "Betalt",
 *                             meaning paid.
 * @param transactionType      Type of transaction. E.g. "Overføring innland" or "Omkostninger" or
 *                             "Giro".
 * @param interestDate         Date the transaction is completed / approved. E.g. "25.06.2001"
 * @param moneyOut             Negative number. Only for outgoing transactions, e.g. "-2.010,23".
 * @param moneyIn              Positive number. Only for incoming transactions, e.g. "4.2020,00".
 * @param referenceTextArchive Archive reference, probably DNB's own reference number, transaction
 *                             ID type thing.
 * @param referenceText        User reference, e.g. "Betaling for tjeneste"
 */
public record Transaction(LocalDate dateRegistered,
                          String explanatoryText,
                          String status,
                          String transactionType,
                          LocalDate interestDate,
                          // Use .add(), or .multiply() for BigDecimal. It does math correctly compared to double.
                          // BigDecimal is constructed from String, works perfectly here. .compareTo() == 0, not .equals() (1.0 != 1.00)
                          BigDecimal moneyOut,
                          BigDecimal moneyIn,
                          String referenceTextArchive,
                          String referenceText) {

}

package no.moefrilans.regnskap;

import java.math.BigDecimal;

public record Summary(String accountNumber, String accountName, BigDecimal openingBalance,
                      BigDecimal moneyInTotal,
                      BigDecimal moneyOutTotal, BigDecimal currentBalance) {

}

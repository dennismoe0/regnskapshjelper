package no.moefrilans.regnskap;

import java.util.List;

/**
 * Section record of a csv file, specifically designed for a DNB export format. Seperates the first
 * section: overview, with the second section: all transactions. Doesn't need the specific name
 * DnbSections, but this project isn't too abstract so it's fine.
 *
 * @param summary      The list with the overview strings.
 * @param transactions The list with the transaction strings.
 */
public record DnbSections(List<String> summary, List<String> transactions) {

}

# regnskapshjelper

Lite CLI-verktøy som leser CSV-eksport fra nettbanken og lager en kategorisert regnskapsoversikt.

**Skrevet for hånd, uten KI.** Jeg bruker KI-verktøy aktivt i jobben, og skriver dette prosjektet manuelt for å holde Java-håndverket ved like. Brukes til regnskapet i Guttas AS.

## Status

Under arbeid. Bygges i fire steg:

- [ ] **1 — Innlesing:** les CSV, parse dato og beløp, bygg `Transaksjon`-objekter
- [ ] **2 — Kategorisering:** regelbasert matching på tekst, summer per kategori
- [ ] **3 — Rapport:** månedsoversikt, sortering, formatert utskrift
- [ ] **4 — Testing:** JUnit-tester for parseren, `--help`, robust feilhåndtering

## Kjør

```bash
mvn compile
mvn exec:java -Dexec.args="data/eksempel.csv"
mvn test
```

## Notater

- Beløp håndteres med `BigDecimal`, aldri `double` — flyttall og penger hører ikke sammen.
- `data/` inneholder kun anonymiserte eksempeldata. Ekte kontoutskrifter committes aldri.
- Java 21, Maven, JUnit 5.

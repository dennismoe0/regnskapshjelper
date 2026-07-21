# regnskapshjelper

CLI-verktøy som leser en CSV-eksport fra nettbanken og skriver ut en kategorisert regnskapsoversikt i terminalen.

**Skrevet for hånd, uten KI.** Jeg bruker KI-verktøy aktivt i jobben, og skriver dette prosjektet manuelt for å holde Java-håndverket ved like. Brukes til regnskapet i Guttas AS.

Java 21 · Maven · JUnit 5

---

## Hva programmet gjør

Inn: én CSV-fil fra nettbanken.
Ut: en rapport i terminalen som svarer på fire spørsmål —

1. Hva har jeg brukt penger på, gruppert i kategorier?
2. Hvor mye inn og ut, per måned?
3. Hva var den største enkeltutgiften?
4. Hvilke rader klarte ikke programmet å lese?

Ingen database, ingen GUI, ingen nettverk. Én fil inn, én rapport ut.

## Datakontrakt (inn)

Semikolonseparert CSV med header-rad:

```
dato;beskrivelse;beløp
2026-07-01;VIPPS*KAFFEBAR;-49.00
2026-07-02;LØNN GUTTAS AS;12500.00
```

- `dato` — ISO-format (`yyyy-MM-dd`)
- `beskrivelse` — fritekst fra banken, varierende store/små bokstaver
- `beløp` — negativt = utgift, positivt = inntekt. **Må tåle både punktum og komma som desimalskille** (norske banker eksporterer ofte `-49,00`)

## Slik ser resultatet ut (målbilde)

```
Regnskapsoversikt — data/eksempel.csv
3 transaksjoner lest, 0 rader hoppet over

KATEGORI          ANTALL        SUM
Lønn                   1   12 500,00
Mat                    2     -361,50

Inn i alt                  12 500,00
Ut i alt                     -361,50
Netto                      12 138,50

PER MÅNED            INN         UT      NETTO
2026-07        12 500,00    -361,50  12 138,50

Største enkeltutgift: 2026-07-03  REMA 1000 TRONDHEIM  -312,50
```

Kolonnene skal være justert. Beløp med to desimaler og mellomrom som tusenskille.

---

## Steg 1 — Innlesing

**Skal gjøre:** ta filsti som kommandolinjeargument, lese fila, hoppe over header-raden, og bygge en liste av transaksjoner.

Krav:
- Filsti kommer fra `args`, ikke hardkodet
- Beløp lagres som `BigDecimal` — aldri `double` (flyttall og penger hører ikke sammen)
- Dato lagres som `LocalDate`
- Ugyldig rad (feil antall felt, ubrukelig dato/beløp) skal **ikke** krasje programmet: hopp over raden, tell den, og skriv en advarsel
- Manglende fil eller tomt `args` gir en tydelig feilmelding, ikke stacktrace

**Ferdig når:** programmet skriver ut «N transaksjoner lest, M rader hoppet over» og kan liste alle transaksjonene.

## Steg 2 — Kategorisering

**Skal gjøre:** gi hver transaksjon én kategori basert på beskrivelsen.

Krav:
- Kategoriene er en `enum` — start med: MAT, TRANSPORT, LØNN, ABONNEMENT, GEBYR, OVERFØRING, ANNET
- Regler er nøkkelord-matching mot beskrivelsen, **ikke** case-sensitiv («rema», «REMA» og «Rema» treffer likt)
- Reglene bor i koden (en `Map` eller `switch`), ikke i en konfigfil — det er unødvendig kompleksitet for v1
- Ingen treff → ANNET
- Første treff vinner (dokumentér rekkefølgen hvis den betyr noe)

**Ferdig når:** hver transaksjon har en kategori, og du kan skrive ut transaksjon + kategori.

## Steg 3 — Rapport

**Skal gjøre:** regne ut og skrive ut målbildet over.

Krav:
- Sum og antall per kategori, sortert etter absoluttbeløp synkende
- Inn (positive), ut (negative) og netto totalt
- Månedsoversikt gruppert på `YearMonth`, kronologisk
- Største enkeltutgift, med dato og beskrivelse
- Justerte kolonner (`String.format`), to desimaler, tusenskille

**Ferdig når:** rapporten fra eksempelfila ser ut som målbildet.

**Ekstraøvelse (5 min, verdt det):** skriv kategorisummeringen både med en vanlig løkke og med streams. Behold den du liker best. Å kunne forklare forskjellen er et sannsynlig intervjuspørsmål.

## Steg 4 — Test og hardening

**Skal gjøre:** gjøre programmet trygt å kjøre for noen andre enn deg.

Krav:
- JUnit 5-tester som dekker: gyldig rad, rad med for få felt, beløp med komma, beløp med punktum, tom fil, fil som ikke finnes, tomt `args`
- `--help` skriver bruksanvisning
- Exit-kode 0 ved suksess, 1 ved brukerfeil (manglende fil, ugyldig argument)
- Ingen `printStackTrace()` som eneste feilhåndtering

**Ferdig når:** `mvn test` er grønn, og programmet oppfører seg forutsigbart på alle feilstiene over.

---

## Utenfor omfang (v1)

Eksport av rapporten til fil · konfigurerbare regler · støtte for flere bankformater · GUI · flervalutastøtte. Noteres her for å holde v1 ferdigstillbar — ikke fordi de er uinteressante.

## Prosjektregler

- Ingen KI. Tillatt: offisiell Java-dokumentasjon.
- Committ ofte og rotete — historikken er beviset på håndskrevet, inkrementelt arbeid.
- Ekte bankdata committes aldri. `data/` inneholder kun anonymiserte eksempler.
- Repoet gjøres offentlig når steg 1–4 er ferdige.

## Kjør

```bash
mvn compile exec:java -Dexec.mainClass="no.moefrilans.regnskap.App" -Dexec.args="data/eksempel.csv"
mvn test
```

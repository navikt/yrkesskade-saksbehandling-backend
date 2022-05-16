# yrkesskade-saksbehandling-backend

## Lokal kjøring
Applikasjonen kan startes ved å kjøre `YrkesskadeSaksbehandlingStarter`.

Opprett lokal database med standard oppsett
```sql
CREATE DATABASE yrkesskade_saksbehandling;
```

Legg til VM argumentene `-DYRKESSKADE_SAKSBEHANDLING_DB_USERNAME=<brukernavn>`og `-DYRKESSKADE_SAKSBEHANDLING_DB_PASSWORD=<passord>`

Spring profilen `local` må aktiveres med VM argument `-Dspring.profiles.active=local` eller ved hjelp av Active profile feltet i IntelliJ.

Database og Kafka må kjøre før applikasjonen kan startes

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan:
- stilles som issues her på GitHub
- stilles til yrkesskade@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #team-yrkesskade.
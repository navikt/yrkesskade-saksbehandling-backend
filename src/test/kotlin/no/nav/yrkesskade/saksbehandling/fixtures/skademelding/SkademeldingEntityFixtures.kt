package no.nav.yrkesskade.saksbehandling.fixtures.skademelding

import no.nav.yrkesskade.saksbehandling.skademelding.model.SkademeldingEntity
import no.nav.yrkesskade.skademelding.model.Tidstype
import java.time.Instant

fun enkelSkademeldingEntity() = SkademeldingEntity(
    skademeldingId = 1,
    innmelderIdentitetsnummer = "012334567891",
    innmelderPaaVegneAv = "123456789",
    innmelderVirksomhetsnavn = "Test Firma",
    innmelderRolle = "virksomhetsrepresentant",
    skadelidtIdentitetsnummer = "12345678910",
    skadelidtRolletype = "arbeidstaker",
    dekningsOrganisasjonsnummer = "123456789",
    dekningVirksomhetsnavn = "Test Firma",
    skadeAlvorlighetsgrad = "alvorlig",
    hendelseHvorSkjeddeUlykken = "hjemme",
    hendelseAarsakUlykke = "uflaks",
    hendelseBakgrunnUlykke = "bornThisWay",
    hendelseNaarSkjeddeUlykken = "iTiden",
    hendelseUtfyllendeBeskrivelse = "Det var en helt vanlig dag og alt kræsjet",
    hendelseTidstype = Tidstype.tidspunkt,
    hendelseTidspunkt = Instant.now(),
    hendelsePeriodeFra = null,
    hendelsePeriodeTil = null,
    hendelseStedsbeskrivelse = "iFarligOmraade",
    skadedeDeler = null,
    stillingstitler = null,
    virksomhetsAdressse = null,
    ulykkessted = null,
    bostedsAdressse = null
)
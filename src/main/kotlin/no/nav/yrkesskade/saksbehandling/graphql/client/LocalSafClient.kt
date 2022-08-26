package no.nav.yrkesskade.saksbehandling.graphql.client

import com.expediagroup.graphql.generated.Journalpost
import com.expediagroup.graphql.generated.enums.*
import com.expediagroup.graphql.generated.journalpost.Bruker
import com.expediagroup.graphql.generated.journalpost.DokumentInfo
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * En lokal versjon for test av SAF
 *
 */
@Component
@Qualifier("safClient")
@Profile("local")
class LocalSafClient(
    @Value("\${saf.graphql.url}") private val safGraphqlUrl: String,
    @Value("\${spring.application.name}") applicationName: String,
    private val tokenUtil: TokenUtil
) : ISafClient {

    private val journalposter = listOf(
        genererJournalpost("1")
    )

    override fun hentOppdatertJournalpost(journalpostId: String): Journalpost.Result? {
        val oppdatertJournalpost = Journalpost.Result(journalposter.first())
        return oppdatertJournalpost
    }

    private fun genererJournalpost(journapostId: String) =
            com.expediagroup.graphql.generated.journalpost.Journalpost(
                journalpostId = journapostId,
                journalstatus = Journalstatus.JOURNALFOERT,
                journalposttype = Journalposttype.I,
                journalfoerendeEnhet = "Test",
                behandlingstema = "YRK",
                tema = Tema.UKJ,
                kanal = Kanal.UKJENT,
                datoOpprettet = LocalDateTime.now(),
                bruker = Bruker(id = "012345678910", type = BrukerIdType.FNR),
                dokumenter = listOf(
                    DokumentInfo(dokumentInfoId = "1", "Tannlegeerklæring", "NAV-XXX-XX-01")
                )
            )
}
package no.nav.yrkesskade.saksbehandling.graphql.client.saf

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.expediagroup.graphql.generated.Journalpost
import kotlinx.coroutines.runBlocking
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component


/**
 * Klient for å hente oppdatert journalpost fra saf (Sak og arkiv fasade)
 */
@Component
@Qualifier("safClient")
@Profile("!local")
class SafClient(
    @Value("\${api.client.saf.url.graphql}") private val safGraphqlUrl: String,
    @Value("\${spring.application.name}") val applicationName: String,
    private val tokenUtil: TokenUtil
) : ISafClient {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    private val client = GraphQLWebClient(url = safGraphqlUrl)

    override fun hentOppdatertJournalpost(journalpostId: String): Journalpost.Result? {
        val token = tokenUtil.getAppAccessMaskinTilMaskinTokenWithSafScope()
        logger.info("Hentet token for Saf")
        val journalpostQuery = Journalpost(Journalpost.Variables(journalpostId))

        logger.info("Henter oppdatert journalpost for id $journalpostId på url $safGraphqlUrl")
        val oppdatertJournalpost: Journalpost.Result?
        runBlocking {
            val response: GraphQLClientResponse<Journalpost.Result> = client.execute(journalpostQuery) {
                headers {
                    it.add(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    it.add("Nav-Callid", MDC.get(MDCConstants.MDC_CALL_ID))
                    it.add("Nav-Consumer-Id", applicationName)
                }
            }
            oppdatertJournalpost = response.data
            if (!response.errors.isNullOrEmpty()) {
                secureLogger.error("Responsen fra SAF inneholder feil: ${response.errors}")
                throw RuntimeException("Responsen fra SAF inneholder feil! Se securelogs")
            }
        }
        return oppdatertJournalpost
    }
}
package no.nav.yrkesskade.saksbehandling.client.saf

import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.util.*
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Component
@Profile("!local")
class SafRestClient(@Value("\${saf.rest.url}") private val safRestUrl: String,
@Value("\${spring.application.name}") val applicationName: String,
private val tokenUtil: TokenUtil) : ISafRestClient
{
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    val client = ClientBuilder.newClient()

    /**
     * Henter et dokumentdata for ett dokument i en journalpost med dokumentinfoId.
     *
     * Returneres som BASE64 av dataene.
     *
     */
    override fun hentDokument(journalpostId: String, dokumentinfoId: String): String {
        val token = tokenUtil.getAppAccessTokenWithSafScope()
        logger.info("Hentet token for Saf")

        logger.info("Henter dokument for journalpost for id $journalpostId og med dokumentinfoId $dokumentinfoId på url $safRestUrl")

        val response: Response = client.target(safRestUrl)
            .path("/rest/hentdokument")
            .path(journalpostId)
            .path(dokumentinfoId)
            .path("ARKIV") // denne er ARKIV for PDF-dokumenter eller potensielt ORIGINAL for XML-Søknader
            .request(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .header("Nav-Callid", MDC.get(MDCConstants.MDC_CALL_ID))
            .header("Nav-Consumer-Id", applicationName)
            .get()

        if (response.statusInfo.family != Response.Status.Family.SUCCESSFUL) {
            throw SafException("Kunne ikke hente dokument. Status: ${response.status}}")
        }

        logger.info("Hentet dokument for journalpost for id $journalpostId og med dokumentinfoId $dokumentinfoId")
        val bytes = response.readEntity(ByteArray::class.java)
        return Base64.getEncoder().encodeToString(bytes)
    }
}
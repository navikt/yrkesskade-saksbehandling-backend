package no.nav.yrkesskade.saksbehandling.graphql.client.pdl

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.expediagroup.graphql.generated.HentAdresse
import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.HentPerson
import com.expediagroup.graphql.generated.Long
import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.expediagroup.graphql.generated.hentperson.Person
import kotlinx.coroutines.runBlocking
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.ws.rs.core.HttpHeaders

/**
 * Klient for å hente ut personinfo fra PDL (Persondataløsningen)
 */
@Component
@Profile("!local")
class PdlClient(
    @Value("\${pdl.graphql.url}") private val pdlGraphqlUrl: String,
    private val tokenUtil: TokenUtil
) : IPdlClient {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    private val client = GraphQLWebClient(url = pdlGraphqlUrl)

    override fun hentAktorId(fodselsnummer: String): String? {
        val identerResult = hentIdenter(fodselsnummer, listOf(IdentGruppe.AKTORID), false)
        return extractAktorId(identerResult)
    }

    /**
     * @param ident fødselsnummer eller aktørId
     */
    override fun hentIdenter(ident: String, grupper: List<IdentGruppe>, historikk: Boolean): HentIdenter.Result? {
        val token = tokenUtil.getAppAccessOnBehalfOfTokenWithPdlScope()
        val hentIdenterQuery = HentIdenter(
            HentIdenter.Variables(
                ident = ident,
                grupper = grupper,
                historikk = historikk
            )
        )

        val identerResult: HentIdenter.Result?
        runBlocking {
            logger.info("Henter identer (grupper=$grupper, historikk=$historikk) fra PDL på url $pdlGraphqlUrl")
            secureLogger.info("Henter identer (grupper=$grupper, historikk=$historikk) fra PDL for person med ident $ident på url $pdlGraphqlUrl")
            val response: GraphQLClientResponse<HentIdenter.Result> = client.execute(hentIdenterQuery) {
                headers {
                    it.add(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    it.add("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
                }
            }
            identerResult = response.data
            logger.info("Returnerte fra PDL, se securelogs for detaljer")
            secureLogger.info("Returnerte fra PDL, data: " + response.data)
            if (!response.errors.isNullOrEmpty()) {
                logger.error("Responsen fra PDL inneholder feil! Se securelogs")
                secureLogger.error("Responsen fra PDL inneholder feil: ${response.errors}")
                throw RuntimeException("Responsen fra PDL inneholder feil! Se securelogs")
            }
        }

        return identerResult
    }

    override fun hentPerson(foedselsnummer: String): Person? {
        return hentPersonResult(foedselsnummer)?.hentPerson
    }

    private fun extractAktorId(identerResult: HentIdenter.Result?): String? {
        return identerResult?.hentIdenter?.identer?.stream()
            ?.filter { identInfo -> identInfo.gruppe == IdentGruppe.AKTORID }?.findFirst()?.get()?.ident
    }

    private fun hentPersonResult(fodselsnummer: String): HentPerson.Result? {
        val token = tokenUtil.getAppAccessOnBehalfOfTokenWithPdlScope()
        val hentPersonQuery = HentPerson(HentPerson.Variables(fodselsnummer))

        val personResult: HentPerson.Result?
        runBlocking {
            logger.info("Henter person fra PDL på url $pdlGraphqlUrl")
            secureLogger.info("Henter person fra PDL for person med fnr $fodselsnummer på url $pdlGraphqlUrl")
            val response: GraphQLClientResponse<HentPerson.Result> = client.execute(hentPersonQuery) {
                headers {
                    it.add(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    it.add("Tema", "YRK")
                    it.add("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
                }
            }
            personResult = response.data
            logger.info("Returnerte fra PDL, se securelogs for detaljer")
            secureLogger.info("Returnerte fra PDL, data: " + response.data)
            if (!response.errors.isNullOrEmpty()) {
                logger.error("Responsen fra PDL inneholder feil! Se securelogs")
                secureLogger.error("Responsen fra PDL inneholder feil: ${response.errors}")
                throw RuntimeException("Responsen fra PDL inneholder feil! Se securelogs")
            }
        }

        return personResult
    }

    private fun hentAdresse(matrikkelId: Long): HentAdresse.Result? {
        val token = tokenUtil.getAppAccessOnBehalfOfTokenWithPdlScope()
        val hentAdresseQuery = HentAdresse(HentAdresse.Variables(matrikkelId))

        val adresseResult: HentAdresse.Result?
        runBlocking {
            logger.info("Henter adresse fra PDL på url $pdlGraphqlUrl")
            secureLogger.info("Henter adresse fra PDL for matrikkelId $matrikkelId på url $pdlGraphqlUrl")
            val response: GraphQLClientResponse<HentAdresse.Result> = client.execute(hentAdresseQuery) {
                headers {
                    it.add(HttpHeaders.AUTHORIZATION, "Bearer $token")
                    it.add("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
                }
            }
            adresseResult = response.data
            logger.info("Returnerte fra PDL, se securelogs for detaljer")
            secureLogger.info("Returnerte fra PDL, data: " + response.data)
            if (!response.errors.isNullOrEmpty()) {
                logger.error("Responsen fra PDL inneholder feil! Se securelogs")
                secureLogger.error("Responsen fra PDL inneholder feil: ${response.errors}")
                throw RuntimeException("Responsen fra PDL inneholder feil! Se securelogs")
            }
        }

        return adresseResult
    }

}



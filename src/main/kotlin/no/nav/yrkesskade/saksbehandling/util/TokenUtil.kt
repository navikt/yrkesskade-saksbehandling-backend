package no.nav.yrkesskade.saksbehandling.util

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties

import org.springframework.stereotype.Service

@Service
class TokenUtil(
    private val clientConfigurationProperties: ClientConfigurationProperties,
    private val oAuth2AccessTokenService: OAuth2AccessTokenService
) {
    fun getAppAccessMaskinTilMaskinTokenWithSafScope() = getTokenForRegistration("saf-maskintilmaskin")

    fun getAppAccessOnBehalfOfTokenWithSafScope() = getTokenForRegistration("saf-onbehalfof")

    fun getAppAccessTokenWithDokarkivScope() = getTokenForRegistration("dokarkiv-maskintilmaskin")

    fun getAppAccessOnBehalfOfTokenWithPdlScope() = getTokenForRegistration("pdl-onbehalfof")

    fun getAppAccessWithKodeverkScope() = getTokenForRegistration("kodeverk-maskintilmaskin")

    fun getAppAccessTokenWithOppgaveScope() = getTokenForRegistration("oppgave-maskintilmaskin")

    private fun getTokenForRegistration(registration: String): String {
        val clientProperties = clientConfigurationProperties.registration[registration]
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.accessToken
    }
}
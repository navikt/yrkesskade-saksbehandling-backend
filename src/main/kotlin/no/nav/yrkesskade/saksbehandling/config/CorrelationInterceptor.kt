package no.nav.yrkesskade.saksbehandling.config

import no.nav.yrkesskade.saksbehandling.util.MDCConstants.MDC_CALL_ID
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Implementation of [HandlerInterceptor] that attaches a correlation ID to SLF4J's MDC
 * (Mapped Diagnostic Context) when a REST endpoint is called. If one is provided as a header
 * then it will be used; otherwise, a new one will be created.
 * Before the response is returned, the correlation ID will be added as a response header and removed from the MDC.
 */
@Component
class CorrelationInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest,
                           response: HttpServletResponse,
                           handler: Any): Boolean {
        MDC.clear()
        val correlationId = getCorrelationIdFromHeaderOrCreateNew(request)
        MDC.put(MDC_CALL_ID, correlationId)
        return true
    }

    override fun afterCompletion(request: HttpServletRequest,
                                 response: HttpServletResponse,
                                 handler: Any,
                                 ex: Exception?) {
        response.addHeader(MDC_CALL_ID, MDC.get(MDC_CALL_ID))
    }

    private fun getCorrelationIdFromHeaderOrCreateNew(request: HttpServletRequest): String {
        return request.getHeader(MDC_CALL_ID) ?: UUID.randomUUID().toString()
    }

}
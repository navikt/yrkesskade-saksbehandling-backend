package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.graphql.common.AbstractGraphQLException

class BehandlingException : AbstractGraphQLException {
    constructor(message: String) : super(message)
    constructor(message: String, additionalParams: Map<String, Object>?) : super(message, additionalParams)
}
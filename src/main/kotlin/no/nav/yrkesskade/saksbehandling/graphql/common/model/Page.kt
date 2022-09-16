package no.nav.yrkesskade.saksbehandling.graphql.common.model

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class Page(val page: Int, val size: Int) {
    fun tilPageRequest(): Pageable {
        return PageRequest.of(page, size, Sort.by(
            Sort.Direction.ASC, "opprettetTidspunkt"))
    }
}
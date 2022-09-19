package no.nav.yrkesskade.saksbehandling.graphql.common.model

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class Page(
    val page: Int,
    val size: Int,
    val sortField: SortFieldType?,
    val sortDirection: SortDirectionType?
    ) {
    fun tilPageRequest(): Pageable {
        val direction = if (sortDirection != null) Sort.Direction.fromString(sortDirection.name) else Sort.Direction.ASC
        val field = sortField?.name ?: "opprettetTidspunkt"
        return PageRequest.of(
            page, size, Sort.by(direction, field)
        )
    }
}
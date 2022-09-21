package no.nav.yrkesskade.saksbehandling.graphql.common

import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting.Sortering
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.lang.reflect.Method


class PageRequestBuilder<T>(
    val page: Page,
    val sortering: Sortering<T>?
) {

    inline fun <reified T> build(): PageRequest {
        checkNotNull(page)

        var direction = Sort.Direction.ASC
        var field = hentNavnFraType(T::class.java.name)
        if (sortering != null) {
            direction = if (sortering!!.sortDirectionType() != null) Sort.Direction.fromString(sortering!!.sortDirectionType()!!.name) else Sort.Direction.ASC
            field = sortering!!.sortFieldType()?.thisName() ?: hentNavnFraType(T::class.java.name)
        }

        return PageRequest.of(page!!.page, page!!.size, direction, field)
    }

    fun hentNavnFraType(enumClassName: String): String {
        val cls = Class.forName(enumClassName)

        val m: Method = cls.getMethod("name")
        return m.invoke(cls.enumConstants.get(0)).toString()
    }
}
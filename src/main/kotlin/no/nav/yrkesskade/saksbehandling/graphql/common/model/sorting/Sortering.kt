package no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting

interface Sortering<T> {
    fun sortFieldType(): SortFieldType?
    fun sortDirectionType(): SortDirectionType?
}
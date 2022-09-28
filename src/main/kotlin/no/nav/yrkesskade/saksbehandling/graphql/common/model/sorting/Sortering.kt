package no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting

interface Sortering<T : SortFieldType> {
    fun sortFieldTypes(): List<T>?
    fun sortDirectionType(): SortDirectionType?
}
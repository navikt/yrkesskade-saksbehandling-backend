package no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting

data class BehandlingSortering(val sortField: BehandlingSortFieldType?, val sortDirection: SortDirectionType?) :
    Sortering<BehandlingSortFieldType> {
    override fun sortFieldType() = sortField

    override fun sortDirectionType() = sortDirection
}
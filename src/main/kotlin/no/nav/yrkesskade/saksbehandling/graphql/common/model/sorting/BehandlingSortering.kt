package no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting

data class BehandlingSortering(val sortFields: List<BehandlingSortFieldType>?, val sortDirection: SortDirectionType?) :
    Sortering<BehandlingSortFieldType> {
    override fun sortFieldTypes() = sortFields

    override fun sortDirectionType() = sortDirection
}
package no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting

/**
 * En enum som sørger for at graphql bruker gyldige JPA kolonnenavn
 */
enum class BehandlingSortFieldType : SortFieldType {
    opprettetTidspunkt,
    behandlingsfrist;

    override fun thisName(): String {
        return this.name;
    }
}
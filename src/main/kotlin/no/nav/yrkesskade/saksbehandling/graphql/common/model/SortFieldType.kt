package no.nav.yrkesskade.saksbehandling.graphql.common.model

/**
 * En enum som sørger for at graphql bruker gyldige JPA kolonnenavn
 */
enum class SortFieldType {
    opprettetTidspunkt,
    behandlingsfrist
}
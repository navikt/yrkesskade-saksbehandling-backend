package no.nav.yrkesskade.saksbehandling.model.dto

import com.expediagroup.graphql.generated.enums.BrukerIdType
import no.nav.yrkesskade.saksbehandling.model.BehandlingsresultatEntity
import no.nav.yrkesskade.saksbehandling.model.SakEntity
import java.time.Instant

data class BehandlingDto(
    val behandlingId: Long,
    val tema: String,
    val brukerId: String,
    val brukerIdType: BrukerIdType,
    val behandlendeEnhet: String?,
    val saksbehandlingsansvarligIdent: String? = null,
    val behandlingstype: String,
    val status: String,
    val behandlingsfrist: Instant,
    val journalpostId: String,
    val dokumentkategori: String,
    val systemreferanse: String,
    val framdriftsstatus: String,
    val opprettetTidspunkt: Instant,
    val opprettetAv: String,
    val endretAv: String? = null,
    val sak: SakEntity? = null, // TODO: sak dto
    val behandlingResultater: List<BehandlingsresultatEntity> // TODO: liste med behandlingsresultater dtos
) {

}
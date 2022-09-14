import com.expediagroup.graphql.generated.enums.BrukerIdType
import no.nav.yrkesskade.saksbehandling.model.*
import java.time.Instant

data class DetaljertBehandling(
    val behandlingId: Long,
    val tema: String,
    val brukerId: String,
    val brukerIdType: BrukerIdType,
    val behandlendeEnhet: String?,
    val saksbehandlingsansvarligIdent: String? = null,
    val behandlingstype: Behandlingstype,
    val status: Behandlingsstatus,
    val behandlingsfrist: Instant,
    val journalpostId: String,
    val utgaaendeJournalpostId: String? = null,
    val dokumentkategori: String,
    val systemreferanse: String,
    val framdriftsstatus: Framdriftsstatus,
    val opprettetTidspunkt: Instant,
    val opprettetAv: String,
    val endretAv: String? = null,
    val sak: SakEntity? = null,
    val behandlingResultater: List<BehandlingsresultatEntity>,
    val dokumenter: List<DokumentInfo>
)
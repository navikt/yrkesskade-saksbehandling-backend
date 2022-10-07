package no.nav.yrkesskade.saksbehandling.model

import com.expediagroup.graphql.generated.enums.BrukerIdType
import net.datafaker.Faker
import no.nav.yrkesskade.saksbehandling.util.FristFerdigstillelseTimeManager
import java.util.*
import java.util.concurrent.TimeUnit

class BehandlingEntityFactory {

    companion object {
        private val faker = Faker()

        fun enBehandling(saksbehandlingsansvarligIdent: String? = null): BehandlingEntity {
            val opprettetTidspunkt = ettTidspunkt()
            val behandlingstype = enBehandlingstype()
            val behandlingsfrist = FristFerdigstillelseTimeManager.nesteGyldigeFristForFerdigstillelseInstant(behandlingstype, opprettetTidspunkt)
            val status = enStatus()
            val endretTispunkt = if (status != Behandlingsstatus.IKKE_PAABEGYNT) {
                ettTidspunkt()
            } else null

            return BehandlingEntity(
                sak = SakEntityFactory.enSak(),
                behandlingId = enBehandlingId(),
                endretAv = enBrukerIdent(),
                status = status,
                opprettetAv = enSystemId(),
                opprettetTidspunkt = opprettetTidspunkt,
                tema = ettTema(),
                behandlingstype = behandlingstype,
                dokumentkategori = enDokumentkategori(),
                systemreferanse = enSystemreferanse(),
                journalpostId = enJournalpostId(),
                framdriftsstatus = enFramdriftsstatus(),
                brukerIdType = enBrukerIdType(),
                brukerId = enBrukerId(),
                behandlingResultater = emptyList(),
                behandlingsfrist = behandlingsfrist,
                behandlendeEnhet = enBehandledeEnhet(),
                saksbehandlingsansvarligIdent = saksbehandlingsansvarligIdent ?: enBrukerIdent(),
                utgaaendeJournalpostId = null,
                endretTidspunkt = endretTispunkt
            )
        }

        fun enBehandlingId() = faker.number().numberBetween(0, 1000).toLong()
        fun enBrukerIdent() = faker.regexify("[A-Z][0-9]{5}")
        fun ettTidspunkt() = faker.date().past(50, TimeUnit.DAYS).toInstant()
        fun enStatus() = faker.options().nextElement(Behandlingsstatus.values())
        fun enSystemId() = faker.options().option("yrkesskade-saksbehandling-backend")
        fun ettTema() = "YRK"
        fun enBehandlingstype() = faker.options().nextElement(Behandlingstype.values())
        fun enDokumentkategori() = faker.options().option("tannlegeerklaering")
        fun enSystemreferanse() = UUID.randomUUID().toString()
        fun enJournalpostId() = faker.number().numberBetween(0, 1000).toString()
        fun enFramdriftsstatus() = faker.options().nextElement(Framdriftsstatus.values())
        fun enBrukerIdType() = faker.options().nextElement(BrukerIdType.values())
        fun enBrukerId(): String {
            return faker.options().option("27051726079","07064640911","02079543589","06015341791","28060075788","14084047776","22010166121","11012741423","21026422989","10040723493","08099429993","08088202415","02011002512","06054030914","23107913688","04053422263","26124241218","10031404318","08013448893","30076095902","23104947603","25067511754","21092328756","27107190037","18011417007","10069535610","25041331618","12060443090","22055134719","21062131052","06118312005","18124421493","19123515121","25113601011","11050090126","03067936034","22090641305","17095232108","10090813582","05098939158","14120924819","19021123024","10116218602","10059402817","04038498296","08102209452","16085520672","10093823791","02060721702","09048894051")
        }
        fun enBehandledeEnhet() = faker.regexify("[0-9]{4}")

        fun BehandlingEntity.medBehandlingId(behandlingId: Long) = this.copy(behandlingId = behandlingId)
        fun BehandlingEntity.medUtgaaendeJournalpostId(journalpostId: String) = this.copy(utgaaendeJournalpostId = journalpostId)
        fun BehandlingEntity.medSak(sak: SakEntity) = this.copy(sak = sak)
        fun BehandlingEntity.medStatus(status: Behandlingsstatus) = this.copy(status = status)
        fun BehandlingEntity.medJournalpostId(journalpostId: String) = this.copy(journalpostId = journalpostId)
        fun BehandlingEntity.medBehandlingstype(behandlingstype: Behandlingstype) = this.copy(behandlingstype = behandlingstype)
        fun BehandlingEntity.medFramdriftsstatus(framdriftsstatus: Framdriftsstatus) = this.copy(framdriftsstatus = framdriftsstatus)
        fun BehandlingEntity.medSaksbehandlingsansvarligIdent(saksbehandlingsansvarligIdent: String?) = this.copy(saksbehandlingsansvarligIdent = saksbehandlingsansvarligIdent)
    }
}
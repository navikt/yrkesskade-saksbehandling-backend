package no.nav.yrkesskade.saksbehandling.model

import net.datafaker.Faker
import java.util.concurrent.TimeUnit

class SakEntityFactory {

    companion object {
        val faker = Faker()

        fun enSak(): SakEntity {
            return SakEntity(
                sakId = enSakId(),
                behandlinger = emptyList(),
                tema = "YRK",
                opprettetAv = enBrukerIdent(),
                sakstype = enSakstype(),
                saksstatus = enSaksstatus(),
                opprettetTidspunkt = ettTidspunkt(),
                brukerIdentifikator = enNorskIdentifikator()
            )
        }

        fun enSakId() = faker.number().numberBetween(0, 1000).toLong()

        fun enBrukerIdent() = faker.regexify("[A-Z][0-9]{5}")

        fun enSakstype() = faker.options().nextElement(Sakstype.values())

        fun enSaksstatus() = faker.options().nextElement(Saksstatus.values())

        fun enNorskIdentifikator(): String {
            return faker.options().option("27051726079","07064640911","02079543589","06015341791","28060075788","14084047776","22010166121","11012741423","21026422989","10040723493","08099429993","08088202415","02011002512","06054030914","23107913688","04053422263","26124241218","10031404318","08013448893","30076095902","23104947603","25067511754","21092328756","27107190037","18011417007","10069535610","25041331618","12060443090","22055134719","21062131052","06118312005","18124421493","19123515121","25113601011","11050090126","03067936034","22090641305","17095232108","10090813582","05098939158","14120924819","19021123024","10116218602","10059402817","04038498296","08102209452","16085520672","10093823791","02060721702","09048894051")
        }

        fun ettTidspunkt() =  faker.date().past(50, TimeUnit.DAYS).toInstant()

        fun SakEntity.medSakId(sakId: Long) = this.copy(sakId = sakId)

        fun SakEntity.medBehandlinger(behandlinger: List<BehandlingEntity>) = this.copy(behandlinger = behandlinger)

        fun SakEntity.medSakstype(sakstype: Sakstype) = this.copy(sakstype = sakstype)

        fun SakEntity.medSaksstatus(saksstatus: Saksstatus) = this.copy(saksstatus = saksstatus)
    }
}
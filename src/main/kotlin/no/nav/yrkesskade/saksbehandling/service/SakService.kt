package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.model.SakEntity
import no.nav.yrkesskade.saksbehandling.model.Saksstatus
import no.nav.yrkesskade.saksbehandling.repository.SakRepository
import org.springframework.stereotype.Service

@Service
class SakService(private val sakRepository: SakRepository) {

    fun hentSak(brukerId: String): List<SakEntity> {
        return sakRepository.findByBrukerIdentifikatorAndSaksstatus(brukerId, Saksstatus.AAPEN)
    }
}

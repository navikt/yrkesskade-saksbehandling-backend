package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.client.oppgave.Oppgave
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.BehandlingsoverfoeringLogEntity
import no.nav.yrkesskade.saksbehandling.repository.BehandlingsoverfoeringLogRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BehandlingsoverfoeringLogService(
    private val behandlingsoverfoeringLogRepository: BehandlingsoverfoeringLogRepository,
    private val autentisertBruker: AutentisertBruker
) {

    fun overfoerBehandling(behandling: BehandlingEntity, oppgave: Oppgave, avviksbegrunnelse: String): BehandlingsoverfoeringLogEntity {

        val behandlingsoverfoeringLogEntity = BehandlingsoverfoeringLogEntity(
            overfoeringLogId = 0L,
            overfoertTidspunkt = Instant.now(),
            overfoertAv = autentisertBruker.preferredUsername,
            journalpostId = behandling.journalpostId,
            behandlingstype = behandling.behandlingstype,
            avviksbegrunnelse = avviksbegrunnelse,
            overfoertTilSystem = oppgave.behandlesAvApplikasjon ?: "ukjent"
        )

        return behandlingsoverfoeringLogRepository.save(behandlingsoverfoeringLogEntity)
    }
}
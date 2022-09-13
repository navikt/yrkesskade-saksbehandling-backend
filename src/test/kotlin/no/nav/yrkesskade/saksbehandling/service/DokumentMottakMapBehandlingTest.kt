package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.journalpost.DokumentInfo
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.dokumentInfoListeMedTannlegeerklaering
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Test

class DokumentMottakMapBehandlingTest {

    @Test
    fun `skal utlede dokumentkategori`() {
        val dokumentkategori = Dokumentmottak.utledDokumentkategori(dokumentInfoListeMedTannlegeerklaering())
        assertThat(dokumentkategori).isEqualTo("tannlegeerklaering")
    }

    @Test
    fun `returner blank når dokumentkategori ikke kan utledes`() {
        val dokumentInfos = listOf(DokumentInfo("id-99", "Dokument hvor kategori ikke kan utledes", "Brevkode xyz"))
        val dokumentkategori = Dokumentmottak.utledDokumentkategori(dokumentInfos)
        assertThat(dokumentkategori).isEqualTo(" ")
    }

}
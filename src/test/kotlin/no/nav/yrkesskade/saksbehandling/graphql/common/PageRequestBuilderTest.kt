package no.nav.yrkesskade.saksbehandling.graphql.common

import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting.BehandlingSortFieldType
import no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting.BehandlingSortering
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort

class PageRequestBuilderTest {

    @Test
    fun `bygg page request`() {
        val page = PageRequestBuilder(Page(1, 10), BehandlingSortering(null, null)).build<BehandlingSortFieldType>()
        assertThat(page).isNotNull
        assertThat(page.pageSize).isEqualTo(10)
        assertThat(page.pageNumber).isEqualTo(1)
        assertThat(page.sort.getOrderFor(BehandlingSortFieldType.opprettetTidspunkt.name)?.direction).isEqualTo(Sort.Direction.ASC)
    }
}
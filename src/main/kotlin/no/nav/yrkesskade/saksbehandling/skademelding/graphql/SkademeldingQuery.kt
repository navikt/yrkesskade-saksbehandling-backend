package no.nav.yrkesskade.saksbehandling.skademelding.graphql

import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.skademelding.graphql.model.SkademeldingPage
import no.nav.yrkesskade.saksbehandling.skademelding.repository.SkademeldingDao
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class SkademeldingQuery(private val skademeldingDao: SkademeldingDao) : GraphQLQueryResolver {

    fun hentAlleSkademeldinger() = skademeldingDao.findAll()

    fun hentSkademeldingerForSkadelidt(skadelidtIdentitetsnummer: String) = skademeldingDao.findBySkadelidtIdentitetsnummer(skadelidtIdentitetsnummer)

    fun hentSkademeldinger(skademeldingPage: SkademeldingPage) =
        skademeldingDao.findAll(PageRequest.of(skademeldingPage.page, skademeldingPage.size))

    fun antallSkademeldinger() = skademeldingDao.count()
}
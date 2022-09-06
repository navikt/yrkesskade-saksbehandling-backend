package no.nav.yrkesskade.saksbehandling.graphql.client.pdl

import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.enums.AdressebeskyttelseGradering
import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.expediagroup.graphql.generated.enums.KjoennType
import com.expediagroup.graphql.generated.hentperson.*
import net.datafaker.Faker
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Qualifier("pdlClient")
@Profile("local")
class LocalPdlClient : IPdlClient {
    override fun hentAktorId(fodselsnummer: String): String? {
        TODO("Not yet implemented")
    }

    override fun hentPerson(foedselsnummer: String): Person? {
        val faker = Faker(Random(foedselsnummer.hashCode().toLong()))

        return Person(
            navn = listOf(
                Navn(
                    fornavn = faker.name().firstName(),
                    mellomnavn = null,
                    etternavn = faker.name().lastName()
                )
            ),
            adressebeskyttelse = listOf(
                Adressebeskyttelse(
                    gradering = AdressebeskyttelseGradering.values()[faker.random().nextInt(AdressebeskyttelseGradering.values().size - 1)]
                )
            ),
            bostedsadresse = listOf(
                Bostedsadresse(
                    vegadresse = Vegadresse(
                        matrikkelId = faker.number().positive().toString()
                    )
                )
            ),
            doedsfall = emptyList(),
            kjoenn = listOf(
                Kjoenn(
                    kjoenn = KjoennType.values()[faker.random().nextInt(KjoennType.values().size - 1)]
                )
            )
        )
    }

    override fun hentIdenter(ident: String, grupper: List<IdentGruppe>, historikk: Boolean): HentIdenter.Result? {
        TODO("Not yet implemented")
    }
}
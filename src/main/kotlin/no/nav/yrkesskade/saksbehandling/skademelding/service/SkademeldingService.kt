package no.nav.yrkesskade.saksbehandling.skademelding.service

import no.nav.yrkesskade.model.SkademeldingInnsendtHendelse
import no.nav.yrkesskade.saksbehandling.skademelding.model.*
import no.nav.yrkesskade.saksbehandling.skademelding.repository.SkademeldingDao
import no.nav.yrkesskade.saksbehandling.skademelding.repository.SkadetDelDao
import no.nav.yrkesskade.saksbehandling.skademelding.repository.StillingstittelDao
import no.nav.yrkesskade.skademelding.model.Adresse
import no.nav.yrkesskade.skademelding.model.SkadetDel
import no.nav.yrkesskade.skademelding.model.Ulykkessted
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SkademeldingService(
    private val skademeldingDao: SkademeldingDao,
    private val skadetDelDao: SkadetDelDao,
    private val stillingstittelDao: StillingstittelDao
) {

    @Transactional
    fun lagreSkademelding(skademeldingInnsendtHendelse: SkademeldingInnsendtHendelse): SkademeldingEntity {
        val skademelding = skademeldingInnsendtHendelse.skademelding

        var lagretSkademelding = skademeldingDao.save(mapSkademeldingTilEntitet(skademeldingInnsendtHendelse)).also {
            // lagre relasjoner
            skadetDelDao.saveAll(mapSkadedeDeler(it, skademelding.skade.skadedeDeler))
            stillingstittelDao.saveAll(mapStillingstitler(it, skademelding.skadelidt.dekningsforhold.stillingstittelTilDenSkadelidte))
        }

        return lagretSkademelding
    }

    @Transactional
    fun hentSkademelding(skademeldingId: Long): SkademeldingEntity =
        skademeldingDao.findById(skademeldingId).orElseThrow {
            throw Exception("Skademelding med id ${skademeldingId} finnes ikke")
        }

    private fun mapSkademeldingTilEntitet(skademeldingInnsendtHendelse: SkademeldingInnsendtHendelse): SkademeldingEntity {
        val skademelding = skademeldingInnsendtHendelse.skademelding
        val beriketData = skademeldingInnsendtHendelse.beriketData

        val tilLagring = SkademeldingEntity(
            skademeldingId = -1, // ID er autogenerert av DB
            innmelderIdentitetsnummer = skademelding.innmelder.norskIdentitetsnummer,
            innmelderPaaVegneAv = skademelding.innmelder.paaVegneAv,
            innmelderVirksomhetsnavn = beriketData.innmeldersOrganisasjonsnavn.first,
            innmelderRolle = skademelding.innmelder.innmelderrolle,
            skadelidtIdentitetsnummer = skademelding.skadelidt.norskIdentitetsnummer,
            skadelidtRolletype = skademelding.skadelidt.dekningsforhold.rolletype,
            dekningsOrganisasjonsnummer = skademelding.skadelidt.dekningsforhold.organisasjonsnummer,
            dekningVirksomhetsnavn = skademelding.skadelidt.dekningsforhold.navnPaaVirksomheten.orEmpty(),
            skadeAlvorlighetsgrad = skademelding.skade.alvorlighetsgrad,
            hendelseNaarSkjeddeUlykken = skademelding.hendelsesfakta.naarSkjeddeUlykken,
            hendelseHvorSkjeddeUlykken = skademelding.hendelsesfakta.hvorSkjeddeUlykken,
            hendelseAarsakUlykke = skademelding.hendelsesfakta.aarsakUlykkeTabellAogE.joinToString(","),
            hendelseBakgrunnUlykke = skademelding.hendelsesfakta.bakgrunnsaarsakTabellBogG.joinToString(","),
            hendelseUtfyllendeBeskrivelse = skademelding.hendelsesfakta.utfyllendeBeskrivelse.orEmpty(),
            hendelseTidstype = skademelding.hendelsesfakta.tid.tidstype,
            hendelseTidspunkt = skademelding.hendelsesfakta.tid.tidspunkt?.toInstant(),
            hendelsePeriodeFra = skademelding.hendelsesfakta.tid.periode?.fra,
            hendelsePeriodeTil = skademelding.hendelsesfakta.tid.periode?.til,
            hendelseStedsbeskrivelse = skademelding.hendelsesfakta.stedsbeskrivelseTabellF.orEmpty(),
            skadedeDeler = null,
            stillingstitler = null,
            virksomhetsAdressse = null,
            ulykkessted = null,
            bostedsAdressse = null
        )

        val ulykkessted = mapUlykkessted(tilLagring, skademelding.hendelsesfakta.ulykkessted)
        tilLagring.ulykkessted = ulykkessted

        val virksomhetAdresse =
            mapVirksomhetsAdresse(tilLagring, skademelding.skadelidt.dekningsforhold.virksomhetensAdresse)
        tilLagring.virksomhetsAdressse = virksomhetAdresse

        return tilLagring
    }

    private fun mapUlykkessted(skademelding: SkademeldingEntity, ulykkessted: Ulykkessted): UlykkesstedEntity {
        return UlykkesstedEntity(
            skademeldingId = skademelding.skademeldingId,
            sammeSomVirksomhetsAdressse = ulykkessted.sammeSomVirksomhetensAdresse,
            adresselinje1 = ulykkessted.adresse.adresselinje1,
            adresselinje2 = ulykkessted.adresse.adresselinje2.orEmpty(),
            adresselinje3 = ulykkessted.adresse.adresselinje3.orEmpty(),
            landkode = ulykkessted.adresse.land.orEmpty(),
            skademelding = skademelding
        )

    }

    private fun mapVirksomhetsAdresse(skademelding: SkademeldingEntity, adresse: Adresse?): VirksomhetsAdresseEntity? {
        if (adresse == null) {
            return null;
        }
        return VirksomhetsAdresseEntity(
            skademeldingId = skademelding.skademeldingId,
            adresselinje1 = adresse.adresselinje1,
            adresselinje2 = adresse.adresselinje2,
            adresselinje3 = adresse.adresselinje3,
            landkode = adresse.land,
            skademelding = skademelding
        )
    }

    private fun mapSkadedeDeler(
        skademeldingEntity: SkademeldingEntity, skadedeDeler: List<SkadetDel>
    ): Set<SkadetDelEntity> {
        if (skadedeDeler.isNullOrEmpty()) {
            return emptySet()
        }

        return skadedeDeler.map {
            SkadetDelEntity(
                skadetDelId = SkadetDelId(
                    skademeldingEntity.skademeldingId, it.skadeartTabellC, it.kroppsdelTabellD
                )
            )
        }.toSet()
    }

    private fun mapStillingstitler(
        skademeldingEntity: SkademeldingEntity,
        stillingstitler: List<String>?
    ): Set<SkadelidtStillingEntity> {
        if (stillingstitler.isNullOrEmpty()) {
            return emptySet()
        }

        return stillingstitler.map {
            SkadelidtStillingEntity(
                skadelidtStillingId = SkadelidtStillingId(skademeldingEntity.skademeldingId, it)
            )
        }.toSet()
    }
}
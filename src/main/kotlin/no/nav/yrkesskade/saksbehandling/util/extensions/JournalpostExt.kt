import com.expediagroup.graphql.generated.journalpost.DokumentInfo
import com.expediagroup.graphql.generated.journalpost.Journalpost

fun Journalpost.hentHovedDokument(): DokumentInfo? = dokumenter?.firstOrNull { it!!.brevkode != null }
fun Journalpost.hentHovedDokumentTittel(): String = hentHovedDokument()?.tittel.orEmpty()
fun Journalpost.hentBrevkode(): String  = hentHovedDokument()?.brevkode.orEmpty()

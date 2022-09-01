import java.nio.file.Files
import java.nio.file.Path

fun tannlegeerklaeringVeiledningbrev() : String {
    return Files.readString(Path.of("src/test/resources/brev/tannlegeerklæring.json"))
}
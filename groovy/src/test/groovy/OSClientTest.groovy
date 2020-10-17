import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import org.junit.Test
import sk.ikim23.opensubtitles.client.Config
import sk.ikim23.opensubtitles.client.OSClient
import sk.ikim23.opensubtitles.result.Subtitles

class OSClientTest {
    @Test
    void searchTest() {
        def movieHash = '7d9cd5def91c9432'
        def movieByteSize = 735934464
        def client = new OSClient()

        def result = client.search([[moviehash: movieHash, sublanguageid: 'all', moviebytesize: movieByteSize]])
        assert !result.subtitles.empty
    }

    @Test
    void observableSubtitlesTransformTest() {
        def subtitles = new Subtitles(
                fileName: '27.Dresses.DVDRip.XviD-NeDiVx.CZ.srt',
                language: 'cs',
                addDate: Date.parse("yyyy-MM-dd HH:mm:ss", '2008-04-12 05:56:29'),
                downloadsCount: new Integer('9065'),
                format: 'srt',
                downloadLink: 'http://dl.opensubtitles.org/en/download/src-api/vrf-19b80c55/sid-uvTAcD47Ty-sZf157eiBF,zl3T1/filead/1951853345.gz',
                encoding: 'UTF-8'
        )

        def observable = subtitles.toObservable()

        assert observable.fileName instanceof StringProperty
        assert observable.language instanceof StringProperty
        assert observable.addDate instanceof ObjectProperty<Date>
        assert observable.downloadsCount instanceof IntegerProperty
        assert observable.format instanceof StringProperty
        assert observable.downloadLink instanceof StringProperty
        assert observable.encoding instanceof StringProperty
    }

    @Test
    void configTest() {
        def config = Config.instance

        assert !config.apiUrl.empty
        assert !config.userAgent.empty
        assert !config.languages.empty
    }
}

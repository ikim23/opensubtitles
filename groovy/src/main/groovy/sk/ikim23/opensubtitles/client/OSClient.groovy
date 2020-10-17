package sk.ikim23.opensubtitles.client

import groovy.net.xmlrpc.XMLRPCServerProxy
import sk.ikim23.opensubtitles.result.SearchResult
import sk.ikim23.opensubtitles.result.Subtitles

class OSClient {
    XMLRPCServerProxy server = new XMLRPCServerProxy(Config.instance.apiUrl, true)

    SearchResult search(File movieFile) {
        def movieHash = OSUtils.computeHash(movieFile)
        def langId = Config.instance.checkedLangIds
        def movieByteSize = movieFile.length()
        search([[moviehash: movieHash, sublanguageid: langId, moviebytesize: movieByteSize]])
    }

    SearchResult search(String movieName) {
        def langId = Config.instance.checkedLangIds
        search([[query: movieName, sublanguageid: langId]])
    }

    SearchResult search(List args) {
        try {
            def token = server.LogIn('', '', '', Config.instance.userAgent).token
            def result = server.SearchSubtitles(token, args)
            def searchResult = new SearchResult(status: result.status)
            if (result.status == '200 OK') {
                searchResult.subtitles = result.data.collect {
                    new Subtitles(
                            fileName: it.SubFileName,
                            language: it.ISO639,
                            addDate: Date.parse("yyyy-MM-dd HH:mm:ss", it.SubAddDate),
                            downloadsCount: new Integer(it.SubDownloadsCnt),
                            format: it.SubFormat,
                            downloadLink: it.SubDownloadLink,
                            encoding: it.SubEncoding
                    )
                }
            }
            searchResult
        } catch (Exception e) {
            e.printStackTrace()
            new SearchResult(status: 'failed to search')
        }
    }
}

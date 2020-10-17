package sk.ikim23.opensubtitles.result

import groovy.transform.TupleConstructor

@TupleConstructor
class SearchResult {
    List subtitles
    String status
}

package sk.ikim23.opensubtitles.client

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class Config {
    static Config instance = new Config()
    private static String FILE_NAME = 'OpenSubtitlesDownloader.config'
    private Map data

    private Config() {
        def home = System.getProperty('user.home')
        def file = new File(home, FILE_NAME)
        if (file.exists()) {
            data = new JsonSlurper().parse(file)
        } else {
            data = defaultConfig()
            file.write(new JsonBuilder(data).toPrettyString())
        }
    }

    String getApiUrl() {
        data?.get('url')
    }

    String getUserAgent() {
        data?.get('userAgent')
    }

    List getLanguages() {
        data?.getOrDefault('languages', [])
    }

    List getLangNames() {
        languages.collect { it.name }
    }

    String getCheckedLangIds() {
        languages.findAll({ it.checked })
                .collect({ it.id })
                .join(',')
    }

    List getCheckedLangIndexes() {
        def indexes = []
        languages.eachWithIndex { entry, idx ->
            if (entry.checked) {
                indexes << idx
            }
        }
        indexes
    }

    void setCheckedLangIndexes(List checkedItems) {
        languages.each {
            it.checked = checkedItems.contains(it.name)
        }
        saveConfig()
    }

    private void saveConfig() {
        def home = System.getProperty('user.home')
        def file = new File(home, FILE_NAME)
        file.write(new JsonBuilder(data).toPrettyString())
    }

    private Map defaultConfig() {
        [
            'url'      : 'http://api.opensubtitles.org/xml-rpc',
            'userAgent': 'TemporaryUserAgent',
            'languages': [
                ['id': 'alb', 'name': 'Albanian', 'checked': false],
                ['id': 'ara', 'name': 'Arabic', 'checked': false],
                ['id': 'ast', 'name': 'Asturian', 'checked': false],
                ['id': 'baq', 'name': 'Basque', 'checked': false],
                ['id': 'bre', 'name': 'Breton', 'checked': false],
                ['id': 'bul', 'name': 'Bulgarian', 'checked': false],
                ['id': 'cat', 'name': 'Catalan', 'checked': false],
                ['id': 'chi', 'name': 'Chinese (simplified)', 'checked': false],
                ['id': 'zht', 'name': 'Chinese (traditional)', 'checked': false],
                ['id': 'hrv', 'name': 'Croatian', 'checked': false],
                ['id': 'cze', 'name': 'Czech', 'checked': false],
                ['id': 'dan', 'name': 'Danish', 'checked': false],
                ['id': 'dut', 'name': 'Dutch', 'checked': false],
                ['id': 'eng', 'name': 'English', 'checked': true],
                ['id': 'epo', 'name': 'Esperanto', 'checked': false],
                ['id': 'est', 'name': 'Estonian', 'checked': false],
                ['id': 'fin', 'name': 'Finnish', 'checked': false],
                ['id': 'fre', 'name': 'French', 'checked': false],
                ['id': 'glg', 'name': 'Galician', 'checked': false],
                ['id': 'geo', 'name': 'Georgian', 'checked': false],
                ['id': 'ger', 'name': 'German', 'checked': false],
                ['id': 'ell', 'name': 'Greek', 'checked': false],
                ['id': 'heb', 'name': 'Hebrew', 'checked': false],
                ['id': 'hin', 'name': 'Hindi', 'checked': false],
                ['id': 'hun', 'name': 'Hungarian', 'checked': false],
                ['id': 'ice', 'name': 'Icelandic', 'checked': false],
                ['id': 'ind', 'name': 'Indonesian', 'checked': false],
                ['id': 'ita', 'name': 'Italian', 'checked': false],
                ['id': 'jpn', 'name': 'Japanese', 'checked': false],
                ['id': 'khm', 'name': 'Khmer', 'checked': false],
                ['id': 'kor', 'name': 'Korean', 'checked': false],
                ['id': 'mac', 'name': 'Macedonian', 'checked': false],
                ['id': 'may', 'name': 'Malay', 'checked': false],
                ['id': 'nor', 'name': 'Norwegian', 'checked': false],
                ['id': 'oci', 'name': 'Occitan', 'checked': false],
                ['id': 'per', 'name': 'Persian', 'checked': false],
                ['id': 'pol', 'name': 'Polish', 'checked': false],
                ['id': 'por', 'name': 'Portuguese', 'checked': false],
                ['id': 'pob', 'name': 'Portuguese (BR)', 'checked': false],
                ['id': 'rum', 'name': 'Romanian', 'checked': false],
                ['id': 'rus', 'name': 'Russian', 'checked': false],
                ['id': 'scc', 'name': 'Serbian', 'checked': false],
                ['id': 'sin', 'name': 'Sinhalese', 'checked': false],
                ['id': 'slo', 'name': 'Slovak', 'checked': false],
                ['id': 'slv', 'name': 'Slovenian', 'checked': false],
                ['id': 'spa', 'name': 'Spanish', 'checked': false],
                ['id': 'swe', 'name': 'Swedish', 'checked': false],
                ['id': 'tgl', 'name': 'Tagalog', 'checked': false],
                ['id': 'tha', 'name': 'Thai', 'checked': false],
                ['id': 'tur', 'name': 'Turkish', 'checked': false],
                ['id': 'ukr', 'name': 'Ukrainian', 'checked': false],
                ['id': 'uzb', 'name': 'Uzbek', 'checked': false],
                ['id': 'vie', 'name': 'Vietnamese', 'checked': false]
            ]
        ]
    }
}

package sk.ikim23.opensubtitles.result

import groovy.transform.Canonical
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import sk.ikim23.opensubtitles.controls.SimpleDateProperty

@Canonical
class Subtitles {
    String fileName
    String language
    Integer downloadsCount
    Date addDate
    String format
    String downloadLink
    String encoding

    Expando toObservable() {
        def expando = new Expando()
        properties.each {
            switch (it.value) {
                case String:
                    expando."${it.key}" = new SimpleStringProperty(it.value)
                    break
                case Integer:
                    expando."${it.key}" = new SimpleIntegerProperty(it.value)
                    break
                case Date:
                    expando."${it.key}" = new SimpleDateProperty(it.value)
                    break
            }
        }
        expando
    }
}

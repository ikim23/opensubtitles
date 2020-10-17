package sk.ikim23.opensubtitles.controls

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty

class SimpleDateProperty extends ObjectProperty<Date> {
    @Delegate
    private SimpleObjectProperty<Date> dateValue

    SimpleDateProperty(Date value) {
        dateValue = new SimpleObjectProperty<>(value)
        dateValue.value.metaClass.toString = { -> delegate.format('yyyy') }
    }

    @Override
    String toString() {
        dateValue.value.format('yyyy')
    }
}

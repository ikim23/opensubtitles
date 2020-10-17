package sk.ikim23.opensubtitles.controls

import javafx.scene.control.TableCell

class DateCell extends TableCell<Object, Date> {
    private final String format

    DateCell(String format){
        this.format = format
    }

    @Override
    protected void updateItem(Date item, boolean empty) {
        super.updateItem(item, empty)
        setText(!empty ? item?.format(format) : null)
    }
}


package sk.ikim23.opensubtitles.controls

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.TableCell

class ButtonCell<T> extends TableCell<T, Boolean> {
    static interface OnClickListener<T> {
        void onClick(ActionEvent event, T value)
    }

    private final Button button

    ButtonCell(OnClickListener listener) {
        this.button = new Button('Download')
        this.button.onAction = { event ->
            def index = delegate.index
            T item = delegate.tableView.items[index]
            listener.onClick(event, item)
        }
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty)
        setGraphic(!empty ? button : null)
    }

}

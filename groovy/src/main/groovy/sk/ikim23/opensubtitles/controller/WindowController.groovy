package sk.ikim23.opensubtitles.controller

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.FileChooser
import org.controlsfx.control.CheckComboBox
import sk.ikim23.opensubtitles.Downloader
import sk.ikim23.opensubtitles.client.Config
import sk.ikim23.opensubtitles.controls.ButtonCell
import sk.ikim23.opensubtitles.client.OSClient
import sk.ikim23.opensubtitles.controls.DateCell
import sk.ikim23.opensubtitles.result.Subtitles

import java.awt.Desktop

class WindowController {
    @FXML TextField filePath
    @FXML Button chooseFile
    @FXML TextField movieName
    @FXML Button searchByName
    @FXML CheckComboBox language
    @FXML TableView table
    @FXML Label placeholder
    @FXML ProgressIndicator progress
    @FXML Label status
    @FXML BooleanProperty isDownloading = new SimpleBooleanProperty()
    File file

    @FXML
    void initialize() {
        chooseFile.disableProperty().bind(isDownloading)
        searchByName.disableProperty().bind(Bindings.or(isDownloading, movieName.textProperty().isEmpty()))
        placeholder.visibleProperty().bind(Bindings.not(isDownloading))
        progress.visibleProperty().bind(isDownloading)
        initLanguages()
        initTable()
        chooseFile.onAction = { event ->
            def stage = ((Control) event.source).scene.window
            def fileChooser = new FileChooser()
            fileChooser.title = 'Choose File'
            fileChooser.initialDirectory = file?.parentFile
            fileChooser.extensionFilters.addAll(
                    new FileChooser.ExtensionFilter('Video Files', '*.avi','*.mkv', '*.mp4'),
                    new FileChooser.ExtensionFilter('All Files', '*.*')
            )
            file = fileChooser.showOpenDialog(stage)
            if (file) {
                filePath.text = file
                withSearchStatus {
                    def client = new OSClient()
                    def result = client.search(file)
                    Platform.runLater {
                        table.items.addAll(result.subtitles)
                    }
                }
            }
        }
        def actionSearchByName = {
            if (!movieName.text?.empty) {
                withSearchStatus {
                    def client = new OSClient()
                    def result = client.search(movieName.text)
                    Platform.runLater {
                        table.items.addAll(result.subtitles)
                    }
                }
            }
        }
        searchByName.onAction = actionSearchByName
        movieName.onAction = actionSearchByName
    }

    void withSearchStatus(Closure searchClosure) {
        isDownloading.value = true
        status.text = 'searching'
        table.items.clear()
        Thread.start {
            searchClosure()
            Platform.runLater {
                status.text = "${table.items.size()} subtitles found"
                isDownloading.value = false
            }
        }
    }

    void downloadSubtitles(ActionEvent event, Subtitles subtitles) {
        def stage = ((Control) event.source).scene.window
        def fileChooser = new FileChooser()
        fileChooser.title = 'Save Subtitles'
        fileChooser.initialDirectory = file?.parentFile
        fileChooser.initialFileName = subtitles.fileName
        fileChooser.extensionFilters.addAll(
                new FileChooser.ExtensionFilter('Subtitles File', "*.${subtitles.format}"),
                new FileChooser.ExtensionFilter('All Types', '*.*')
        )
        def saveFile = fileChooser.showSaveDialog(stage)
        if (saveFile) {
            isDownloading.set(true)
            status.text = 'saving'
            Thread.start {
                Downloader.downloadAndSave(subtitles.downloadLink, subtitles.encoding, saveFile)
                Desktop.desktop.open(saveFile.parentFile)
                Platform.runLater {
                    status.text = 'saved'
                    isDownloading.set(false)
                }
            }
        }
    }

    void initLanguages() {
        language.items.addAll(Config.instance.langNames)
        Config.instance.checkedLangIndexes.each {
            language.checkModel.check(it)
        }
        language.checkModel.checkedItems.addListener({
            Config.instance.checkedLangIndexes = language.checkModel.checkedItems.toList()
        } as ListChangeListener)
    }

    void initTable() {
        def colName = new TableColumn("Name")
        colName.setCellValueFactory(new PropertyValueFactory("fileName"))

        def colLanguage = new TableColumn("Language")
        colLanguage.setCellValueFactory(new PropertyValueFactory("language"))

        def colDownloads = new TableColumn("Downloads")
        colDownloads.setCellValueFactory(new PropertyValueFactory("downloadsCount"))

        def colCreated = new TableColumn("Created")
        colCreated.setCellValueFactory(new PropertyValueFactory("addDate"))
        colCreated.setCellFactory({
            new DateCell("dd.MM.yyyy")
        })

        def colFormat = new TableColumn("Format")
        colFormat.setCellValueFactory(new PropertyValueFactory("format"))

        def colButton = new TableColumn()
        colButton.sortable = false
        colButton.setCellFactory({
            new ButtonCell({ event, subtitles ->
                downloadSubtitles(event, subtitles)
            })
        })

        table.columns.addAll(colName, colLanguage, colDownloads, colCreated, colFormat, colButton)
    }
}

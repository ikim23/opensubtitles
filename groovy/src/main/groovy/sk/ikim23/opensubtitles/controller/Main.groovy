package sk.ikim23.opensubtitles.controller

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class Main extends Application {
    static void main(String[] args) {
        launch(Main.class, args)
    }

    @Override
    void start(Stage stage) {
        def root = FXMLLoader.load(getClass().classLoader.getResource('Window.fxml'))
        def scene = new Scene(root)
        stage.title = 'OpenSubtitles.org Downloader'
        stage.scene = scene
        stage.show()
    }
}

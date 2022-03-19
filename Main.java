/*
 * File: proj6BayyurtWenZhang.Main.java
 * Names: Izge Bayyurt, Muqing Wen, Chloe Zhang
 * Class: CS361
 * Project 6
 * Date: 3/17/2022
 */

package proj6BayyurtWenZhang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

/**
 * Main class initializes the base Application.
 *
 */
public class Main extends Application {
    /**
     * Constructs the base elements on the stage.
     *
     * @param stage the stage on which to build the Application.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
            scene.getStylesheets().add(CodeAreaHighlighter.class.getResource(
                    "java-keywords.css").toExternalForm());
            stage.setTitle("Project 6 Izge, Wen, Chloe");
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(E -> {
                ((Controller) fxmlLoader.getController()).handleExit();
                E.consume();
            });
        }catch(Exception e){
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Fatal Error on Launch");
            dialog.setContentText("This project failed to find the fxml or css file.\n" +
                    "Ensure all files are in the same location.");

            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(ok);

            dialog.showAndWait();
        }
    }

    /**
     * Initializes the Application.
     *
     * @param args args passed on run.
     */
    public static void main(String[] args) {
        launch();
    }
}
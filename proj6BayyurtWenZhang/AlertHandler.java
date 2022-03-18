/*
 * File: proj5BayyurtEnglishTymkiw.AlertHandler.java
 * Names: Izge Bayyurt, Nick English, Dylan Tymkiw
 * Class: CS361
 * Project 5
 * Date: 3/07/2022
 */
package proj6BayyurtWenZhang;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class AlertHandler {

    public AlertHandler(){}

    public void showAlert(String message, String title) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setContentText(message);

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(ok);

        dialog.showAndWait();
    }

}

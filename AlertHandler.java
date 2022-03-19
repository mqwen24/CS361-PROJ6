/*
 * File: proj6BayyurtWenZhang.AlertHandler.java
 * Names: Izge Bayyurt, Muqing Wen, Chloe Zhang
 * Class: CS361
 * Project 6
 * Date: 3/17/2022
 */

package proj6BayyurtWenZhang;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class AlertHandler {

    public AlertHandler(){}

    /*
        Creates a dialog box with custom message and title to display alerts
        @param message: the alert text to show
        @param title: the title of the alert box
     */
    public void showAlert(String message, String title) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setContentText(message);

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(ok);

        dialog.showAndWait();
    }

}

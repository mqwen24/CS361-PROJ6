/*
 * File: proj6BayyurtWenZhang.DialogHelper.java
 * Names: Izge Bayyurt, Muqing Wen, Chloe Zhang
 * Class: CS361
 * Project 6
 * Date: 3/17/2022
 */
package proj6BayyurtWenZhang;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;

/**
 * Contains helper methods for creating dialogs.
 */
public class DialogHelper {
    private final TabHelper tabHelper;
    private final FileController fileHelper;
    private final TabPane tabPane;
    private HashMap<Tab, File> filenameFileMap;
    private HashMap<Tab, Boolean> textHasChangedMap;
    private final FileChooser chooser = new FileChooser();

    public DialogHelper(TabPane tabPane, TabHelper tabHelper,
                        FileController fileHelper, HashMap<Tab, File> filenameFileMap,
                        HashMap<Tab, Boolean> textHasChangedMap){
        this.tabHelper = tabHelper;
        this.fileHelper = fileHelper;
        this.tabPane = tabPane;
        this.filenameFileMap = filenameFileMap;
        this.textHasChangedMap = textHasChangedMap;
    }

    /**
     * Constructs and manages the About Dialog.
     */
    public void aboutDialog(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("About...");
        dialog.setContentText("This is project 6 by Izge, Wen, and Chloe");

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(ok);

        dialog.showAndWait();
    }

    /**
     * Constructs and manages the What's New Dialog.
     */
    public void whatsnewDialog(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("What's New?");
        dialog.setContentText("New Feature: Toggle Comments,Help Menu, Find Menu Item.");
        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(ok);

        dialog.showAndWait();
    }

    /**
     * Builds and manages the Save As Dialog box.  Saves a Tab's contents
     * to a file path.
     *
     * @return true if file is saved, false if process is cancelled
     */
    public boolean saveAsDialog()
    {
        String currentTabTitle = tabHelper.getCurrentTabTitle();
        chooser.setTitle("Save as...");
        chooser.setInitialFileName(currentTabTitle);
        File currentFile = chooser.showSaveDialog(null);
        if (currentFile != null) {
            this.filenameFileMap.put(tabHelper.getCurrentTab(), currentFile);
            boolean saved = fileHelper.saveCurrentFile(currentFile);
            if(saved){
                this.textHasChangedMap.put(tabHelper.getCurrentTab(), false);
                tabHelper.getCurrentTab().setTooltip(new Tooltip(currentFile.toString()));
            }
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Builds and manages the Open Dialog.  Opens a file into a Tab.
     *
     */
    public void openDialog()
    {
        chooser.setTitle("Open file...");
        File openedFile = chooser.showOpenDialog(null);
        if (openedFile != null){
            for(Tab tab:tabPane.getTabs()){
                if(openedFile.equals(filenameFileMap.getOrDefault(tab, null))){
                    tabPane.getSelectionModel().select(tab);
                    return;
                }
            }
            Tab newTab = new Tab(openedFile.getName());
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
            tabHelper.createCodeAreaForTab(newTab);
            fileHelper.writeFileToCodeArea(openedFile);
            // remember path when saving
            filenameFileMap.put(tabHelper.getCurrentTab(), openedFile);
            textHasChangedMap.put(tabHelper.getCurrentTab(), false);
            newTab.setTooltip(new Tooltip(openedFile.toString()));
        }
    }

    /**
     * Constructs the Close Dialog and returns user selection.
     *
     * @return which type of Button was clicked within the dialog.
     */
    public Optional<ButtonType> closeDialog(){
        Alert alert = createConfirmationAlert( String.format(
                        "Do you want to save " + "your progress on %s " +
                        "before closing?", tabHelper.getCurrentTab().getText()));
        return alert.showAndWait();
    }

    /**
     * Constructs the Save Dialog and returns user selection.
     *
     * @return which type of Button was clicked within the dialog.
     */
    public Optional<ButtonType> saveDialog() {
        Alert alert = createConfirmationAlert(
           "Do you want to save your changes before compiling?");
        return alert.showAndWait();
    }

    /**
     * Constructs a confirmation alert box with custom header string
     *
     * @param headerText - the header text to be displayed for the alert
     * @return Alert - the alert object created
     */
    private Alert createConfirmationAlert(String headerText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(headerText);
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no, cancel);
        return alert;
    }

}

/*
 * File: proj5BayyurtEnglishTymkiw.Controller.java
 * Names: Izge Bayyurt, Nick English, Dylan Tymkiw
 * Class: CS361
 * Project 5
 * Date: 3/07/2022
 */

package proj5BayyurtEnglishTymkiw;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * Controller handles ActionEvents for the Application.
 *
 */
public class Controller {

    @FXML
    private TabPane tabPane;
    @FXML
    private MenuItem close;
    @FXML
    private MenuItem save;
    @FXML
    private MenuItem saveAs;
    @FXML
    private Menu edit;
    @FXML
    private Button compile;
    @FXML
    private Button compileAndRun;
    @FXML
    private Button stop;
    @FXML
    private StyleClassedTextArea console;

    /** Stores the File contained by each tab.*/
    private final HashMap<Tab, File> tabFileMap = new HashMap<>();

    /** Stores whether the contents of each tab have changed since last save.*/
    private final HashMap<Tab, Boolean> textHasChangedMap = new HashMap<>();

    /** Helper objects for the Controller to use */
    private TabHelper tabHelper;
    private FileController fileController;
    private DialogHelper dialogHelper;
    private AlertHandler alertHandler;

    /** records new tabs for untitled tab naming. */
    private int numNewTabs = 1;

    /** Thread to run compile and run in */
    private Thread currentThread;


    /** Objects to control output and input of the console */
    public static OutputStream outputStream;
    private String outputString = "";

    /**
     *
     * Loads initial content on launch.
     */
    @FXML
    public void initialize() {
        tabHelper = new TabHelper(tabPane, textHasChangedMap);
        fileController = new FileController(tabHelper);
        dialogHelper = new DialogHelper(tabPane, tabHelper, fileController, tabFileMap,
                                        textHasChangedMap);
        alertHandler = new AlertHandler();
        tabHelper.createCodeAreaForTab(tabHelper.getCurrentTab());
        tabHelper.getCurrentTab().setTooltip(new Tooltip("Untitled"));
        stop.setDisable(true);
        console.setOnKeyPressed(event -> {
            handleInput(event);
        });

    }

    /**
     * Displays about dialog.
     *
     */
    @FXML
    private void handleAbout(){
        dialogHelper.aboutDialog();
    }

    /**
     * Handles saving under a specified filepath.
     *
     *
     */
    @FXML
    private boolean handleSaveAs(){
        return dialogHelper.saveAsDialog();
    }

    /**
     * Saves a file if saved previously, else prompts to save as.
     */
    @FXML
    private void handleSave(){
        Tab currentTab = tabHelper.getCurrentTab();
        if(!tabFileMap.containsKey(currentTab)){
            handleSaveAs();
        }else{
            boolean saved = fileController.saveCurrentFile(tabFileMap.get(currentTab),
                                                           null);
            if(saved){textHasChangedMap.put(currentTab, false);}
        }
    }

    /**
     * Opens a new Tab with a CodeArea.
     *
     *
     */
    @FXML
    private void handleNew(){
        Tab newTab = new Tab("Untitled Tab " + numNewTabs++);
        newTab.setOnCloseRequest(e -> handleClose());
        newTab.setTooltip(new Tooltip(newTab.getText()));

        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);

        tabHelper.createCodeAreaForTab(newTab);

        if(tabPane.getTabs().size() != 0){
            close.setDisable(false);
            save.setDisable(false);
            saveAs.setDisable(false);
            compile.setDisable(false);
            compileAndRun.setDisable(false);
            for (MenuItem item : edit.getItems())
            {
                item.setDisable(false);
            }
        }
    }

    /**
     * Closes a tab if all changes have been saved, or prompts the
     * user to save progress before closing.
     * @return whether to continue with closing
     */
    @FXML
    private boolean handleClose(){
        Tab currentTab = tabHelper.getCurrentTab();
        if (currentTab == null){
            return false;
        }
        if(!textHasChangedMap.get(currentTab)){
            tabPane.getTabs().remove(currentTab);
        }else{
            Optional<ButtonType> result = dialogHelper.closeDialog();
            if (result.get().getText().equals("Yes")){
                handleSave();
                return handleClose(); // remove tab if saved, else repeat
            } else if (result.get().getText().equals("No")){
                tabPane.getTabs().remove(currentTab);
            } else {
                return false; // user pressed cancel
            }
        }
        if(tabPane.getTabs().size() == 0){
            close.setDisable(true);
            save.setDisable(true);
            saveAs.setDisable(true);
            compile.setDisable(true);
            compileAndRun.setDisable(true);
            for (MenuItem item : edit.getItems())
            {
                item.setDisable(true);
            }
        }
        return true;
    }

    /**
     * Opens a file into a new Tab and CodeArea.
     *
     *
     */
    @FXML
    private void handleOpen(){
        dialogHelper.openDialog();
        if(tabPane.getTabs().size() != 0){
            close.setDisable(false);
            save.setDisable(false);
            saveAs.setDisable(false);
            for (MenuItem item : edit.getItems())
            {
                item.setDisable(false);
            }

        }
    }

    /**
     * Closes each tab and the Application after checking whether unsaved changes exist.
     */
    @FXML
    public void handleExit(){
        boolean closing = true;
        while (closing){
            closing = handleClose();
        }
        if (tabHelper.getCurrentTab() == null) {
            System.exit(0);
        }
    }

    @FXML
    /** Handles undo menu item */
    private void handleUndo(){ tabHelper.getCurrentCodeArea().undo(); }

    @FXML
    /** Handles redo menu item */
    private void handleRedo(){
        tabHelper.getCurrentCodeArea().redo();
    }

    @FXML
    /** Handles cut menu item */
    private void handleCut(){
        tabHelper.getCurrentCodeArea().cut();
    }

    @FXML
    /** Handles copy menu item */
    private void handleCopy(){
        tabHelper.getCurrentCodeArea().copy();
    }

    @FXML
    /** Handles paste menu item */
    private void handlePaste(){
        tabHelper.getCurrentCodeArea().paste();
    }


    @FXML
    /** Handles selectAll menu item */
    private void handleSelectAll(){
        tabHelper.getCurrentCodeArea().selectAll();
    }

    @FXML
    /** Handles stop button */
    private void stop(){
        System.out.println("stop");
        if(currentThread != null){
            System.out.println("in stop");
            currentThread.interrupt();
        }
    }

    /** Takes the string given to the console and writes it to the output stream */
    private void handleInput(KeyEvent key) {
        System.out.println(key);
        if (key.getCode() != KeyCode.ENTER){
            if(key.getText() != null && key.getText() != ""){
                outputString += key.getText();
            }
        }
        else {
            try {
                System.out.println("hello");
                outputStream.write(outputString.getBytes(StandardCharsets.UTF_8));
                outputStream.write(10); // 10 is the bytecode for new line
                outputStream.flush();
                outputString = "";
            } catch (IOException ex) {
                System.out.println("System Error");
            }
        }
    }

    @FXML
    /** Handles compile button*/
    private boolean compile() {
        File currentFile = tabFileMap.get(tabHelper.getCurrentTab());

        // if the current file hasn't been saved before, save it first
        if (currentFile == null) {
            // if saving process is cancelled, do not continue with compiling
            if (!handleSaveAs())
                System.out.println("");
                //return false;
            else
                currentFile = tabFileMap.get(tabHelper.getCurrentTab());
        }

        // if the file has been changed since last save, give the save prompt
        else if (textHasChangedMap.get(tabHelper.getCurrentTab())) {
            Optional<ButtonType> saveResult = dialogHelper.saveDialog();
            if (saveResult.get().getText().equals("Yes")) {
                handleSave();
            } else if (saveResult.get().getText().equals("No")) {
                // if user presses no, don't do anything
            } else {
                return false; // user pressed cancel, quit the method
            }
        }

        stop.setDisable(false);

        final String[] message = {""};
        File finalCurrentFile = currentFile;

        // Start in new thread
        this.currentThread = new Thread(() -> {

            Compile comp = new Compile(finalCurrentFile, console);
            comp.start();

            while (true) {
                if (Thread.interrupted()) {
                    comp.interrupt();
                    break;
                }
                if (!comp.isAlive()) {
                    break;
                } else {
                    try {
                        comp.join(1);
                    } catch (InterruptedException e) {
                        this.alertHandler.showAlert("Compilation interrupted, exiting.",
                                "Process interruption!");
                        break;
                    }
                }
            }

            if (comp.hasErrorMessage())
                message[0] = comp.getErrorMessage();

            stop.setDisable(true);
        });

        currentThread.start();

        while (currentThread.isAlive()){
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                this.alertHandler.showAlert("Compilation interrupted, exiting.",
                        "Process interruption!");
                return false;
            }
        }

        // display the compilation result message
        console.append("******************\n", "");
        if (message[0].length() > 0) {
            console.append(message[0], "");
            //return false;
        } else {
            console.append("Compilation successful!\n", "");
        }
        console.requestFollowCaret();

        return true;


    }

    @FXML
    /** Handles compile and run button*/
    private void compileAndRun(){
        // if we have error in compiling, do not run
        if(!compile()){
            return;
        }
        stop.setDisable(false);
        final String[] message = {""};
        this.currentThread = new Thread(() -> {
            File currentFile = tabFileMap.get(tabHelper.getCurrentTab());

            //stop.setDisable(false);
            Run run = new Run(currentFile, console);
            run.start();

            while (true) {
                if (Thread.interrupted()) {
                    run.interrupt();
                    break;
                }
                if(!run.isAlive()){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        alertHandler.showAlert("Error while closing outputStream", "Error");
                    }
                    break;
                }else {
                    try {
                        run.join(1);
                    } catch (InterruptedException e) {
                        System.out.println("run interrupted");
                        break;
                    }
                }
            }

            if (run.hasErrorMessage())
                message[0] = run.getErrorMessage();

            stop.setDisable(true);
        });
        currentThread.start();

        if (message[0].length()>0)
            console.append(message[0], "");
        else
            console.append("Run Successful!\n", "");

        console.append("******************\n\n", "");
        console.requestFollowCaret();
    }
}
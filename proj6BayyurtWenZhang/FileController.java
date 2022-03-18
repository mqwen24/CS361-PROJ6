/*
 * File: proj5BayyurtEnglishTymkiw.FileController.java
 * Names: Izge Bayyurt, Nick English, Dylan Tymkiw
 * Class: CS361
 * Project 5
 * Date: 3/07/2022
 */

package proj6BayyurtWenZhang;

import org.fxmisc.richtext.CodeArea;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Contains helper methods for saving and opening files.
 */
public class FileController {
    private final TabHelper tabHelper;
    private final AlertHandler alertHandler;

    public FileController(TabHelper tabHelper){
        this.tabHelper = tabHelper;
        this.alertHandler = new AlertHandler();
    }

    /**
     * Writes the content of a file to the new CodeArea.
     *
     * @param openedFile the file to write into the CodeArea.
     */
    public void writeFileToCodeArea(File openedFile){
        CodeArea currentArea = this.tabHelper.getCurrentCodeArea();
        currentArea.replaceText(""); // clear default "Sample text" message
        try {
            Scanner scan = new Scanner(new File(String
                    .valueOf(openedFile))).useDelimiter("\\s+");
            while (scan.hasNextLine()) {
                currentArea.appendText(scan.nextLine() + "\n");
            }
        }catch (FileNotFoundException ex) {
            this.alertHandler.showAlert("The requested file was not found.  Check the file" +
                    "exists and try again.", "File Not Found");
        }
    }

    /**
     * Writes the content of a Tab to a given file path.
     *
     * @param currentFile the File currently referenced by the Tab.
     * @param outFile the file to write the CodeArea contents to.
     */
    public boolean saveCurrentFile(File currentFile, PrintWriter outFile){
        try {
            outFile = new PrintWriter(String.valueOf(currentFile));
        } catch (FileNotFoundException e) {
            this.alertHandler.showAlert("File failed to save.  Ensure a valid path is specified" +
                    "and try again.", "Failed to Save");
            return false;
        }
        outFile.println(this.tabHelper.getCurrentCodeArea().getText());
        outFile.close();
        this.tabHelper.getCurrentTab().setText(currentFile.getName());
        return true;
    }
}

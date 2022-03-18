/*
 * File: proj5BayyurtEnglishTymkiw.Compile.java
 * Names: Izge Bayyurt, Nick English, Dylan Tymkiw
 * Class: CS361
 * Project 5
 * Date: 3/07/2022
 */


package proj6BayyurtWenZhang;

import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Handles a new thread to compile a java program.
 */
public class Compile extends Thread{

    private File fileToCompile;
    private AlertHandler alertHandler;
    private StyleClassedTextArea console;
    private String errorMessage;

    public Compile(File fileToCompile, StyleClassedTextArea console) {
        this.fileToCompile = fileToCompile;
        this.alertHandler = new AlertHandler();
        this.console = console;
        this.errorMessage = null;
    }

    /**
     * Compiles a java file and tells the user if it succeeded in doing so
     * */
    public void run() {

        ProcessBuilder pb = new ProcessBuilder();
        try {
            pb.command("javac", this.fileToCompile.getCanonicalPath());
            Process p = pb.start();
            p.waitFor();

            // if the compilation failed for some reason, set an error message
            if (p.exitValue() != 0) {
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String readLine;
                String errorMessage = "";

                while ((readLine = r.readLine()) != null) {
                    errorMessage += readLine + "\n";
                }

                this.errorMessage = errorMessage + "\n";

            }
        } catch (IOException e) {
            this.alertHandler.showAlert("File not found or not accessible. Ensure a valid path is specified" +
                    "and try again.", "Failed to Open File");
            return;
        } catch (InterruptedException e) {
            this.alertHandler.showAlert("User interrupted the process, exiting.", "Process interrupted");
            return;
        }
    }

    /**
     * @return a boolean that tells if an Error Message exists
     * */
    public boolean hasErrorMessage(){
        return errorMessage != null;
    }

    /**
     * @return the error message
     * */
    public String getErrorMessage() {
        return errorMessage;
    }

}

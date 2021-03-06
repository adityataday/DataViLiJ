package actions;

import java.io.File;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import javafx.stage.FileChooser;
import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_FILE_EXT_DESC;
import static settings.AppPropertyTypes.INCORRECT_FILE_EXTENSION;
import static settings.AppPropertyTypes.SAVE_UNSAVED_WORK;
import static settings.AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE;
import static settings.AppPropertyTypes.INITIAL_SAVE_FILE_NAME;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;

/**
 * This is the concrete implementation of the action handlers required by the
 * application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;

    /**
     * Path to the data file currently active.
     */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        try {
            // TODO for homework 1
            this.promptToSave();
        } catch (IOException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(ex.getClass().getName(), ex.getMessage());
        }

    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        System.exit(0);
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. The user will be presented
     * with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and
     * continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the
     * action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to
     * continue with the action, but also does not want to save the work at this
     * point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and
     * <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method

        ConfirmationDialog save = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);

        save.show(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

        if (save.getSelectedOption() == ConfirmationDialog.Option.YES) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(applicationTemplate.manager.getPropertyValue(INITIAL_SAVE_FILE_NAME.name()));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));

            File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

            if (file != null) {
                if (!file.getName().contains(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()))) {
                    throw new IOException(applicationTemplate.manager.getPropertyValue(INCORRECT_FILE_EXTENSION.name()));
                }
                try {
                    FileWriter filewriter = new FileWriter(file);
                    filewriter.write(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText());
                    filewriter.close();
                } catch (IOException ex) {
                    throw new IOException();
                }
            }

            return true;

        } else if (save.getSelectedOption() == ConfirmationDialog.Option.NO) {
            ((AppUI) applicationTemplate.getUIComponent()).clear();
            return true;
        }

        return false;
    }
}

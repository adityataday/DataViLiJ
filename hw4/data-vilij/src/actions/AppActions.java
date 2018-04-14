package actions;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.FileChooser;
import settings.AppPropertyTypes;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_FILE_EXT_DESC;
import static settings.AppPropertyTypes.IMAGE_FILE_EXT;
import static settings.AppPropertyTypes.IMAGE_FILE_EXT_DESC;
import static settings.AppPropertyTypes.INCORRECT_FILE_EXTENSION_DATA;
import static settings.AppPropertyTypes.INCORRECT_FILE_EXTENSION_IMAGE;
import static settings.AppPropertyTypes.LOAD_DATA;
import static settings.AppPropertyTypes.SAVE_IMAGE;

import ui.AppUI;
import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;

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

    /**
     * The boolean property marking whether or not there are any unsaved
     * changes.
     */
    SimpleBooleanProperty isUnsaved;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = new SimpleBooleanProperty(false);
    }

    public void setIsUnsavedProperty(boolean property) {
        isUnsaved.set(property);
    }

    @Override
    public void handleNewRequest() {
        ((AppUI) (applicationTemplate.getUIComponent())).setLeftSideProperty(3);
        try {
            if (!isUnsaved.get() || promptToSave()) {
                applicationTemplate.getDataComponent().clear();
                applicationTemplate.getUIComponent().clear();
                isUnsaved.set(false);
                dataFilePath = null;
                ((AppUI) applicationTemplate.getUIComponent()).getNewButton().setDisable(true);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                ((AppUI) applicationTemplate.getUIComponent()).setToggleSwitchIsOn(true);
            }
        } catch (IOException e) {
            errorHandlingHelper();
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        try {
            if (dataFilePath == null) {
                if (promptToSave() && !isUnsaved.get()) {
                    ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                }

            } else if (isUnsaved.get()) {
                save();
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            }

            isUnsaved.set(false);

        } catch (IOException e) {
            errorHandlingHelper();
        }

    }

    @Override
    public void handleLoadRequest() {
        try {
            // TODO: NOT A PART OF HW 1
            load();
            isUnsaved.set(false);
        } catch (IOException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(ex.getClass().getName(), ex.getMessage());
        }
    }

    @Override
    public void handleExitRequest() {
        try {
            if (!isUnsaved.get() || promptToSave()) {
                System.exit(0);
            }
        } catch (IOException e) {
            errorHandlingHelper();
        }
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() {
        // TODO: NOT A PART OF HW 1
        try {
            saveImage();
        } catch (IOException ex) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(ex.getClass().getName(), ex.getMessage());
        }

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
        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog dialog = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) {
            return false; // if user closes dialog using the window's close button
        }
        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
            if (dataFilePath == null) {
                FileChooser fileChooser = new FileChooser();
                String dataDirPath = "/" + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL dataDirURL = getClass().getResource(dataDirPath);

                if (dataDirURL == null) {
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));
                }

                //fileChooser.setInitialDirectory(new File(dataDirURL.getPath()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), "*" + applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));

                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

                if (selected != null) {
                    if (!selected.getName().contains(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()))) {
                        throw new IOException(applicationTemplate.manager.getPropertyValue(INCORRECT_FILE_EXTENSION_DATA.name()));
                    }
                    dataFilePath = selected.toPath();
                    save();
                } else {
                    return false; // if user presses escape after initially selecting 'yes'
                }
            } else {
                save();
            }
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }

    private void save() throws IOException {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        isUnsaved.set(false);
    }

    private void errorHandlingHelper() {
        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager = applicationTemplate.manager;
        String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
        String errMsg = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
        String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
        dialog.show(errTitle, errMsg + errInput);
    }

    private void load() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(LOAD_DATA.name()));

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), "*" + applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name())));

        File selected = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        if (selected != null) {
            if (!selected.getName().contains(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()))) {
                throw new IOException(applicationTemplate.manager.getPropertyValue(INCORRECT_FILE_EXTENSION_DATA.name()));
            }
            dataFilePath = selected.toPath();
            applicationTemplate.getDataComponent().loadData(dataFilePath);
        }
    }

    private void saveImage() throws IOException {
        WritableImage image = ((AppUI) applicationTemplate.getUIComponent()).getChart().snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(SAVE_IMAGE.name()));

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(IMAGE_FILE_EXT_DESC.name()), "*" + applicationTemplate.manager.getPropertyValue(IMAGE_FILE_EXT.name())));

        File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        if (selected != null) {
            if (!selected.getName().contains(applicationTemplate.manager.getPropertyValue(IMAGE_FILE_EXT.name()))) {
                throw new IOException(applicationTemplate.manager.getPropertyValue(INCORRECT_FILE_EXTENSION_IMAGE.name()));
            }
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), applicationTemplate.manager.getPropertyValue(IMAGE_FILE_EXT.name()).substring(1), selected);
        }
    }

}

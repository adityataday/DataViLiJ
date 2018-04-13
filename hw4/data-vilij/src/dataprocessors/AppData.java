package dataprocessors;

import actions.AppActions;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This is the concrete application-specific implementation of the data
 * component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor processor;

    private ApplicationTemplate applicationTemplate;
    private boolean success;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor(applicationTemplate);
        this.applicationTemplate = applicationTemplate;
        success = false;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        try {
            StringBuffer text = new StringBuffer();
            Files.lines(dataFilePath)
                    .forEach(list -> {
                        text.append(list + "\n");
                    });
            processor.clear();
            loadData(text.toString());

            if (success) {
                updateGUI(text.toString());
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadData(String dataString) {
        try {
            processor.processString(dataString);
            ((AppUI) applicationTemplate.getUIComponent()).setHasNewText(true);
            success = true;
        } catch (Exception e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput + e.getMessage());
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(false);
            processor.clear();
        }
    }

    @Override
    public void saveData(Path dataFilePath) {
        // NOTE: completing this method was not a part of HW 1. You may have implemented file saving from the
        // confirmation dialog elsewhere in a different way.
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))) {
            writer.write(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }

    private String Parse(String dataString) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This is a helper method to update the GUI
     *
     * @param text
     */
    private void updateGUI(String text) {
        ((AppUI) (applicationTemplate.getUIComponent())).setLeftSideProperty(2);

        StringBuilder displayText = new StringBuilder();
        String[] token = text.split("\\n");
        for (int i = 0; i < 10; i++) {
            displayText.append(token[i] + "\n");
        }

        ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText(displayText.toString());
        ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);

    }
}

package dataprocessors;

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
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
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
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText(text.toString());
            ((AppUI) applicationTemplate.getUIComponent()).setHasNewText(true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadData(String dataString) {
        try {
            processor.processString(dataString);
        } catch (Exception e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput + e.getMessage());
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
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
}

package ui;

import actions.AppActions;
import dataprocessors.AppData;
import static java.io.File.separator;
import javafx.geometry.Insets;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static settings.AppPropertyTypes.CHART_TITLE;
import static settings.AppPropertyTypes.DATA_ENTRY_LABEL;
import static settings.AppPropertyTypes.DISPLAY_BUTTON;
import static settings.AppPropertyTypes.SCREENSHOT_ICON;
import static settings.AppPropertyTypes.SCREENSHOT_TOOLTIP;
import vilij.propertymanager.PropertyManager;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton; // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button displayButton;  // workspace button to display data on the chart
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display

    public TextArea getTextArea() {
        return textArea;
    }

    private String scrnshoticonPath; // path to the 'Screen Shot' button

    public ScatterChart<Number, Number> getChart() {
        return chart;
    }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = "/" + String.join(separator,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));

        scrnshoticonPath = String.join(separator, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // Calling this method from the super class.
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        scrnshotButton = setToolbarButton(scrnshoticonPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar = new ToolBar(newButton, saveButton, loadButton, printButton, exitButton, scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    /**
     * Resets the UI and clears all data.
     */
    @Override
    public void clear() {
        // TODO for homework 1
        textArea.clear();
        chart.getData().clear();
        newButton.setDisable(true);
        saveButton.setDisable(true);

    }

    /**
     * This method defines the layout for the APP. This is where the chart and
     * textArea are initialized.
     */
    private void layout() {
        // TODO for homework 1
        HBox hbox = new HBox(8); // Main Hbox frame under the appPane toolbar

        VBox dataElements = new VBox(8);   // This VBox has all the Data File elements which contains the textbox and display button under the Hbox

        textArea = new TextArea();
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(DISPLAY_BUTTON.name()));

        textArea.setPrefWidth(200);
        textArea.setPrefHeight(100);

        dataElements.setPadding(new Insets(10));
        dataElements.prefHeightProperty().bind(appPane.heightProperty());
        dataElements.prefWidthProperty().bind(appPane.widthProperty().divide(2));

        dataElements.getChildren().addAll(new Label(applicationTemplate.manager.getPropertyValue(DATA_ENTRY_LABEL.name())), textArea, displayButton);

        VBox chartElement = new VBox(8);  // This Vbox has the scatterchart under the HBox.

        // Initializing the Chart
        chart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        chart.setTitle(applicationTemplate.manager.getPropertyValue(CHART_TITLE.name()));

        chartElement.setPadding(new Insets(10));
        chartElement.prefHeightProperty().bind(appPane.heightProperty());
        chartElement.prefWidthProperty().bind(appPane.widthProperty());
        chartElement.getChildren().addAll(chart);

        hbox.getChildren().addAll(dataElements, chartElement);

        appPane.getChildren().addAll(hbox);

    }

    /**
     * This is the method where the action on the UI is handled.
     */
    private void setWorkspaceActions() {
        // TODO for homework 1

        //ActionListener for the DisplayButton
        displayButton.setOnAction(e -> {
            if (hasNewText) {
                AppData data = ((AppData) applicationTemplate.getDataComponent());
                data.clear();
                chart.getData().clear();
                data.loadData(textArea.getText());
                data.displayData();
            }
            hasNewText = false;

        });

        //ActionLister for textArea. When the user releases the key this action is triggered.
        // It checks if there is any text in the application then the new and save button's are enabled.
        // When the text is empty then the save and new buttons are disabled.
        textArea.setOnKeyReleased(e -> {
            hasNewText = true;
            newButton.setDisable(false);
            saveButton.setDisable(false);
            if (textArea.getText().isEmpty()) {
                newButton.setDisable(true);
                saveButton.setDisable(true);
            }

        });

    }
}

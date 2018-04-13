package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.scene.shape.Rectangle;
import static java.io.File.separator;
import javafx.util.Duration;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import static vilij.settings.PropertyTypes.CSS_RESOURCE_FILENAME;
import static vilij.settings.PropertyTypes.CSS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

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
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button displayButton;  // workspace button to display data on the chart
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display
    private CheckBox checkBox;

    SimpleStringProperty metaData;

    public void setMetaData(String property) {
        this.metaData.set(property);
    }

    //The boolean property marking whether or not the leftSide of my layout should be visible.
    SimpleIntegerProperty leftSide;
    BooleanProperty toggleSwitchIsOn;

    public void setToggleSwitchIsOn(boolean property) {
        this.toggleSwitchIsOn.set(property);
    }

    public BooleanProperty switchedOnProperty() {
        return toggleSwitchIsOn;
    }

    public void setLeftSideProperty(int value) {
        this.leftSide.setValue(value);
    }

    public void setHasNewText(boolean hasNewText) {
        this.hasNewText = hasNewText;
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
        leftSide = new SimpleIntegerProperty();
        toggleSwitchIsOn = new SimpleBooleanProperty(false);
        metaData = new SimpleStringProperty();
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        cssPath = String.join("/",
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                manager.getPropertyValue(CSS_RESOURCE_FILENAME.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = "/" + String.join(separator,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath = String.join(separator,
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnshotButton = setToolbarButton(scrnshoticonPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),
                true);
        newButton.setDisable(false);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
        chart.getData().clear();
    }

    public TextArea getTextArea() {
        return textArea;
    }

    private void layout() {
        PropertyManager manager = applicationTemplate.manager;
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));

        VBox leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight * 0.3);

        Text leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();

        //Following is the code that adds Toogle like switch to processButtonBOx
        HBox processButtonsBox = new HBox();
        Pane buttonBody = new Pane();
        Text toggleText = new Text();
        toggleText.getStyleClass().add("tb-text");
        toggleText.textProperty().bind(Bindings.when(switchedOnProperty()).then("EDIT").otherwise("DONE"));
        processButtonsBox.setHgrow(processButtonsBox, Priority.ALWAYS);
        processButtonsBox.getChildren().addAll(toggleSwitch(buttonBody), toggleText);

        HBox metaDataBox = new HBox();
        Text metaDataInfo = new Text();
        metaDataInfo.getStyleClass().add("md-text");
        metaDataInfo.setText(metaData.get());
        metaDataBox.getChildren().add(metaDataInfo);

        //Add the textArea, leftPanelTitle and processbuttonsBox to leftPanel.
        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox, metaDataBox);

        leftPanel.setVisible(false);

        //Change Listener on the string metaData value
        metaData.addListener((obs, oldState, newState) -> {
            metaDataInfo.setText(newState);

        });

        // Change Listener on the boolean variable leftSide
        leftSide.addListener((observable, oldValue, newValue) -> {

            
            // 2 is for loadData
            if (newValue.intValue() == 2) {
                //hide the processButtonsBox
                processButtonsBox.setVisible(false);
                toggleSwitchIsOn.set(false);
                textArea.setEditable(false);
                textArea.setStyle("-fx-control-inner-background: #D3D3D3");
                newButton.setDisable(false);

            } //3 is for newData
            else if (newValue.intValue() == 3) {
                //show the processButtonsBox
                processButtonsBox.setVisible(true);
                metaData.set(null);
                toggleSwitchIsOn.set(true);
                textArea.setStyle(null);
                textArea.setEditable(true);

            }

            leftPanel.setVisible(true);

        });

        StackPane rightPanel = new StackPane(chart);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);

        applicationTemplate.getUIComponent().getPrimaryScene().getStylesheets().add(cssPath);
    }

    private void setWorkspaceActions() {
        setTextAreaActions();
        //setDisplayButtonActions();
        //setCheckBoxAction();
    }

    //ActionLister for textArea. When the user releases the key this action is triggered.
    // It checks if there is any text in the application then the new and save button's are enabled.
    // When the text is empty then the save and new buttons are disabled.
    private void setTextAreaActions() {

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.equals(oldValue) && !newValue.isEmpty()) {
                hasNewText = true;
                newButton.setDisable(false);
                saveButton.setDisable(false);
                ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(true);

            } else {
                hasNewText = false;
                newButton.setDisable(true);
                saveButton.setDisable(true);
                ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(false);
            }

        });
    }

//    private void setDisplayButtonActions() {
//        displayButton.setOnAction(event -> {
//            if (hasNewText) {
//                try {
//                    chart.getData().clear();
//                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
//                    dataComponent.clear();
//                    dataComponent.loadData(textArea.getText());
//                    dataComponent.displayData();
//
//                    if (chart.getData().isEmpty()) {
//                        scrnshotButton.setDisable(true);
//                    } else {
//                        scrnshotButton.setDisable(false);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//            hasNewText = false;
//        });
//    }
//
//    private void setCheckBoxAction() {
//        checkBox.setOnAction(event -> {
//            if (checkBox.isSelected()) {
//                textArea.setEditable(false);
//                textArea.setStyle("-fx-control-inner-background: #D3D3D3");
//            } else {
//                textArea.setEditable(true);
//                textArea.setStyle(null);
//            }
//        });
//    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getNewButton() {
        return newButton;
    }

    /**
     * This is the method that create the layout for the toggleSwitch
     *
     * @param buttonBody - A pane passed from the layout method.
     * @return - updates the buttonBody Pane.
     */
    private Pane toggleSwitch(Pane buttonBody) {

        Circle circle = new Circle(12.5);
        circle.setCenterX(12.5);
        circle.setCenterY(12.5);
        circle.getStyleClass().add("tb-circle");

        Rectangle rectangle = new Rectangle(50, 25);
        rectangle.getStyleClass().add("tb-rectangle");

        buttonBody.getChildren().addAll(rectangle, circle);

        setToggleSwitchAction(circle, rectangle, buttonBody);

        return buttonBody;

    }

    /**
     * This is the method that sets the actionHandler and actionListener for the
     * toggleSwitch. This method is also responsible for the animation.
     *
     * @param circle
     * @param rectangle
     * @param buttonBody
     */
    private void setToggleSwitchAction(Circle circle, Rectangle rectangle, Pane buttonBody) {

        TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
        ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        translateAnimation.setNode(circle);

        fillAnimation.setShape(rectangle);

        toggleSwitchIsOn.addListener((obs, oldState, newState) -> {
            boolean isOn = newState;
            translateAnimation.setToX(isOn ? 50 - 25 : 0);
            fillAnimation.setFromValue(isOn ? Color.WHITE : Color.DODGERBLUE);
            fillAnimation.setToValue(isOn ? Color.DODGERBLUE : Color.WHITE);
            animation.play();

            if (isOn) {
                textArea.setEditable(true);
                textArea.setStyle(null);
            } else if (leftSide.getValue() != 2) {
                textArea.setEditable(false);
                textArea.setStyle("-fx-control-inner-background: #D3D3D3");

                //when the edit is completed this part sends data to AppData to check if the data is valid.
                try {
                    chart.getData().clear();
                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                    dataComponent.clear();
                    dataComponent.loadData(textArea.getText());
                    leftSide.setValue(4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        buttonBody.setOnMouseClicked(event -> {
            toggleSwitchIsOn.set(!toggleSwitchIsOn.get());
        });
    }

}

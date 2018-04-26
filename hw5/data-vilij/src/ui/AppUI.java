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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import static vilij.settings.PropertyTypes.CSS_RESOURCE_FILENAME;
import static vilij.settings.PropertyTypes.CSS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.IS_WINDOW_RESIZABLE;

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
    private Button run;  // workspace button to display data on the chart
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display
    private Button classification;
    private Button clustering;
    private RadioButton radioButton;
    private Button configuration;
    private int maxIterations;
    private int updateInterval;
    private boolean isContinous;
    private int noOfClusters;

    SimpleStringProperty metaData;

    //The boolean property marking whether or not the leftSide of my layout should be visible.
    SimpleIntegerProperty leftSide;

    BooleanProperty toggleSwitchIsOn;
    BooleanProperty showAlgorithmType;
    BooleanProperty bothAlgorithm;
    BooleanProperty showSubAlgorithms;
    BooleanProperty showRun;
    BooleanProperty isClusteringAlgorithm;

    public void setShowRun(boolean property) {
        this.showRun.set(property);
    }

    public void setShowSubAlgorithms(boolean showSubAlgorithms) {
        this.showSubAlgorithms.set(showSubAlgorithms);
    }

    public void setBothAlgorithm(boolean property) {
        this.bothAlgorithm.set(property);
    }

    public void setShowAlgorithmType(boolean property) {
        this.showAlgorithmType.set(property);
    }

    public SimpleIntegerProperty getLeftSide() {
        return leftSide;
    }

    public void setToggleSwitchIsOn(boolean property) {
        this.toggleSwitchIsOn.set(property);
    }

    public BooleanProperty switchedOnProperty() {
        return toggleSwitchIsOn;
    }

    public void setMetaData(String property) {
        this.metaData.set(property);
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
        showAlgorithmType = new SimpleBooleanProperty();
        bothAlgorithm = new SimpleBooleanProperty(true);
        showSubAlgorithms = new SimpleBooleanProperty(true);
        showRun = new SimpleBooleanProperty(true);
        isClusteringAlgorithm = new SimpleBooleanProperty();
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

        //MetaData Info
        HBox metaDataBox = new HBox();
        Text metaDataInfo = new Text();
        metaDataInfo.getStyleClass().add("md-text");
        metaDataInfo.setText(metaData.get());
        metaDataBox.getChildren().add(metaDataInfo);

        //Adding Algorithm buttons to the leftPanel
        //Intializing the algorithmBox
        VBox algorithmBox = new VBox();
        Text algorithmBoxTitle = new Text("Algorithm Type");
        classification = new Button("Classification");
        clustering = new Button("Clustering");

        //Styling the alogrithm buttons
        algorithmBoxTitle.getStyleClass().add("ab-text");
        classification.getStyleClass().add("ab-button");
        clustering.getStyleClass().add("ab-button");
        algorithmBox.setSpacing(10);

        algorithmBox.getChildren().addAll(algorithmBoxTitle, classification, clustering);

        //SubAlgorithmModule
        VBox subAlgorithmModule = new VBox();

        isClusteringAlgorithm.addListener((observable, oldValue, newValue) -> {
            subAlgorithmModule.getChildren().clear();
            showRun.set(false);
            subAlgorithmModuleProcessing(subAlgorithmModule, oldValue);
        });

        subAlgorithmModule.setSpacing(20);

        //Run Button
        HBox runBox = new HBox();
        run = new Button();
        String iconsPath = "/" + String.join(separator,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String configPath = String.join(separator,
                iconsPath, "play-button.png");

        ImageView imageView = new ImageView(configPath);

        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        run.setGraphic(imageView);

        runBox.getChildren().add(run);

        //Add the textArea, leftPanelTitle and processbuttonsBox to leftPanel.
        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox, metaDataBox, algorithmBox, subAlgorithmModule, runBox);

        leftPanel.setVisible(false);

        showRun.addListener((obs, oldState, newState) -> {
            if (newState) {
                runBox.setVisible(true);
            } else {
                runBox.setVisible(false);
            }
        }
        );

        showSubAlgorithms.addListener((obs, oldState, newState) -> {
            if (newState) {
                subAlgorithmModule.setVisible(true);
            } else {
                subAlgorithmModule.setVisible(false);
                showRun.set(false);

            }
        }
        );

        showAlgorithmType.addListener((obs, oldState, newState) -> {
            boolean check = newState;
            if (check) {
                algorithmBox.setVisible(false);
                showSubAlgorithms.set(false);
            } else {
                algorithmBox.setVisible(true);

            }
        });

        //Change Listener on the string metaData value
        metaData.addListener((obs, oldState, newState) -> {
            metaDataInfo.setText(newState);

            if (metaDataInfo.getText().isEmpty()) {
                showAlgorithmType.set(true);
            } else {
                showAlgorithmType.set(false);
            }

        });

        bothAlgorithm.addListener((obs, oldState, newState) -> {
            if (newState) {
                classification.setVisible(true);
            } else {
                classification.setVisible(false);
            }
        });

        // Change Listener on the boolean variable leftSide
        leftSide.addListener((observable, oldValue, newValue) -> {

            switch (newValue.intValue()) {

                // 2 is for loadData
                case 2:
                    //hide the processButtonsBox
                    toggleSwitchIsOn.set(false);
                    metaData.set("");
                    textArea.setEditable(false);
                    textArea.setStyle("-fx-control-inner-background: #D3D3D3");
                    newButton.setDisable(false);
                    leftPanel.setVisible(true);
                    processButtonsBox.setVisible(false);
                    break;

                //3 is for newData
                case 3:
                    //show the processButtonsBox
                    metaData.set("");
                    toggleSwitchIsOn.set(true);
                    textArea.setStyle(null);
                    textArea.setEditable(true);
                    leftPanel.setVisible(true);
                    processButtonsBox.setVisible(true);
                    break;

                //This is the case where we want to make the leftPanel invisible
                case 4:
                    leftPanel.setVisible(false);
                    break;
                default:
                    break;
            }

        });

        StackPane rightPanel = new StackPane(chart);

        rightPanel.setMaxSize(windowWidth
                * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth
                * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);

        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren()
                .add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);

        applicationTemplate.getUIComponent()
                .getPrimaryScene().getStylesheets().add(cssPath);
    }

    private void setWorkspaceActions() {
        setTextAreaActions();
        setClassificationActions();
        setClusteringActions();
        //setDisplayButtonActions();
        //setCheckBoxAction();
    }

    private void setClassificationActions() {
        classification.setOnMouseClicked(e -> {
            clustering.getStyleClass().removeAll("algo-selected");
            classification.getStyleClass().add("algo-selected");
            showSubAlgorithms.set(true);
            if (!isClusteringAlgorithm.get()) {
                isClusteringAlgorithm.set(!isClusteringAlgorithm.get());
            }

        });
    }

    private void setClusteringActions() {
        clustering.setOnMouseClicked(e -> {
            clustering.getStyleClass().add("algo-selected");
            classification.getStyleClass().removeAll("algo-selected");
            showSubAlgorithms.set(true);
            if (isClusteringAlgorithm.get()) {
                isClusteringAlgorithm.set(!isClusteringAlgorithm.get());
            }

        });
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
            metaData.set("");
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
                chart.getData().clear();
                AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                dataComponent.clear();
                dataComponent.loadData(textArea.getText());

            }

        });

        buttonBody.setOnMouseClicked(event -> {
            toggleSwitchIsOn.set(!toggleSwitchIsOn.get());
        });
    }

    /**
     * This is the method where the sub algorithm properties are set depending
     * if its a clustering or classification.
     *
     * @param subAlgorithmModule
     * @param value - This is a boolean that defines what type of algorithm was
     * selected.
     */
    private void subAlgorithmModuleProcessing(VBox subAlgorithmModule, Boolean value) {
        PropertyManager manager = applicationTemplate.manager;

        ToggleGroup group = new ToggleGroup();

        for (int i = 1; i <= 3; i++) {
            HBox listOfAlgorithms = new HBox();
            radioButton = new RadioButton("Algorithm " + i);
            configuration = new Button();
            String iconsPath = "/" + String.join(separator,
                    manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                    manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
            String configPath = String.join(separator,
                    iconsPath, "config.png");

            ImageView imageView = new ImageView(configPath);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            configuration.setGraphic(imageView);

            radioButton.setOnMouseClicked(e -> {
                showRun.set(true);
            });

            configuration.setOnMouseClicked(e -> {
                
                if(isClusteringAlgorithm.get()){
                    maxIterations = 0;
                }

                Stage secondryStage = new Stage();
                secondryStage.setTitle("Configuration");
                GridPane gridPane = new GridPane();

                gridPane.setAlignment(Pos.CENTER);
                gridPane.setPadding(new Insets(40, 40, 40, 40));
                gridPane.setHgap(10);
                gridPane.setVgap(10);

                ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
                columnOneConstraints.setHalignment(HPos.RIGHT);
                ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
                columnTwoConstrains.setHgrow(Priority.ALWAYS);

                gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

                // Add Header
                Label header = new Label("Algorithm Run Configuration");
                header.setFont(Font.font("San serif", FontWeight.BOLD, 20));
                gridPane.add(header, 0, 0, 2, 1);
                GridPane.setHalignment(header, HPos.CENTER);
                GridPane.setMargin(header, new Insets(20, 0, 20, 0));

                // Add Name Label
                Label maxIterationLabel = new Label("Max Iterations : ");
                gridPane.add(maxIterationLabel, 0, 1);

                // Add MaxIteration Label
                TextField maxIterationtext = new TextField();
                maxIterationtext.setPrefHeight(20);
                if (maxIterations != 0) {
                    maxIterationtext.setText(String.valueOf(maxIterations));
                }
                gridPane.add(maxIterationtext, 1, 1);

                // Add Update Interval Label
                Label updateIntervalLabel = new Label("Update Interval : ");
                gridPane.add(updateIntervalLabel, 0, 2);

                // Add Update Interval Field
                TextField updateIntervalText = new TextField();
                updateIntervalText.setPrefHeight(20);
                if (updateInterval != 0) {
                    updateIntervalText.setText(String.valueOf(updateInterval));
                }
                gridPane.add(updateIntervalText, 1, 2);

                Label noOfClustersLabel = new Label("No of Clusters : ");

                // Add Update Interval Field
                TextField noOfClustersText = new TextField();
                noOfClustersText.setPrefHeight(20);
                if (noOfClusters != 0) {
                    noOfClustersText.setText(String.valueOf(noOfClusters));
                }

                gridPane.add(noOfClustersLabel, 0, 3);
                gridPane.add(noOfClustersText, 1, 3);

                noOfClustersLabel.setVisible(value);
                noOfClustersText.setVisible(value);

                Label runPlay = new Label("Continous Run?");
                gridPane.add(runPlay, 0, 4);

                // Is continous checkbox
                CheckBox checkBox = new CheckBox();
                checkBox.setPrefHeight(20);
                checkBox.setSelected(isContinous);
                gridPane.add(checkBox, 1, 4);

                // Add Submit Button
                Button submit = new Button("Submit");
                submit.setPrefHeight(40);
                submit.setPrefWidth(100);
                gridPane.add(submit, 0, 5, 2, 1);
                GridPane.setHalignment(submit, HPos.CENTER);
                GridPane.setMargin(submit, new Insets(20, 0, 20, 0));

                //submit button set on action
                submit.setOnMouseClicked(event -> {
                    try {
                        maxIterations = Integer.parseInt(maxIterationtext.getText());
                        updateInterval = Integer.parseInt(updateIntervalText.getText());
                        if (value) {
                            noOfClusters = Integer.parseInt(noOfClustersText.getText());
                        }
                        isContinous = checkBox.isSelected();

                        secondryStage.close();
                    } catch (NumberFormatException ex) {
                        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                        //manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
                        String errTitle = "";
                        String errMsg = "";
                        String errInput = "";
                        dialog.show(errTitle, errMsg + errInput);
                    }

                });

                //Scene and stage addition
                Scene secondryScene = new Scene(gridPane, 400, 300);
                secondryStage.setScene(secondryScene);
                secondryStage.initOwner(primaryStage);
                secondryStage.initModality(Modality.WINDOW_MODAL);
                secondryStage.setResizable(applicationTemplate.manager.getPropertyValueAsBoolean(IS_WINDOW_RESIZABLE.name()));
                secondryStage.show();
            });

            radioButton.setToggleGroup(group);

            listOfAlgorithms.getChildren().addAll(radioButton, configuration);
            listOfAlgorithms.setSpacing(10);
            subAlgorithmModule.getChildren().addAll(listOfAlgorithms);
        }

    }

}

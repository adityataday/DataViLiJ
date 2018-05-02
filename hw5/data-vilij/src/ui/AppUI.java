package ui;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.Classifier;
import classification.RandomClassifier;
import data.DataSet;
import dataprocessors.AppData;
import javafx.scene.shape.Rectangle;

import static java.io.File.separator;

import java.util.List;

import javafx.util.Duration;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private Text metaDataInfo;


    private BooleanProperty showToggleSwitchBox;
    private BooleanProperty showTextArea;
    private BooleanProperty toggleSwitchIsOn;
    private BooleanProperty showMetaData;
    private BooleanProperty showClassificationAlgorithm;
    private BooleanProperty isClusteringAlgorithm;
    private BooleanProperty showSubAlgorithms;
    private BooleanProperty showRunButton;
    private BooleanProperty istFirstRun;


    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
        toggleSwitchIsOn = new SimpleBooleanProperty();
        showTextArea = new SimpleBooleanProperty();
        showToggleSwitchBox = new SimpleBooleanProperty();
        showMetaData = new SimpleBooleanProperty();
        showClassificationAlgorithm = new SimpleBooleanProperty();
        isClusteringAlgorithm = new SimpleBooleanProperty();
        showSubAlgorithms = new SimpleBooleanProperty();
        showRunButton = new SimpleBooleanProperty();
        istFirstRun = new SimpleBooleanProperty(true);

    }

    public boolean isShowToggleSwitchBox() {
        return showToggleSwitchBox.get();
    }


    public void setMetaDataInfo(String metaDataInfo) {
        this.metaDataInfo.setText(metaDataInfo);
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setHasNewText(boolean hasNewText) {
        this.hasNewText = hasNewText;
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public void setShowToggleSwitchBox(boolean showToggleSwitchBox) {
        this.showToggleSwitchBox.set(showToggleSwitchBox);
    }

    public void setShowTextArea(boolean showTextArea) {
        this.showTextArea.set(showTextArea);
    }

    public void setShowMetaData(boolean showMetaData) {
        this.showMetaData.set(showMetaData);
    }

    public void setShowClassificationAlgorithm(boolean showClassificationAlgorithm) {
        this.showClassificationAlgorithm.set(showClassificationAlgorithm);
    }

    public void setShowSubAlgorithms(boolean showSubAlgorithms) {
        this.showSubAlgorithms.set(showSubAlgorithms);
    }

    public void setShowRunButton(boolean showRunButton) {
        this.showRunButton.set(showRunButton);
    }

    public void setToggleSwitchIsOn(boolean toggleSwitchIsOn) {
        this.toggleSwitchIsOn.set(toggleSwitchIsOn);
    }

    public boolean isToggleSwitchIsOn() {
        return toggleSwitchIsOn.get();
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
        leftPanelTitle.visibleProperty().bind(showTextArea);

        textArea = new TextArea();
        textArea.visibleProperty().bind(showTextArea);
        textArea.editableProperty().bind(toggleSwitchIsOn);


        //Following is the code that adds Toogle like switch to processButtonBOx
        HBox processButtonsBox = new HBox();
        Pane buttonBody = new Pane();
        Text toggleText = new Text();
        toggleText.getStyleClass().add("tb-text");
        toggleText.textProperty().bind(Bindings.when(toggleSwitchIsOn).then("EDIT").otherwise("DONE"));
        processButtonsBox.setHgrow(processButtonsBox, Priority.ALWAYS);
        processButtonsBox.getChildren().addAll(toggleSwitch(buttonBody), toggleText);
        processButtonsBox.visibleProperty().bind(showToggleSwitchBox);


        //MetaData Info
        HBox metaDataBox = new HBox();
        metaDataInfo = new Text();
        metaDataInfo.getStyleClass().add("md-text");
        metaDataBox.getChildren().add(metaDataInfo);
        metaDataBox.visibleProperty().bind(showMetaData);

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
        classification.visibleProperty().bind(showClassificationAlgorithm);
        algorithmBox.visibleProperty().bind(metaDataBox.visibleProperty());


        //SubAlgorithmModule
        VBox subAlgorithmModule = new VBox();

//        isClusteringAlgorithm.addListener((observable, oldValue, newValue) -> {
//            subAlgorithmModule.getChildren().clear();
//            showRun.set(false);
//            subAlgorithmModuleProcessing(subAlgorithmModule, newValue);
//        });
        subAlgorithmModuleProcessing(subAlgorithmModule);
        subAlgorithmModule.setSpacing(20);
        subAlgorithmModule.visibleProperty().bind(showSubAlgorithms);


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
        runBox.visibleProperty().bind(showRunButton);


        //Add the textArea, leftPanelTitle and processbuttonsBox to leftPanel.
        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox, metaDataBox, algorithmBox, subAlgorithmModule, runBox);


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
        setClassificationActions();
        setClusteringActions();
        setRunActions();
        //setDisplayButtonActions();
        //setCheckBoxAction();
    }

    private void setRunActions() {
        run.setOnMouseClicked(e -> {
            if (istFirstRun.get()) {
                if (maxIterations != 0 || updateInterval != 0) {
                    if (isClusteringAlgorithm.get()) {

                    } else {
                        //Clustering Algorithms
                        classificationAlgorithmProcessing();
                    }
                } else {
                    ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                    //manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
                    String errTitle = "Cannot Run";
                    String errMsg = "No run configuration found.";
                    String errInput = " Please input valid run configurations";
                    dialog.show(errTitle, errMsg + errInput);
                }

            } else {
                synchronized (this) {
                    notify();
                }
            }

        });
    }

    private void classificationAlgorithmProcessing() {

        AppData dataComponent = ((AppData) applicationTemplate.getDataComponent());

        if (((RadioButton) radioButton.getToggleGroup().getSelectedToggle()).getText().equalsIgnoreCase("RandomClassifier1")) {

            initializeChart(dataComponent);
            DataSet dataset = DataSet.fromTSDProcessor(dataComponent.getProcessor());
            RandomClassifier classifier = new RandomClassifier(dataset, maxIterations, updateInterval, isContinous);
            new Thread(classifier).start();
            consumer(classifier);
            istFirstRun.set(false);

        } else {
            //other algorithms
        }


    }

    private void consumer(Classifier classifier) {
        AppData dataComponent = ((AppData) applicationTemplate.getDataComponent());

        Runnable task = () -> {

            try {
                while (!Thread.interrupted()) {
                    List<Integer> algorithmOutput = classifier.getQueue().take();

                    classificationAlgorithmOutput(algorithmOutput, dataComponent);

////                        Do Something
//                    System.out.println("OUT" + " " + classifier.getQueue().size());

                    if (!isContinous) {
                        scrnshotButton.setDisable(false);
                        showRunButton.set(true);
                        synchronized (this) {
                            wait();
                        }
                    } else {
                        scrnshotButton.setDisable(true);
                        showRunButton.set(false);
                        Thread.sleep(1000);
                    }


                }

            } catch (InterruptedException ex) {

            }

        };
        new Thread(task).start();

    }

    private void classificationAlgorithmOutput(List<Integer> algorithmOutput, AppData dataComponent) {
        double y1 = ((dataComponent.getProcessor().getMin_x() * algorithmOutput.get(0)) + algorithmOutput.get(2)) / algorithmOutput.get(1);
        double y2 = ((dataComponent.getProcessor().getMax_x() * algorithmOutput.get(0)) + algorithmOutput.get(2)) / algorithmOutput.get(1);

        XYChart.Series<Number, Number> regression = new XYChart.Series<>();
        regression.getData().add(0, new XYChart.Data<>(dataComponent.getProcessor().getMin_x(), y1));
        regression.getData().add(1, new XYChart.Data<>(dataComponent.getProcessor().getMax_x(), y2));
        regression.setName("Regression");

        Platform.runLater(() -> {
            if (chart.getData().get(chart.getData().size() - 1).getName().equals("Regression")) {
                chart.getData().remove(chart.getData().size() - 1);
            }

            chart.getData().add(regression);
            chart.setId("classification");

        });

    }

    private void initializeChart(AppData dataComponent) {
        chart.getXAxis().setAutoRanging(false);
        chart.getYAxis().setAutoRanging(false);
        ((NumberAxis) chart.getXAxis()).setLowerBound(dataComponent.getProcessor().getMin_x() - 1);
        ((NumberAxis) chart.getXAxis()).setUpperBound(dataComponent.getProcessor().getMax_x() + 1);
        ((NumberAxis) chart.getYAxis()).setLowerBound(dataComponent.getProcessor().getMin_y() - 1);
        ((NumberAxis) chart.getYAxis()).setUpperBound(dataComponent.getProcessor().getMax_y() + 1);
        chart.setAnimated(false);
        dataComponent.displayData();
    }

    private void setClassificationActions() {
        classification.setOnMouseClicked(e -> {
            clustering.getStyleClass().removeAll("algo-selected");
            classification.getStyleClass().add("algo-selected");
            showSubAlgorithms.set(true);
            isClusteringAlgorithm.set(false);

        });
    }

    private void setClusteringActions() {
        clustering.setOnMouseClicked(e -> {
            clustering.getStyleClass().add("algo-selected");
            classification.getStyleClass().removeAll("algo-selected");
            showSubAlgorithms.set(true);
            isClusteringAlgorithm.set(true);

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
            boolean isOn = newState;
            translateAnimation.setToX(isOn ? 50 - 25 : 0);
            fillAnimation.setFromValue(isOn ? Color.WHITE : Color.DODGERBLUE);
            fillAnimation.setToValue(isOn ? Color.DODGERBLUE : Color.WHITE);
            animation.play();

            if (isOn) {
                textArea.setStyle(null);
                showMetaData.set(false);
                showSubAlgorithms.set(false);
                showRunButton.set(false);
                clustering.getStyleClass().removeAll("algo-selected");
                classification.getStyleClass().removeAll("algo-selected");
            } else {

                textArea.setStyle("-fx-control-inner-background: #D3D3D3");

                if (showToggleSwitchBox.get()) {

                    //when the edit is completed this part sends data to AppData to check if the data is valid.
                    chart.getData().clear();
                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                    dataComponent.clear();
                    dataComponent.loadData(textArea.getText());
                }

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
     */
    private void subAlgorithmModuleProcessing(VBox subAlgorithmModule) {
        PropertyManager manager = applicationTemplate.manager;

        ToggleGroup group = new ToggleGroup();

        for (int i = 1; i <= 3; i++) {
            HBox listOfAlgorithms = new HBox();
            radioButton = new RadioButton("RandomClassifier" + i);
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
                showRunButton.set(true);
            });

            configuration.setOnMouseClicked(e -> {

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

                noOfClustersLabel.visibleProperty().bind(isClusteringAlgorithm);
                noOfClustersText.visibleProperty().bind(isClusteringAlgorithm);

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
                        if (isClusteringAlgorithm.get()) {
                            noOfClusters = Integer.parseInt(noOfClustersText.getText());
                        }
                        isContinous = checkBox.isSelected();

                        secondryStage.close();
                    } catch (NumberFormatException ex) {
                        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                        //manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
                        String errTitle = "Invalid Input";
                        String errMsg = "Invalid input type.";
                        String errInput = " Please input integer values only.";
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

    public void resetUI() {
        newButton.setDisable(false);
        showToggleSwitchBox.set(false);
        showTextArea.set(false);
        toggleSwitchIsOn.set(false);
        showMetaData.set(false);
        showClassificationAlgorithm.set(false);
        isClusteringAlgorithm.set(false);
        showSubAlgorithms.set(false);
        showRunButton.set(false);
    }

}

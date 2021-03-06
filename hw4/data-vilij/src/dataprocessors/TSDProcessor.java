package dataprocessors;

import java.nio.file.Path;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import static settings.AppPropertyTypes.LABEL_ALREADY_EXISTS;
import static settings.AppPropertyTypes.TO_MANY_LINES;
import static settings.AppPropertyTypes.TO_MANY_LINES_MSG_1;
import static settings.AppPropertyTypes.TO_MANY_LINES_MSG_2;
import ui.AppUI;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

/**
 * The data files used by this data visualization applications follow a
 * tab-separated format, where each data point is named, labeled, and has a
 * specific location in the 2-dimensional X-Y plane. This class handles the
 * parsing and processing of such data. It also handles exporting the data to a
 * 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's
 * <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private static final String ERROR_LINE_ID = "Error in line : ";
    private static final String CORRECT_DATA_FORMAT = "Correct Input must be [@Instance \\t label \\t data]";

    private Map<String, String> dataLabels;
    private Map<String, Point2D> dataPoints;
    private ApplicationTemplate applicationTemplate;

    public TSDProcessor(ApplicationTemplate applicationTemplate) {
        dataLabels = new LinkedHashMap<>();
        dataPoints = new HashMap<>();
        this.applicationTemplate = applicationTemplate;
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the
     * <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        AtomicInteger count = new AtomicInteger();
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        count.incrementAndGet();
                        String name = checkedname(list.get(0));
                        String label = list.get(1);
                        String[] pair = list.get(2).split(",");
                        Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                        checkInstanceDuplicates(name);
                        dataLabels.put(name, label);
                        dataPoints.put(name, point);
                    } catch (Exception e) {
                        //errorMessage.setLength(0);
                        if (e instanceof ArrayIndexOutOfBoundsException) {
                            errorMessage.append("\n").append(ERROR_LINE_ID).append(count).append(" ").append(list.toString()).append(" ").append(CORRECT_DATA_FORMAT);
                        } else {
                            errorMessage.append("\n").append(ERROR_LINE_ID).append(count).append(" ").append(list.toString()).append(" ").append(e.getMessage());
                        }
                        hadAnError.set(true);
                    }
                });
        if (errorMessage.length() > 0) {
            throw new Exception(errorMessage.toString());
        }
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        labels.stream().map((label) -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            return series;
        }).forEachOrdered((series) -> {
            chart.getData().add(series);
        });
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@")) {
            throw new InvalidDataNameException(name);
        }

        return name;
    }

    private void checkInstanceDuplicates(String name) throws Exception {
        if (dataLabels.containsKey(name)) {
            throw new Exception(name + applicationTemplate.manager.getPropertyValue(LABEL_ALREADY_EXISTS.name()));
        }

    }

    private void displayFilter() {
        ArrayList<String> keys = new ArrayList<>(dataLabels.keySet());

        for (int i = 0; i < keys.size(); i++) {
            if (i >= 10) {
                dataLabels.remove(keys.get(i));
            }
        }

        errorHandlingHelper(keys.size());

    }

    private void errorHandlingHelper(int size) {
        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager = applicationTemplate.manager;
        String errTitle = manager.getPropertyValue(TO_MANY_LINES.name());
        String errMsg = manager.getPropertyValue(TO_MANY_LINES_MSG_1.name()) + size + manager.getPropertyValue(TO_MANY_LINES_MSG_2.name());
        dialog.show(errTitle, errMsg);
    }

    public String metaData(Path dataFilePath) {
        StringBuilder metadata = new StringBuilder();
        Set<String> valueSet = new HashSet<>();
        dataLabels.keySet().forEach((s) -> {
            valueSet.add(dataLabels.get(s));
        });

        if (valueSet.size() <= 2) {
            ((AppUI) (applicationTemplate.getUIComponent())).setBothAlgorithm(true);
        } else {
            ((AppUI) (applicationTemplate.getUIComponent())).setBothAlgorithm(false);
        }

        metadata.append(dataLabels.keySet().size()).append(" instances with \n").append(valueSet.size()).append(" labels loaded from :\n").append(dataFilePath.toString());
        metadata.append("\nThe labels are: \n").append(valueSet.toString());

        return metadata.toString();
    }

    public String metaData() {
        StringBuilder metadata = new StringBuilder();
        Set<String> valueSet = new HashSet<>();
        dataLabels.keySet().forEach((s) -> {
            valueSet.add(dataLabels.get(s));
        });

        if (valueSet.size() <= 2) {
            ((AppUI) (applicationTemplate.getUIComponent())).setBothAlgorithm(true);
        } else {
            ((AppUI) (applicationTemplate.getUIComponent())).setBothAlgorithm(false);
        }

        metadata.append(dataLabels.keySet().size()).append(" instances with \n").append(valueSet.size()).append(" labels loaded from :\n");
        metadata.append("The labels are: \n").append(valueSet.toString());

        return metadata.toString();
    }

//    private void averageY(XYChart<Number, Number> chart) {
//        double sum = 0;
//        int count = 0;
//        double minX = Integer.MAX_VALUE;
//        double maxX = Integer.MIN_VALUE;
//        for (int i = 0; i < chart.getData().size(); i++) {
//            for (int j = 0; j < chart.getData().get(i).getData().size(); j++) {
//
//                if (minX > (Double) chart.getData().get(i).getData().get(j).getXValue()) {
//                    minX = (Double) chart.getData().get(i).getData().get(j).getXValue();
//                }
//
//                if (maxX < (Double) chart.getData().get(i).getData().get(j).getXValue()) {
//                    maxX = (Double) chart.getData().get(i).getData().get(j).getXValue();
//                }
//
//                sum += (Double) chart.getData().get(i).getData().get(j).getYValue();
//                count++;
//            }
//
//        }
//
//        XYChart.Series<Number, Number> averageY = new XYChart.Series<>();
//        averageY.getData().add(new XYChart.Data<>(minX, (sum / count)));
//        averageY.getData().add(new XYChart.Data<>(maxX, (sum / count)));
//        averageY.setName("Average Y Value");
//
//        chart.getData().add(averageY);
//
//        for (int i = 0; i < chart.getData().size(); i++) {
//            if (i == chart.getData().size() - 1) {
//                break;
//            }
//
//            chart.getData().get(i).getNode().setVisible(false);
//
//        }
//
//        for (int i = 0; i < chart.getData().size(); i++) {
//            for (int j = 0; j < chart.getData().get(i).getData().size(); j++) {
//                Tooltip.install(chart.getData().get(i).getData().get(j).getNode(), new Tooltip(chart.getData().get(i).getData().get(j).getXValue().toString()+", " +chart.getData().get(i).getData().get(j).getYValue().toString()));
//               
//            }
//
//        }
}

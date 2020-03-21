package solver;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.mariuszgromada.math.mxparser.Expression;

import java.net.URL;

public class Main extends Application {

    @FXML
    public TextField func;
    public TextField a;
    public TextField b;
    public TextField epsilon;
    public HBox chartBox;
    public Label error;
    public Label bisectionResult;
    public Label secantResult;

    @FXML
    public void onSingleButtonClick() {
        error.setText("");
        if (a.getText().trim().equals("") || b.getText().trim().equals("") || epsilon.getText().trim().equals("") ||
                func.getText().trim().equals("")) {
            error.setText("Some fields are empty");
            return;
        }
        double aVal;
        double bVal;
        double epsVal;
        try {
            aVal = Double.parseDouble(a.getText().trim().replaceAll("[,]+", "."));
            bVal = Double.parseDouble(b.getText().trim().replaceAll("[,]+", "."));
            epsVal = Double.parseDouble(epsilon.getText().trim().replaceAll("[,]+", "."));
        } catch (NumberFormatException e) {
            error.setText("Incorrect values");
            return;
        }
        System.out.println(String.format("a = %f, b = %f, eps = %f", aVal, bVal, epsVal));

        Expression expression = new Expression(func.getText().trim().replaceAll("\\*", " * ")
                .replaceAll("\\+", " + ")
                .replaceAll("-", " - ")
                .replaceAll("/", " / "));
        System.out.println(expression.getExpressionString());
        if (!expression.checkLexSyntax()) {
            error.setText("Invalid function");
            return;
        }
        if (expression.getMissingUserDefinedArguments().length > 1) {
            error.setText("Equation should use one argument");
            return;
        }
        String arg = expression.getMissingUserDefinedArguments()[0];
        expression.defineArgument(arg, 1);
        if (aVal > bVal) {
            double t = aVal;
            aVal = bVal;
            bVal = t;
        }
        double bisectionRoot = Solver.getRootByBisectionMethod(expression, aVal, bVal, epsVal);
        System.out.println("bx = " + bisectionRoot);
        bisectionResult.setText(String.format("%s = %f", arg, bisectionRoot));
        try {
            double secantRoot = Solver.getRootBySecantMethod(expression, aVal, bVal, epsVal);
            System.out.println("secx = " + secantRoot);
            secantResult.setText(String.format("%s = %f", arg, secantRoot));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            secantResult.setText(e.getMessage());
        }
        drawPlot(expression, aVal, bVal);
    }

    private void drawPlot(Expression f, double a, double b) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        ObservableList<XYChart.Data<Number, Number>> data = series.getData();

        double step = (Math.ceil(b) - Math.floor(a)) / 20;
        String arg = f.getArgument(0).getArgumentName();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double fx;

        for (double i = Math.floor(a); i <= Math.ceil(b); i += step) {
            f.setArgumentValue(arg, i);
            fx = f.calculate();
            data.add(new XYChart.Data<>(i, fx));
            if (Math.ceil(fx) > max) max = Math.ceil(fx);
            if (Math.floor(fx) < min) min = Math.floor(fx);
        }

        NumberAxis yAxis = new NumberAxis(min - (max - min) / 10, max + (max - min) / 10, (max - min) / 10);
        NumberAxis xAxis = new NumberAxis(Math.floor(a), Math.ceil(b), step * 10);
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.getData().add(series);
        chart.setTitle("Function plot");
        chart.setLegendVisible(false);

        chartBox.getChildren().clear();
        chartBox.getChildren().add(chart);
        System.out.println("Chart!!!!");
    }

    @FXML
    public void test1() {
        func.setText("2*x^2-20");
        a.setText("-10");
        b.setText("2");
        epsilon.setText("0.000001");
        onSingleButtonClick();
    }

    @FXML
    public void test2() {
        func.setText("10*cos(x)-8");
        a.setText("5");
        b.setText("6");
        epsilon.setText("0.00000001");
        onSingleButtonClick();
    }

    @FXML
    public void test3() {
        func.setText("12/x^3+4");
        a.setText("1");
        b.setText("5");
        epsilon.setText("0.00001");
        onSingleButtonClick();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/scene2.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Non-linear Equation Solver");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}

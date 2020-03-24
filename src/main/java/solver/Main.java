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

    public Label xResult;
    public Label yResult;
    public TextField dx1;
    public TextField dx2;
    public TextField dy1;
    public TextField dy2;
    public TextField function1;
    public TextField function2;
    public TextField xArg;
    public TextField yArg;
    public TextField epsilonSys;

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
            if (epsVal >= 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            error.setText("Incorrect values");
            return;
        }
        System.out.println(String.format("a = %f, b = %f, eps = %f", aVal, bVal, epsVal));

        Expression expression = new Expression(formatToExpression(func.getText()));
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
        int precision = epsilon.getText().trim().replaceAll("[,]+", ".").length() - 2;
        double bisectionRoot = Solver.getRootByBisectionMethod(expression, aVal, bVal, epsVal);
        System.out.println("bx = " + bisectionRoot);
        bisectionResult.setText(String.format("%s = %s", arg, Solver.formatToPrecision(bisectionRoot, precision)));
        Double secantRoot = null;
        try {
            secantRoot = Solver.getRootBySecantMethod(expression, aVal, bVal, epsVal);
            System.out.println("secx = " + secantRoot);
            secantResult.setText(String.format("%s = %s", arg, Solver.formatToPrecision(secantRoot, precision)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            secantResult.setText(e.getMessage());
        }
        drawPlot(expression, aVal, bVal, bisectionRoot, secantRoot);
    }

    private void drawPlot(Expression f, double a, double b, Double x1, Double x2) {
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
//        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);

        addRootToChart(f, x1, arg, chart);

        addRootToChart(f, x2, arg, chart);

        chartBox.getChildren().clear();
        chartBox.getChildren().add(chart);
        System.out.println("Chart!!!!");
    }

    private void addRootToChart(Expression f, Double x1, String arg, LineChart<Number, Number> chart) {
        if (x1 != null) {
            f.setArgumentValue(arg, x1);
            XYChart.Series<Number, Number> s2 = new XYChart.Series<>();
            s2.getData().add(new XYChart.Data<>(x1, f.calculate()));
            chart.getData().add(s2);
        }
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

    private String formatToExpression(String expression) {
        return expression.trim().replaceAll("\\*", " * ")
                .replaceAll("\\+", " + ")
                .replaceAll("-", " - ")
                .replaceAll("/", " / ");
    }

    public void solveSystem() {
        Expression[] f = {new Expression(formatToExpression(function1.getText())),
                new Expression(formatToExpression(function2.getText()))};

        Expression[][] d = {{new Expression(formatToExpression(dx1.getText())), new Expression(formatToExpression(dx2.getText()))},
                {new Expression(formatToExpression(dy1.getText())), new Expression(formatToExpression(dy2.getText()))}};

        double[] x = {Double.parseDouble(xArg.getText()), Double.parseDouble(yArg.getText())};
        double epsilon = Double.parseDouble(epsilonSys.getText());

        double[] result = NewtonSystem.solveSystem(f, d, x, epsilon);

        xResult.setText("x = " + Solver.formatToPrecision(result[0], epsilonSys.getText().trim().replaceAll("[,]+", ".").length() - 2));
        yResult.setText("y = " + Solver.formatToPrecision(result[1], epsilonSys.getText().trim().replaceAll("[,]+", ".").length() - 2));
    }

    public void testSystem1() {
        function1.setText("0.1*x^2 + x + 0.2*y^2 - 0.3");
        function2.setText("0.2*x^2 + y - 0.1*x*y - 0.7");

        dx1.setText("0.2*x + 0*y + 1");
        dx2.setText("0.4*x - 0.1*y");
        dy1.setText("0*x + 0.4*y");
        dy2.setText("1 - 0.1*x + 0*y");

        xArg.setText("0.25");
        yArg.setText("0.75");

        epsilonSys.setText("0.00001");

        solveSystem();
    }

    public void testSystem2() {
        function1.setText("sin(2*x - y) - 1.2*x - 0.4");
        function2.setText("0.8*x^2 + 1.5*y^2 - 1");

        dx1.setText("2*cos(2*x - y) - 1.2");
        dx2.setText("1.6*x + 0*y");
        dy1.setText("- cos (2*x - y)");
        dy2.setText("0*x + 3*y");

        xArg.setText("0.4");
        yArg.setText("-0.75");

        epsilonSys.setText("0.001");

        solveSystem();
    }
}

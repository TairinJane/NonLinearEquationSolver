package solver;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mariuszgromada.math.mxparser.Expression;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    public Label zResult;
    public TextField dx1;
    public TextField dx2;
    public TextField dx3;
    public TextField dy1;
    public TextField dy2;
    public TextField dy3;
    public TextField dz1;
    public TextField dz2;
    public TextField dz3;
    public TextField function1;
    public TextField function2;
    public TextField function3;
    public TextField xArg;
    public TextField yArg;
    public TextField zArg;
    public TextField epsilonSys;
    public ToggleGroup functionsToggleGroup;
    public RadioButton toggle2;
    public RadioButton toggle3;

    public VBox functionsBox;
    public VBox dzBox;
    public HBox argZBox;
    public Label dFunction3Label;
    public HBox chartBoxSystem;
    public TextField iterMax;
    public Label errorSysLabel;

    RadioButton selectedBtn;
    int systemSize = 2;
    boolean firstTimeSelected = true;
    List<Node> systemInvisibleList;
    TextField[] functionsArray;
    TextField[][] derivativeArray;
    TextField[] argsArray;
    Label[] resultsArray;


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

        Expression function = new Expression(formatToExpression(func.getText()));
        System.out.println(function.getExpressionString());
        if (!function.checkLexSyntax()) {
            error.setText("Invalid function");
            return;
        }
        if (function.getMissingUserDefinedArguments().length > 1) {
            error.setText("Equation should use one argument");
            return;
        }
        String arg = function.getMissingUserDefinedArguments()[0];
        function.defineArgument(arg, 1);
        if (aVal > bVal) {
            double t = aVal;
            aVal = bVal;
            bVal = t;
        }
        int precision = epsilon.getText().trim().replaceAll("[,]+", ".").length() - 2;
        LineChart<Number, Number> chart = drawPlot(function, aVal, bVal, chartBox);

        double bisectionRoot;
        try {
            bisectionRoot = Solver.getRootByBisectionMethod(function, aVal, bVal, epsVal);
            System.out.println("bx = " + bisectionRoot);
            bisectionResult.setText(String.format("%s = %s", arg, Solver.formatToPrecision(bisectionRoot, precision)));
            addRootToChart(function, bisectionRoot, arg, chart, "Bisection method");
        } catch (Exception e) {
            System.out.println("ex b " + e.getMessage());
            bisectionResult.setText(e.getMessage());
        }

        double secantRoot;
        try {
            secantRoot = Solver.getRootBySecantMethod(function, aVal, bVal, epsVal);
            System.out.println("secx = " + secantRoot);
            secantResult.setText(String.format("%s = %s", arg, Solver.formatToPrecision(secantRoot, precision)));
            addRootToChart(function, secantRoot, arg, chart, "Secant method");
        } catch (Exception e) {
            System.out.println("ex s " + e.getMessage());
            secantResult.setText(e.getMessage());
        }

    }

    private LineChart<Number, Number> drawPlot(Expression f, double a, double b, HBox chartBox) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(f.getExpressionString());
        ObservableList<XYChart.Data<Number, Number>> data = series.getData();

        double step = (Math.ceil(b) - Math.floor(a)) / 30;
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
        yAxis.setLabel("y");
        NumberAxis xAxis = new NumberAxis(Math.floor(a), Math.ceil(b), step * 10);
        xAxis.setLabel("x");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.getData().add(series);

        for (XYChart.Data<Number, Number> dataNode : series.getData()) {
            StackPane stackPane = (StackPane) dataNode.getNode();
            stackPane.setVisible(false);
        }

        chart.setTitle("Function plot");

        chartBox.getChildren().clear();
        chartBox.getChildren().add(chart);
        System.out.println("Chart!!!!");
        return chart;
    }

    private void addRootToChart(Expression f, Double root, String arg, LineChart<Number, Number> chart, String rootName) {
        if (root != null && !Double.isNaN(root)) {
            f.setArgumentValue(arg, root);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(root, f.calculate()));
            series.setName(rootName);
            chart.getData().add(series);
        }
    }

    @FXML
    public void test1() {
        func.setText("2*x^2-20");
        a.setText("-10");
        b.setText("2");
        epsilon.setText("0.00001");
        onSingleButtonClick();
    }

    @FXML
    public void test2() {
        func.setText("10*cos(x)-8");
        a.setText("5");
        b.setText("6");
        epsilon.setText("0.000001");
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

    private void changeSystemLayout(int selectedSystemSize) {
        System.out.println("change layout = " + selectedSystemSize);
        if (systemSize == selectedSystemSize) return;
        systemSize = selectedSystemSize;
        if (systemSize == 3) {
            for (Node node : systemInvisibleList) {
                node.setVisible(true);
            }
        } else if (systemSize == 2) {
            for (Node node : systemInvisibleList) {
                node.setVisible(false);
            }
        }
    }

    private String formatToExpression(String expression) {
        return expression.trim().replaceAll("\\*", " * ")
                .replaceAll("\\+", " + ")
                .replaceAll("-", " - ")
                .replaceAll("/", " / ");
    }

    public void solveSystem() {
        try {
            Expression[] f = new Expression[systemSize];
            double[] x = new double[systemSize];
            Expression[][] d = new Expression[systemSize][systemSize];
            for (int i = 0; i < systemSize; i++) {
                if (functionsArray[i].getText().equals("")) throw new Exception("Enter " + (i + 1) + "th function");
                f[i] = new Expression(formatToExpression(functionsArray[i].getText()));
                if (argsArray[i].getText().equals("")) throw new Exception("Enter " + (i + 1) + "th argument");
                x[i] = Double.parseDouble(argsArray[i].getText());
                for (int j = 0; j < systemSize; j++) {
                    if (derivativeArray[i][j].getText().equals(""))
                        throw new Exception("Enter derivative for function " + (j + 1) + " by " + (i + 1) + "th argument");
                    d[i][j] = new Expression(formatToExpression(derivativeArray[i][j].getText()));
                }
            }
            double epsilon = 0.00001f;
            if (!epsilonSys.getText().equals("")) epsilon = Double.parseDouble(epsilonSys.getText());
            else epsilonSys.setText(String.valueOf(epsilon));
            if (epsilon <= 0 || epsilon >= 1) throw new Exception("Epsilon must be between 0 and 1");

            int maxIterations = 50;
            if (!iterMax.getText().equals("")) maxIterations = Integer.parseInt(iterMax.getText());
            else iterMax.setText(String.valueOf(maxIterations));
            if (maxIterations <= 0 || maxIterations > 50) throw new Exception("Iterations must be between 1 and 50");
            errorSysLabel.setText("");

            double[] result = NewtonSystem.solveSystem(f, d, x, epsilon, maxIterations);
            int precision = epsilonSys.getText().trim().replaceAll("[,]+", ".").length() - 2;
            for (double r : result) {
                if (Double.isNaN(r)) throw new Exception("System doesn't converge");
            }
            xResult.setText("x = " + Solver.formatToPrecision(result[0], precision));
            yResult.setText("y = " + Solver.formatToPrecision(result[1], precision));
            if (systemSize == 3)
                zResult.setText("z = " + Solver.formatToPrecision(result[2], precision));
        } catch (Exception e) {
            errorSysLabel.setText(e.getMessage());
        }
    }

    public void testSystem1() {
        functionsToggleGroup.selectToggle(toggle2);
        function1.setText("0.1*x^2 + x + 0.2*y^2 - 0.3");
        function2.setText("0.2*x^2 + y - 0.1*x*y - 0.7");

        dx1.setText("0.2*x + 0*y + 1");
        dx2.setText("0.4*x - 0.1*y");
        dy1.setText("0*x + 0.4*y");
        dy2.setText("1 - 0.1*x + 0*y");

        xArg.setText("0.25");
        yArg.setText("0.75");

        epsilonSys.setText("0.00001");
        iterMax.setText("50");

        solveSystem();
    }

    public void testSystem2() {
        functionsToggleGroup.selectToggle(toggle3);
        function1.setText("sin(2*x - y) - 1.2*x - 0.4 + 2*z");
        function2.setText("0.8*x^2 + 1.5*y^2 - 1 + 8*z");
        function3.setText("0.2*x^2 + y +2*z^2");

        dx1.setText("2*cos(2*x - y) - 1.2 + 0*z");
        dx2.setText("1.6*x + 0*y + 0*z");
        dx3.setText("0.4*x - 0*y + 0*z");
        dy1.setText("- cos (2*x - y) +0*z");
        dy2.setText("0*x + 3*y + 0*z");
        dy3.setText("1 - 0*x + 0*y+ 0*z");
        dz1.setText("0*x + 0*y+ 0*z +2");
        dz2.setText("0*x + 0*y+ 0*z + 8");
        dz3.setText("0*x + 0*y+ 4*z");

        xArg.setText("0.4");
        yArg.setText("-0.75");
        zArg.setText("0.3");

        epsilonSys.setText("0.001");
        iterMax.setText("50");

        solveSystem();
    }

    public void systemTabSelected() {
        if (!firstTimeSelected) return;
        firstTimeSelected = false;
        functionsToggleGroup.selectedToggleProperty().addListener((changed, oldValue, newValue) -> {
            selectedBtn = (RadioButton) newValue;
            changeSystemLayout(Integer.parseInt(selectedBtn.getText()));
        });
        systemInvisibleList = new ArrayList<>();
        systemInvisibleList.add(argZBox);
        systemInvisibleList.add(dFunction3Label);
        systemInvisibleList.add(dx3);
        systemInvisibleList.add(dy3);
        systemInvisibleList.add(dzBox);
        systemInvisibleList.add(function3);
        systemInvisibleList.add(zResult);
        for (Node node : systemInvisibleList) {
            node.setVisible(false);
        }
        functionsArray = new TextField[]{function1, function2, function3};
        derivativeArray = new TextField[][]{
                {dx1, dx2, dx3},
                {dy1, dy2, dy3},
                {dz1, dz2, dz3}};
        argsArray = new TextField[]{xArg, yArg, zArg};
        resultsArray = new Label[]{xResult, yResult, zResult};
    }
}

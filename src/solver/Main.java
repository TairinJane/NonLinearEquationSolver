package solver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Function;

class IntegrationResult {
    double integral;
    int steps;
    double calculationError;

    public IntegrationResult(double integral, int steps, double calculationError) {
        this.integral = integral;
        this.steps = steps;
        this.calculationError = calculationError;
    }
}

public class Main {

    private static DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    private static IntegrationResult getIntegralByRungeRule(Function<Double, Double> function, double leftLimit, double rightLimit, double epsilon) {
        double integral = 0;
        double previousIntegral;
        int direction = 1;

        if (leftLimit == rightLimit) {
            return new IntegrationResult(integral, 1, 0);
        } else if (leftLimit > rightLimit) {
            double temp = leftLimit;
            leftLimit = rightLimit;
            rightLimit = temp;
            direction = -1;
        }

        int steps = 10;
        integral = direction * getSimpsonsIntegral(function, leftLimit, rightLimit, steps);

        double theta = 1.0 / 15.0;
        do {
            steps *= 2;
            previousIntegral = integral;
            integral = direction * getSimpsonsIntegral(function, leftLimit, rightLimit, steps);
        } while (theta * Math.abs(integral - previousIntegral) > epsilon);

        return new IntegrationResult(integral, steps, theta * Math.abs(integral - previousIntegral));
    }

    private static double getSimpsonsIntegral(Function<Double, Double> function, double leftLimit, double rightLimit, int steps) {
        double h = (rightLimit - leftLimit) / steps;
        double integral = 0;
        for (int i = 1; i < steps; i += 2) {
            integral += function.apply(leftLimit + h * (i - 1));
            integral += 4 * function.apply(leftLimit + h * i);
            integral += function.apply(leftLimit + h * (i + 1));
        }
        return integral * h / 3;
    }

    private static double getRootByBisectionMethod(Function<Double, Double> f, double a, double b, double epsilon) {
        double x = (a + b) / 2;
        while (Math.abs(f.apply(x)) >= epsilon) {
            if (f.apply(a) * f.apply(x) > 0) a = x;
            else b = x;
            x = (a + b) / 2;
        }
        return x;
    }

    private static double getRootBySecantMethod(Function<Double, Double> f, double a, double b, double epsilon) throws Exception{
        if (f.apply(a)*f.apply(b)>0) throw new Exception("Method is incorrect for this interval");
        double x = b;
        while (Math.abs(f.apply(x)) >= epsilon) {
            x = x - ((a - x) / (f.apply(a) - f.apply(x))) * f.apply(x);
        }
        return x;
    }

    private static double getDoubleFromConsole(BufferedReader reader) {
        double input = 0;
        boolean got = false;
        while (!got) {
            try {
                input = Double.parseDouble(reader.readLine().trim().replaceAll("[,]+", "."));
                got = true;
            } catch (Exception e) {
                System.out.println("Enter correct number:");
            }
        }
        return input;
    }

    public static void main(String[] args) {
        df.setMaximumFractionDigits(10);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter equation coefficients:\n" +
                "ax^4 + bx^3 + cx^2 + dx + e");

        double a = getDoubleFromConsole(reader);
        double b = getDoubleFromConsole(reader);
        double c = getDoubleFromConsole(reader);
        double d = getDoubleFromConsole(reader);
        double e = getDoubleFromConsole(reader);

        Function<Double, Double> function = x -> a * Math.pow(x, 4) + b * Math.pow(x, 3) + c * x * x + d * x + e;

        System.out.println("Enter left limit:");
        double leftLimit = getDoubleFromConsole(reader);
        System.out.println("Enter right limit:");
        double rightLimit = getDoubleFromConsole(reader);
        System.out.println("Enter epsilon:");
        double epsilon = getDoubleFromConsole(reader);
        System.out.println();

        double x = getRootByBisectionMethod(function, leftLimit, rightLimit, epsilon);
        System.out.println("Bisection method:");
        System.out.println("x = " + df.format(x));
        System.out.println("f(x) = " + df.format(function.apply(x)));
        System.out.println();

        try {
            x = getRootBySecantMethod(function, leftLimit, rightLimit, epsilon);
            System.out.println("Secant method:");
            System.out.println("x = " + df.format(x));
            System.out.println("f(x) = " + df.format(function.apply(x)));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}

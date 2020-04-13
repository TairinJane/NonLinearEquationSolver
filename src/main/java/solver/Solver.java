package solver;

import org.mariuszgromada.math.mxparser.Expression;

import java.io.BufferedReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Function;

public class Solver {

    private static DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    public static String formatToPrecision(double x, int precision) {
        df.setMaximumFractionDigits(precision);
        return df.format(x);
    }

    public static double getRootByBisectionMethod(Expression f, double a, double b, double epsilon) throws Exception {
        double x = (a + b) / 2;
        String arg = f.getArgument(0).getArgumentName();
        f.setArgumentValue(arg, x);
        double fx = f.calculate();
        double fa;
        while (Math.abs(fx) >= epsilon && Math.abs(a - b) > epsilon) {
            f.setArgumentValue(arg, a);
            fa = f.calculate();
            if (fa * fx > 0) a = x;
            else b = x;
            x = (a + b) / 2;
            f.setArgumentValue(arg, x);
            fx = f.calculate();
            if (Double.isNaN(fa) || Double.isNaN(fx)) throw new Exception("Method is incorrect for this interval");
        }
        return x;
    }

    public static double getRootBySecantMethod(Expression f, double a, double b, double epsilon) throws Exception {
        int maxn = 1000;
        int n = 0;
        String arg = f.getArgument(0).getArgumentName();
        f.setArgumentValue(arg, a);
        double fa = f.calculate();
        f.setArgumentValue(arg, b);
        double fb = f.calculate();
        if (fa * fb > 0 || Double.isNaN(fa) || Double.isNaN(fb))
            throw new Exception("Method is incorrect for this interval");

        double x = a;
        double xn = a - (b - a) / (fb - fa) * fa;
        System.out.println("sc xn = " + xn);
        f.setArgumentValue(arg, xn);
        double fxn = f.calculate();

        while (Math.abs(xn - x) > epsilon && Math.abs(fxn) > epsilon) {
            x = xn;
            xn = xn - (a - xn) / (fa - fxn) * fxn;
            f.setArgumentValue(arg, xn);
            fxn = f.calculate();
            n++;
            if (n > maxn) throw new Exception("Method doesn't converge on this interval");
        }

        return xn;
    }

    private static double getRootByBisectionMethod(Function<Double, Double> f, double a, double b, double epsilon) {
        double x = (a + b) / 2;
        while (Math.abs(f.apply(x)) >= epsilon && (a - b) >= epsilon) {
            if (f.apply(a) * f.apply(x) > 0) a = x;
            else b = x;
            x = (a + b) / 2;
        }
        return x;
    }

    private static double getRootBySecantMethod(Function<Double, Double> f, double a, double b, double epsilon) throws Exception {
        if (f.apply(a) * f.apply(b) > 0) throw new Exception("Method is incorrect for this interval");
        double x = b;
        double xn = x + 10;
        while (Math.abs(f.apply(x)) >= epsilon && Math.abs(x - xn) > epsilon) {
            xn = x;
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

    /*public static void main(String[] args) {
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

    }*/
}

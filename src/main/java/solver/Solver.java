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
        f.setArgumentValue(arg, xn);
        double fxn = f.calculate();

        while (Math.abs(xn - x) > epsilon && Math.abs(fxn) > epsilon) {
            x = xn;
//            xn = xn - (a - xn) / (fa - fxn) * fxn;
            if (fxn * fa < 0) b = x;
            else a = x;
            f.setArgumentValue(arg, a);
            fa = f.calculate();
            f.setArgumentValue(arg, b);
            fb = f.calculate();
            xn = (a * fb - b * fa) / (fb - fa);
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
        double x = a;
        double xn = a - (b - a) / (f.apply(b) - f.apply(a)) * f.apply(a);
        while (Math.abs(f.apply(x)) >= epsilon && Math.abs(x - xn) > epsilon) {
            xn = x;
            if (f.apply(xn) * f.apply(a) < 0) b = x;
            else a = x;
            x = x - ((a - x) / (f.apply(a) - f.apply(x))) * f.apply(x);
            xn = (a * f.apply(b) - b * f.apply(a)) / (f.apply(b) - f.apply(a));
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
}

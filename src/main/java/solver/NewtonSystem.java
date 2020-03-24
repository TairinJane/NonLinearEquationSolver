package solver;

import org.mariuszgromada.math.mxparser.Expression;

public class NewtonSystem {

    public static double[][] jacobian(Expression[][] d, double[] x) {
        double[][] j = new double[x.length][d.length];
        for (int i = 0; i < d.length; i++) {
            for (int k = 0; k < x.length; k++) {
                d[i][k].setArgumentValue(d[i][k].getArgument(k).getArgumentName(), x[k]);
                j[i][k] = d[i][k].calculate();
            }
        }
        return j;
    }

    public static double[] fx(Expression[] f, double[] x) {
        double[] fx = new double[f.length];
        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < x.length; j++) {
                f[i].setArgumentValue(f[i].getArgument(j).getArgumentName(), x[j]);
            }
            fx[i] = f[i].calculate();
        }
        return fx;
    }

    private static void defineArgs(Expression[] ex, double[] x) {
        for (Expression e : ex) {
            String[] a = e.getMissingUserDefinedArguments();
            for (int i = 0; i < a.length; i++) {
                e.defineArgument(a[i], x[i]);
            }
        }
    }

    public static double[] solveSystem(Expression[] f, Expression[][] d, double[] x, double epsilon) {
        int n = 0;
        int maxn = 10;
        defineArgs(f, x);

        for (Expression[] row : d)
            defineArgs(row, x);

        double[][] j;
        double[] fx, delta, xn = new double[x.length];

        do {
            if (n != 0) x = xn;
            j = jacobian(d, x);
            fx = fx(f, x);
            delta = Matrix.multiply(Matrix.inverse(j), fx);
            xn = Matrix.substract(x, delta);
            n++;
        } while (Matrix.maxAbs(Matrix.substract(x, xn)) > epsilon && n < maxn);

        return xn;
    }

    /*public static void main(String[] args) {
        int n = 0;
        int maxn = 10;
        double epsilon;

// test 1
        Expression[] f = {new Expression("0.1 * x^2 + x + 0.2 * y^2 - 0.3"),
                new Expression("0.2 * x^2 + y - 0.1 * x * y - 0.7")};
        Expression[][] d = {{new Expression("0.2 * x + 0 * y + 1"), new Expression("0 * x + 0.4 * y")},
                {new Expression("0.4 * x - 0.1 * y"), new Expression("1 - 0.1 * x + 0  * y")}};
        double[] x = {0.25, 0.75};
        epsilon = 0.00001;

//test 2
        Expression[] f = {new Expression("sin(2 * x - y) - 1.2 * x - 0.4"),
                new Expression("0.8 * x^2 + 1.5 * y^2 - 1")};
        Expression[][] d = {{new Expression("2 * cos(2 * x - y) - 1.2"), new Expression("- cos (2 * x - y)")},
                {new Expression("1.6 * x + 0 * y"), new Expression("0 * x + 3 * y")}};
        double[] x = {0.4, -0.75};
        epsilon = 0.001;

        defineArgs(f, x);

        for (Expression[] row : d)
            defineArgs(row, x);

        double[][] j;
        double[] fx, delta, xn = new double[x.length];

        do {
            if (n != 0) x = xn;
            j = jacobian(d, x);
            fx = fx(f, x);

            System.out.println("Jacobian:");
            Matrix.printMatrix(j);
            System.out.println();

            System.out.println("F(x):");
            Matrix.printMatrix(fx);
            System.out.println();

            delta = Matrix.multiply(Matrix.inverse(j), fx);
            System.out.println("Delta:");
            Matrix.printMatrix(delta);
            System.out.println();

            xn = Matrix.substract(x, delta);
            System.out.println("xn:");
            Matrix.printMatrix(xn);
            System.out.println();

            n++;
        } while (Matrix.maxAbs(Matrix.substract(x, xn)) > epsilon && n < maxn);

    }*/
}

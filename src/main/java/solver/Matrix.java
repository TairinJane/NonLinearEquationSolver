package solver;

public class Matrix {

    public static double determinant(double[][] matrix) {
        if (matrix.length == 1) return matrix[0][0];
        if (matrix.length != matrix[0].length)
            throw new IllegalStateException("invalid dimensions");

        if (matrix.length == 2)
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        double det = 0;
        for (int i = 0; i < matrix[0].length; i++)
            det += Math.pow(-1, i) * matrix[0][i]
                    * determinant(minor(matrix, 0, i));
        return det;
    }

    public static double[][] inverse(double[][] matrix) {
        double[][] inverse = new double[matrix.length][matrix.length];

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                inverse[i][j] = Math.pow(-1, i + j)
                        * determinant(minor(matrix, i, j));

        double det = 1.0 / determinant(matrix);
        for (int i = 0; i < inverse.length; i++) {
            for (int j = 0; j <= i; j++) {
                double temp = inverse[i][j];
                inverse[i][j] = inverse[j][i] * det;
                inverse[j][i] = temp * det;
            }
        }

        return inverse;
    }

    public static double[][] minor(double[][] matrix, int row, int column) {
        double[][] minor = new double[matrix.length - 1][matrix.length - 1];

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; i != row && j < matrix[i].length; j++)
                if (j != column)
                    minor[i < row ? i : i - 1][j < column ? j : j - 1] = matrix[i][j];
        return minor;
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        if (a[0].length != b.length)
            throw new IllegalStateException("invalid dimensions");

        double[][] matrix = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                double sum = 0;
                for (int k = 0; k < a[i].length; k++)
                    sum += a[i][k] * b[k][j];
                matrix[i][j] = sum;
            }
        }

        return matrix;
    }

    public static double[] multiply(double[][] a, double[] b) {
        if (a[0].length != b.length)
            throw new IllegalStateException("invalid dimensions");

        double[] matrix = new double[b.length];
        for (int i = 0; i < a.length; i++) {
            double sum = 0;
            for (int k = 0; k < a[i].length; k++)
                sum += a[i][k] * b[k];
            matrix[i] = sum;
        }

        return matrix;
    }

    public static double[] substract(double[] a, double[] b) {
        if (a.length != b.length)
            throw new IllegalStateException("invalid dimensions");

        double[] matrix = new double[b.length];
        for (int i = 0; i < a.length; i++) {
            matrix[i] = a[i] - b[i];
        }

        return matrix;
    }

    public static double maxAbs(double[] x) {
        double max = -1;
        for (double xi : x)
            if (Math.abs(xi) > max) max = Math.abs(xi);
        return max;
    }

    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double x : row) System.out.print(x + " ");
            System.out.println();
        }
    }

    public static void printMatrix(double[] matrix) {
        for (double x : matrix) System.out.println(x + " ");
    }

}

package org.bitbucket.eirlis.conetc.core;

import java.util.Arrays;

/**
 * Created by Elena on 22.03.2016.
 */
public class TwoDimensionProblemSolver {
    private static final int mf = 102;

    public double[][] calculateTemperature(
            double l,
            double height,
            double lambda,
            double ro,
            double c,
            double T0,
            double Th,
            double Tc,
            double t
    ) {
        int Nx = 50;
        int Ny = 50;
        double[][] T = new double[Nx][Ny];
        double[] alfa = new double[mf];
        double[] beta = new double[mf];
        double hx = l / (Nx - 1);
        double hy = height / (Ny - 1);
        double a = lambda / ro * c;
        double tau = t / 100.0;

        for (int i = 0; i < Nx; i++) {
            for (int j = 0; j < Ny; j++) {
                T[i][j] = T0;
            }
        }

        double time = 0;
        while (time < t) {
            time += tau;

            for (int j = 0; j < Ny; j++) {
                alfa[1] = 0.0;
                beta[1] = Th;
                for (int i = 2; i <= Nx - 1; i++) {
                    double ai = lambda / (hx * hx);
                    double bi = 2.0 * lambda / (hx * hx) + ro * c / tau;
                    double ci = lambda / (hx * hx);
                    double fi = -ro * c * T[i - 1][j] / tau;

                    alfa[i] = ai / (bi - ci * alfa[i - 1]);
                    beta[i] = (ci * beta[i - 1] - fi) / (bi - ci * alfa[i - 1]);

                        /*double ai = 0.5 * lambda * (2 * j - 1) / (Math.pow(hy, 2) * (j - 1));
                        double ci = 0.5 * lambda * (2 * j - 3) / (Math.pow(hy, 2) * (j - 1));
                        double bi = ai + ci + ro * c / tau;
                        double fi = -ro * c * T[i][j] / tau;

                        alfa[j] = ai / (bi - ci * alfa[j - 1]);
                        beta[j] = (ci * beta[j - 1] - fi) / (bi - ci * alfa[j - 1]); */
                }
                T[Nx - 1][j] = Tc;
                for (int i = Nx - 2; i >= 0; i--) {
                    T[i][j] = alfa[i + 1] * T[i + 1][j] + beta[i + 1];
                }
            }

            for (int i = 1; i < Nx - 1; i++) {
                alfa[1] = 2.0 * a * tau / (2.0 * a * tau + (hy * hy));
                beta[1] = (hy * hy) * T[i][0] / (2.0 * a * tau + (hy * hy));
                for (int j = 2; j <= Ny - 1; j++) {
                    double ai = lambda / (hy * hy);
                    double bi = 2.0 * lambda / (hy * hy) + ro * c / tau;
                    double ci = lambda / (hy * hy);
                    double fi = -ro * c * T[i][j - 1] / tau;

                    alfa[j] = ai / (bi - ci * alfa[j - 1]);
                    beta[j] = (ci * beta[j - 1] - fi) / (bi - ci * alfa[j - 1]);
                }
                T[i][Ny - 1] = (2.0 * a * tau * beta[Ny - 1] + (hy * hy) * T[i][Ny - 1]) / (2.0 * a * tau * (1.0 - alfa[Ny - 1]) + (hy * hy));
                for (int j = Ny - 2; j >= 0; j--) {
                    T[i][j] = alfa[j + 1] * T[i][j + 1]+ beta[j + 1];
                }
            }
                    /*T[i,j]:=alfa[i]*T[i+1,j]+beta[i];*/
        }
        return T;
    }

    public static void main(String[] args) {
        double[][] result =
                new TwoDimensionProblemSolver()
                        .calculateTemperature(
                                0.5,
                                0.5,
                                380,
                                8900,
                                400,
                                5,
                                80,
                                30,
                                60
                        );
        for (int i = 0; i < result.length; i++) {
            System.out.println(Arrays.toString(result[i]));
        }
        System.out.println(Arrays.toString(result[(int)(50 / 0.5 * 0.2)]));
    }
}

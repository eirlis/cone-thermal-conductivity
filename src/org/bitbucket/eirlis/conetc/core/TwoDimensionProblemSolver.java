package org.bitbucket.eirlis.conetc.core;

import java.util.Arrays;

/**
 * Created by Elena on 22.03.2016.
 */
public class TwoDimensionProblemSolver {
    private static final int mf = 102;
    private int nr;
    private int nz;


    public TwoDimensionProblemSolver(int nr, int nz) {
        this.nr = nr;
        this.nz = nz;
    }

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

    /**
     * Рассчитывает поле температуры в определённый момент времени для цилиндра
     * @param radius радиус цилиндра
     * @param height высота цилиндра
     * @param lambda коэффициент теплопроводности
     * @param ro плотность
     * @param c удельная теплоёмкость
     * @param T0 начальная температура
     * @param Th температура на границе
     * @param t время
     */
    public double[][] calculateTemperatureCylinder(
            double radius,
           double height,
           double lambda,
           double ro,
           double c,
           double T0,
           double Th,
           double t
    ) {
        int Nr = nr;
        int Nz = nz;
        double[][] T = new double[Nr][Nz];
        double[] alpha = new double[mf];
        double[] beta = new double[mf];
        double hr = radius / (Nr - 1);
        double hz = height / (Nz - 1);
        double a = lambda / ro * c;
        double tau = t / 100.0;

        for (int i = 0; i < Nr; i++) {
            for (int j = 0; j < Nz; j++) {
                T[i][j] = T0;
            }
        }

        double time = 0;

        while (time < t) {
            time += tau;
            for (int j = 0; j < Nz; j++) {
                alpha[1] = 1.0;
                beta[1] = 0.0;

                for (int i = 2; i <= Nr - 1; i++) {
                    double ai = 0.5 * lambda * (2 * i - 1) / (Math.pow(hr, 2) * (i - 1));
                    double ci = 0.5 * lambda * (2 * i - 3) / (Math.pow(hr, 2) * (i - 1));
                    double bi = ai + ci + ro * c / tau;
                    double fi = -ro * c * T[i - 1][j] / tau;

                    alpha[i] = ai / (bi - ci * alpha[i - 1]);
                    beta[i] = (ci * beta[i - 1] - fi) / (bi - ci * alpha[i - 1]);
                }

                T[Nr - 1][j] = Th;

                for (int i = Nr - 2; i >= 0; i--) {
                    T[i][j] = alpha[i + 1] * T[i + 1][j] + beta[i + 1];
                }
            }

            for (int i = 1; i < Nz - 1; i++) {
                alpha[1] = 2.0 * a * tau / (2.0 * a * tau + (hz * hz));
                beta[1] = (hz * hz) * T[i][0] / (2.0 * a * tau + (hz * hz));
                for (int j = 2; j <= Nz - 1; j++) {
                    double ai = lambda / (hz * hz);
                    double bi = 2.0 * lambda / (hz * hz) + ro * c / tau;
                    double ci = lambda / (hz * hz);
                    double fi = -ro * c * T[i][j - 1] / tau;

                    alpha[j] = ai / (bi - ci * alpha[j - 1]);
                    beta[j] = (ci * beta[j - 1] - fi) / (bi - ci * alpha[j - 1]);
                }
                T[i][Nz - 1] = (2.0 * a * tau * beta[Nz - 1] + (hz * hz) * T[i][Nz - 1]) / (2.0 * a * tau * (1.0 - alpha[Nz - 1]) + (hz * hz));
                for (int j = Nz - 2; j >= 0; j--) {
                    T[i][j] = alpha[j + 1] * T[i][j + 1]+ beta[j + 1];
                }
            }
        }
        return T;
    }

    /**
     * Рассчитывает поле температуры в определённый момент времени для цилиндра
     * @param radius радиус цилиндра
     * @param height высота цилиндра
     * @param lambda коэффициент теплопроводности
     * @param ro плотность
     * @param c удельная теплоёмкость
     * @param T0 начальная температура
     * @param Th температура на границе
     * @param Tbottom температура на дне цилиндра
     * @param Ttop температура на верхушке цилиндра
     * @param t время
     */
    public double[][] calculateTemperatureCylinder(
            double radius,
            double height,
            double lambda,
            double ro,
            double c,
            double T0,
            double Th,
            double Tbottom,
            double Ttop,
            double t
    ) {
        int Nr = nr;
        int Nz = nz;
        double[][] T = new double[Nr][Nz];
        double[] alpha = new double[mf];
        double[] beta = new double[mf];
        double hr = radius / (Nr - 1);
        double hz = height / (Nz - 1);
        double a = lambda / ro * c;
        double tau = t / 100.0;

        for (int i = 0; i < Nr; i++) {
            for (int j = 0; j < Nz; j++) {
                T[i][j] = T0;
            }
        }

        double time = 0;

        while (time < t) {
            time += tau;
            for (int j = 0; j < Nz; j++) {
                alpha[1] = 1.0;
                beta[1] = 0.0;

                for (int i = 2; i <= Nr - 1; i++) {
                    double ai = 0.5 * lambda * (2 * i - 1) / (Math.pow(hr, 2) * (i - 1));
                    double ci = 0.5 * lambda * (2 * i - 3) / (Math.pow(hr, 2) * (i - 1));
                    double bi = ai + ci + ro * c / tau;
                    double fi = -ro * c * T[i - 1][j] / tau;

                    alpha[i] = ai / (bi - ci * alpha[i - 1]);
                    beta[i] = (ci * beta[i - 1] - fi) / (bi - ci * alpha[i - 1]);
                }

                T[Nr - 1][j] = Th;

                for (int i = Nr - 2; i >= 0; i--) {
                    T[i][j] = alpha[i + 1] * T[i + 1][j] + beta[i + 1];
                }
            }

            for (int i = 1; i < Nr - 1; i++) {
                alpha[1] = 0;
                beta[1] = Tbottom;
                for (int j = 2; j <= Nz - 1; j++) {
                    double ai = lambda / (hz * hz);
                    double bi = 2.0 * lambda / (hz * hz) + ro * c / tau;
                    double ci = lambda / (hz * hz);
                    double fi = -ro * c * T[i][j - 1] / tau;

                    alpha[j] = ai / (bi - ci * alpha[j - 1]);
                    beta[j] = (ci * beta[j - 1] - fi) / (bi - ci * alpha[j - 1]);
                }
                T[i][Nz - 1] = Ttop;
                for (int j = Nz - 2; j >= 0; j--) {
                    T[i][j] = alpha[j + 1] * T[i][j + 1]+ beta[j + 1];
                }
            }
        }
        return T;
    }


    public double[][] calculateTemperatureCylinder(
            double radius,
            double height,
            double lambda,
            double ro,
            double c,
            double[][] T0,
            double Th,
            double Tbottom,
            double Ttop,
            double t
    ) {
        int Nr = nr;
        int Nz = nz;
        double[][] T = new double[Nr][Nz];
        double[] alpha = new double[mf];
        double[] beta = new double[mf];
        double hr = radius / (Nr - 1);
        double hz = height / (Nz - 1);
        double a = lambda / ro * c;
        double tau = t / 100.0;

        for (int i = 0; i < Nr; i++) {
            for (int j = 0; j < Nz; j++) {
                T[i][j] = T0[i][j];
            }
        }

        double time = 0;

        while (time < t) {
            time += tau;
            for (int j = 0; j < Nz; j++) {
                alpha[1] = 1.0;
                beta[1] = 0.0;

                for (int i = 2; i <= Nr - 1; i++) {
                    double ai = 0.5 * lambda * (2 * i - 1) / (Math.pow(hr, 2) * (i - 1));
                    double ci = 0.5 * lambda * (2 * i - 3) / (Math.pow(hr, 2) * (i - 1));
                    double bi = ai + ci + ro * c / tau;
                    double fi = -ro * c * T[i - 1][j] / tau;

                    alpha[i] = ai / (bi - ci * alpha[i - 1]);
                    beta[i] = (ci * beta[i - 1] - fi) / (bi - ci * alpha[i - 1]);
                }

                T[Nr - 1][j] = Th;

                for (int i = Nr - 2; i >= 0; i--) {
                    T[i][j] = alpha[i + 1] * T[i + 1][j] + beta[i + 1];
                }
            }

            for (int i = 1; i < Nr - 1; i++) {
                alpha[1] = 0;
                beta[1] = Tbottom;
                for (int j = 2; j <= Nz - 1; j++) {
                    double ai = lambda / (hz * hz);
                    double bi = 2.0 * lambda / (hz * hz) + ro * c / tau;
                    double ci = lambda / (hz * hz);
                    double fi = -ro * c * T[i][j - 1] / tau;

                    alpha[j] = ai / (bi - ci * alpha[j - 1]);
                    beta[j] = (ci * beta[j - 1] - fi) / (bi - ci * alpha[j - 1]);
                }
                T[i][Nz - 1] = Ttop;
                for (int j = Nz - 2; j >= 0; j--) {
                    T[i][j] = alpha[j + 1] * T[i][j + 1]+ beta[j + 1];
                }
            }
        }
        return T;
    }

    public static void main(String[] args) {
        double[][] result =
                new TwoDimensionProblemSolver(50, 50)
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
        result =
                new TwoDimensionProblemSolver(50, 50)
                .calculateTemperatureCylinder(
                        0.1,
                        0.1,
                        0.7,
                        1500,
                        750,
                        20,
                        50,
                        50,
                        50,
                        60
                );
        for (int i = 0; i < result.length; i++) {
            System.out.println(Arrays.toString(result[i]));
        }
        System.out.println(Arrays.toString(result[(int)(50 / 0.5 * 0.2)]));
    }
}

// [19.999999999999993, 19.999999999999993, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 19.999999999999993, 20.000000000000018, 20.0000000000001, 20.000000000000398, 20.000000000001453, 20.00000000000523, 20.00000000001841, 20.00000000006401, 20.000000000219227, 20.000000000738275, 20.000000002442373, 20.00000000793014, 20.000000025250756, 20.000000078784105, 20.00000024066558, 20.00000071917516, 20.000002100518362, 20.000005991081338, 20.000016671525998, 20.00004521984132, 20.000119438756943, 20.00030689476342, 20.000766325444932, 20.001857610155046, 20.0043665509005, 20.009942107707058, 20.021901711112264, 20.046626641339316, 20.095816617070362, 20.189843169554067, 20.362245753660638, 20.664954207710494, 21.17305153142392, 21.986925335278006, 23.228867641719173, 25.0312053921076, 27.514558551305168, 30.758090584036744, 34.767948234258505, 39.45366756175232, 44.62281091861709, 50.0]
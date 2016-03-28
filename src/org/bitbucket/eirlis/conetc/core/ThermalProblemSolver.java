package org.bitbucket.eirlis.conetc.core;

import java.util.Arrays;

/**
 * Created by Elena on 14.03.2016.
 */
public class ThermalProblemSolver {
    private static final int N = 200;

    /**
     * Рассчитывает поле температуры в определённый момент времени для цилиндра
     * @param radius радиус цилиндра
     * @param lambda коэффициент теплопроводности
     * @param ro плотность
     * @param c удельная теплоёмкость
     * @param T0 начальная температура
     * @param Th температура на границе
     * @param t время
     */
    public double[] currentTemperatureCylinder(
            double radius,
            double lambda,
            double ro,
            double c, double T0, double Th, double t) {
        double h = radius / (N - 1);
        double tau = t / 100.0;
        double[] T = new double[N];
        double[] alpha = new double[N];
        double[] beta = new double[N];
        for (int i = 0; i < N; i++) {
            T[i] = T0;
        }

        double time = 0;
        while (time < t) {
            time += tau;
            alpha[1] = 1.0;
            beta[1] = 0.0;

            for (int i = 2; i <= N - 1; i++) {
                double ai = 0.5 * lambda * (2 * i - 1) / (Math.pow(h, 2) * (i - 1));
                double ci = 0.5 * lambda * (2 * i - 3) / (Math.pow(h, 2) * (i - 1));
                double bi = ai + ci + ro * c / tau;
                double fi = -ro * c * T[i] / tau;

                alpha[i] = ai / (bi - ci * alpha[i - 1]);
                beta[i] = (ci * beta[i - 1] - fi) / (bi - ci * alpha[i - 1]);
            }

            T[N - 1] = Th;

            for (int i = N - 2; i >= 0; i--) {
                T[i] = alpha[i + 1] * T[i + 1] + beta[i + 1];
            }
        }
        return T;
    }

    public static void main(String[] args) {
        ThermalProblemSolver tps = new ThermalProblemSolver();
        System.out.println(Arrays.toString(
                tps.currentTemperatureCylinder(
                        0.1,
                        0.7,
                        1500,
                        750,
                        20,
                        50,
                        5
                )
        ));
        System.out.println(Arrays.toString(
                tps.currentTemperatureCylinder(
                        0.1,
                        0.7,
                        1500,
                        750,
                        20,
                        50,
                        10
                )
        ));
        System.out.println(Arrays.toString(
                tps.currentTemperatureCylinder(
                        0.1,
                        0.7,
                        1500,
                        750,
                        20,
                        50,
                        30
                )
        ));
        System.out.println(Arrays.toString(
                tps.currentTemperatureCylinder(
                        0.1,
                        0.7,
                        1500,
                        750,
                        20,
                        50,
                        3600
                )
        ));
    }
}

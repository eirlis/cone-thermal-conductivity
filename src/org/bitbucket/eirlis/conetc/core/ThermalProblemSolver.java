package org.bitbucket.eirlis.conetc.core;
//import java.util.Arrays;

/**
 * Updated by Eduard on 15.03.2016.
 */
public class ThermalProblemSolver {
    private static final int Nx = 5; // количество пространственных узлов в пластине по оси x
    private static final int Ny = 5; // количество пространственных узлов в пластине по оси y

    /**
     * Рассчитывает поле температуры в определённый момент времени для цилиндра
     *
     * @param Nx     количество пространственных узлов в пластине по оси x
     * @param Ny     количество пространственных узлов в пластине по оси y
     * @param radius радиус цилиндра
     * @param height высота цилиндра
     * @param lambda коэффициент теплопроводности материала
     * @param ro     плотность материала
     * @param c      удельная теплоёмкость
     * @param T0     начальная температура
     * @param Th     температура на границе x = 0 области решения
     * @param Tc     температура на границе x = radius области решения
     * @param t      время
     */
    public double[][] currentTemperatureCylinder(int Nx, int Ny, double radius, double height, double lambda, double ro, double c, double T0, double Th, double Tc, double t) {
        double hx = radius / (Nx - 1); // расчётный шаг сетки по координате x
        double hy = height / (Ny - 1); // расчётный шаг сетки по координате y
        double a = lambda / (ro * c); // коэффициент температуропроводности
        double tau = t / 100000.0; // расчётный шаг сетки по времени
        double[][] T = new double[Nx][Ny];
        double[] alpha = new double[Nx]; // Attention
        double[] beta = new double[Ny]; // Attention

        // определяем поле температуры в начальный момент времени
        for (int i = 0; i < Nx; i++) {
            for (int j = 0; j < Ny; j++) {
                T[i][j] = T0;
            }
        }

        // проводим интегрирование нестационарного уравнения теплопроводности
        double time = 0;
        while (time < t) {
            // увеличиваем переменную времени на шаг tau
            time += tau;

            // решаем СЛАУ в направлении оси Ox для определения поля температуры на промежуточном временном слое
            for (int j = 0; j < Ny; j++) {
                // определяем начальные прогоночные коэффициенты на основе левого граничного условия
                alpha[1] = 0.0;
                beta[1] = Th;

                // цикл с параметром для определения прогоночных коэффициентов по формуле
                for (int i = 1; i < Nx - 1; i++) {
                    // коэффициенты канонического представления СЛАУ с трехдиагональной матрицей
                    double ai = lambda / Math.pow(hx, 2);
                    double bi = 2.0 * lambda / Math.pow(hx, 2) + ro * c / tau;
                    double ci = lambda / Math.pow(hx, 2);
                    double fi = -ro * c * T[i][j] / tau;

                    // прогоночные коэффициенты
                    alpha[i] = ai / (bi - ci * alpha[i - 1]);
                    beta[i] = (ci * beta[i - 1] - fi) / (bi - ci * alpha[i - 1]);
                }

                // определяем значение температуры на правой границе на основе правого граничного условия
                T[Nx - 1][j] = Tc;

                // определяем неизвестное поле температуры на промежуточном временном слое
                for (int i = Nx - 2; i >= 0; i--)
                    T[i][j] = alpha[i] * T[i + 1][j] + beta[i];
            }

            // решаем СЛАУ в направлении оси Oy для определения поля температуры на целом временном слое
            for (int i = 1; i < Nx - 1; i++) {
                // определяем начальные прогоночные коэффициенты на основе нижнего граничного условия (при условии, что q1 = 0)
                alpha[1] = 2.0 * a * tau / (2.0 * a * tau + Math.pow(hy, 2));
                beta[1] = Math.pow(hy, 2) * T[i][1] / (2.0 * a * tau + Math.pow(hy, 2));

                // цикл с параметром для определения прогоночных коэффициентов по формуле
                for (int j = 1; j < Ny - 1; j++) {
                    // коэффициенты канонического представления СЛАУ с трехдиагональной матрицей
                    double ai = lambda / Math.pow(hy, 2);
                    double bi = 2.0 * lambda / Math.pow(hy, 2) * ro * c / tau;
                    double ci = lambda / Math.pow(hy, 2);
                    double fi = -ro * c * T[i][j] / tau;

                    // прогоночные коэффициенты
                    alpha[j] = ai / (bi - ci * alpha[j - 1]);
                    beta[j] = (ci * beta[j - 1] - fi) / (bi - ci * alpha[j - 1]);
                }

                // определяем значение температуры на верхней границе (при условии, что q2 = 2)
                T[i][Ny - 1] = (2.0 * a * tau * beta[Ny - 2] + Math.pow(hy, 2) * T[i][Ny - 1]) / (2.0 * a * tau * (1.0 - alpha[Ny - 2]) + Math.pow(hy, 2));

                // определяем неизвестное поле температуры на промежуточном временном слое
                for (int j = Ny - 2; j >= 0; j--)
                    T[i][j] = alpha[j] * T[i][j + 1] + beta[j];
            }
        }
        return T;
    }

    public static void main(String[] args) {

        ThermalProblemSolver tps = new ThermalProblemSolver();

//        System.out.println(tps.currentTemperatureCylinder(
//                Nx, // количество пространственных узлов в пластине по оси x
//                Ny, // количество пространственных узлов в пластине по оси y
//                0.5, // радиус цилиндра
//                0.5, // высота цилиндра
//                401, // коэффициент теплопроводности материала
//                8.92, // плотность материала
//                0.385, // удельная теплоёмкость
//                5, // начальная температура
//                80, // температура на границе x = 0 области решения
//                30, // температура на границе x = radius области решения
//                5 // время
//        ));

        for (int i = 0; i < Nx; i++) {
            System.out.println();
            for (int j = 0; j < Ny; j++)
                System.out.print(tps.currentTemperatureCylinder(Nx, Ny, 50, 50, 401, 8.92, 0.385, 5, 80, 30, 280000)[i][j] + ", ");
        }
    }
}

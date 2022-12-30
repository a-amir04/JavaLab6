import java.awt.geom.Rectangle2D;

public class BurningShip extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;
    /*
    Метод getInitialRange(Rectangle2D.Double range) позволяет генератору фракталов определить
    наиболее «интересную» область комплексной плоскости для конкретного фрактала.
    Начальный диапазон для данного фрактала должен быть от (-2, -2.5) до (2, 1.5).
    Это значит что х = -2, у = -2.5, а ширина и высота = 4.
     */
    @Override
    public void getInitialRange(Rectangle2D.Double range){
        range.x= -2;
        range.y= -2.5;
        range.height= 4;
        range.width= 4;
    }

    // Метод numIterations(double x, double y) реализует итеративную функцию для фрактала Tricorn.
    @Override
    public int numIterations(double x, double y) {
        double r = x;
        double i = y;
        int counter = 0;
        while((counter < MAX_ITERATIONS)) {
            counter++;
            double k = r * r - i * i + x;
            double m = Math.abs(2 * r * i) + y;
            r = k;
            i = m;
            if((r * r + i * i) > 4) break;
        }

        // В случае, если алгоритм дошел до значения MAX_ITERATIONS нужно
        // вернуть -1, чтобы показать, что точка не выходит за границы.
        if (counter == MAX_ITERATIONS)
            return -1;
        return counter;
    }

    // Возвращение имени фрактала.
    @Override
    public String toString() {
        return "Burning Ship";
    }
}

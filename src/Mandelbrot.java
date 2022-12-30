import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;
    /*
    Метод getInitialRange(Rectangle2D.Double range) позволяет генератору фракталов определить
    наиболее «интересную» область комплексной плоскости для конкретного фрактала.
    Этот метод должен установить начальный диапазон в (-2 - 1.5i) - (1 + 1.5i).
    Т.е. значения x и y будут равны -2 и -1.5 соответственно, а ширина и высота будут равны 3.
     */
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x= -2;
        range.y= -1.5;
        range.height= 3;
        range.width= 3;
    }
    // Метод numIterations(double x, double y) реализует итеративную функцию для фрактала Мандельброта.
    @Override
    public int numIterations(double x, double y) {
        double a = x;
        double b = y;
        int counter = 0;
        while(counter < MAX_ITERATIONS) {
            counter++;
            double k = a * a - b * b + x;
            double m = 2 * a * b + y;
            a = k;
            b = m;
            if(a * a + b * b >4)
                break;
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
        return "Mandelbrot";
    }
}



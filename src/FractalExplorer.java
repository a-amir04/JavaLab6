import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;

// FractalExplorer отслеживает несколько важных полей для состояния программ
public class FractalExplorer {
    // Размер экрана
    private final int displaySize;
    // Ссылка JImageDisplay, для обновления отображения в разных методах в процессе вычисления фрактала.
    private JImageDisplay imageDisplay;
    // Объект FractalGenerator. Будет использоваться ссылка на базовый
    // класс для отображения других видов фракталов в будущем.
    private FractalGenerator fractalGenerator;
    // Объект Rectangle2D.Double, указывающий диапазона комплексной плоскости, которая выводится на экран.
    private Rectangle2D.Double range;

    private JComboBox<FractalGenerator> comboBox;
    private int rowsRemaining;
    private JButton buttonReset;
    private JButton buttonSave;
    /*
    Конструктор, который принимает значение размера отображения в качестве аргумента,
    затем сохраняет это значение в соответствующем поле, а также инициализирует
    объекты диапазона и фрактального генератора.
     */
    private FractalExplorer(int displaySize) {
        this.displaySize = displaySize;
        this.fractalGenerator = new Mandelbrot();
        this.range = new Rectangle2D.Double(0,0,0,0);
        fractalGenerator.getInitialRange(this.range);
    }
    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(600);
        fractalExplorer.setGUI();
        fractalExplorer.drawFractal();
    }
    /*
     Метод setGUI(), который инициализирует
     графический интерфейс Swing: JFrame, содержащий объект JimageDisplay, и
     кнопку для сброса отображения, а так же сохранения.
    */
    public void setGUI() {
        JFrame frame = new JFrame("Fractal Generator");
        JPanel jPanel_1 = new JPanel();
        JPanel jPanel_2 = new JPanel();
        JLabel label = new JLabel("Fractal:");

        imageDisplay= new JImageDisplay(displaySize, displaySize);
        imageDisplay.addMouseListener(new MouseListener());

        // Выпадающий список.
        comboBox= new JComboBox<>();
        comboBox.addItem(new Mandelbrot());
        comboBox.addItem(new Tricorn());
        comboBox.addItem(new BurningShip());
        comboBox.addActionListener(new ActionHandler());

        // Кнопка reset.
        buttonReset= new JButton("Reset");
        buttonReset.setActionCommand("Reset");
        buttonReset.addActionListener(new ActionHandler());

        // Кнопка сохранить.
        buttonSave= new JButton("Save image");
        buttonSave.setActionCommand("Save");
        buttonSave.addActionListener(new ActionHandler());

        jPanel_1.add(label, BorderLayout.CENTER);
        jPanel_1.add(comboBox, BorderLayout.CENTER);
        jPanel_2.add(buttonReset, BorderLayout.CENTER);
        jPanel_2.add(buttonSave, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(imageDisplay, BorderLayout.CENTER);
        frame.add(jPanel_1, BorderLayout.NORTH);
        frame.add(jPanel_2, BorderLayout.SOUTH);

        // Закрытия окна по умолчанию.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
        Данные операции правильно разметят содержимое окна, сделают его
        видимым и затем запретят изменение размеров окна:
        */
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
    // Отрисовка фрактала в JImageDisplay.
    private void drawFractal() {
        // Отключаем интерфейс на момент рисования.
        enableGUI(false);
        rowsRemaining= displaySize;
        for (int i = 0; i<displaySize; i++) {
            FractalWorker drawRow = new FractalWorker(i);
            drawRow.execute();
        }
    }

    // Включение - отключение gui
    public void enableGUI(boolean b) {
        buttonSave.setEnabled(b);
        buttonReset.setEnabled(b);
        comboBox.setEnabled(b);
    }
    // Обработчик кнопок.
    public class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Reset")) {
                // Перерисовка фрактала.
                fractalGenerator.getInitialRange(range);
                drawFractal();
            } else if (e.getActionCommand().equals("Save")) {
                // Сохранение.
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int t = fileChooser.showSaveDialog(imageDisplay);
                if (t == JFileChooser.APPROVE_OPTION) {
                    try {
                        ImageIO.write(imageDisplay.getImage(), "png", fileChooser.getSelectedFile());
                    } catch (NullPointerException | IOException ee) {
                        JOptionPane.showMessageDialog(imageDisplay, ee.getMessage(), "Cannot save image", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                fractalGenerator= (FractalGenerator) comboBox.getSelectedItem();
                range = new Rectangle2D.Double(0,0,0,0);
                fractalGenerator.getInitialRange(range);
                drawFractal();
            }
        }
    }

    // Внутренний класс для обработки событий java.awt.event.MouseListener с дисплея.
    public class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            double x = FractalGenerator.getCoord(range.x, range.x+ range.width, displaySize, e.getX());
            double y = FractalGenerator.getCoord(range.y, range.y+ range.width, displaySize, e.getY());
            fractalGenerator.recenterAndZoomRange(range, x, y, 0.5);
            drawFractal();
        }
    }

    /*
     Класс FractalWorker будет отвечать за вычисление значений цвета для
     одной строки фрактала, поэтому ему потребуются два поля:
     целочисленная y- координата вычисляемой строки, и массив чисел типа int для хранения
     вычисленных значений RGB для каждого пикселя в этой строке. Конструктор
     должен будет получать y-координату в качестве параметра и сохранять это.
    */
    public class FractalWorker extends SwingWorker<Object, Object> {
        private final int y_coord;
        private int[] rgb;

        public FractalWorker(int y_coord) {
            this.y_coord= y_coord;
        }

        /*
        Метод doInBackground() вызывается в фоновом потоке и отвечает за
        выполнение длительной задачи. Поэтому в вашей реализации вам нужно будет
        взять часть кода из вашей предыдущей функции «drawfractal» и поместить ее в
        этот метод. Вместо того, чтобы рисовать изображение в окне, цикл должен
        будет сохранить каждое значение RGB в соответствующем элементе
        целочисленного массива. Вы не сможете изменять отображение из этого
        потока, потому что вы нарушите ограничения ограничения потоков Swing.
        */
        @Override
        protected Object doInBackground() throws Exception {
            rgb= new int[displaySize];
            for(int i = 0; i <displaySize; i++) {
                int count = fractalGenerator.numIterations(FractalGenerator.getCoord(range.x, range.x+ range.width, displaySize, i),
                        FractalGenerator.getCoord(range.y, range.y+range.width, displaySize, y_coord));
                if(count == -1)
                    rgb[i] = 0;
                else{
                    double hue = 0.7f + (float) count / 200f;
                    int rgbColor = Color.HSBtoRGB((float) hue, 1f, 1f);
                    rgb[i] = rgbColor;
                }
            }
            return null;
        }
        /*
           Метод done() вызывается, когда фоновая задача завершена, и этот метод вызывается из потока обработки событий Swing. Это означает,
           что  вы можете модифицировать компоненты Swing на ваш вкус. Поэтому в этом методе вы можете перебирать массив строк данных,
           рисуя пиксели, которые были вычислены в doInBackground ().
        */
        @Override
        protected void done() {
            for (int i = 0; i<displaySize; i++) {
                imageDisplay.drawPixel(i, y_coord, rgb[i]);
            }
            imageDisplay.repaint(0,0,y_coord,displaySize,1);
            rowsRemaining--;
            if (rowsRemaining == 0)
                enableGUI(true);
        }
    }
}


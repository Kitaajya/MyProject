package org.designer;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DrawDataPicture extends JFrame {
    private DrawingPanel drawingPanel;
    private JRadioButton curveBtn, barBtn, regBtn;

  // private double[] overtime = {10000,3,10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
  // private double[] salary   = {3000, 3500, 4200, 4800, 5100, 5800, 6200, 6900, 7500, 8000, 8600, 9100, 9800};
     private static double[] overtime;
     private static double[] salary;
    public DrawDataPicture(double[] overtime,double[] salary) {
        setTitle("\u6570\u636E\u56FE\u8868\u7ED8\u5236\u7A0B\u5E8F");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel tipLabel = new JLabel("\u56FE\u8868\u7C7B\u578B: ");
        tipLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        curveBtn = new JRadioButton("\u66F2\u7EBF\u56FE", true);
        barBtn   = new JRadioButton("\u67F1\u72B6\u56FE");
        regBtn   = new JRadioButton("\u56DE\u5F52\u7EBF\u6027\u65B9\u7A0B\u56FE");

        Font btnFont = new Font("SansSerif", Font.PLAIN, 13);
        curveBtn.setFont(btnFont);
        barBtn.setFont(btnFont);
        regBtn.setFont(btnFont);

        ButtonGroup group = new ButtonGroup();
        group.add(curveBtn);
        group.add(barBtn);
        group.add(regBtn);

        curveBtn.addActionListener(_ -> drawingPanel.setChartType(ChartType.CURVE));
        barBtn.addActionListener(_ -> drawingPanel.setChartType(ChartType.BAR));
        regBtn.addActionListener(_ -> drawingPanel.setChartType(ChartType.REGRESSION));

        controlPanel.add(tipLabel);
        controlPanel.add(curveBtn);
        controlPanel.add(barBtn);
        controlPanel.add(regBtn);

        add(controlPanel, BorderLayout.NORTH);

        drawingPanel = new DrawingPanel(overtime, salary);
        add(drawingPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        double[] overtime = {10000,3,10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
        double[] salary   = {3000, 3500, 4200, 4800, 5100, 5800, 6200, 6900, 7500, 8000, 8600, 9100, 9800};
        SwingUtilities.invokeLater(() -> new DrawDataPicture(overtime,salary).setVisible(true));
    }
}

enum ChartType {
    CURVE, BAR, REGRESSION
}

class DrawingPanel extends JPanel {
    private double[] xData;
    private double[] yData;
    private ChartType chartType = ChartType.CURVE;
    private LinearRegression regression;

    private final int PAD_L = 80;
    private final int PAD_R = 60;
    private final int PAD_T = 70;
    private final int PAD_B = 80;

    public DrawingPanel(double[] xData, double[] yData) {
        this.xData = xData;
        this.yData = yData;
        regression = new LinearRegression(xData, yData);
        setBackground(Color.WHITE);
    }

    public void setChartType(ChartType type) {
        this.chartType = type;
        repaint();
    }

    private double findMax(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v > m) m = v;
        return m;
    }

    private double findMin(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v < m) m = v;
        return m;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        int x0 = PAD_L, y0 = H - PAD_B;
        int xMax = W - PAD_R, yMax = PAD_T;
        int dataW = xMax - x0;
        int dataH = y0 - yMax;

        if (dataW <= 0 || dataH <= 0) return;

        int n = xData.length;
        double xMin = findMin(xData);
        double xMaxVal = findMax(xData);
        double yMin = findMin(yData);
        double yMaxVal = findMax(yData);

        double xRange = xMaxVal - xMin;
        double yRange = yMaxVal - yMin;
        if (xRange == 0) xRange = 1;
        if (yRange == 0) yRange = 1;
        // 略微扩充
        xMin -= xRange * 0.05;
        xMaxVal += xRange * 0.05;
        yMin -= yRange * 0.05;
        yMaxVal += yRange * 0.05;
        xRange = xMaxVal - xMin;
        yRange = yMaxVal - yMin;

        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = x0 + (int)((xData[i] - xMin) / xRange * dataW);
            ys[i] = y0 - (int)((yData[i] - yMin) / yRange * dataH);
        }

        // grid + Y axis labels
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
        g2.setColor(new Color(220, 220, 220));
        Font labelFont = new Font("SansSerif", Font.PLAIN, 11);
        g2.setFont(labelFont);
        int gridLines = 6;
        for (int i = 0; i <= gridLines; i++) {
            int y = y0 - (dataH * i / gridLines);
            g2.drawLine(x0, y, xMax, y);
            String val = String.format("%.1f", yMin + yRange * i / gridLines);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(val, x0 - fm.stringWidth(val) - 8, y + 5);
            g2.setColor(new Color(220, 220, 220));
        }

        // axes
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.drawLine(x0, y0, xMax, y0);
        g2.drawLine(x0, y0, x0, yMax);

        // axis labels
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("\u52A0\u73ED\u65F6\u95F4 (h)", xMax - 60, y0 + 45);
        g2.drawString("\u85AA\u8D44 (¥)", x0 - 5, yMax - 10);

        // X axis labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics xfm = g2.getFontMetrics();
        for (int i = 0; i < n; i++) {
            String xl = String.format("%.0f", xData[i]);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(xl, xs[i] - xfm.stringWidth(xl) / 2, y0 + 18);
            // tick
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(xs[i], y0, xs[i], y0 + 5);
        }

        // title
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        String title = "";
        switch (chartType) {
            case CURVE:      title = "\u66F2\u7EBF\u56FE \u2014 \u52A0\u73ED\u65F6\u95F4\u4E0E\u85AA\u8D44\u5173\u7CFB"; break;
            case BAR:        title = "\u67F1\u72B6\u56FE \u2014 \u52A0\u73ED\u65F6\u95F4\u4E0E\u85AA\u8D44\u5173\u7CFB"; break;
            case REGRESSION: title = "\u7EBF\u6027\u56DE\u5F52\u65B9\u7A0B\u56FE"; break;
        }
        FontMetrics tfm = g2.getFontMetrics();
        g2.setColor(Color.BLACK);
        g2.drawString(title, W / 2 - tfm.stringWidth(title) / 2, 35);

        switch (chartType) {
            case CURVE:      drawCurve(g2, xs, ys, n); break;
            case BAR:        drawBar(g2, xs, ys, n, y0, dataW, yMin, yRange, dataH); break;
            case REGRESSION: drawRegression(g2, xs, ys, n, x0, y0, dataW, dataH, xMin, xRange, yMin, yRange); break;
        }
    }

    private void drawCurve(Graphics2D g2, int[] xs, int[] ys, int n) {
        // smooth curve
        g2.setColor(new Color(0, 102, 204));
        g2.setStroke(new BasicStroke(3));
        Path2D path = new Path2D.Double();
        path.moveTo(xs[0], ys[0]);
        for (int i = 0; i < n - 1; i++) {
            int c1x = xs[i] + (xs[i + 1] - xs[i]) / 3;
            int c1y = ys[i];
            int c2x = xs[i + 1] - (xs[i + 1] - xs[i]) / 3;
            int c2y = ys[i + 1];
            path.curveTo(c1x, c1y, c2x, c2y, xs[i + 1], ys[i + 1]);
        }
        g2.draw(path);

        // data points
        for (int i = 0; i < n; i++) {
            g2.setColor(new Color(220, 50, 50));
            g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
            g2.setColor(Color.WHITE);
            g2.fillOval(xs[i] - 2, ys[i] - 2, 4, 4);
        }
    }

    private void drawBar(Graphics2D g2, int[] xs, int[] ys, int n, int y0, int dataW,
                         double yMin, double yRange, int dataH) {
        int barW = Math.min(dataW / (n * 3), 35);
        if (barW < 8) barW = 8;

        Color[] colors = {
                new Color(255, 99, 71), new Color(60, 179, 113), new Color(30, 144, 255),
                new Color(255, 215, 0), new Color(147, 112, 219), new Color(255, 140, 0)
        };

        for (int i = 0; i < n; i++) {
            g2.setColor(colors[i % colors.length]);
            int x = xs[i] - barW / 2;
            int barH = y0 - ys[i];
            g2.fillRoundRect(x, ys[i], barW, barH, 6, 6);
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(colors[i % colors.length].darker());
            g2.drawRoundRect(x, ys[i], barW, barH, 6, 6);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            String val = String.format("%.0f", yData[i]);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(val, x + barW / 2 - fm.stringWidth(val) / 2, ys[i] - 6);
        }
    }

    private void drawRegression(Graphics2D g2, int[] xs, int[] ys, int n,
                                int x0, int y0, int dataW, int dataH,
                                double xMin, double xRange, double yMin, double yRange) {
        // scatter points
        for (int i = 0; i < n; i++) {
            g2.setColor(new Color(70, 130, 180));
            g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
            g2.setColor(Color.WHITE);
            g2.fillOval(xs[i] - 2, ys[i] - 2, 4, 4);
        }

        // regression line (draw from xMin to xMaxVal in data space)
        double a = regression.getSlope();
        double b = regression.getIntercept();

        int rx1 = x0;
        int ry1 = y0 - (int)((a * xMin + b - yMin) / yRange * dataH);
        int rx2 = x0 + dataW;
        int ry2 = y0 - (int)((a * (xMin + xRange) + b - yMin) / yRange * dataH);

        g2.setColor(new Color(200, 30, 30));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(rx1, ry1, rx2, ry2);

        // confidence band (simple visual: draw parallel dashed lines)
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 4}, 0));
        g2.setColor(new Color(200, 30, 30, 120));

        double stdErr = regression.getStdError();
        int offsetY = (int)(stdErr / yRange * dataH);

        g2.drawLine(rx1, ry1 - offsetY, rx2, ry2 - offsetY);
        g2.drawLine(rx1, ry1 + offsetY, rx2, ry2 + offsetY);

        // equation box
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(x0 + 15, yMax() + 20, 260, 130, 10, 10);

        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(x0 + 15, yMax() + 20, 260, 130, 10, 10);

        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.setColor(new Color(180, 30, 30));
        String eq = String.format("y = %.4f x + %.4f", a, b);
        g2.drawString(eq, x0 + 25, yMax() + 45);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g2.drawString(String.format("n = %d", regression.getN()), x0 + 25, yMax() + 68);
        g2.drawString(String.format("R\u00B2 = %.6f", regression.getRSquared()), x0 + 25, yMax() + 88);
        g2.drawString(String.format("Pearson r = %.6f", regression.getCorrelation()), x0 + 25, yMax() + 108);
        g2.drawString(String.format("Std Err = %.4f", stdErr), x0 + 25, yMax() + 128);

        // legend
        int lx = x0 + dataW - 150;
        int ly = yMax() + 20;
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(lx, ly, 135, 55, 8, 8);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(lx, ly, 135, 55, 8, 8);

        g2.setColor(new Color(70, 130, 180));
        g2.fillOval(lx + 10, ly + 12, 10, 10);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("\u6570\u636E\u70B9", lx + 25, ly + 22);

        g2.setColor(new Color(200, 30, 30));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(lx + 10, ly + 40, lx + 20, ly + 40);
        g2.setColor(Color.BLACK);
        g2.drawString("\u56DE\u5F52\u7EBF", lx + 25, ly + 44);
    }

    private int yMax() { return PAD_T; }
}

class LinearRegression {
    private double slope, intercept, rSquared, correlation, stdError;
    private int n;

    public LinearRegression(double[] x, double[] y) {
        n = Math.min(x.length, y.length);
        if (n < 2) return;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }
        double meanX = sumX / n;
        double meanY = sumY / n;
        slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        intercept = meanY - slope * meanX;

        // R² and Pearson r
        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < n; i++) {
            double pred = slope * x[i] + intercept;
            ssRes += (y[i] - pred) * (y[i] - pred);
            ssTot += (y[i] - meanY) * (y[i] - meanY);
        }
        rSquared = 1 - ssRes / ssTot;
        correlation = (n * sumXY - sumX * sumY) /
                Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        // standard error of estimate
        stdError = Math.sqrt(ssRes / (n - 2));
    }

    public double getSlope() { return slope; }
    public double getIntercept() { return intercept; }
    public double getRSquared() { return rSquared; }
    public double getCorrelation() { return correlation; }
    public double getStdError() { return stdError; }
    public int getN() { return n; }
}

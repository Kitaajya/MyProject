package wakeup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class WeekPanel extends JPanel {
    private static final String[] DAY_HEADERS = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final String[] PERIOD_LABELS = {
        "第1节\n8:00", "第2节\n8:55", "第3节\n10:00", "第4节\n10:55",
        "第5节\n12:00", "第6节\n14:00", "第7节\n14:55", "第8节\n16:00",
        "第9节\n16:55", "第10节\n18:00", "第11节\n19:00", "第12节\n19:55"
    };

    private static final Color GRID_LINE = new Color(220, 220, 220);
    private static final Color HEADER_BG = new Color(245, 245, 245);
    private static final Color EMPTY_CELL_BG = Color.WHITE;
    private static final Color TODAY_HIGHLIGHT = new Color(255, 243, 224);
    private static final Color CURRENT_WEEK_HIGHLIGHT = new Color(232, 245, 233);

    protected Course selectedCourse = null;
    protected int selectedDay = -1;
    protected int selectedPeriod = -1;

    public WeekPanel() {
        setPreferredSize(new Dimension(900, 650));
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY(), e.getClickCount());
            }
        });
    }

    private int getPeriodCount() { return 12; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int periods = getPeriodCount();
        int w = getWidth(), h = getHeight();
        int headerH = 40, rowH = Math.max(50, (h - headerH) / periods);
        int timeColW = 70, colW = (w - timeColW) / 7;
        int currentWeek = getCurrentWeek();

        drawGrid(g2, w, h, headerH, rowH, timeColW, colW, periods, currentWeek);
        drawHeader(g2, headerH, rowH, timeColW, colW, currentWeek);
        drawTimeLabels(g2, headerH, rowH, timeColW);
        drawCourses(g2, headerH, rowH, timeColW, colW, currentWeek);
        drawSelection(g2, headerH, rowH, timeColW, colW);
    }

    private void drawGrid(Graphics2D g2, int w, int h, int hh, int rh, int tw, int cw, int periods, int week) {
        g2.setColor(EMPTY_CELL_BG);
        g2.fillRect(0, 0, w, h);

        // highlight today column
        int today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);
        int todayCol = (today == java.util.Calendar.SUNDAY) ? 7 : today - 1;
        g2.setColor(TODAY_HIGHLIGHT);
        g2.fillRect(tw + (todayCol - 1) * cw, hh, cw, h - hh);

        // highlight current week (if within range) - faint overlay on header
        g2.setColor(CURRENT_WEEK_HIGHLIGHT);
        g2.fillRect(0, 0, w, hh);

        // grid lines
        g2.setColor(GRID_LINE);
        for (int col = 0; col <= 7; col++) {
            int x = (col == 0) ? tw : tw + (col - 1) * cw;
            g2.drawLine(x, hh, x, h);
        }
        for (int row = 0; row <= periods; row++) {
            int y = hh + row * rh;
            g2.drawLine(0, y, w, y);
        }
    }

    private void drawHeader(Graphics2D g2, int hh, int rh, int tw, int cw, int week) {
        g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(HEADER_BG);
        g2.fillRect(0, 0, tw, hh);
        g2.setColor(Color.DARK_GRAY);
        String weekText = "第" + week + "周";
        int tw2 = fm.stringWidth(weekText);
        g2.drawString(weekText, (tw - tw2) / 2, (hh + fm.getAscent()) / 2 - 2);

        for (int i = 0; i < 7; i++) {
            int x = tw + i * cw;
            g2.setColor(HEADER_BG);
            g2.fillRect(x, 0, cw, hh);
            g2.setColor(Color.DARK_GRAY);
            String text = DAY_HEADERS[i + 1];
            int tw3 = fm.stringWidth(text);
            g2.drawString(text, x + (cw - tw3) / 2, (hh + fm.getAscent()) / 2 - 2);
        }
    }

    private void drawTimeLabels(Graphics2D g2, int hh, int rh, int tw) {
        g2.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < getPeriodCount() && i < PERIOD_LABELS.length; i++) {
            int y = hh + i * rh;
            g2.setColor(HEADER_BG);
            g2.fillRect(0, y, tw, rh);
            g2.setColor(new Color(100, 100, 100));
            String[] lines = PERIOD_LABELS[i].split("\n");
            if (lines.length > 0) {
                int tw2 = fm.stringWidth(lines[0]);
                g2.drawString(lines[0], (tw - tw2) / 2, y + rh / 2 - 3);
            }
        }
    }

    private void drawCourses(Graphics2D g2, int hh, int rh, int tw, int cw, int week) {
        List<Course> courses = getCourses();
        if (courses == null) return;

        g2.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        for (Course c : courses) {
            if (!c.isActiveInWeek(week)) continue;

            int x = tw + (c.getDayOfWeek() - 1) * cw + 2;
            int y = hh + (c.getStartPeriod() - 1) * rh + 2;
            int w = cw - 4;
            int h = c.getSpan() * rh - 4;

            g2.setColor(c.getColor());
            g2.fillRoundRect(x, y, w, h, 8, 8);
            g2.setColor(c.getColor().darker());
            g2.drawRoundRect(x, y, w, h, 8, 8);

            g2.setColor(Color.WHITE);
            int textY = y + 18;
            String name = c.getName();
            if (fm.stringWidth(name) > w - 8) {
                name = truncateString(name, fm, w - 8);
            }
            g2.drawString(name, x + 5, textY);

            if (h > 40) {
                String info = "";
                if (!c.getClassroom().isEmpty()) info = c.getClassroom();
                else if (!c.getTeacher().isEmpty()) info = c.getTeacher();

                if (!info.isEmpty()) {
                    textY += 16;
                    g2.setFont(new Font("微软雅黑", Font.PLAIN, 10));
                    if (fm.stringWidth(info) > w - 8) {
                        info = truncateString(info, fm, w - 8);
                    }
                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.drawString(info, x + 5, textY);
                    g2.setFont(new Font("微软雅黑", Font.PLAIN, 12));
                }
            }
        }
    }

    private void drawSelection(Graphics2D g2, int hh, int rh, int tw, int cw) {
        if (selectedDay < 0 || selectedPeriod < 0) return;
        int x = tw + (selectedDay - 1) * cw + 2;
        int y = hh + (selectedPeriod - 1) * rh + 2;
        g2.setColor(new Color(255, 0, 0, 80));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, cw - 4, rh - 4);
        g2.setStroke(new BasicStroke(1));
    }

    private void handleClick(int mx, int my, int clicks) {
        int periods = getPeriodCount();
        int headerH = 40, rowH = Math.max(50, (getHeight() - headerH) / periods);
        int timeColW = 70, colW = Math.max(80, (getWidth() - timeColW) / 7);

        if (my < headerH || mx < timeColW) return;

        int day = (mx - timeColW) / colW + 1;
        int period = (my - headerH) / rowH + 1;

        if (day < 1 || day > 7 || period < 1 || period > periods) return;

        selectedDay = day;
        selectedPeriod = period;

        // find if there's a course at this cell
        List<Course> courses = getCourses();
        int currentWeek = getCurrentWeek();

        Course clicked = null;
        if (courses != null) {
            for (Course c : courses) {
                if (!c.isActiveInWeek(currentWeek)) continue;
                if (c.getDayOfWeek() == day &&
                    period >= c.getStartPeriod() && period <= c.getEndPeriod()) {
                    clicked = c;
                    break;
                }
            }
        }

        if (clicks == 2) {
            if (clicked != null) {
                editCourse(clicked);
            } else {
                addCourse(day, period);
            }
        } else if (clicks == 1 && clicked != null) {
            selectedCourse = clicked;
            int choice = JOptionPane.showOptionDialog(this,
                "课程：" + clicked.getName() + "\n教师：" + clicked.getTeacher() +
                "\n教室：" + clicked.getClassroom(),
                "课程操作", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new String[]{"编辑", "删除"}, "编辑");
            if (choice == 0) editCourse(clicked);
            else if (choice == 1) deleteCourse(clicked);
        }

        repaint();
    }

    private String truncateString(String s, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(s) <= maxWidth) return s;
        while (!s.isEmpty() && fm.stringWidth(s + "…") > maxWidth) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "…";
    }

    // methods to be overridden by MainFrame integration
    protected List<Course> getCourses() { return null; }
    protected int getCurrentWeek() { return 1; }
    protected void addCourse(int day, int period) {}
    protected void editCourse(Course c) {}
    protected void deleteCourse(Course c) {}
}

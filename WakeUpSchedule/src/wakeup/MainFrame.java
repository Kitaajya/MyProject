package wakeup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.Base64;

public class MainFrame extends JFrame {
    private ScheduleData data;
    private SchedulePanel schedulePanel;
    private JLabel weekLabel;
    private JComboBox<String> scheduleSelector;
    private File dataFile;
    private JPanel bgPanel;
    private javax.swing.Timer autoRefreshTimer;
    private JCheckBoxMenuItem miAutoWeek;

    private static final String DATA_FILE_PATH = System.getProperty("user.dir") + "/data/schedule.dat";

    public MainFrame() {
        super("WakeUp课程表");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        dataFile = new File(DATA_FILE_PATH);
        data = loadData();

        if (data.getCourses().isEmpty()) {
            String tmpDir = System.getenv("TEMP");
            if (tmpDir == null) tmpDir = System.getProperty("java.io.tmpdir");
            File pf = new File(tmpDir, "courses_imported.txt");
            if (pf.exists()) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pf), StandardCharsets.UTF_8))) {
                    br.readLine();
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split("\\|");
                        if (parts.length < 7) continue;
                        Course c = new Course();
                        c.setName(parts[0].trim());
                        c.setTeacher(parts.length > 1 ? parts[1].trim() : "");
                        c.setClassroom(parts.length > 2 ? parts[2].trim() : "");
                        try {
                            c.setDayOfWeek(Integer.parseInt(parts[3].trim()));
                            c.setStartPeriod(Integer.parseInt(parts[4].trim()));
                            c.setEndPeriod(Integer.parseInt(parts[5].trim()));
                            c.setStartWeek(Integer.parseInt(parts[6].trim()));
                            c.setEndWeek(parts.length > 7 ? Integer.parseInt(parts[7].trim()) : c.getStartWeek());
                            if (parts.length > 8 && !parts[8].trim().isEmpty()) {
                                c.setWeekType(Course.WeekType.valueOf(parts[8].trim().toUpperCase()));
                            }
                        } catch (NumberFormatException ignored) { continue; }
                        data.addCourse(c);
                    }
                } catch (Exception ignored) {}
            }
        }

        initUI();
        updateScheduleView();
        recalcAutoWeek();
        startAutoRefresh();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (autoRefreshTimer != null) autoRefreshTimer.stop();
                saveData();
            }
        });
    }

    private void startAutoRefresh() {
        autoRefreshTimer = new javax.swing.Timer(3600000, e -> {
            if (miAutoWeek.isSelected()) recalcAutoWeek();
        });
        autoRefreshTimer.start();
    }

    private void recalcAutoWeek() {
        if (!miAutoWeek.isSelected()) return;
        ScheduleData.SingleSchedule s = data.getActiveSchedule();
        int calculated = s.calculateCurrentWeek();
        if (calculated != s.getSemesterWeek()) {
            s.setSemesterWeek(calculated);
            SwingUtilities.invokeLater(this::updateScheduleView);
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSchedule = new JMenu("课表管理");
        JMenuItem miNew = new JMenuItem("新建课表");
        JMenuItem miRename = new JMenuItem("重命名课表");
        JMenuItem miDelete = new JMenuItem("删除课表");
        menuSchedule.add(miNew);
        menuSchedule.add(miRename);
        menuSchedule.add(miDelete);
        menuBar.add(menuSchedule);

        JMenu menuImport = new JMenu("导入/导出");
        JMenuItem miImportCSV = new JMenuItem("从 CSV 文件导入");
        JMenuItem miExportCSV = new JMenuItem("导出到 CSV 文件");
        JMenuItem miImportPipe = new JMenuItem("从课表 TSV 导入");
        JMenuItem miImportCode = new JMenuItem("从分享码导入");
        JMenuItem miExportCode = new JMenuItem("导出分享码");
        menuImport.add(miImportCSV);
        menuImport.add(miImportPipe);
        menuImport.add(miExportCSV);
        menuImport.addSeparator();
        menuImport.add(miImportCode);
        menuImport.add(miExportCode);
        menuBar.add(menuImport);

        JMenu menuView = new JMenu("视图");
        JMenuItem miSetWeeks = new JMenuItem("学期设置");
        miAutoWeek = new JCheckBoxMenuItem("自动计算当前周", true);
        menuView.add(miSetWeeks);
        menuView.add(miAutoWeek);
        menuBar.add(menuView);

        JMenu menuHelp = new JMenu("帮助");
        JMenuItem miAbout = new JMenuItem("关于");
        menuHelp.add(miAbout);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);

        // toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JButton btnPrev = new JButton("◀ 上一周");
        JButton btnNext = new JButton("下一周 ▶");
        JButton btnToday = new JButton("本周");
        weekLabel = new JLabel("第 1 周", SwingConstants.CENTER);
        weekLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        weekLabel.setPreferredSize(new Dimension(100, 30));

        scheduleSelector = new JComboBox<>();
        scheduleSelector.setPreferredSize(new Dimension(140, 28));
        scheduleSelector.addActionListener(e -> {
            if (scheduleSelector.getSelectedIndex() >= 0) {
                data.setActiveIndex(scheduleSelector.getSelectedIndex());
                updateScheduleView();
            }
        });

        toolbar.add(new JLabel("当前课表: "));
        toolbar.add(scheduleSelector);
        toolbar.addSeparator(new Dimension(20, 10));
        toolbar.add(btnPrev);
        toolbar.add(weekLabel);
        toolbar.add(btnNext);
        toolbar.add(btnToday);
        toolbar.add(Box.createHorizontalGlue());

        JButton btnAddCourse = new JButton("+ 添加课程");
        btnAddCourse.setFont(new Font("微软雅黑", Font.BOLD, 12));
        btnAddCourse.setBackground(new Color(66, 133, 244));
        btnAddCourse.setForeground(Color.WHITE);
        btnAddCourse.setOpaque(true);
        btnAddCourse.setBorderPainted(false);
        toolbar.add(btnAddCourse);

        add(toolbar, BorderLayout.NORTH);

        // center - schedule panel
        bgPanel = new JPanel(new BorderLayout());
        bgPanel.setBackground(Color.WHITE);
        schedulePanel = new SchedulePanel();
        bgPanel.add(schedulePanel, BorderLayout.CENTER);
        add(bgPanel, BorderLayout.CENTER);

        // bottom status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 3));
        statusBar.setBackground(new Color(245, 245, 245));
        JLabel statusLabel = new JLabel("双击空白处添加课程 | 单击课程进行编辑/删除 | 支持单双周");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);

        // actions
        btnPrev.addActionListener(e -> changeWeek(-1));
        btnNext.addActionListener(e -> changeWeek(1));
        btnToday.addActionListener(e -> goToCurrentWeek());
        btnAddCourse.addActionListener(e -> addCourseAtSelection());
        miNew.addActionListener(e -> newSchedule());
        miRename.addActionListener(e -> renameSchedule());
        miDelete.addActionListener(e -> deleteSchedule());
        miImportCSV.addActionListener(e -> importFromCSV());
        miImportPipe.addActionListener(e -> importFromPipeFile());
        miExportCSV.addActionListener(e -> exportToCSV());
        miImportCode.addActionListener(e -> importFromCode());
        miExportCode.addActionListener(e -> exportCode());
        miAutoWeek.addActionListener(e -> { if (miAutoWeek.isSelected()) recalcAutoWeek(); });
        miSetWeeks.addActionListener(e -> semesterSettings());
        miAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "WakeUp课程表 v1.0\n极简 · 轻量 · 无广告\n\n基于 Java Swing 构建",
            "关于", JOptionPane.INFORMATION_MESSAGE));
    }

    private void changeWeek(int delta) {
        ScheduleData.SingleSchedule s = data.getActiveSchedule();
        int newWeek = Math.max(1, Math.min(s.getTotalWeeks(), s.getSemesterWeek() + delta));
        s.setSemesterWeek(newWeek);
        updateScheduleView();
    }

    private void goToCurrentWeek() {
        data.getActiveSchedule().setSemesterWeek(1);
        updateScheduleView();
    }

    private void updateScheduleView() {
        ScheduleData.SingleSchedule s = data.getActiveSchedule();
        weekLabel.setText("第 " + s.getSemesterWeek() + " 周 / 共 " + s.getTotalWeeks() + " 周");
        schedulePanel.refresh();
        updateScheduleSelector();
    }

    private void updateScheduleSelector() {
        scheduleSelector.removeActionListener(scheduleSelector.getActionListeners().length > 0 ?
            scheduleSelector.getActionListeners()[0] : null);
        scheduleSelector.removeAllItems();
        for (String name : data.getScheduleNames()) {
            scheduleSelector.addItem(name);
        }
        if (data.getActiveIndex() >= 0 && data.getActiveIndex() < scheduleSelector.getItemCount()) {
            scheduleSelector.setSelectedIndex(data.getActiveIndex());
        }
        scheduleSelector.addActionListener(e -> {
            if (scheduleSelector.getSelectedIndex() >= 0) {
                data.setActiveIndex(scheduleSelector.getSelectedIndex());
                updateScheduleView();
            }
        });
    }

    private void newSchedule() {
        String name = JOptionPane.showInputDialog(this, "请输入课表名称：", "新建课表", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            data.addSchedule(name.trim());
            data.setActiveIndex(data.getScheduleCount() - 1);
            updateScheduleView();
        }
    }

    private void renameSchedule() {
        String oldName = data.getActiveSchedule().getName();
        Object result = JOptionPane.showInputDialog(this, "请输入新的课表名称：", "重命名课表", JOptionPane.PLAIN_MESSAGE, null, null, oldName);
        String name = (result instanceof String) ? (String) result : null;
        if (name != null && !name.trim().isEmpty()) {
            data.renameSchedule(data.getActiveIndex(), name.trim());
            updateScheduleView();
        }
    }

    private void deleteSchedule() {
        if (data.getScheduleCount() <= 1) {
            JOptionPane.showMessageDialog(this, "至少保留一个课表");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "确定删除课表「" +
            data.getActiveSchedule().getName() + "」？", "删除课表", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            data.removeSchedule(data.getActiveIndex());
            updateScheduleView();
        }
    }

    private void semesterSettings() {
        ScheduleData.SingleSchedule s = data.getActiveSchedule();
        JSpinner spWeeks = new JSpinner(new SpinnerNumberModel(s.getTotalWeeks(), 1, 54, 1));
        JSpinner spPeriods = new JSpinner(new SpinnerNumberModel(s.getPeriodsPerDay(), 1, 14, 1));

        JSpinner spYear = new JSpinner(new SpinnerNumberModel(s.getSemesterStart().getYear(), 2020, 2035, 1));
        JSpinner spMonth = new JSpinner(new SpinnerNumberModel(s.getSemesterStart().getMonthValue(), 1, 12, 1));
        JSpinner spDay = new JSpinner(new SpinnerNumberModel(s.getSemesterStart().getDayOfMonth(), 1, 31, 1));

        Object[] msg = {
            "总周数：", spWeeks,
            "每天节数：", spPeriods,
            "", new JLabel("学期开始日期："),
            "年：", spYear, "月：", spMonth, "日：", spDay
        };
        int r = JOptionPane.showConfirmDialog(this, msg, "学期设置", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            s.setTotalWeeks((Integer) spWeeks.getValue());
            s.setPeriodsPerDay((Integer) spPeriods.getValue());
            try {
                LocalDate d = LocalDate.of((Integer) spYear.getValue(), (Integer) spMonth.getValue(), (Integer) spDay.getValue());
                s.setSemesterStart(d);
            } catch (Exception ignored) {}
            if (miAutoWeek.isSelected()) s.setSemesterWeek(s.calculateCurrentWeek());
            updateScheduleView();
        }
    }

    private void addCourseAtSelection() {
        int day = schedulePanel.getSelectedDay();
        int period = schedulePanel.getSelectedPeriod();
        if (day < 0) day = 1;
        if (period < 0) period = 1;
        addCourse(day, period);
    }

    private void addCourse(int day, int period) {
        CourseDialog dlg = new CourseDialog(this, data.getActiveSchedule().getPeriodsPerDay());
        dlg.getCourse().setDayOfWeek(day);
        dlg.getCourse().setStartPeriod(period);
        dlg.getCourse().setEndPeriod(Math.min(period + 1, data.getActiveSchedule().getPeriodsPerDay()));
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            data.addCourse(dlg.getCourse());
            updateScheduleView();
        }
    }

    private void editCourse(Course c) {
        CourseDialog dlg = new CourseDialog(this, c, data.getActiveSchedule().getPeriodsPerDay());
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            Course updated = dlg.getCourse();
            c.setName(updated.getName());
            c.setTeacher(updated.getTeacher());
            c.setClassroom(updated.getClassroom());
            c.setDayOfWeek(updated.getDayOfWeek());
            c.setStartPeriod(updated.getStartPeriod());
            c.setEndPeriod(updated.getEndPeriod());
            c.setStartWeek(updated.getStartWeek());
            c.setEndWeek(updated.getEndWeek());
            c.setWeekType(updated.getWeekType());
            c.setColor(updated.getColor());
            updateScheduleView();
        }
    }

    private void deleteCourse(Course c) {
        int r = JOptionPane.showConfirmDialog(this, "确定删除课程「" + c.getName() + "」？",
            "删除课程", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            data.removeCourse(c);
            updateScheduleView();
        }
    }

    private ScheduleData loadData() {
        if (dataFile.exists()) {
            try {
                return ScheduleData.loadFromFile(dataFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("加载数据失败: " + e.getMessage());
            }
        }
        return new ScheduleData();
    }

    private void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            ScheduleData.saveToFile(data, dataFile.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存数据失败: " + e.getMessage());
        }
    }

    private void importFromCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择 CSV 文件导入");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();

        try (BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            int imported = 0;
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = parseCSVLine(line);
                if (parts.length < 7) continue;

                Course c = new Course();
                c.setName(parts[0].trim());
                c.setTeacher(parts.length > 1 ? parts[1].trim() : "");
                c.setClassroom(parts.length > 2 ? parts[2].trim() : "");
                try {
                    c.setDayOfWeek(Integer.parseInt(parts[3].trim()));
                    c.setStartPeriod(Integer.parseInt(parts[4].trim()));
                    c.setEndPeriod(Integer.parseInt(parts[5].trim()));
                    c.setStartWeek(Integer.parseInt(parts[6].trim()));
                    c.setEndWeek(parts.length > 7 ? Integer.parseInt(parts[7].trim()) : c.getStartWeek());
                    if (parts.length > 8 && !parts[8].trim().isEmpty()) {
                        c.setWeekType(Course.WeekType.valueOf(parts[8].trim().toUpperCase()));
                    }
                    if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                        c.setColorRGB(Integer.parseUnsignedInt(parts[9].trim()));
                    }
                } catch (NumberFormatException ignored) {
                    continue;
                }
                data.addCourse(c);
                imported++;
            }
            JOptionPane.showMessageDialog(this, "成功导入 " + imported + " 门课程");
            updateScheduleView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage());
        }
    }

    private void exportToCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出课表到 CSV");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            bw.write("课程名称,教师,教室,星期(1-7),开始节次,结束节次,开始周,结束周,周类型(ALL/ODD/EVEN),颜色RGB");
            bw.newLine();
            for (Course c : data.getCourses()) {
                bw.write(escapeCSV(c.getName()) + "," +
                         escapeCSV(c.getTeacher()) + "," +
                         escapeCSV(c.getClassroom()) + "," +
                         c.getDayOfWeek() + "," +
                         c.getStartPeriod() + "," +
                         c.getEndPeriod() + "," +
                         c.getStartWeek() + "," +
                         c.getEndWeek() + "," +
                         c.getWeekType().name() + "," +
                         Integer.toUnsignedString(c.getColorRGB()));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "已导出到: " + f.getName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage());
        }
    }

    private void importFromCode() {
        String code = JOptionPane.showInputDialog(this,
            "请输入分享码：", "从分享码导入", JOptionPane.PLAIN_MESSAGE);
        if (code == null || code.trim().isEmpty()) return;

        try {
            byte[] decoded = Base64.getDecoder().decode(code.trim());
            String content = new String(decoded, StandardCharsets.UTF_8);
            String[] lines = content.split("\n");
            int imported = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 7) continue;

                Course c = new Course();
                c.setName(parts[0]);
                c.setTeacher(parts.length > 1 ? parts[1] : "");
                c.setClassroom(parts.length > 2 ? parts[2] : "");
                c.setDayOfWeek(Integer.parseInt(parts[3]));
                c.setStartPeriod(Integer.parseInt(parts[4]));
                c.setEndPeriod(Integer.parseInt(parts[5]));
                c.setStartWeek(Integer.parseInt(parts[6]));
                c.setEndWeek(parts.length > 7 ? Integer.parseInt(parts[7]) : c.getStartWeek());
                if (parts.length > 8) c.setWeekType(Course.WeekType.valueOf(parts[8]));
                if (parts.length > 9) c.setColorRGB(Integer.parseInt(parts[9]));
                data.addCourse(c);
                imported++;
            }
            JOptionPane.showMessageDialog(this, "成功导入 " + imported + " 门课程");
            updateScheduleView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "分享码无效: " + e.getMessage());
        }
    }

    private void exportCode() {
        StringBuilder sb = new StringBuilder();
        for (Course c : data.getCourses()) {
            sb.append(c.getName()).append("|");
            sb.append(c.getTeacher()).append("|");
            sb.append(c.getClassroom()).append("|");
            sb.append(c.getDayOfWeek()).append("|");
            sb.append(c.getStartPeriod()).append("|");
            sb.append(c.getEndPeriod()).append("|");
            sb.append(c.getStartWeek()).append("|");
            sb.append(c.getEndWeek()).append("|");
            sb.append(c.getWeekType().name()).append("|");
            sb.append(c.getColorRGB());
            sb.append("\n");
        }
        String encoded = Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
        
        JTextArea ta = new JTextArea(encoded, 6, 40);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "分享码（复制以下内容）", JOptionPane.INFORMATION_MESSAGE);
    }

    private void importFromPipeFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择课表数据文件（TSV/管道分隔）");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String header = br.readLine();
            int imported = 0;
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 7) continue;

                Course c = new Course();
                c.setName(parts[0].trim());
                c.setTeacher(parts.length > 1 ? parts[1].trim() : "");
                c.setClassroom(parts.length > 2 ? parts[2].trim() : "");
                try {
                    c.setDayOfWeek(Integer.parseInt(parts[3].trim()));
                    c.setStartPeriod(Integer.parseInt(parts[4].trim()));
                    c.setEndPeriod(Integer.parseInt(parts[5].trim()));
                    c.setStartWeek(Integer.parseInt(parts[6].trim()));
                    c.setEndWeek(parts.length > 7 ? Integer.parseInt(parts[7].trim()) : c.getStartWeek());
                    if (parts.length > 8 && !parts[8].trim().isEmpty()) {
                        c.setWeekType(Course.WeekType.valueOf(parts[8].trim().toUpperCase()));
                    }
                } catch (NumberFormatException ignored) {
                    continue;
                }
                data.addCourse(c);
                imported++;
            }
            JOptionPane.showMessageDialog(this, "成功导入 " + imported + " 门课程");
            if (miAutoWeek.isSelected()) recalcAutoWeek();
            updateScheduleView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage());
        }
    }

    private String escapeCSV(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(ch);
                }
            } else {
                if (ch == '"') {
                    inQuotes = true;
                } else if (ch == ',') {
                    result.add(current.toString().trim());
                    current = new StringBuilder();
                } else {
                    current.append(ch);
                }
            }
        }
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }

    private class SchedulePanel extends WeekPanel {
        @Override
        protected List<Course> getCourses() {
            return data.getCourses();
        }

        @Override
        protected int getCurrentWeek() {
            return data.getActiveSchedule().getSemesterWeek();
        }

        @Override
        protected void addCourse(int day, int period) {
            MainFrame.this.addCourse(day, period);
        }

        @Override
        protected void editCourse(Course c) {
            MainFrame.this.editCourse(c);
        }

        @Override
        protected void deleteCourse(Course c) {
            MainFrame.this.deleteCourse(c);
        }

        int getSelectedDay() { return selectedDay; }
        int getSelectedPeriod() { return selectedPeriod; }
        void refresh() { repaint(); }
    }
}

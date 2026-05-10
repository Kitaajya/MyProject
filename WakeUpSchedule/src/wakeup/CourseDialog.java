package wakeup;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CourseDialog extends JDialog {
    private JTextField txtName, txtTeacher, txtClassroom;
    private JComboBox<String> cbDay, cbStartPeriod, cbEndPeriod;
    private JSpinner spStartWeek, spEndWeek;
    private JComboBox<String> cbWeekType;
    private ColorChooserButton btnColor;
    private boolean confirmed = false;

    private static final String[] DAYS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final String[] WEEK_TYPES = {"全周", "单周", "双周"};
    private static final Color[] PRESET_COLORS = {
        new Color(66, 133, 244), new Color(234, 67, 53), new Color(52, 168, 83),
        new Color(251, 188, 4), new Color(154, 74, 208), new Color(0, 188, 212),
        new Color(255, 112, 67), new Color(233, 30, 99), new Color(0, 150, 136),
        new Color(63, 81, 181), new Color(96, 125, 139), new Color(121, 85, 72)
    };

    public CourseDialog(JFrame owner, int maxPeriod) {
        super(owner, "添加课程", true);
        init(maxPeriod);
    }

    public CourseDialog(JFrame owner, Course course, int maxPeriod) {
        super(owner, "编辑课程", true);
        init(maxPeriod);
        loadCourse(course);
    }

    private void init(int maxPeriod) {
        setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 5, 4, 5);

        txtName = new JTextField(15);
        txtTeacher = new JTextField(10);
        txtClassroom = new JTextField(10);

        String[] periods = new String[maxPeriod];
        for (int i = 0; i < maxPeriod; i++) periods[i] = "第" + (i + 1) + "节";
        cbDay = new JComboBox<>(DAYS);
        cbStartPeriod = new JComboBox<>(periods);
        cbEndPeriod = new JComboBox<>(periods);
        if (maxPeriod > 0) cbEndPeriod.setSelectedIndex(Math.min(1, maxPeriod - 1));

        spStartWeek = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        spEndWeek = new JSpinner(new SpinnerNumberModel(18, 1, 30, 1));
        cbWeekType = new JComboBox<>(WEEK_TYPES);
        btnColor = new ColorChooserButton(PRESET_COLORS[0]);

        addRow(form, g, 0, "课程名称：", txtName);
        addRow(form, g, 1, "授课教师：", txtTeacher);
        addRow(form, g, 2, "上课教室：", txtClassroom);
        addRow(form, g, 3, "上课日期：", cbDay);
        addRow(form, g, 4, "起始节次：", cbStartPeriod);
        addRow(form, g, 5, "结束节次：", cbEndPeriod);

        JPanel weekPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        weekPanel.add(new JLabel("第"));
        weekPanel.add(spStartWeek);
        weekPanel.add(new JLabel("周 - 第"));
        weekPanel.add(spEndWeek);
        weekPanel.add(new JLabel("周"));
        g.gridx = 1; g.gridy = 6; form.add(weekPanel, g);
        addLabel(form, g, 6, "周次范围：");

        addRow(form, g, 7, "周数类型：", cbWeekType);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colorPanel.add(btnColor);
        g.gridx = 1; g.gridy = 8; form.add(colorPanel, g);
        addLabel(form, g, 8, "课程颜色：");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnOk = new JButton("确定");
        JButton btnCancel = new JButton("取消");
        bottom.add(btnOk);
        bottom.add(btnCancel);

        btnOk.addActionListener(e -> {
            if (validateInput()) { confirmed = true; dispose(); }
        });
        btnCancel.addActionListener(e -> dispose());

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }

    private void addLabel(JPanel panel, GridBagConstraints g, int y, String text) {
        g.gridx = 0; g.gridy = y;
        panel.add(new JLabel(text), g);
    }

    private void addRow(JPanel panel, GridBagConstraints g, int y, String label, JComponent comp) {
        addLabel(panel, g, y, label);
        g.gridx = 1; g.gridy = y;
        panel.add(comp, g);
    }

    private void loadCourse(Course c) {
        txtName.setText(c.getName());
        txtTeacher.setText(c.getTeacher());
        txtClassroom.setText(c.getClassroom());
        cbDay.setSelectedIndex(Math.max(0, Math.min(c.getDayOfWeek() - 1, 6)));
        cbStartPeriod.setSelectedIndex(Math.max(0, c.getStartPeriod() - 1));
        cbEndPeriod.setSelectedIndex(Math.max(0, c.getEndPeriod() - 1));
        spStartWeek.setValue(c.getStartWeek());
        spEndWeek.setValue(c.getEndWeek());
        cbWeekType.setSelectedIndex(c.getWeekType().ordinal());
        btnColor.setColor(c.getColor());
    }

    private boolean validateInput() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入课程名称");
            txtName.requestFocus();
            return false;
        }
        int sp = cbStartPeriod.getSelectedIndex() + 1;
        int ep = cbEndPeriod.getSelectedIndex() + 1;
        if (ep < sp) {
            JOptionPane.showMessageDialog(this, "结束节次不能早于起始节次");
            return false;
        }
        int sw = (Integer) spStartWeek.getValue();
        int ew = (Integer) spEndWeek.getValue();
        if (ew < sw) {
            JOptionPane.showMessageDialog(this, "结束周不能早于起始周");
            return false;
        }
        return true;
    }

    public Course getCourse() {
        Course c = new Course();
        c.setName(txtName.getText().trim());
        c.setTeacher(txtTeacher.getText().trim());
        c.setClassroom(txtClassroom.getText().trim());
        c.setDayOfWeek(cbDay.getSelectedIndex() + 1);
        c.setStartPeriod(cbStartPeriod.getSelectedIndex() + 1);
        c.setEndPeriod(cbEndPeriod.getSelectedIndex() + 1);
        c.setStartWeek((Integer) spStartWeek.getValue());
        c.setEndWeek((Integer) spEndWeek.getValue());
        c.setWeekType(Course.WeekType.values()[cbWeekType.getSelectedIndex()]);
        c.setColor(btnColor.getColor());
        return c;
    }

    public boolean isConfirmed() { return confirmed; }

    private static class ColorChooserButton extends JButton {
        private Color color;

        ColorChooserButton(Color initial) {
            setColor(initial);
            setPreferredSize(new Dimension(80, 28));
            addActionListener(e -> {
                Color c = JColorChooser.showDialog(this, "选择颜色", color);
                if (c != null) setColor(c);
            });
        }

        void setColor(Color c) {
            this.color = c;
            setBackground(c);
            setOpaque(true);
            setBorderPainted(false);
            int brightness = (c.getRed() * 299 + c.getGreen() * 587 + c.getBlue() * 114) / 1000;
            setForeground(brightness < 128 ? Color.WHITE : Color.BLACK);
            setText(String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue()));
        }

        Color getColor() { return color; }
    }
}

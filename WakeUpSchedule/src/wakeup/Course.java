package wakeup;

import java.awt.Color;
import java.io.Serializable;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum WeekType { ALL, ODD, EVEN }

    private String name;
    private String teacher;
    private String classroom;
    private int dayOfWeek;      // 1=Mon, 2=Tue ... 7=Sun
    private int startPeriod;
    private int endPeriod;
    private int startWeek;
    private int endWeek;
    private WeekType weekType;
    private int colorRGB;

    public Course() {
        this("", "", "", 1, 1, 1, 1, 18, WeekType.ALL, new Color(66, 133, 244));
    }

    public Course(String name, String teacher, String classroom,
                  int dayOfWeek, int startPeriod, int endPeriod,
                  int startWeek, int endWeek, WeekType weekType, Color color) {
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.weekType = weekType;
        this.colorRGB = color.getRGB();
    }

    public boolean isActiveInWeek(int week) {
        if (week < startWeek || week > endWeek) return false;
        return switch (weekType) {
            case ALL -> true;
            case ODD -> week % 2 == 1;
            case EVEN -> week % 2 == 0;
        };
    }

    public int getSpan() { return endPeriod - startPeriod + 1; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }
    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public int getStartPeriod() { return startPeriod; }
    public void setStartPeriod(int startPeriod) { this.startPeriod = startPeriod; }
    public int getEndPeriod() { return endPeriod; }
    public void setEndPeriod(int endPeriod) { this.endPeriod = endPeriod; }
    public int getStartWeek() { return startWeek; }
    public void setStartWeek(int startWeek) { this.startWeek = startWeek; }
    public int getEndWeek() { return endWeek; }
    public void setEndWeek(int endWeek) { this.endWeek = endWeek; }
    public WeekType getWeekType() { return weekType; }
    public void setWeekType(WeekType weekType) { this.weekType = weekType; }
    public Color getColor() { return new Color(colorRGB); }
    public void setColor(Color color) { this.colorRGB = color.getRGB(); }
    public int getColorRGB() { return colorRGB; }
    public void setColorRGB(int rgb) { this.colorRGB = rgb; }

    @Override
    public String toString() {
        return name + " (" + teacher + ") " + classroom;
    }
}

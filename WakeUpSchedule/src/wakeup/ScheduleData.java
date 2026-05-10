package wakeup;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ScheduleData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SingleSchedule> schedules;
    private int activeIndex;

    public ScheduleData() {
        schedules = new ArrayList<>();
        schedules.add(new SingleSchedule("默认课表"));
        activeIndex = 0;
    }

    public SingleSchedule getActiveSchedule() {
        return schedules.get(activeIndex);
    }

    public SingleSchedule getSchedule(int index) {
        return schedules.get(index);
    }

    public int getScheduleCount() { return schedules.size(); }
    public int getActiveIndex() { return activeIndex; }
    public void setActiveIndex(int index) { activeIndex = index; }

    public void addSchedule(String name) {
        schedules.add(new SingleSchedule(name));
    }

    public void removeSchedule(int index) {
        if (schedules.size() <= 1) return;
        schedules.remove(index);
        if (activeIndex >= schedules.size()) activeIndex = schedules.size() - 1;
    }

    public void renameSchedule(int index, String name) {
        schedules.get(index).setName(name);
    }

    public List<String> getScheduleNames() {
        return schedules.stream().map(SingleSchedule::getName).toList();
    }

    public void addCourse(Course c) { getActiveSchedule().addCourse(c); }
    public void removeCourse(Course c) { getActiveSchedule().removeCourse(c); }
    public List<Course> getCourses() { return getActiveSchedule().getCourses(); }

    public static void saveToFile(ScheduleData data, String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(data);
        }
    }

    public static ScheduleData loadFromFile(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (ScheduleData) ois.readObject();
        }
    }

    public static class SingleSchedule implements Serializable {
        private static final long serialVersionUID = 2L;
        private String name;
        private List<Course> courses;
        private int semesterWeek = 1;
        private int totalWeeks = 20;
        private int periodsPerDay = 12;
        private long semesterStartEpochDay = LocalDate.of(2026, 3, 9).toEpochDay();

        public SingleSchedule(String name) {
            this.name = name;
            this.courses = new ArrayList<>();
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Course> getCourses() { return courses; }
        public void addCourse(Course c) { courses.add(c); }
        public void removeCourse(Course c) { courses.remove(c); }

        public int getSemesterWeek() { return semesterWeek; }
        public void setSemesterWeek(int w) { semesterWeek = w; }
        public int getTotalWeeks() { return totalWeeks; }
        public void setTotalWeeks(int w) { totalWeeks = w; }
        public int getPeriodsPerDay() { return periodsPerDay; }
        public void setPeriodsPerDay(int p) { periodsPerDay = p; }

        public LocalDate getSemesterStart() { return LocalDate.ofEpochDay(semesterStartEpochDay); }
        public void setSemesterStart(LocalDate d) { semesterStartEpochDay = d.toEpochDay(); }

        public int calculateCurrentWeek() {
            long days = ChronoUnit.DAYS.between(getSemesterStart(), LocalDate.now());
            int week = (int)(days / 7) + 1;
            return Math.max(1, Math.min(totalWeeks, week));
        }
    }
}

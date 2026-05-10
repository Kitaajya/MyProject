package wakeup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TSVImporter {

    public static void convertTSVtoPipe(String tsvPath, String outputPath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tsvPath), StandardCharsets.UTF_8))) {
            String l;
            while ((l = br.readLine()) != null) lines.add(l);
        }

        System.out.println("Lines: " + lines.size());
        System.out.println("Line 0: '" + lines.get(0).substring(0, Math.min(20, lines.get(0).length())) + "'");
        System.out.println("Line 4: '" + lines.get(4).substring(0, Math.min(40, lines.get(4).length())) + "'");
        System.out.println("Line 5: '" + lines.get(5).substring(0, Math.min(40, lines.get(5).length())) + "'");

        List<String> out = new ArrayList<>();
        out.add("Name|Teacher|Classroom|DayOfWeek|StartPeriod|EndPeriod|StartWeek|EndWeek|WeekType");

        int[] blockStarts = {5, 12, 19, 24, 31, 38, 45, 52, 59, 64, 71};

        int totalCourses = 0;
        for (int start : blockStarts) {
            if (start >= lines.size()) continue;
            String[] row0 = lines.get(start).split("\t");
            String[] row1 = (start + 1 < lines.size()) ? lines.get(start + 1).split("\t") : new String[0];
            String[] row2 = (start + 2 < lines.size()) ? lines.get(start + 2).split("\t") : new String[0];
            String[] row3 = (start + 3 < lines.size()) ? lines.get(start + 3).split("\t") : new String[0];

            System.out.println("Block " + start + ": row0 cols=" + row0.length + " row0[2]='" + (row0.length>2?row0[2]:"N/A") + "'");

            for (int d = 2; d <= 8; d++) {
                String name = (d < row0.length) ? row0[d].trim() : "";
                if (name.isEmpty()) continue;
                if (name.matches("^\\(\\d") || name.contains("校区") || name.contains("楼") || name.contains("室")) continue;
                String cn = name.replace("【调】", "").replace("★", "").replace("☆", "").trim();
                if (cn.isEmpty()) continue;

                String wt = (d < row1.length) ? row1[d].trim() : "";
                String ct = (d < row2.length) ? row2[d].trim() : "";
                String tt = (d < row3.length) ? row3[d].trim() : "";

                var pm = findPattern(wt, "\\((\\d+)\\s*-\\s*(\\d+)");
                if (pm == null) continue;
                int sp = Integer.parseInt(pm[0]), ep = Integer.parseInt(pm[1]);

                String wp = wt.replaceAll("^.*\u8282\\)\\s*", "");
                String[] segs = wp.split(",");
                for (String seg : segs) {
                    seg = seg.trim();
                    if (seg.isEmpty()) continue;
                    String wtype = "ALL";
                    String s = seg;
                    if (s.contains("\u5355")) { wtype = "ODD"; s = s.replaceAll("\\(\\u5355\\)", "").replaceAll("\\（\\u5355\\）", ""); }
                    else if (s.contains("\u53cc")) { wtype = "EVEN"; s = s.replaceAll("\\(\\u53cc\\)", "").replaceAll("\\（\\u53cc\\）", ""); }
                    s = s.replace("\u5468", "");

                    var rm = findPattern(s, "(\\d+)\\s*-\\s*(\\d+)");
                    int sw, ew;
                    if (rm != null) { sw = Integer.parseInt(rm[0]); ew = Integer.parseInt(rm[1]); }
                    else {
                        var sm = findPattern(s, "(\\d+)");
                        if (sm != null) { sw = Integer.parseInt(sm[0]); ew = sw; }
                        else continue;
                    }

                    int day = d - 1;
                    out.add(String.format("%s|%s|%s|%d|%d|%d|%d|%d|%s", cn, tt, ct, day, sp, ep, sw, ew, wtype));
                    totalCourses++;
                }
            }
        }

        System.out.println("Total courses parsed: " + totalCourses);

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {
            for (String l : out) { bw.write(l); bw.newLine(); }
        }
        System.out.println("OK: " + (out.size() - 1) + " courses");
    }

    private static String[] findPattern(String text, String regex) {
        var p = java.util.regex.Pattern.compile(regex);
        var m = p.matcher(text);
        if (m.find()) {
            if (m.groupCount() >= 2) return new String[]{m.group(1), m.group(2)};
            if (m.groupCount() >= 1) return new String[]{m.group(1), m.group(1)};
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String tsv = System.getenv("TEMP") + "\\schedule_raw_tsv.txt";
        String out = System.getenv("TEMP") + "\\courses_imported.txt";
        convertTSVtoPipe(tsv, out);
    }
}

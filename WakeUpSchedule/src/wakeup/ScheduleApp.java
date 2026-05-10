package wakeup;

import javax.swing.*;
import java.awt.*;

public class ScheduleApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}
        }

        // set global font
        Font font = new Font("微软雅黑", Font.PLAIN, 13);
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Spinner.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("TitledBorder.font", new Font("微软雅黑", Font.BOLD, 13));

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

/*
 * Kian Dsouza - 1142463
 * Dictionary Server GUI
 * */

import javax.swing.*;
import java.awt.*;

public class DictionaryServerGUI {
    private JFrame frame;
    private JTextArea logArea;

    public DictionaryServerGUI() {
        frame = new JFrame("Dictionary Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel buttonPanel = new JPanel();

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void logMessage(String message) {
        logArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DictionaryServerGUI());
    }
}

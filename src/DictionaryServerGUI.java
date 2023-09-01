/*
 * Kian Dsouza - 1142463
 * Dictionary Server GUI
 * */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DictionaryServerGUI {
    private JFrame frame;
    private JButton stopButton;
    private JTextArea logArea;
    private Map<Socket, JLabel> clientLabels;

    public DictionaryServerGUI() {
        frame = new JFrame("Dictionary Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        stopButton = new JButton("Stop Server");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stopServer();
                logMessage("Server stopped.");
                stopButton.setEnabled(false);
            }
        });

        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(stopButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        clientLabels = new HashMap<>();
    }

    public void logMessage(String message) {
        logArea.append(message + "\n");
    }

    public void addClient(Socket clientSocket) {
        JLabel label = new JLabel("Client " + (clientLabels.size() + 1));
        clientLabels.put(clientSocket, label);
        frame.add(label, BorderLayout.NORTH);
        frame.revalidate();
        frame.repaint();
    }

    public void removeClient(Socket clientSocket) {
        JLabel label = clientLabels.remove(clientSocket);
        if (label != null) {
            frame.remove(label);
            frame.revalidate();
            frame.repaint();
        }
    }

    public void updateLog(Socket clientSocket, String log) {
        if (clientLabels.containsKey(clientSocket)) {
            logArea.append(clientLabels.get(clientSocket).getText() + ": " + log + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DictionaryServerGUI());
    }
}

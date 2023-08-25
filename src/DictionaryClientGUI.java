import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class DictionaryClientGUI {
    private JTextArea outputArea;
    private JTextField inputField;
    private DictionaryClient client;

    public DictionaryClientGUI(String serverIP, int serverPort) {
        JFrame frame = new JFrame("Dictionary Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        inputField = new JTextField(30);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(sendButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        try {
            client = new DictionaryClient(serverIP, serverPort);
            startListening();
        } catch (IOException e) {
            showError("Error connecting to the server.");
        }
    }

    private void startListening() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String response = client.receiveResponse();
                    SwingUtilities.invokeLater(() -> outputArea.append(response + "\n"));
                }
            } catch (IOException e) {
                showError("Connection to server lost.");
            }
        });
        thread.start();
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userInput = inputField.getText();
            if (!userInput.isEmpty()) {
                client.sendRequest(userInput);
                inputField.setText("");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (args.length != 2) {
                System.err.println("Usage: java DictionaryClientGUI <serverIP> <serverPort>");
            } else {
                try {
                    String serverIP = args[0];
                    int serverPort = Integer.parseInt(args[1]);
                    new DictionaryClientGUI(serverIP, serverPort);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number.");
                }
            }
        });
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class DictionaryClientGUI {
    private JTextArea outputArea;
    private JTextField inputField;
    private DictionaryClient client;


    public DictionaryClientGUI(DictionaryClient client) {
        this.client = client;

        JFrame frame = new JFrame("Dictionary Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        outputArea = new JTextArea(15, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        inputField = new JTextField(30);

        // Add buttons for different functionalities
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton searchButton = new JButton("Search");

        // Add ActionListener to each button
        addButton.addActionListener(new DictionaryButtonListener("ADD"));
        updateButton.addActionListener(new DictionaryButtonListener("UPDATE"));
        deleteButton.addActionListener(new DictionaryButtonListener("DELETE"));
        searchButton.addActionListener(new DictionaryButtonListener("SEARCH"));

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(searchButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        startListening();  // Call startListening() to listen for server responses
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

    private class DictionaryButtonListener implements ActionListener {
        private String operation;

        public DictionaryButtonListener(String operation) {
            this.operation = operation;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String userInput = inputField.getText();
            if (!userInput.isEmpty()) {
                client.sendRequest(operation + " " + userInput);
                inputField.setText("");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}

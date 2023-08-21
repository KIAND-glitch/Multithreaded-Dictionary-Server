import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class DictionaryClientGUI {
    private JTextArea outputArea;
    private JTextField inputField;
    private PrintWriter out;
    private BufferedReader in;

    public DictionaryClientGUI() {
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

        // Connect to the server
        final String SERVER_IP = "127.0.0.1"; // Change to server's IP address
        final int SERVER_PORT = 12345; // Change to server's port number

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListening() {
        Thread thread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    String finalResponse = response;
                    SwingUtilities.invokeLater(() -> outputArea.append(finalResponse + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userInput = inputField.getText();
            out.println(userInput);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DictionaryClientGUI());
    }
}

/*
 * Kian Dsouza - 1142463
 * Dictionary Client
 * */


import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;

public class DictionaryClient {
    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds
    private static Logger logger = Logger.getLogger("DictionaryClient");

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    static {
        try {
            FileHandler fileHandler = new FileHandler("client_logs.txt", true);
            fileHandler.setLevel(Level.INFO);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            logger.addHandler(fileHandler);
            logger.info("Client has connected.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Client disconnected.");
                logger.removeHandler(fileHandler);
                fileHandler.close();
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public DictionaryClient(String serverIP, int serverPort) throws IOException {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverIP, serverPort), CONNECTION_TIMEOUT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            showError("Invalid server IP address.");
            logger.severe("Invalid server IP address.");
            System.exit(1);
        } catch (ConnectException e) {
            showError("Could not connect to the server. Please make sure the server is running.");
            logger.warning("Could not connect to the server." +
                    " Please make sure the server is running.");
            System.exit(1);
        } catch (IOException e) {
            showError("Error connecting to the server.");
            logger.warning("Error connecting to the server.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java DictionaryClient <serverIP> <serverPort>");
            return;
        }

        String serverIP = args[0];
        int serverPort;

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Port must be a valid integer.");
            return;
        }

        if (serverPort < 0 || serverPort > 65535) {
            System.err.println("Invalid port number. Port must be in the range 0-65535.");
            return;
        }

        try {
            DictionaryClient client = new DictionaryClient(serverIP, serverPort);
            SwingUtilities.invokeLater(() -> new DictionaryClientGUI(client));
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
        }
    }


    public void sendRequest(String request) {
        String encryptedRequest = CaesarCipher.encrypt(request);
        out.println(encryptedRequest);

        // Log the request
        logger.info("Client request: " + request);
    }

    public String receiveResponse() throws IOException {
        String encryptedResponse = in.readLine();
        String decryptedResponse = CaesarCipher.decrypt(encryptedResponse);

        // Log the response
        logger.info("Client received response: " + decryptedResponse);

        return decryptedResponse;
    }


    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

/*
 * Kian Dsouza - 1142463
 * Dictionary Server
 * */

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.io.IOException;
import java.util.logging.*;



public class DictionaryServer {

    private volatile boolean isRunning = true;
    private static DictionaryServerGUI gui;
    private Dictionary dictionary;

    private static final Logger logger = Logger.getLogger("ServerLog");

    private final CustomThreadPool threadPool; // Add this member

    static {
        LogManager.getLogManager().reset();
        logger.setLevel(Level.ALL);

        try {
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    String encryptedMessage = super.format(record);
                    String decryptedMessage = CaesarCipher.decrypt(encryptedMessage);
                    return decryptedMessage;
                }
            });
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
    }



    public DictionaryServer(int port, String dictionaryFilePath, DictionaryServerGUI gui) throws IOException {
        this.gui = gui;

        File dictionaryFile = new File(dictionaryFilePath);
        dictionary = new Dictionary(dictionaryFile);
        threadPool = new CustomThreadPool(5);

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            logger.info("Starting server on IP address: " + ipAddress);

            gui.logMessage("Starting server on IP address: " + ipAddress);
            gui.logMessage("Server is running and waiting for connections...");

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                gui.logMessage("Client connected: " + clientSocket.getInetAddress());

                threadPool.submitTask(new ClientHandler(clientSocket, dictionary));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java DictionaryServer <port> <dictionary_file>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number format.");
            return;
        }

        String dictionaryFilePath = args[1];
        File dictionaryFile = new File(dictionaryFilePath);

        if (!dictionaryFile.exists()) {
            System.out.println("Dictionary file does not exist. Do you want to create a new one? (Y/N)");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String response = reader.readLine().trim().toLowerCase();

                if (!response.equals("y")) {
                    System.out.println("Exiting...");
                    return;
                }

                if (!dictionaryFile.createNewFile()) {
                    System.out.println("Failed to create a new dictionary file.");
                    return;
                }

                // Initialize an empty JSON dictionary
                JSONObject initialDictionary = new JSONObject();
                writeJsonToFile(dictionaryFile, initialDictionary);
            } catch (IOException e) {
                System.out.println("An error occurred while creating the dictionary file.");
                e.printStackTrace();
                return;
            }
        } else {
            try {
                JSONObject json = readJsonFromFile(dictionaryFile);
                if (json == null) {
                    System.out.println("Invalid JSON format in dictionary file. Do you want to create a new one? (Y/N)");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String response = reader.readLine().trim().toLowerCase();

                    if (!response.equals("y")) {
                        System.out.println("Exiting...");
                        return;
                    }

                    writeJsonToFile(dictionaryFile, new JSONObject());
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the dictionary file.");
                e.printStackTrace();
                return;
            }
        }

        DictionaryServerGUI gui = new DictionaryServerGUI();
        new DictionaryServer(port, dictionaryFilePath, gui);
    }

    public void stopServer() {
        isRunning = false;
        gui.logMessage("Server stopping...");
    }


    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Dictionary dictionary;

        private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
        private final PrintWriter out;

        private CustomThreadPool threadPool;

        public ClientHandler(Socket clientSocket, Dictionary dictionary) throws IOException {
            this.clientSocket = clientSocket;
            this.dictionary = dictionary;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.threadPool = new CustomThreadPool(5);
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                logger.info("Client connected: " + clientSocket.getInetAddress());

                String request;
                while ((request = in.readLine()) != null) {
                    // Submit the task to the custom thread pool for processing
                    String finalRequest = request;
                    threadPool.submitTask(() -> processRequest(finalRequest));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private synchronized void processRequest(String encryptedRequest) {
            String decryptedRequest = CaesarCipher.decrypt(encryptedRequest);
            String response;
            String[] tokens = decryptedRequest.split(" ", 2);
            if (tokens.length < 2) {
                response =  "Invalid request format.";
            }

            String operation = tokens[0];
            String parameter = tokens[1];

            switch (operation) {
                case "SEARCH":
                    response = dictionary.search(parameter);
                    break;
                case "ADD":
                    response = dictionary.add(parameter);
                    break;
                case "REMOVE":
                    response = dictionary.remove(parameter);
                    break;
                case "UPDATE":
                    response = dictionary.update(parameter);
                    break;
                default:
                    response = "Unknown operation.";
            }

            // Log client requests and responses (in the main thread)
            String finalResponse = response;
            SwingUtilities.invokeLater(() -> {
                logger.info("Client: " + clientSocket.getInetAddress() + " - Request: " + decryptedRequest);
                logger.info("Client: " + clientSocket.getInetAddress() + " - Response: " + finalResponse);
                gui.logMessage("Client " + clientSocket.getInetAddress() + ": " + decryptedRequest);
                gui.logMessage("Server response to " + clientSocket.getInetAddress() + ": " + finalResponse);
            });

            // Send the response to the client
            out.println(CaesarCipher.encrypt(finalResponse));
        }
    }

    private static JSONObject readJsonFromFile(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            JSONTokener tokener = new JSONTokener(fileReader);
            return new JSONObject(tokener);
        } catch (JSONException e) {
            return null;
        }
    }

    private static void writeJsonToFile(File file, JSONObject json) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(json.toString());
        }
    }

}

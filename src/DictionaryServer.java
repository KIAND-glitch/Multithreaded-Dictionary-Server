import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.util.logging.*;



public class DictionaryServer {

    private volatile boolean isRunning = true; // 'volatile' keyword ensures proper visibility in multi-threading
    private static DictionaryServerGUI gui;
    private Dictionary dictionary;

    private static final Logger logger = Logger.getLogger(DictionaryServer.class.getName());

    static {
        // Configure the logger
        LogManager.getLogManager().reset(); // Reset the default configuration
        logger.setLevel(Level.ALL); // Set the desired logging level

        try {
            FileHandler fileHandler = new FileHandler("server.log", true); // Create a log file named "server.log"
            fileHandler.setLevel(Level.ALL); // Set the desired logging level for the file
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL); // Set the desired logging level for console output
        logger.addHandler(consoleHandler);
    }


    public DictionaryServer(int port, String dictionaryFilePath, DictionaryServerGUI gui) throws IOException {
        this.gui = gui;

        File dictionaryFile = new File(dictionaryFilePath);
        JSONObject json = readJsonFromFile(dictionaryFile);
        dictionary = new Dictionary(dictionaryFile); // Initialize the dictionary with the File object


        try {
            ServerSocket serverSocket = new ServerSocket(port);

            // Get the local IP address
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            logger.info("Starting server on IP address: " + ipAddress);

            gui.logMessage("Starting server on IP address: " + ipAddress);
            gui.logMessage("Server is running and waiting for connections...");

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                gui.logMessage("Client connected: " + clientSocket.getInetAddress());

                // Create a new thread to handle the client's requests
                Thread clientThread = new Thread(new ClientHandler(clientSocket, dictionary));
                clientThread.start();
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

        DictionaryServerGUI gui = new DictionaryServerGUI(); // Create the GUI instance
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

        public ClientHandler(Socket clientSocket, Dictionary dictionary) throws IOException {
            this.clientSocket = clientSocket;
            this.dictionary = dictionary;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                logger.info("Client connected: " + clientSocket.getInetAddress());

                String request;
                while ((request = in.readLine()) != null) {
                    String response = processRequest(request);
                    out.println(response);

                    // Log client requests and responses
                    logger.info("Client: " + clientSocket.getInetAddress() + " - Request: " + request);
                    logger.info("Client: " + clientSocket.getInetAddress() + " - Response: " + response);

                    // Also send client logs to the GUI
                    gui.logMessage("Client " + clientSocket.getInetAddress() + ": " + request);
                    gui.logMessage("Server response to " + clientSocket.getInetAddress() + ": " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized String processRequest(String request) {
            String[] tokens = request.split(" ", 2);
            if (tokens.length < 2) {
                return "Invalid request format.";
            }

            String operation = tokens[0];
            String parameter = tokens[1];

            String response;
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
            return response;
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

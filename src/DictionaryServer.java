import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class DictionaryServer {

    public static void main(String[] args) {
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
        }

        Dictionary dictionary = new Dictionary(dictionaryFile);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running and waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Create a new thread to handle the client's requests
                Thread clientThread = new Thread(new ClientHandler(clientSocket, dictionary));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Dictionary dictionary;

        public ClientHandler(Socket clientSocket, Dictionary dictionary) {
            this.clientSocket = clientSocket;
            this.dictionary = dictionary;
        }
        @Override
        public void run() {
            try {
                // Set up input and output streams for the client socket
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Read client's requests and respond accordingly
                String request;
                while ((request = in.readLine()) != null) {
                    String response = processRequest(request);
                    out.println(response);
                }

                // Close the streams and socket when done
                in.close();
                out.close();
                clientSocket.close();
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

    private static void writeJsonToFile(File file, JSONObject json) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(json.toString());
        }
    }

}

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.*;

public class DictionaryServer {
    // Shared dictionary and lock for synchronization
    private static final Lock dictionaryLock = new ReentrantLock();
    private static final File dictionaryFile = new File("src/dictionary.json");
    private static final Dictionary dictionary = new Dictionary();

    public static void main(String[] args) {
        final int PORT = 12345; // Choose a port number

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running and waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Create a new thread to handle the client's requests
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        Dictionary dictionary = new Dictionary();

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
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

        private String processRequest(String request) {
            String[] tokens = request.split(" ", 2);
            if (tokens.length < 2) {
                return "Invalid request format.";
            }

            String operation = tokens[0];
            String parameter = tokens[1];

            String response;
            try {
                dictionaryLock.lock(); // Acquire the lock before accessing the shared dictionary
                switch (operation) {
                    case "SEARCH":
                        response = dictionary.search(parameter);
                        break;
                    case "ADD":
                        response = dictionary.add(parameter);
                        break;
//                    case "REMOVE":
//                        response = dictionary.remove(parameter);
//                        break;
//                    case "UPDATE":
//                        response = dictionary.update(parameter);
//                        break;
                    default:
                        response = "Unknown operation.";
                }
            } finally {
                dictionaryLock.unlock(); // Release the lock after accessing the shared dictionary
            }

            return response;
        }
    }

    static class Dictionary {
        private JSONObject dictionaryData;

        public Dictionary() {
            // Initialize the dictionary by reading from the JSON file
            try {
                String fileContent = new String(Files.readAllBytes(dictionaryFile.toPath()));
                dictionaryData = new JSONObject(fileContent);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        public String search(String word) {
            try {
                dictionaryLock.lock();
                if (dictionaryData.has(word)) {
                    JSONArray meanings = dictionaryData.getJSONArray(word);
                    return "Meaning of " + word + ": " + meanings.toString();
                } else {
                    return "Word not found.";
                }
            } finally {
                dictionaryLock.unlock();
            }
        }

        public String add(String input) {
            try {
                dictionaryLock.lock();
                String[] parts = input.split(" ", 2);
                if (parts.length == 2) {
                    String word = parts[0];
                    String[] meanings = parts[1].split(";");
                    if (!dictionaryData.has(word)) {
                        dictionaryData.put(word, new JSONArray(meanings));
                        saveDictionary();
                        return "Added " + word + " successfully.";
                    } else {
                        return "Word already exists.";
                    }
                } else {
                    return "Invalid input format.";
                }
            } catch (JSONException e) {
                return "Error: " + e.getMessage();
            } finally {
                dictionaryLock.unlock();
            }
        }

        // ... (similar modifications for remove and update methods)

        private void saveDictionary() {
            try (FileWriter writer = new FileWriter(dictionaryFile)) {
                writer.write(dictionaryData.toString(4));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
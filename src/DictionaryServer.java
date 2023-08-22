import java.io.*;
import java.net.*;

public class DictionaryServer {

    public static void main(String[] args) {
        final int PORT = 12345;
        Dictionary dictionary = new Dictionary();

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
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

}

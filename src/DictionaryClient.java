import javax.swing.*;
import java.io.*;
import java.net.*;

public class DictionaryClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public DictionaryClient(String serverIP, int serverPort) throws IOException {
        socket = new Socket(serverIP, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java DictionaryClient <serverIP> <serverPort>");
        } else {
            try {
                String serverIP = args[0];
                int serverPort = Integer.parseInt(args[1]);

                // Create an instance of DictionaryClient
                DictionaryClient client = new DictionaryClient(serverIP, serverPort);

                // Create an instance of DictionaryClientGUI and pass the client instance to it
                SwingUtilities.invokeLater(() -> new DictionaryClientGUI(client));

            } catch (NumberFormatException e) {
                System.err.println("Invalid port number.");
            } catch (IOException e) {
                System.err.println("Error connecting to the server.");
            }
        }
    }

    public void sendRequest(String request) {
        out.println(request);
    }

    public String receiveResponse() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}

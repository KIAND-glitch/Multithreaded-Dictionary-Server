import java.io.*;
import java.net.*;

public class DictionaryClient {
    public static void main(String[] args) {
        final String SERVER_IP = "localhost"; // Change to server's IP address
        final int SERVER_PORT = 12345; // Change to server's port number

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);

            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the server. Enter commands:");

            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                out.println(userInput);
                String response = in.readLine();
                System.out.println("Server response: " + response);
            }

            // Close the streams and socket when done
            userInputReader.close();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

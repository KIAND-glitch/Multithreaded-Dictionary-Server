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

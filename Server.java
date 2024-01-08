import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static Map<Socket, PrintWriter> clientWriters = new HashMap<>();

    public static void main(String[] args) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Server is listening on port 12345...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientWriters.put(clientSocket, writer);

                    handleClient(clientSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Received from client: " + clientMessage);

                String response = processClientMessage(clientSocket, clientMessage);

                PrintWriter writer = clientWriters.get(clientSocket);
                if (writer != null) {
                    writer.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processClientMessage(Socket clientSocket, String clientMessage) {
        return "Server received: " + clientMessage;
    }
}
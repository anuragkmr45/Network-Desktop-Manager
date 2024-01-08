import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PortSharing {

    public static void main(String[] args) {
        final int SERVER_PORT = 12345; // Choose a port for the server

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server listening on port " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection from " + clientSocket.getInetAddress());

                List<Integer> openPorts = checkOpenPorts();

                // Convert the list of open ports to a string and send it to the client
                String portsStr = String.join(",", openPorts.stream().map(Object::toString).toArray(String[]::new));
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(portsStr.getBytes());

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> checkOpenPorts() {
        List<Integer> openPorts = new ArrayList<>();
        for (int port = 1024; port <= 65535; port++) { // Adjust the range as needed
            try (Socket ignored = new Socket("localhost", port)) {
                openPorts.add(port);
            } catch (IOException ignored) {
                // Port is closed
            }
        }
        return openPorts;
    }
}

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PortScanner {
    public static void main(String[] args) {
        scanPorts("localhost", 1000, 10000);
    }

    public static List<Integer> scanPorts(String targetIP, int startPort, int endPort) {
        List<Integer> occupiedPorts = new ArrayList<>();

        System.out.println("Scanning ports...");

        for (int port = startPort; port <= endPort; port++) {
            if (isPortOccupied(targetIP, port)) {
                occupiedPorts.add(port);
                System.out.println("Port " + port + " is occupied");
            }
        }

        System.out.println("Port scanning completed.");

        // Print all occupied ports
        System.out.println("Occupied ports: " + occupiedPorts);

        return occupiedPorts;
    }

    private static boolean isPortOccupied(String ip, int port) {
        try (Socket ignored = new Socket(ip, port)) {
            // If the connection is successful, the port is occupied
            return true;
        } catch (Exception ignored) {
            // If an exception occurs, the port is not occupied
            return false;
        }
    }
}

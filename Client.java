import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    // private static final int PORT_SCANNING_PORT = 6000;
    private static final int DESKTOP_SHARING_PORT = 5000;
    private static final int LOCKING_PORT = 7000;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 2000;

    public static void main(String[] args) {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    "Select an option:\n1. Port Scanning\n2. Desktop Sharing\n3. Desktop Locking\n4. Server Messagin\n5. Exit");

            if (input == null || input.isEmpty()) {
                continue;
            }

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        startPortScanning("localhost", 1000, 10000);
                        break;
                    case 2:
                        startDesktopSharing();
                        break;
                    case 3:
                        startDesktopLocking();
                        break;
                    case 4:
                        sendMessage();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
            }
        }
    }

    public static List<Integer> startPortScanning(String targetIP, int startPort, int endPort) {
        List<Integer> occupiedPorts = new ArrayList<>();

        StringBuilder resultMessage = new StringBuilder("Scanning ports...\n");

        for (int port = startPort; port <= endPort; port++) {
            if (isPortOccupied(targetIP, port)) {
                occupiedPorts.add(port);
                resultMessage.append("Port ").append(port).append(" is occupied\n");
            }
        }

        resultMessage.append("Port scanning completed.\n");

        // Display the information in a JOptionPane
        JOptionPane.showMessageDialog(null, resultMessage.toString(), "Port Scanning Results",
                JOptionPane.INFORMATION_MESSAGE);

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

    private static void startDesktopSharing() {
        try {
            try (Socket socket = new Socket("localhost", DESKTOP_SHARING_PORT)) {
                System.out.println("Connected to Desktop Sharing Server...");

                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setVisible(true);

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    byte[] imageBytes = (byte[]) ois.readObject();
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    BufferedImage screenshot = ImageIO.read(bais);
                    bais.close();

                    ImageIcon icon = new ImageIcon(screenshot);
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(new JLabel(icon));
                    frame.repaint();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void startDesktopLocking() {
        try (Socket socket = new Socket("localhost", LOCKING_PORT)) {
            System.out.println("Connected to Desktop Locking Server...");

            String command = JOptionPane.showInputDialog("Enter command: LOCK or UNLOCK");

            if (command != null) {
                command = command.trim(); // Trim leading and trailing whitespaces

                if (!command.isEmpty() && (command.equalsIgnoreCase("LOCK") || command.equalsIgnoreCase("UNLOCK"))) {
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    writer.println(command);

                    // Wait for acknowledgment from the server
                    String response = reader.readLine();
                    System.out.println("Server response: " + response);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid command. Please enter LOCK or UNLOCK.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid command. Please enter LOCK or UNLOCK.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            System.out.println("Connected to Messaging Server...");

            String message = JOptionPane.showInputDialog("Enter your message:");

            if (message != null) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(message);

                // Wait for acknowledgment from the server
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String response = reader.readLine();
                    System.out.println("Server response: " + response);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Invalid message. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

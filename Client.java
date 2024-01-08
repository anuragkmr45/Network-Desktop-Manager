import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final int PORT_SCANNING_PORT = 6000;
    private static final int DESKTOP_SHARING_PORT = 5000;
    private static final int LOCKING_PORT = 7000;
    private static final String SERVER_IP = "localhost"; 
    private static final int SERVER_PORT = 12345;

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
                        startPortScanning();
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

    private static void startPortScanning() {
        try {
            String targetIpAddress = JOptionPane.showInputDialog("Enter target IP address:");
            String targetPortStr = JOptionPane.showInputDialog("Enter target port:");

            if (targetIpAddress == null || targetPortStr == null) {
                return;
            }

            int targetPort = Integer.parseInt(targetPortStr);

            try (Socket socket = new Socket("localhost", PORT_SCANNING_PORT)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(targetIpAddress);
                writer.println(targetPort);

                // Handle the response from the server if needed
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
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
                    BufferedImage screenshot = (BufferedImage) ois.readObject();
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

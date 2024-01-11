import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.HeadlessException;
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
// import java.io.ByteArrayInputStream;
// import java.io.ObjectInputStream;

public class Client {
    // private static final int PORT_SCANNING_PORT = 6000;
    // private static final int LOCKING_PORT = 7000;
    private static final int DESKTOP_SHARING_PORT = 5000;

    // for port scanning 
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 2000;

    public static void main(String[] args) {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    "Select an option:\n1. Port Scanning\n2. Desktop Sharing\n3. Desktop Shutdown\n4. Desktop Restart\n5. Client-Server Messaging \n6. Exit");

            if (input == null || input.isEmpty()) {
                continue;
            }

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        startPortScanning("localhost", 100, 10000);
                        break;
                    case 2:
                        startDesktopSharing();
                        break;
                    case 3:
                        startDesktopShutdown();
                        break;
                    case 4:
                        startDesktopRestart();
                        break;
                    case 5:
                        sendMessage();
                        break;
                    case 6:
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

        JOptionPane.showMessageDialog(null, resultMessage.toString(), "Port Scanning Results",
                JOptionPane.INFORMATION_MESSAGE);

        return occupiedPorts;
    }

    private static boolean isPortOccupied(String ip, int port) {
        try (Socket ignored = new Socket(ip, port)) {
            // connection is successful => port occupied
            return true;
        } catch (Exception ignored) {
            // exception occurs => port not occupied
            return false;
        }
    }
    

    private static void startDesktopRestart() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // window
                Runtime.getRuntime().exec("shutdown.exe -r -t 0");

            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // unix / linux 
                Runtime.getRuntime().exec("sudo shutdown -r now");

            } else {
                System.out.println("Unsupported operating system for system restart.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startDesktopSharing() {
        try {
            try (Socket socket = new Socket("localhost", DESKTOP_SHARING_PORT)) {
                System.out.println("Connected to the server.");

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                JFrame frame = new JFrame("Screen Sharing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JLabel label = new JLabel();
                frame.getContentPane().add(label);

                frame.setSize(800, 600);
                frame.setVisible(true);

                while (true) {
                    byte[] imageBytes = (byte[]) ois.readObject();
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    ImageIcon icon = new ImageIcon(image);
                    label.setIcon(icon);
                    frame.repaint();
                }
            } catch (HeadlessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void startDesktopShutdown() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // For Windows
                Runtime.getRuntime().exec("shutdown.exe -s -t 0");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // For Unix/Linux/Mac
                Runtime.getRuntime().exec("sudo shutdown -h now");
            } else {
                System.out.println("Unsupported operating system for system shutdown.");
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

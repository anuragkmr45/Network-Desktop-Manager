import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

public class DesktopSharing {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Desktop Sharing Server listening on port " + PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    new Thread(new DesktopSharingHandler(clientSocket)).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DesktopSharingHandler implements Runnable {
    private Socket clientSocket;

    public DesktopSharingHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            Robot robot = new Robot();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            while (true) {
                BufferedImage screenshot = robot.createScreenCapture(new Rectangle(screenSize));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // Convert BufferedImage to byte array
                ImageIO.write(screenshot, "png", baos);
                baos.flush();
                byte[] imageBytes = baos.toByteArray();
                baos.close();

                oos.writeObject(imageBytes);

                // Introduce a delay to control the speed of desktop sharing
                Thread.sleep(100);
            }
        } catch (IOException | AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

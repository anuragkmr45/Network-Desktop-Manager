import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopLockingServer {
    private static final int LOCKING_PORT = 7000;

    public static void main(String[] args) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(LOCKING_PORT)) {
                System.out.println("Desktop Locking Server listening on port " + LOCKING_PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected for locking/unlocking: " + clientSocket.getInetAddress());

                    new Thread(new DesktopLockingHandler(clientSocket)).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DesktopLockingHandler implements Runnable {
    private Socket clientSocket;
    private static final AtomicBoolean isLocked = new AtomicBoolean(false);

    public DesktopLockingHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String command;
            while ((command = reader.readLine()) != null) {
                if (command.equals("LOCK")) {
                    lockDesktop();
                    System.out.println("Desktop locked by client: " + clientSocket.getInetAddress());
                } else if (command.equals("UNLOCK")) {
                    unlockDesktop();
                    System.out.println("Desktop unlocked by client: " + clientSocket.getInetAddress());
                } else {
                    System.out.println("Unknown command from client: " + command);
                }

                writer.println("ACK"); 
            }

            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lockDesktop() {
        isLocked.set(true);
    }

    private void unlockDesktop() {
        isLocked.set(false);
    }
}

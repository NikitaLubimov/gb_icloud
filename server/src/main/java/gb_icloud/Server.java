package gb_icloud;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(7172)) {
            System.out.println("Server started");
            while (true){
                Socket socket = server.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            System.out.println("Close");
        }
    }
}

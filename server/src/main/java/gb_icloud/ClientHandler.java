package gb_icloud;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ClientHandler implements Runnable {

    private DataInputStream in;
    private DataOutputStream out;
    private final String serverDir = "server_files";

    public ClientHandler(Socket socket) throws IOException {
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connect");
        sendListOfFiles(serverDir);
    }

    private void sendListOfFiles(String dir) throws IOException {
        out.writeUTF("#list#");
        List<String> files = getFiles(serverDir);
        out.writeInt(files.size());
        for (String file : files) {
            out.writeUTF(file);
        }
        out.flush();
    }

    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    @Override
    public void run() {
        byte[] buf = new byte[256];
        try {
            while (true) {
                String command = in.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    String fileName = in.readUTF();
                    long size = in.readLong();
                    File file = Path.of(serverDir).resolve(fileName).toFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        for (int i = 0; i < (size + 255) / 256; i++) {
                            int read = in.read(buf);
                            fos.write(buf,0,read);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendListOfFiles(serverDir);
                }
            }
        } catch (Exception e) {
            System.out.println("Connection was broken");
        }
    }
}

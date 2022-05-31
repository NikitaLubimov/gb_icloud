package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {


    private DataInputStream in;
    private DataOutputStream out;

    public Network(int port) throws IOException {
        Socket socket = new Socket("localhost",port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void writeMSG (String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    public String readString() throws IOException {
        return in.readUTF();
    }

    public int readInt() throws IOException {
        return in.readInt();
    }

    public DataOutputStream getOs() {
        return out;
    }

    public DataInputStream getIs() {
        return in;
    }
}

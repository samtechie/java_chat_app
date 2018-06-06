package com.samuel;
import java.io.IOException;
import java.net.ServerSocket
import java.util.ArrayList;

public class Server {

    private  int port;
    private  List<User> clients;
    private  ServerSocket server;

     public static void main(String[] args) throws IOException {

          new Server(67008).run();
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();

    }

    public void run() throws IOException {
         server = new ServerSocket(port);

    }

}

package com.samuel;
import java.io.IOException;
import java.net.ServerSocket
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

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
         server = new ServerSocket(port){
             @Override
             protected void finalize() throws IOException {
                 this.close();
             }
         };
     System.out.println("Port 67008 is now Open");

     while (true){
         Socket client = server.accept();

         //get username of user
         String username = (new Scanner(client.getInputStream())).nextLine();
         System.out.println("New User: \"" + username + "\"\n\t  Host:" + client.getInetAddress().getHostAddress());

         //Create new user
         User newUser = new User(client, nickname);

         // add newUser message to list
         this.clients.add(newUser);

         //TODO. Add user welcome message


         // create a new thread for newUser incoming messages handling
         new Thread(new UserHandler(this, newUser)).start();



     }

    }

}

package com.samuel;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.PrintStream;
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
         newUser.getOutStream().println(
                 "Welcome" + newUser.toString()
         );


         // create a new thread for newUser incoming messages handling
         new Thread(new UserHandler(this, newUser)).start();



     }

    }

    // delete a user from the list
    public void removeUser(User user){
        this.clients.remove(user);
    }

    // send incoming msg to all Users
    public void broadcastMessages(String msg, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println(
                    userSender.toString() + msg);
        }
    }

    // send list of clients to all Users
    public void broadcastAllUsers(){
        for (User client : this.clients) {
            client.getOutStream().println(this.clients);
        }
    }

    // send message to a User (String)
    public void sendMessageToUser(String msg, User userSender, String user){
        boolean find = false;
        for (User client : this.clients) {
            if (client.getNickname().equals(user) && client != userSender) {
                find = true;
                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getOutStream().println(userSender.toString() + ": " + msg);
            }
        }
        if (!find) {
            userSender.getOutStream().println(userSender.toString() + " -> (no one!): " + msg);
        }
    }
}

class UserHandler implements Runnable {

    private Server server;
    private User user;

    public UserHandler(Server server, User user) {
        this.server = server;
        this.user = user;
        this.server.broadcastAllUsers();
    }

    @Override
    public void run() {
        String message;

        // when there is a new message, broadcast to all
        Scanner sc = new Scanner(this.user.getInputStream());

        while (sc.hasNextLine()) {
            message = sc.nextLine();

            if (message.charAt(0) == '@'){
                if(message.contains(" ")){
                    System.out.println("private msg : " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate= message.substring(1, firstSpace);
                    server.sendMessageToUser(
                            message.substring(
                                    firstSpace+1, message.length()
                            ), user, userPrivate
                    );
                }

            }else if (message.charAt(0) == '#'){
                // update color for all other users
                this.server.broadcastAllUsers();
            }else{
                // update user list
                server.broadcastMessages(message, user);
            }
        }
        // end of Thread
        server.removeUser(user);
        this.server.broadcastAllUsers();
        sc.close();
      }

     }

     class User {
         private static int nbUser = 0;
         private int userId;
         private PrintStream streamOut;
         private InputStream streamIn;
         private String nickname;
         private Socket client;

         // constructor
         public User(Socket client, String name) throws IOException {
             this.streamOut = new PrintStream(client.getOutputStream());
             this.streamIn = client.getInputStream();
             this.client = client;
             this.nickname = name;
             this.userId = nbUser;
             nbUser += 1;
         }


         public PrintStream getOutStream(){
             return this.streamOut;
         }

         public InputStream getInputStream(){
             return this.streamIn;
         }

         public String getNickname(){
             return this.nickname;
         }


     }

    }


}

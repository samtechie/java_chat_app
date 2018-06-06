package com.samuel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String host;
    private int port;


    public static void main(String[] args) throws UnknownHostException,IOException {

          new Client("127.0.0.1",67008).run();
    }

    public Client(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public void run() throws UnknownHostException, IOException {
        Socket client = new Socket(host,port);
        System.out.println("Client connected to Server");

        PrintStream output = new PrintStream(client.getOutputStream());

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username:  ");
        String username = scanner.nextLine();

        //send username to Server
        output.println(username);

        //create a thread handling servers messages

        new Thread(new ReceivedMessagesHandler(client.getInputStream())).start();

        //Read messages and send them to the server
        System.out.println("Messages: \n");

        //Retrieve new messages
        while(scanner.hasNextLine()){
            output.println(scanner.nextLine());
        }

        output.close();
        scanner.close();
        client.close();


    }

}


class ReceivedMessagesHandler implements Runnable {
    private InputStream server;


    public ReceivedMessagesHandler(InputStream server) {
        this.server = server;
    }

    @Override
    public void run() {
        //receive messages from the server and print them to the screen

        Scanner sc = new Scanner(server);
        String temp = "";
        while (sc.hasNextLine()){
            temp = sc.nextLine();
            try {
                System.out.println(temp);
            }catch (Exception ignore){

            }

        }

        sc.close();

    }
}

/**
 * Conference Client
 * @author Coby Arambula
 */

import java.io.*;
import java.net.*;
import java.util.*;


// User needs to send host, port, and NAME to server

public class ConfClient implements Runnable 
{
    private BufferedReader fromUserReader;
    private PrintWriter toSocketWriter;
    private static String clientName;

    public ConfClient(BufferedReader reader, PrintWriter writer)
    {
        fromUserReader = reader;
        toSocketWriter = writer;
    }

    public void run()  // Read from keyboard, write to socket
    {
        try {
            // toSocketWriter.println(clientName + " joined the conference");
            toSocketWriter.println(clientName);
            while (true) {
                String line = fromUserReader.readLine();
                if(line == null)
                    break;
                toSocketWriter.println(line);
            }
        }
    catch (Exception e) {
        System.out.println(e);
        System.exit(1);
    }
    System.exit(0);
    }

public static void main(String[] args) 
{
    if (args.length != 3) {
        System.out.println("usage: java ConfClient <server host> <server port> <client name>");
        System.exit(1);
    }

    Socket sock = null;  // Connect to server at given host and port
    try {
        sock = new Socket(args[0], Integer.parseInt(args[1]));
        clientName = args[2];
        System.out.println(
            "You (" + clientName + ") connected to server at " + args[0] + ":" + args[1]);
    }
    catch(Exception e) {
        System.out.println(e);
        System.exit(1);
    }

    // Set up thread to read from user and write to socket
    try {
        PrintWriter toSockWriter =
            new PrintWriter(sock.getOutputStream(), true);

        BufferedReader fromUserReader = new BufferedReader(
                new InputStreamReader(System.in));
        
        Thread child = new Thread(new ConfClient(fromUserReader, toSockWriter));
        child.start();
    }
    catch(Exception e) {
        System.out.println(e);
        System.exit(1);
    }

    // Read from socket, display to user (socket should display user's name)
    try {
        BufferedReader fromSocketReader = new BufferedReader(
            new InputStreamReader(sock.getInputStream()));
        while (true) {
            String line = fromSocketReader.readLine();
            if (line == null) {
                System.out.println("*** Server quit ***");
                break;
            }
            System.out.println(line);
        }
    }
    catch(Exception e) {
        System.out.println("*** Server quit ***");
        System.exit(1);
    }
    System.exit(0);
}
}
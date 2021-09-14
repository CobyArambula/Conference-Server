/**
 * Conference Server
 * @author Coby Arambula
 */

// connect 2 clients to server simultaneously

 import java.io.*;
 import java.net.*;
 import java.util.*;
 import java.lang.Object;
 

 public class ConfServer implements Runnable
 {
    private Socket clientSocket;
    public static ArrayList<PrintWriter> clientList = new ArrayList<>();

    public ConfServer(Socket cSock)
    {
        clientSocket = cSock;
    }

    public static synchronized boolean addClient(PrintWriter toClientWriter)
    {
        return(clientList.add(toClientWriter));
    }

    public static synchronized boolean removeClient(PrintWriter toClientWriter)
    {
        return(clientList.remove(toClientWriter));
    }

    public static synchronized void relayMessage(PrintWriter toClientWriter, 
                                                String msg, String ClientName)
    {
        for (PrintWriter client : clientList) {
            if(client == toClientWriter)
                continue;
            else
                client.println(ClientName + ": " + msg);
        }
    }

    public void run()
	{   
		try {
            // Prepare to read from socket
            BufferedReader fromSockReader = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
            // get client name
            String clientName = fromSockReader.readLine();
            System.out.println(clientName + " joined the conference");
            // Prepare to write to socket with auto flush on
            PrintWriter toClientWriter =
					new PrintWriter(clientSocket.getOutputStream(), true);
            // Add this client to the active client list
            addClient(toClientWriter);

			while (true) 
            {
                // Read line from client
				String line = fromSockReader.readLine();
				    if (line == null) {
                        System.out.println(clientName + " left the conference");
                        break;
                    }
                // relay line to all active clients
                relayMessage(toClientWriter, line, clientName);
			}
            // remove client from client list
            removeClient(toClientWriter);
		}
		catch (Exception e) {
			System.out.println(e);
            System.exit(1);
		}
	}

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: java ConfServer <server port>");
            System.exit(1);
        }

      
        try {
            ServerSocket servSock = 
                new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Waiting for clients ...");
                // keep accepting/serving new clients
            while (true) {
            Socket cliSock = null;
            cliSock = servSock.accept();
            Thread child = new Thread(new ConfServer(cliSock));
            child.start();
        }
        }
        catch(Exception e) {
            System.out.println(e);
            System.exit(1);
        }
     }
 }
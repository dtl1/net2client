import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Time;


public class DMBServer {


    static Configuration c_;
    static int port; //server port


    public static void main(String[] args) {

        c_ = new Configuration("cs2003-net2.properties");

        try {
            port = c_.serverPort;
        } catch (NumberFormatException e) {
            System.out.println("can't configure port: " + e.getMessage());
        }


        //variables to hold the response message and the received message, and a boolean one that tells the while loop
        //if the program is finished
        boolean finished = false;


        try {
            //creating a socket
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

            System.out.println("\nSocket created at port: " + port + ", waiting for client");

            while (!finished) {

                Socket socket = serverSocket.accept();

                System.out.println("\nConnection made");

                //creating an input and a reader
                InputStream input = socket.getInputStream();

                //creating an output and writer
                //OutputStream output = socket.getOutputStream();
                //PrintWriter writer = new PrintWriter(output, true);

                //receive the clients input
                String s = new String(input.readAllBytes(), StandardCharsets.UTF_8);



                System.out.println("\nReceived data from client: " + s);

                String[] ee = new String[3];

                TimeStamp timeStamp = new TimeStamp();
                ee[0] = timeStamp.getDate();
                ee[1] = ee[0].substring(0,10);
                ee[2] = s;




                DirAndFile.main(ee);

                socket.close();
                System.out.println("\nConnection closed");
            }


            //catch an IO exception thrown by creating a bad socket
        } catch (IOException e) {
            System.out.println("I/O error" + e);
        }
    }
}
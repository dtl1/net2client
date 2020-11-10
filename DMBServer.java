import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Time;

public class DMBServer {

    static Configuration c_;
    static int port; //server port
    static String boardDirectory;

    public static void main(String[] args) {

        c_ = new Configuration("cs2003-net2.properties");

        try {
            port = c_.serverPort;
            boardDirectory = c_.boardDirectory;
        } catch (NumberFormatException e) {
            System.out.println("can't configure port: " + e.getMessage());
            System.exit(0);
        }

        try {
            //creating a socket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("\n++ Socket created at port: " + port);

            boolean finished = false;
            while (!finished) {

                //receive connection from client
                System.out.println("\n++ Waiting for client connection");
                Socket socket = serverSocket.accept();
                System.out.println("\n++ Connection made");

                //receive clients input
                InputStream input = socket.getInputStream();

                //convert client's input to string
                String inputStr = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                inputStr = inputStr.substring(2);
                System.out.println("\n++ Received data from client: " + inputStr +"\n");

                //array of details to be sent to dirandfile program
                String[] details = new String[3];
                TimeStamp timeStamp = new TimeStamp();
                details[0] = timeStamp.getDate();
                details[1] = boardDirectory + details[0].substring(0,10);
                details[2] = inputStr;
                DirAndFile.main(details);

                //close the connection
                socket.close();
                System.out.println("\n++ Connection closed");
            }


            //catch an IO exception thrown by creating a bad socket
        } catch (IOException e) {
            System.out.println("I/O error" + e);
        }
    }
}

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Daily Message Board Client
 * <p>
 * based on code by Saleem Bhatti, 28 Aug 2019
 */
public class DMBClient {

    static int maxTextLen_ = 256;
    static Configuration c_;

    // from configuration file
    static String server; // FQDN
    static int port; //server port

    public static void main(String[] args) {

        c_ = new Configuration("cs2003-net2.properties");
        try {
            server = c_.serverAddress;
            port = c_.serverPort;
        } catch (NumberFormatException e) {
            System.out.println("can't configure port: " + e.getMessage());
        }

        //create hashmap of users and ports from csv file
        HashMap<String, Integer> userPorts = readInCSV();

        //get users input
        Scanner scanner = new Scanner(System.in);

        boolean validInput = false;
        String user = null;
        String message = "";

        while (!validInput) {
            System.out.println("\nInput a command:");
            String userInput = scanner.nextLine();

            String[] inputArray = userInput.split(" ");

            if(inputArray.length < 3 || !(inputArray[0].equals("::to")) ){
                System.out.println("\nInvalid input syntax\nCorrect syntax: \"::to <user> <message>\"");
            } else if(!(userPorts.containsKey(inputArray[1]))){
                System.out.println("No user exists with this username");
            } else{
                validInput = true;

                user = inputArray[1];

                for(int i = 2; i < inputArray.length; i ++ ){
                    message += inputArray[i] + " ";
                }

                message = message.substring(0, message.length() -1);
            }

        }

        server = user + server;
        port = userPorts.get(user);

        try {
            Socket connection;
            OutputStream tx;
            InputStream rx;
            byte[] buffer;
            String s = "";
            String quit = "quit";
            int r;

            connection = startClient(server, port);

            //check if connection successfully created
            if (connection != null) {
                System.out.println("\n++ Connection successful\n");
            } else {
                System.out.println("\n++ Connection unsuccessful\n");
                System.exit(0);
            }

            tx = connection.getOutputStream();
            rx = connection.getInputStream();
            TimeStamp timeStamp = new TimeStamp();

            buffer = ("::from " + timeStamp.getUser() +" " +message).getBytes();
            r = buffer.length;
            if (r > maxTextLen_) {
                System.out.println("++ You entered more than " + maxTextLen_ + "bytes ... truncating.");
                r = maxTextLen_;
            }
            System.out.println("Sending " + r + " bytes");
            tx.write(buffer, 0, r); // to server

            System.out.print("\n++ Closing connection ... ");
            connection.close();
            System.out.println("... closed.");

        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

    }

    static Socket startClient(String hostname, int portnumber) {

        Socket connection = null;

        try {
            String address;
            int port;

            address = hostname;
            port = portnumber;

            connection = new Socket(address, port); // make a socket

            System.out.println("\n++ Connecting to " + hostname + ":" + port
                    + " -> " + connection);

        } catch (UnknownHostException e) {

            System.err.println("UnknownHost Exception: " + hostname + " "
                    + e.getMessage());

        } catch (IOException e) {

            System.err.println("IO Exception: " + e.getMessage());
        }

        return connection;
    }

    static HashMap<String, Integer> readInCSV() {

        HashMap<String, Integer> userPorts = new HashMap<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader("CS2003-usernames-2020.csv"));

            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] lineData = line.split(",");
                userPorts.put(lineData[0], Integer.valueOf(lineData[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userPorts;
    }
}

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    static Vector<ClientHandler> ClientsVec = new Vector<>();

    static int nClients = 0;

    @SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9999);
        System.out.println("The Server is waiting for the client...");
        while (true) {
                Socket SocServer = server.accept();
                System.out.println("Start Connection");

                System.out.println("The client with port number = " + SocServer.getPort() + " Added to the server");
                System.out.println("Waiting for the ID of the Client");

                ObjectInputStream ServerInput = new ObjectInputStream(SocServer.getInputStream());
                ObjectOutputStream ServerOutput = new ObjectOutputStream(SocServer.getOutputStream());

                //BufferedReader ClientMessage = new BufferedReader(new InputStreamReader(ServerInput));
                String id = ServerInput.readUTF();

                System.out.println("Client ID : " + id);
                ClientHandler ClientsHand = new ClientHandler(SocServer, id, ServerInput, ServerOutput);
                Thread thread = new Thread(ClientsHand);//user
                System.out.println("Adding this client to active client list");
                ClientsVec.add(ClientsHand);
                thread.start();
                nClients++;

        }
    }
}

class ClientHandler implements Runnable {
    static Vector<ClientHandler> NEWClientsVec = new Vector<>();
    final ObjectInputStream input;
    final ObjectOutputStream output;
    public String ID;
    Socket s;
    public ClientHandler(Socket s, String ID, ObjectInputStream input, ObjectOutputStream output) {
        this.ID = ID;
        this.s = s;
        this.input = input;
        this.output = output;
    }

    public void run() {

        try 
        {
            while (true) {
                System.out.println("The Client Sent this Text to share..");
                String received = input.readUTF();

                if (received.equals("Share")) {
                    received = input.readUTF();
                    NEWClientsVec.clear();

                    String[] IDs = received.split("\\-");
                    for (String s : IDs) {
                        for (ClientHandler mc : Server.ClientsVec) {
                            if (s.equals(mc.ID) && !mc.ID.equals(this.ID)) {
                                NEWClientsVec.add(mc);
                                break;
                            }
                        }
                        NEWClientsVec.add(this);
                    }
                }


                
                    received = input.readUTF();
                    for (ClientHandler mc : NEWClientsVec){
                        if (!ID.equals(mc.ID)) {
                            mc.output.writeUTF(received);
                            mc.output.flush();
                        }
                    }
                }

            
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            e.getMessage();
        }

    }
}
package chat;


import java.io.*;
import java.util.*;
import java.net.*;


public class Server
{

	
	static Vector<ClientHandler> ar = new Vector<>();
	
	static int i = 1;

	public static void main(String[] args) throws IOException
	{
		
		ServerSocket ss = new ServerSocket(3337);
		
		
		Socket s;
		
		while (true)
		{
			
			s = ss.accept();

			System.out.println("Solicitud de nuevo cliente recibida : " + s);
			
			
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			System.out.println("Creando nombre para este cliente...");

			// Create a new handler object for handling this request.
			ClientHandler mtch = new ClientHandler(s,"cliente " + i, dis, dos);

			// Se crea un nuevo hilo con este objeto
			Thread t = new Thread(mtch);
			
			System.out.println("Adicionando al cliente a la lista");
			System.out.println("El nombre para el cliente será: " + "cliente " +i);

			
			ar.add(mtch);

			// Se inicia el hilo
			t.start();

			
			i++;

		} 
	}
}




class ClientHandler implements Runnable
{
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isloggedin;
	
	
	public ClientHandler(Socket s, String name,
							DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.s = s;
		this.isloggedin=true;
	}

	@Override
	public void run() {

		String received;
		while (true)
		{
			try
			{
				// se recibe la cadena 
				received = dis.readUTF();
				
				System.out.println(received);
				
				if(received.equals("logout")){
					this.isloggedin=false;
					this.s.close();
					break;
				}
				
				
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.nextToken();
				String recipient = st.nextToken();

				
				for (ClientHandler mc : Server.ar)
				{
					
					if (mc.name.equals(recipient) && mc.isloggedin==true)
					{
						mc.dos.writeUTF(this.name+" : "+MsgToSend);
						break;
					}
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		try
		{
			
			this.dis.close();
			this.dos.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

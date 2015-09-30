package ca.polymtl.inf4402.tp1.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Client {

	public static void main(String[] args) {
		String distantHostname = null;

		Client client = new Client(distantHostname);
		client.run(args);

	}

	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;
	private Integer clientId;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}

		try {
			if(Files.exists(Paths.get("client_id"))){
		
				Scanner scanner = new Scanner(new File("client_id"));
				clientId = scanner.nextInt();
			}else {
				clientId = localServerStub.generateClientId();
				FileWriter wr = new FileWriter("client_id");
				wr.write(clientId.toString());
				wr.close();
			}
			
			System.out.println("Client Id : " + clientId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void run(String args[]) {

		try {
			if (args.length == 1) {

				if (args[0].equals("list")) {
					list();
				} else if (args[0].equals("syncLocalDir")) {
					get(args[1]);
				} 
			} else if (args.length == 2) {
				if (args[0].equals("get")) {
					get(args[1]);
				} else if (args[0].equals("create")) {
					create(args[1]);
				} else if (args[0].equals("push")) {
					push(args[1]);
				} else if (args[0].equals("lock")) {
					lock(args[1]);
				}
			} else {
				System.out.println("Wrong arguments");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void create(String nom) throws RemoteException {
		localServerStub.create(nom);
	}
	
	private void get(String nom) throws RemoteException {

		byte[] checksum = { -1 };
		byte[] file = null;
		if (!Files.exists(Paths.get(nom))) {
			file = localServerStub.get(nom, checksum);
		} else {
			file = localServerStub.get(nom, computeChecksum(getLocalFile(nom)));
		}
		
		if(file != null)
			writeToLocalFile(nom, file);
	}

	private void list() throws RemoteException {
		HashMap<String, Integer> fileList = localServerStub.list();

		System.out.println(fileList.toString());
	}
	
	private void push(String nom) throws RemoteException { 
		
		 System.out.println(localServerStub.push(nom, getLocalFile(nom), clientId));
	}
	
	private void lock(String nom) throws RemoteException {
		byte[] file = getLocalFile(nom);
		System.out.println(localServerStub.lock(nom, clientId, computeChecksum(file)));
	}
	
	private byte[] computeChecksum(byte[] file) {
		
		try {
			return MessageDigest.getInstance("md5").digest(file);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] getLocalFile(String nom) {
		
		byte[] file = null;
		
		try {
			 file = Files.readAllBytes(Paths.get(nom));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
	
	private void writeToLocalFile(String nom, byte[] file) {
		try {
			Files.write(Paths.get(nom), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

}
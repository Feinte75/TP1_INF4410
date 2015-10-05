package ca.polymtl.inf4402.tp1.client;

import java.io.File;
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

import ca.polymtl.inf4402.tp1.shared.CustomException;
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
				scanner.close();
			}else {
				clientId = localServerStub.generateClientId();
				FileWriter wr = new FileWriter("client_id");
				wr.write(clientId.toString());
				wr.close();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void run(String args[]) {

		try {
			if (args.length == 1) {

				if (args[0].equals("list")) {
					list();
				} else if (args[0].equals("syncLocalDir")) {
					syncLocalDir();
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
				System.out.println("Les commandes disponibles sont :\n" +
						"list\n" +
						"syncLocalDir\n" +
						"get <file name>\n" +
						"create <file name>\n" +
						"push <file name>\n" +
						"lock <file name>\n");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void create(String fileName) throws RemoteException {
		System.out.println(localServerStub.create(fileName));
	}
	
	private void get(String fileName) throws RemoteException {

		byte[] checksum = { -1 };
		byte[] file = null;
		
		if (!Files.exists(Paths.get(fileName))) {
			file = localServerStub.get(fileName, checksum);
		} else {
			file = localServerStub.get(fileName, computeChecksum(getLocalFile(fileName)));
		}
		
		if(file != null){
			writeToLocalFile(fileName, file);
			System.out.println("Fichier "+ fileName + " a bien été téléchargé ou mis à jour");
		}
		else {
			System.out.println("Fichier "+ fileName + " est à jour");
		}
			
	}

	private void list() throws RemoteException {
		HashMap<String, Integer> fileList = localServerStub.list();
		
		for(String fileName : fileList.keySet()){
			System.out.print(fileName + " : ");
			if(fileList.get(fileName) == null) {
				System.out.println("Non verrouillé");
			}
			else{
				System.out.println("Verrouillé par : " + fileList.get(fileName) );
			}
		}
		System.out.println( fileList.size() + " fichier(s)");
	}
	
	private void push(String fileName) throws RemoteException { 
		
		 System.out.println(localServerStub.push(fileName, getLocalFile(fileName), clientId));
	}
	
	private void lock(String fileName) throws RemoteException {
		byte[] file = getLocalFile(fileName);
		
		try {
			file = localServerStub.lock(fileName, clientId, computeChecksum(file));
			writeToLocalFile(fileName, file);
			System.out.println("Fichier "+fileName+ " à jour et verrouillé sur le serveur");
		}
		catch(CustomException e) {
			System.out.println(e.getCustomMessage());	
		} 
	}
	
	private void syncLocalDir() throws RemoteException {
		HashMap<String, Integer> fileList = localServerStub.list();
		
		for(String fileName : fileList.keySet()){
			get(fileName);
		}
	}
	
	private byte[] computeChecksum(byte[] file) {
		
		try {
			return MessageDigest.getInstance("md5").digest(file);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private byte[] getLocalFile(String fileName) {
		
		byte[] file = null;
		
		try {
			 file = Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
	
	private void writeToLocalFile(String fileName, byte[] file) {
		try {
			Files.write(Paths.get(fileName), file);
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
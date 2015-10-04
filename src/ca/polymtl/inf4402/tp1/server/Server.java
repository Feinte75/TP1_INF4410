package ca.polymtl.inf4402.tp1.server;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Server implements ServerInterface {

	private HashMap<String, byte[]> fileMap;
	private HashMap<String, Integer> lockMap;
	private Integer counter;
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
		fileMap = new HashMap<String, byte[]>(10);
		lockMap = new HashMap<String, Integer>(10);
		counter = 0;
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	@Override
	public Integer generateClientId() {
		counter = counter + 1;
		return counter;
	}

	@Override
	public void create(String fileName) {
		if(!fileMap.containsKey(fileName)) {
			fileMap.put(fileName, null);
			lockMap.put(fileName, null);
		}
	}

	@Override
	public HashMap<String, Integer> list() {		
		return lockMap;
	}

	@Override
	public HashMap<String, byte[]> syncLocalDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] get(String fileName, byte[] clientChecksum) {
		
		byte[] serverChecksum = null;
		
		try {
			serverChecksum = MessageDigest.getInstance("md5").digest(fileMap.get(fileName));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		if(MessageDigest.isEqual(clientChecksum, serverChecksum))
			return null;
		else 
			return fileMap.get(fileName);
	}

	@Override
	public byte[] lock(String fileName, Integer clientId, byte[] checksum) {
		
		if (lockMap.get(fileName) == null || lockMap.get(fileName).equals(clientId)) {
			lockMap.put(fileName, clientId);
			return fileMap.get(fileName);
		}
		
		return null;
	}

	@Override
	public String push(String fileName, byte[] contenu, Integer clientid) {
		
		if(lockMap.get(fileName) == null || lockMap.get(fileName).equals(clientid)) {
			fileMap.put(fileName, contenu);
			lockMap.put(fileName, null);
			return fileName + " a été enregistré sur le serveur";
		}
		else {
			return "L'opération a échouée";
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

}

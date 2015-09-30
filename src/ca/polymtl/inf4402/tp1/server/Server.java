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
	public void create(String nom) {
		if(!fileMap.containsKey(nom)) {
			fileMap.put(nom, null);
			lockMap.put(nom, null);
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
	public byte[] get(String nom, byte[] clientChecksum) {
		
		byte[] serverChecksum = null;
		
		try {
			serverChecksum = MessageDigest.getInstance("md5").digest(fileMap.get(nom));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(MessageDigest.isEqual(clientChecksum, serverChecksum))
			return null;
		else 
			return fileMap.get(nom);
	}

	// TODO change return string to byte[]
	@Override
	public String lock(String nom, Integer clientId, byte[] checksum) {
		
		if(!lockMap.containsKey(nom)) 
			return "Le fichier n'existe pas";
		
		if (lockMap.get(nom) == null || lockMap.get(nom).equals(clientId)) {
			lockMap.put(nom, clientId);
			return nom + " bien verrouillé";
		}
		else {
			return nom + " a déjà été verouillé par Client :" + lockMap.get(nom);
		}
	}

	@Override
	public String push(String nom, byte[] contenu, Integer clientid) {
		
		if(lockMap.get(nom) == null || lockMap.get(nom).equals(clientid)) {
			fileMap.put(nom, contenu);
			lockMap.put(nom, null);
			return nom + " a été enregistré sur le serveur";
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

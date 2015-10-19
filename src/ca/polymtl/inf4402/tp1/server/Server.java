package ca.polymtl.inf4402.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import ca.polymtl.inf4402.tp1.shared.CustomException;
import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Server implements ServerInterface {

	private HashMap<String, byte[]> fileMap;//Hashmap of the name of the files and their contents
	private HashMap<String, Integer> lockMap;//Hashmap of the name of the files and the id of the client who lock it (Default 0)
	private Integer counter;//to provide new id
	
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

	/*
	 * @return The id of the requesting client
	 */
	@Override
	public Integer generateClientId() {
		counter = counter + 1;
		return counter;
	}
	/*
	 *  Create a file on the server if it does not exist
	 */
	@Override
	public String create(String fileName) {
		if(!fileMap.containsKey(fileName)) {
			fileMap.put(fileName, new byte[1]);
			lockMap.put(fileName, 0);
			return fileName + " ajouté";
		}
		else {
			return fileName + " déjà présent sur le serveur";
		}
	}
	
	/*
	 * 
	 * @return lockMap Hashmap containing all files names and the id of the client who lock it 
	 */
	@Override
	public HashMap<String, Integer> list() {		
		return lockMap;
	}

	/*
	 * 
	 * @return lockMap Hashmap containing all files names and their contents  
	 */
	@Override
	public HashMap<String, byte[]> syncLocalDir() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Return the content of the file fileName if the clientChecksum is different 
	 */
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

	/*
	 * 
	 * Lock the file if the file is not lock and return the version if checksums are differents
	 */
	@Override
	public byte[] lock(String fileName, Integer clientId, byte[] checksum) throws CustomException {
		
		if (!lockMap.containsKey(fileName)) {
			throw new CustomException("Le fichier "+fileName+ " n'existe pas sur le serveur");		
		}
		else if(!lockMap.get(fileName).equals(0) && !lockMap.get(fileName).equals(clientId)) {
			throw new CustomException(fileName + " est déjà verrouillé par "+ lockMap.get(fileName));	
		}	
		
		lockMap.put(fileName, clientId);
		return fileMap.get(fileName);
	}

	/*
	 * 
	 * If the file exists on the server, push a new version if the file is lock
	 */
	@Override
	public String push(String fileName, byte[] contenu, Integer clientid) {
		
		if(lockMap.get(fileName) != null && lockMap.get(fileName).equals(clientid)) {
			fileMap.put(fileName, contenu);
			lockMap.put(fileName, 0);
			return fileName + " a été enregistré sur le serveur";
		}
		else {
			return "L'opération a échouée : veuillez d'abord verrouiller le fichier";
		}
	}
	/*
	 * return the checksum of a file 
	 */
	private byte[] computeChecksum(byte[] file) {
		
		try {
			return MessageDigest.getInstance("md5").digest(file);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}

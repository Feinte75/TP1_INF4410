package ca.polymtl.inf4402.tp1.server;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Set;

import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Server implements ServerInterface {

	HashMap<String, byte[]> fileMap;
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
		fileMap = new HashMap<String, byte[]>(10);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(String nom) {
		if(!fileMap.containsKey(nom)) {
			fileMap.put(nom, null);
		}
	}

	@Override
	public Set<String> list() {
		return fileMap.keySet();
	}

	@Override
	public HashMap<String, byte[]> syncLocalDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] get(String nom, byte[] checksum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer lock(String nom, Integer clientId, byte[] checksum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer push(String nom, byte[] contenu, Integer clientid) {
		// TODO Auto-generated method stub
		return null;
	}


}

package ca.polymtl.inf4402.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

public interface ServerInterface extends Remote {
	
	Integer generateClientId() throws RemoteException;;
	
	void create(String nom) throws RemoteException;;
	
	Set<String> list() throws RemoteException;;
	
	HashMap<String, byte[]> syncLocalDir() throws RemoteException;;
	
	byte[] get(String nom, byte[] checksum) throws RemoteException;;
	
	Integer lock(String nom, Integer clientId, byte[] checksum) throws RemoteException;;
	
	Integer push(String nom, byte[] contenu, Integer clientid) throws RemoteException;;
}

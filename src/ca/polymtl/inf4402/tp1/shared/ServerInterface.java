package ca.polymtl.inf4402.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ServerInterface extends Remote {
	
	Integer generateClientId() throws RemoteException;
	
	void create(String nom) throws RemoteException;
	
	HashMap<String, Integer> list() throws RemoteException;

	HashMap<String, byte[]> syncLocalDir() throws RemoteException;
	
	byte[] get(String nom, byte[] checksum) throws RemoteException;
	
	String lock(String nom, Integer clientId, byte[] checksum) throws RemoteException;
	
	String push(String nom, byte[] contenu, Integer clientid) throws RemoteException;
}

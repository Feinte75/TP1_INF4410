package ca.polymtl.inf4402.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ServerInterface extends Remote {
	
	/**
	 * 
	 * @return The id of the requesting client
	 * @throws RemoteException
	 */
	Integer generateClientId() throws RemoteException;
	
	/**
	 * Create a file on the server
	 * @param fileName Name of the file to create
	 * @return TODO
	 * @throws RemoteException
	 */
	String create(String fileName) throws RemoteException;
	
	/**
	 * Get a list of all file names stored on the server 
	 * @return HashMap containing all file names
	 * @throws RemoteException
	 */
	HashMap<String, Integer> list() throws RemoteException;

	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	HashMap<String, byte[]> syncLocalDir() throws RemoteException;
	
	/**
	 * 
	 * @param fileName
	 * @param checksum
	 * @return
	 * @throws RemoteException
	 */
	byte[] get(String fileName, byte[] checksum) throws RemoteException;
	
	/**
	 * 
	 * @param fileName
	 * @param clientId
	 * @param checksum
	 * @return
	 * @throws CustomException
	 */
	byte[] lock(String fileName, Integer clientId, byte[] checksum) throws RemoteException, CustomException;
	
	/**
	 * 
	 * @param fileName
	 * @param contenu
	 * @param clientid
	 * @return
	 * @throws RemoteException
	 */
	String push(String fileName, byte[] contenu, Integer clientid) throws RemoteException;
}

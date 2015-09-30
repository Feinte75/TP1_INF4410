package ca.polymtl.inf4402.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Client {
	
	public static void main(String[] args) {
		String distantHostname = null;
		
		Client client = new Client(distantHostname);
		client.run(args);

	}

	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	private void run(String args[]) {
		
		if (args.length > 0) {
			try {
				if(args[0].equals("get")) {
					get(args[1]);	
				}
				else if(args[0].equals("create")) {
					localServerStub.create(args[1]);	
				}
				else if(args[0].equals("list")) {
					System.out.println(localServerStub.list().toString());
				}
				else if(args[0].equals("push")) {
					get(args[1]);	
				}
				else if(args[0].equals("syncLocalDir")) {
					get(args[1]);	
				}
				else if(args[0].equals("lock")) {
					get(args[1]);	
				}
				else {
					System.out.println("Wrong arguments");
				}
			}
			catch(RemoteException e){
				e.printStackTrace();
			}

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
	
	private void get(String nom){
		
	}

}
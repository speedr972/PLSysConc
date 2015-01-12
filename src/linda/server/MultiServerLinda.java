package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Iterator;

import linda.Callback;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class MultiServerLinda extends UnicastRemoteObject implements LindaServer {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Chaque serveur connaît les serveurs auquels il est relié (cf figure 2 du sujet du projet)
	private Collection<MultiServerLinda> serveurs;
	private String uri;
	
	private CentralizedLinda linda;
	
	public MultiServerLinda(String uri) throws RemoteException{
		this.uri = uri;
		this.linda = new CentralizedLinda();
	}
	
	public void lancer() throws RemoteException, MalformedURLException {
		Registry registry = LocateRegistry.createRegistry(5005);
		Naming.rebind(this.uri,this);
	}
	
	public void ajouterServeur(String serverURI) throws RemoteException, MalformedURLException, NotBoundException{
		
		//On récupère les références sur les objet Remote que sont les serveurs
		serveurs.add((MultiServerLinda) Naming.lookup(serverURI));
	}
	
	//Read sur les espaces de tuples des autres serveurs (via leur méthode)
	public Tuple readServeurs(Tuple template) throws RemoteException {
		Iterator<MultiServerLinda> it = serveurs.iterator();
		Tuple retour = null;
		boolean nonTrouve = true;
		while(it.hasNext() && nonTrouve) {
			MultiServerLinda s =(MultiServerLinda) it.next();
			retour = s.readLocal(template);
			if (retour != null) {
				nonTrouve = false;
			}
		}
		return retour;
	}
	
	//Take sur les espaces de tuples des autres serveurs (via leur méthode)
	public Tuple takeServeurs(Tuple template) throws RemoteException {
		Iterator<MultiServerLinda> it = serveurs.iterator();
		Tuple retour = null;
		boolean nonTrouve = true;
		while(it.hasNext() && nonTrouve) {
			MultiServerLinda s =(MultiServerLinda) it.next();
			retour = s.takeLocal(template);
			if (retour != null) {
				nonTrouve = false;
			}
		}
		return retour;
	}


	public void write(Tuple t) throws RemoteException {
		this.linda.write(t);
	}
	
	//Take sur l'espace de tuple local puis sur les autres s'il n'y a pas de résultat
	public Tuple take(Tuple template) throws RemoteException{
		Tuple retour = this.linda.take(template);
		if (retour == null) {
			retour = readServeurs(template);
		}
		return retour;
	}

	//Read sur l'espace de tuple local puis sur les autres s'il n'y a pas de résultat
	public Tuple read(Tuple template) throws RemoteException{
		Tuple retour = this.linda.read(template);
		if (retour == null) {
			retour = readServeurs(template);
		}
		return retour;
	}

	//Take seulement sur l'espace de tuple du serveur
	public Tuple takeLocal(Tuple template) throws RemoteException{
		return this.linda.take(template);
	}

	//Read seulement sur l'espace de tuple du serveur
	public Tuple readLocal(Tuple template) throws RemoteException{
		return this.linda.read(template);
	}

	public Tuple tryTake(Tuple template) throws RemoteException{
		return this.linda.tryTake(template);
	}

	public Tuple tryRead(Tuple template) throws RemoteException{
		return this.linda.tryRead(template);
	}

	public Collection<Tuple> takeAll(Tuple template) throws RemoteException{
		return this.linda.takeAll(template);
	}

	public Collection<Tuple> readAll(Tuple template)throws RemoteException{
		return this.linda.readAll(template);
	}

	public void eventRegister(LindaClient.eventMode mode, LindaClient.eventTiming timing,
			Tuple template, Callback callback) throws RemoteException{
		this.linda.eventRegister(mode, timing, template, callback);

	}

	public void debug(String prefix) throws RemoteException{
		this.linda.debug(prefix);
	}

}

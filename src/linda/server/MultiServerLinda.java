package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import linda.Callback;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class MultiServerLinda extends UnicastRemoteObject implements LindaServer {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Chaque serveur connaît les serveurs auquels il est relié (cf figure 2 du sujet du projet)
	//private Collection<MultiServerLinda> serveurs;
	private Collection<String> uri;
	private String monUri;
	
	
	private CentralizedLinda linda;
	
	public MultiServerLinda(String monUri) throws RemoteException{
		this.linda = new CentralizedLinda();
		this.uri = new LinkedList<String>();
		uri.add("//localhost:4001/multi4001");
		uri.add("//localhost:4002/multi4002");
		this.monUri = monUri;
	}
	
//	public void ajouterServeur(String serverURI) throws RemoteException, MalformedURLException, NotBoundException{
//		
//		//On récupère les références sur les objet Remote que sont les serveurs
//		serveurs.add((MultiServerLinda) Naming.lookup(serverURI));
//	}
	
	//Read sur les espaces de tuples des autres serveurs (via leur méthode)
	public Tuple tryReadServeurs(Tuple template) throws RemoteException, MalformedURLException, NotBoundException {
		//Iterator<MultiServerLinda> it = serveurs.iterator();
		Iterator<String> it = uri.iterator();

		Tuple retour = null;
		boolean nonTrouve = true;
		while(it.hasNext() && nonTrouve) {
			
			//MultiServerLinda s =(MultiServerLinda) it.next();
			String next = it.next();
			if (!(next.equals(this.monUri))) {
				
			MultiServerLinda s = (MultiServerLinda) Naming.lookup(next);
			retour = s.tryReadLocal(template);
			if (retour != null) {
				nonTrouve = false;
			}
			}
		}
		return retour;
	}
	
	//Take sur les espaces de tuples des autres serveurs (via leur méthode)
	public Tuple tryTakeServeurs(Tuple template) throws RemoteException, MalformedURLException, NotBoundException {
		//Iterator<MultiServerLinda> it = serveurs.iterator();
		Iterator<String> it = uri.iterator();
		Tuple retour = null;
		boolean nonTrouve = true;
		while(it.hasNext() && nonTrouve) {
			//MultiServerLinda s =(MultiServerLinda) it.next();
			
			String next = it.next();
			if (!(next.equals(this.monUri))) {
				LindaServer s = (LindaServer) Naming.lookup(next);
				retour = s.tryTake(template);
				if (retour != null) {
					nonTrouve = false;
				}
			}
		}
		return retour;
	}


	public void write(Tuple t) throws RemoteException {
		if (t != null) {
			System.out.println("tuple O00" + t);
		} else {
			System.out.println("tuple null");
		}
		System.out.println("--------------------------------------------");
		this.linda.write(t);
		LindaServer m;

		Iterator itserv = this.uri.iterator();
		for (String url : this.uri) {
			if (!url.equals(this.monUri)) {
				try {
					//System.out.println(url);

					//System.out.println("--" + monUri);
					m = (LindaServer) Naming.lookup(url);
					//System.out.println("Connexion au serveur " + url
					//		+ " faite, envoi d'un signal");
					m.signal();
				} catch (MalformedURLException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
	
	//tryTake sur l'espace de tuple local puis sur les autres  et s'il n'y a pas de résultat, s'endort
	
	public Tuple take(Tuple template) throws RemoteException {
		Tuple retour = null;
		boolean recommence = true;
		while (recommence) {
			retour = tryTake(template);
			if (retour == null) {
				this.await();
			} else {
				recommence = false;
			}

		}
		System.out.println("take done : " + retour);
		// System.out.print(template + "done");
		return retour;
	}
	
	

	public String getMonUri() {
		return monUri;
	}

	public void setMonUri(String monUri) {
		this.monUri = monUri;
	}

	public CentralizedLinda getLinda() {
		return linda;
	}

	public void setLinda(CentralizedLinda linda) {
		this.linda = linda;
	}

	//tryRead sur l'espace de tuple local puis sur les autres  et s'il n'y a pas de résultat, s'endort
	public Tuple read(Tuple template) throws RemoteException {
		Tuple retour = null;
		boolean recommence = true;
		while (recommence) {
			retour = tryRead(template);
			if (retour == null) {
				this.await();
			} else {
				recommence = false;
			}
		}
		return retour;
	}

	//Take seulement sur l'espace de tuple du serveur
	public Tuple tryTakeLocal(Tuple template) throws RemoteException{
		return this.linda.tryTake(template);
	}

	//Read seulement sur l'espace de tuple du serveur
	public Tuple tryReadLocal(Tuple template) throws RemoteException{
		return this.linda.tryRead(template);
	}

	//tryTake sur soi Et tous les serveurs
	public Tuple tryTake(Tuple template) throws RemoteException {
		// Tuple retour = this.tryTakeLocal(template);
		// if (retour == null) {
		// try {
		// retour = tryTakeServeurs(template);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// return retour;

		Tuple retour = null;

		retour = this.linda.tryTake(template);
		if (retour == null) {
			try {
				// retour = tryTakeServeurs(template);
				// /////////////////////////////////////
				Iterator<String> it = uri.iterator();
				boolean nonTrouve = true;
				while (it.hasNext() && nonTrouve) {
					// MultiServerLinda s =(MultiServerLinda) it.next();

					String next = it.next();
					if (!(next.equals(this.monUri))) {
						LindaServer s = (LindaServer) Naming
								.lookup(next);
						System.out.println("lookup done");
						retour = s.tryTakeLocal(template);
						if (retour != null) {
							nonTrouve = false;
						}
					}
				}
				// //////////////////////////////////

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			
		}

		System.out.println("take done : " + retour);
		// System.out.print(template + "done");
		return retour;
	}

	//tryRead sur soi Et tous les serveurs
	public Tuple tryRead(Tuple template) throws RemoteException{
		
		Tuple retour = null;

		retour = this.linda.tryRead(template);
		if (retour == null) {
			try {
				// retour = tryTakeServeurs(template);
				// /////////////////////////////////////
				Iterator<String> it = uri.iterator();
				boolean nonTrouve = true;
				while (it.hasNext() && nonTrouve) {
					// MultiServerLinda s =(MultiServerLinda) it.next();

					String next = it.next();
					if (!(next.equals(this.monUri))) {
						LindaServer s = (LindaServer) Naming
								.lookup(next);
						System.out.println("lookup done");
						retour = s.tryReadLocal(template);
						if (retour != null) {
							nonTrouve = false;
						}
					}
				}
				// //////////////////////////////////

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			
		}

		System.out.println("read done : " + retour);
		try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		return retour;
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
	
//	public void lancer() throws RemoteException, MalformedURLException {
//		Registry registry = LocateRegistry.createRegistry(4002);
//		Naming.rebind(this.monUri,this);
//	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			int port = Integer.parseInt(args[0]);
			String URL= "//localhost:"+args[0]+"/multi"+port;
			
			LindaServer sun = new MultiServerLinda(URL);
			Registry registry = LocateRegistry.createRegistry(port);
			Naming.rebind(URL, sun);
			System.out.println("Serveur lancé : " + port);
			
			
//			LindaServer sdeux = new MultiServerLinda("//localhost:4OO2/sdeux");
//			Registry registry2 = LocateRegistry.createRegistry(4002);
//			Naming.rebind("//localhost:4OO2/sdeux", sdeux);

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Tuple waitEvent(eventMode mode, eventTiming timing, Tuple template)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void signal(){
		this.linda.getMoniteur().lock();
		this.linda.getAutorisation().signal();
		this.linda.getMoniteur().lock();
	}
	
	public void await(){
		this.linda.getMoniteur().lock();
		try {
			this.linda.getAutorisation().await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.linda.getMoniteur().lock();
	}


}

package linda.server;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import linda.Callback;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class MonoServerLinda extends UnicastRemoteObject implements LindaServer {
	
	private CentralizedLinda linda;
	private String uri;
	
	public MonoServerLinda(String uri) throws RemoteException{
		this.uri = uri;
		this.linda = new CentralizedLinda();
	}
	
	public void lancer() throws RemoteException, MalformedURLException {
		Registry registry = LocateRegistry.createRegistry(5005);
		Naming.rebind(this.uri,this);
	}

	public void write(Tuple t) throws RemoteException {
		this.linda.write(t);
	}

	public Tuple take(Tuple template) throws RemoteException{
		return this.linda.take(template);
	}

	public Tuple read(Tuple template) throws RemoteException{
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

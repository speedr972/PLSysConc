package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.AsynchronousCallback;
import linda.Linda;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class MonoServerLinda extends UnicastRemoteObject implements LindaServer {
	
	

	private Linda linda;
	private String uri;

	public MonoServerLinda(String uri) throws RemoteException {
		this.uri = uri;
		this.linda = new CentralizedLinda();
	}

	public MonoServerLinda() throws RemoteException {
		this.linda = new CentralizedLinda();
	}

	public void lancer() throws RemoteException, MalformedURLException {
		Registry registry = LocateRegistry.createRegistry(5005);
		Naming.rebind(this.uri, this);
	}

	public void write(Tuple t) throws RemoteException {
		//synchronized (this.linda) {
			this.linda.write(t);
		//}
	}

	public Tuple take(Tuple template) throws RemoteException {
		//synchronized (this.linda) {
			return this.linda.take(template);
		//}
	}

	public Tuple read(Tuple template) throws RemoteException {
		//synchronized (this.linda) {

			return this.linda.read(template);
		//}
	}

	public Tuple tryTake(Tuple template) throws RemoteException {
		//synchronized (this.linda) {

			return this.linda.tryTake(template);
		//}
	}

	public Tuple tryRead(Tuple template) throws RemoteException {
		//synchronized (this.linda) {
			return this.linda.tryRead(template);
		//}
	}

	public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
		//synchronized (this.linda) {
			return this.linda.takeAll(template);
		//}
	}

	public Collection<Tuple> readAll(Tuple template) throws RemoteException {
		//synchronized (this.linda) {
			return this.linda.readAll(template);
		//}
	}

	//renvoie le tuple associé à un evenement en attente
	public Tuple waitEvent(eventMode mode, eventTiming timing, Tuple template)
			throws RemoteException {
		Lock mon = new ReentrantLock();
		Condition cond = mon.newCondition();

//		mon.lock();

//		Semaphore sem = new Semaphore(0);
//		CallBackTuple cbt = new CallBackTuple(sem);
		CallBackTuple cbt = new CallBackTuple(mon, cond);
		this.linda.eventRegister(mode, timing, template,
				new AsynchronousCallback(cbt));
		cbt.lock();
		try {
			System.out.println("OOOOOOOOOOOOOOOOOOOO");
//			sem.acquire();
			cbt.await();
			System.out.println("tuple acquis : " + cbt.getTuple());
		}catch(Exception e){
			
		}finally{
			cbt.unlock();
		}
//		try {
//			synchronized(cond){
//			cond.wait();
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//sem.release();
		System.out.println("coucou2");
		System.out.println("///////////////////////////");
		System.out.println("callback activé, tuple : " + cbt.getTuple());
		if(mode==eventMode.READ){
			System.out.println("mode read");
		}else{
			System.out.println("mode take");
		}
		System.out.println("////////////////////////");
		
		// cbt.getTuple().toString();
//		mon.unlock();
		return cbt.getTuple();

	}

	public void debug(String prefix) throws RemoteException{
		this.linda.debug(prefix);
	}

	public Linda getLinda() {
		return linda;
	}

	public void setLinda(CentralizedLinda linda) {
		this.linda = linda;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
	
	public static void main(String[] args) {
		
		int port  = 4000;
		//int port = Integer.parseInt(args[0]);
		String URL;		
		
		try{
			Registry registry = LocateRegistry.createRegistry(port);
			
			MonoServerLinda server = new MonoServerLinda();
			URL = "//localhost:" + port + "/MonServeur";
			Naming.rebind(URL, server);
			System.out.println("Serveur lancé");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		

	}

	@Override
	public Tuple tryTakeLocal(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tuple tryReadLocal(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void signal() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}

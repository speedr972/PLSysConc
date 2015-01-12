package linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Tuple;

public interface LindaServer extends Remote{

	public void write(Tuple t) throws RemoteException;
	public Tuple take(Tuple template)throws RemoteException;
	public Tuple read(Tuple template)throws RemoteException;
	public Tuple tryTake(Tuple template)throws RemoteException;
	public Tuple tryRead(Tuple template)throws RemoteException;
	public Collection<Tuple> takeAll(Tuple template)throws RemoteException;
    public Collection<Tuple> readAll(Tuple template)throws RemoteException;

    public void eventRegister(LindaClient.eventMode mode, LindaClient.eventTiming timing, Tuple template, Callback callback)throws RemoteException;
    public void debug(String prefix)throws RemoteException;
}
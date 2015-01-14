package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	LindaServer serverLinda;
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
        try {
			this.serverLinda = (LindaServer)Naming.lookup(serverURI);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    } 

	public void write(Tuple t) {
		try {
			serverLinda.write(t);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public Tuple take(Tuple template) {
		Tuple t = null;
		try {
			t = serverLinda.take(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return t;
	}

	
	public Tuple read(Tuple template) {
		Tuple t = null;
		try {
			t = serverLinda.read(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return t;
	}

	
	public Tuple tryTake(Tuple template) {
		Tuple t = null;
		try {
			serverLinda.tryTake(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return t;
	}

	
	public Tuple tryRead(Tuple template) {
		Tuple t = null;
		try {
			serverLinda.tryRead(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return t;
	}

	
	public Collection<Tuple> takeAll(Tuple template) {
		Collection<Tuple> Ct = null;
		try {
			Ct = serverLinda.takeAll(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return Ct;
	}

	
	public Collection<Tuple> readAll(Tuple template) {
		Collection<Tuple> Ct = null;
		try {
			Ct = serverLinda.readAll(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return Ct;
	}

	
	public void eventRegister(eventMode mode, eventTiming timing,
			Tuple template, Callback callback) {
		try {
			serverLinda.eventRegister(mode, timing, template, callback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void debug(String prefix) {
		try {
			serverLinda.debug(prefix);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

}

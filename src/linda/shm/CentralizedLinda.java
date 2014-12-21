package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	private Lock moniteur;
	private List<Tuple> tupleSpace;
	private Condition autorisation;
	private int file = 0;
	private Map<Tuple, Collection<CallEvent>>  waitingCallBack = new HashMap<Tuple, Collection<CallEvent>>();
	
		
	

    public CentralizedLinda() {
    	this.moniteur = new ReentrantLock();
    	this.tupleSpace = new ArrayList<Tuple>();
    	this.autorisation = this.moniteur.newCondition();
    }

	public void write(Tuple t) {
		// TODO Auto-generated method stub
		moniteur.lock();
		this.tupleSpace.add(t);
		if(file>0){
			this.autorisation.signalAll();
		}		
		this.trouveCallBack(t);
		moniteur.unlock();
	}

	public Tuple take(Tuple template) {
	moniteur.lock();
	while(find(template, this.tupleSpace.iterator())==null){
		try{
			file++;
			autorisation.await();

		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
		file--;
	}
	Tuple inter = tupleSpace.remove(tupleSpace.indexOf(find(template, this.tupleSpace.iterator())));
	moniteur.unlock();
	return inter;
		
	}

	public Tuple read(Tuple template) {
		moniteur.lock();
		while(find(template, this.tupleSpace.iterator())==null){
			try{
				file++;
				autorisation.await();

			}catch(InterruptedException e){
				e.printStackTrace();
			}
			file--;
		}
		Tuple inter = this.find(template, this.tupleSpace.iterator());
		moniteur.unlock();
	
		return inter;
	}

	public Tuple tryTake(Tuple template) {
		// TODO Auto-generated method stub
		Tuple t = this.find(template,this.tupleSpace.iterator());
		if(t != null){
			this.tupleSpace.remove(this.tupleSpace.indexOf(t));
		}
		return t;
	}

	public Tuple tryRead(Tuple template) {
		// TODO Auto-generated method stub
		return this.find(template,this.tupleSpace.iterator());
	}

	public Collection<Tuple> takeAll(Tuple template) {
		Collection<Tuple> ft = readAll(template);
		tupleSpace.removeAll(ft);
		return ft;
	}

	public Collection<Tuple> readAll(Tuple template) {
		Collection<Tuple> listTuple = new ArrayList<Tuple>();
		Iterator<Tuple> it = tupleSpace.listIterator();
		Tuple inter = new Tuple();
		
		while(it.hasNext()){
			inter = it.next();
			if(inter.matches(template)){
				listTuple.add(inter);
			}
		}
		return listTuple;
	
	}

	public void eventRegister(eventMode mode, eventTiming timing,
			Tuple template, Callback callback) {
		Tuple inter ;
		if( timing== eventTiming.IMMEDIATE){
			if(mode ==eventMode.TAKE){
				inter = tryTake(template);				
			}
			else{
				inter = tryRead(template);
			}
			if (inter!=null){
				callback.call(inter);
			}
			else{
				this.enregistrerCall(callback, mode, template);
			}			
		}
		else{
			this.enregistrerCall(callback, mode, template);
		}

		
		// TODO Auto-generated method stub
		
	}

	public void debug(String prefix) {
		// TODO Auto-generated method stub
		System.out.println("liste des tuples presents");
		for(Tuple t : tupleSpace){
			
			System.out.println(prefix + t);
		}
		
	}

	/* trouve le tuple recherché dans le serveur où sont stocké les tuples
	 * @param Tuple le tuple recherché
	 * @return Tuple
	 */
	public Tuple find(Tuple template, Iterator<Tuple> it){
		Tuple ft = null;
		boolean arret = false;
		while(it.hasNext() && arret==false){
			Tuple i = it.next();
			if(template.matches(i) || i.matches(template)){
				arret = true;
				ft = i;
			}
		}
		return ft;
	}
	
	/* met un callback en attente sur le bon motif de tuple qu'il recherche
	 * @param Callback le callback en question
	 * @param eventMode l'eventMode du callback
	 * @param Tuple le motif du tuple
	 */
	public void enregistrerCall(Callback callback, eventMode e, Tuple tuple) {
		CallEvent inter = new CallEvent(callback, e);
		if (this.waitingCallBack.get(tuple)== null){
			List<CallEvent> intermediaire = new ArrayList<CallEvent>();
			intermediaire.add(inter);
			this.waitingCallBack.put(tuple, intermediaire);
		}
		else{
			this.waitingCallBack.get(tuple).add(inter);
		}
	}

	/* à partir d'un motif, cherche le callback à réveiller et le réveille. si plusieurs callback
	 * sont présent pour le même motif, tant que l'eventmode des callback n'est pas read, on les réveille
	 * tous.
	 * @param Tuple
	 */
	public void trouveCallBack(Tuple template){
		Iterator<Tuple> it = this.waitingCallBack.keySet().iterator();
		Tuple Ituple = this.find(template,it);
		ArrayList<CallEvent> inter = (ArrayList<CallEvent>) this.waitingCallBack.get(Ituple);
		Boolean isTake = false;
		if(inter != null){
			Iterator<CallEvent> iterator = inter.listIterator();
			while(iterator.hasNext() && !isTake){
				CallEvent event = iterator.next();
				if(event.getEvent()== eventMode.TAKE){
					isTake = true;
				}
				this.eventRegister(event.getEvent(), eventTiming.IMMEDIATE, template, event.getCallback());
				iterator.remove();
			}			
			if (inter.isEmpty()){
				this.waitingCallBack.remove(template);
			}
		}		
	}
}

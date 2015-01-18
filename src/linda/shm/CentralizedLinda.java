package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	private BlockingQueue<Tuple> tupleSpace;
	private Map<Tuple, BlockingQueue<CallEvent>>  waitingCallBack = new HashMap<Tuple, BlockingQueue<CallEvent>>();
	
		
	

    public CentralizedLinda() {
    	this.tupleSpace = new LinkedBlockingQueue<Tuple>();

    }

	public void write(Tuple t) {
		// TODO Auto-generated method stub

		this.tupleSpace.add(t);
		this.trouveCallBack(t);

	}

	public Tuple take(Tuple template) {

	Tuple inter = null;
	inter = this.findTupleSpace(template, this.tupleSpace.iterator());
	this.tupleSpace.remove(inter);
	return inter;
		
	}

	public Tuple read(Tuple template) {
		Tuple inter = this.findTupleSpace(template, this.tupleSpace.iterator());

		return inter;
	}

	public Tuple tryTake(Tuple template) {
		// TODO Auto-generated method stub
		Tuple t = this.findTupleSpace(template,this.tupleSpace.iterator());
		if(t != null){
			this.tupleSpace.remove(t);
		}
		return t;
	}

	public Tuple tryRead(Tuple template) {
		// TODO Auto-generated method stub
		return this.findTupleSpace(template,this.tupleSpace.iterator());
	}

	public Collection<Tuple> takeAll(Tuple template) {
		Collection<Tuple> ft = readAll(template);
		tupleSpace.removeAll(ft);
		return ft;
	}

	public Collection<Tuple> readAll(Tuple template) {
		Collection<Tuple> listTuple = new ArrayList<Tuple>();
		Iterator<Tuple> it = tupleSpace.iterator();
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
				//
				this.enregistrerCall(callback, mode, template);
			}			
		}
		else{
			//
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

	/* trouve le tuple recherch� dans le serveur o� sont stock� les tuples
	 * @param Tuple le tuple recherch�
	 * @return Tuple
	 */
	public Tuple findTupleSpace(Tuple template, Iterator<Tuple> it){
		Tuple ft = null;
		boolean arret = false;
		while(it.hasNext() && arret==false){
			Tuple i = it.next();
			if(i.matches(template) ){
				arret = true;
				ft = i;
			}
		}
		return ft;
	}
	
	/* trouve le tuple recherch� dans la file d'attente des callback o� sont stock� les tuples
	 * @param Tuple le tuple recherch�
	 * @return Tuple
	 */
	public Tuple findTupleCall(Tuple template, Iterator<Tuple> it){
		Tuple ft = null;
		boolean arret = false;
		while(it.hasNext() && arret==false){
			Tuple i = it.next();
			if(template.matches(i)){
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
			BlockingQueue<CallEvent> intermediaire = new LinkedBlockingQueue<CallEvent>();
			intermediaire.add(inter);
			this.waitingCallBack.put(tuple, intermediaire);
		}
		else{
			this.waitingCallBack.get(tuple).add(inter);
		}
	}

	/* � partir d'un motif, cherche le callback � r�veiller et le r�veille. si plusieurs callback
	 * sont pr�sent pour le m�me motif, tant que l'eventmode des callback n'est pas read, on les r�veille
	 * tous.
	 * @param Tuple
	 */
	public void trouveCallBack(Tuple template){
		Iterator<Tuple> it = this.waitingCallBack.keySet().iterator();
		Tuple Ituple = this.findTupleCall(template, it);
		LinkedBlockingQueue<CallEvent> inter = (LinkedBlockingQueue<CallEvent>) this.waitingCallBack.get(Ituple);
		Boolean isTake = false;
		if(inter != null){			
			Iterator<CallEvent> iterator = inter.iterator();
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

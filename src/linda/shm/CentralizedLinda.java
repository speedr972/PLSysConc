package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
	
    public CentralizedLinda() {
    	this.moniteur = new ReentrantLock();
    	this.tupleSpace = new ArrayList<>();
    	this.autorisation = this.moniteur.newCondition();
    }

	@Override
	public void write(Tuple t) {
		// TODO Auto-generated method stub
		moniteur.lock();
		this.tupleSpace.add(t);
		if(file>0){
			this.autorisation.signalAll();
		}		
		moniteur.unlock();
	}

	@Override
	public Tuple take(Tuple template) {
	moniteur.lock();
	//if(!tupleSpace.contains(template)){
	while(find(template)==null){
		try{
			file++;
			autorisation.await();

		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
		file--;
	}
	Tuple inter = tupleSpace.remove(tupleSpace.indexOf(find(template)));
	moniteur.unlock();
	return inter;
		
	}

	@Override
	public Tuple read(Tuple template) {
		moniteur.lock();
		while(find(template)==null){
			try{
				file++;
				autorisation.await();

			}catch(InterruptedException e){
				e.printStackTrace();
			}
			file--;
		}
		Tuple inter = this.find(template);
		moniteur.unlock();
	
		return inter;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		// TODO Auto-generated method stub
		Tuple t = this.find(template);
		if(t != null){
			this.tupleSpace.remove(this.tupleSpace.indexOf(t));
		}
		return t;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		// TODO Auto-generated method stub
		return this.find(template);
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		Collection<Tuple> ft = readAll(template);
		tupleSpace.removeAll(ft);
		return ft;
	}

	@Override
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

	@Override
	public void eventRegister(eventMode mode, eventTiming timing,
			Tuple template, Callback callback) {
		Tuple inter ;
		if( timing== eventTiming.IMMEDIATE){
			if(mode ==eventMode.TAKE){
				inter = tryTake(template);
			}
			inter = find(template);
			while(inter == null){
				inter = find(template);
			}
			
		}
	
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String prefix) {
		// TODO Auto-generated method stub
		System.out.println("liste des tuples presents");
		for(Tuple t : tupleSpace){
			
			System.out.println(prefix + t);
		}
		
	}

	public Tuple find(Tuple template){
		Tuple ft = null;
		boolean arret = false;
		Iterator<Tuple> it = this.tupleSpace.iterator();
		while(it.hasNext() && arret==false){
			Tuple i = it.next();
			if(i.matches(template)){
				arret = true;
				ft = i;
			}
		}
		return ft;
	}
	


}

package linda.server;

import java.util.concurrent.Semaphore;

import linda.Callback;
import linda.Tuple;



public class CallBackTuple implements Callback{

	private Tuple t;
	private Semaphore sem;
//	private Lock l;
//	private Condition c;
	
	public CallBackTuple(Semaphore s) {
		this.sem = s;
	}
	
//	public CallBackTuple(Lock lock, Condition cond){
//		this.l = lock;
//		this.c = cond;
//	}
	
	public void call(Tuple tb){
//		l.lock();
		this.t= tb;
		System.out.println("----------------------");
		System.out.println("tuple recupéré : " + tb);
		System.out.println("----------------------");
		sem.release();
//		synchronized(c){
//			c.notify();
//		}
//		
//		l.unlock();
		
	}
	
	public Tuple getTuple(){
		return this.t;
	}
}

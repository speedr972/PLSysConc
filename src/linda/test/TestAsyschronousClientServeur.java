package linda.test;

import linda.*;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class TestAsyschronousClientServeur {

    private static class MyCallback implements Callback {
        public void call(Tuple t) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Got "+t);
        }
    }

    public static void main(String[] a) {
        //Linda linda = new linda.shm.CentralizedLinda();
        Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");
        //Linda linda2 = new linda.server.LindaClient("//localhost:4000/MonServeur");
        Tuple motif = new Tuple(Integer.class, String.class);
        Tuple motif2 = new Tuple(Integer.class, Integer.class);
        
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif, new AsynchronousCallback(new MyCallback()));
        //linda2.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif2, new AsynchronousCallback(new MyCallback()));
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif, new AsynchronousCallback(new MyCallback()));
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //linda.eventRegister(eventMode.READ, eventTiming.IMMEDIATE, motif, new AsynchronousCallback(new MyCallback()));
        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");
        System.out.println("avant");
//        linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, motif2, new AsynchronousCallback(new MyCallback()));
        System.out.println("apres");
        Tuple t3 = new Tuple(4, "foo");
        System.out.println("(2) write: " + t3);
        linda.write(t3);
        
//        try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        Tuple t4 = new Tuple(72, "world");
        System.out.println("(2) write: " + t4);
        linda.write(t4);
//        

//      Tuple t5 = new Tuple(72, 81);
//      System.out.println("(2) write: " + t5);
//      linda.write(t5);
        linda.debug("(2)");

    }

}
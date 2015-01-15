package linda.test;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import linda.Linda;
import linda.Tuple;
import linda.server.LindaClient;

public class BasicTest1 {

    public static void main(String[] a) throws RemoteException, MalformedURLException {
        
    	//linda.server.LindaServerImpl serv = new linda.server.LindaServerImpl("//localhost:5004/aaa");  	
    	//serv.lancer();
        //final Linda linda = new linda.shm.CentralizedLinda();
         final Linda linda = new LindaClient("//localhost:4000/MonServeur");
         try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda.take(motif);
                System.out.println("(1) Resultat:" + res);
                linda.debug("(1)");
            }
        }.start();
                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                System.out.println("(2) write: " + t1);
                linda.write(t1);

                Tuple t11 = new Tuple(4, 5);
                System.out.println("(2) write: " + t11);
                linda.write(t11);

                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(2) write: " + t2);
                linda.write(t2);

                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(2) write: " + t3);
                linda.write(t3);
                                
                linda.debug("(2)");

            }
        }.start();
                
    }
}

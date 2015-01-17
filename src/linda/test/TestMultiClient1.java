package linda.test;

import linda.Linda;
import linda.Tuple;

public class TestMultiClient1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("test Connexion 1");
		final Linda linda = new linda.server.LindaClient("//localhost:4001/multi4001");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Tuple motif = new Tuple(Integer.class, String.class);
        Tuple res = linda.take(motif);
        System.out.println("apres le take");
        System.out.println("(1) Resultat:" + res);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = linda.take(motif);
        System.out.println("(1) Resultat:" + res);
//        Tuple t1 = new Tuple(194, 66);
//        System.out.println("(1) write: " + t1);
//        linda.write(t1);
        
        linda.debug("(1)");

	}

}

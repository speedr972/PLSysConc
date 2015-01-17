package linda.test;

import linda.Linda;
import linda.Tuple;

public class TestMultiServer1 {

	public static void main(String[] args) {
		
        
		     
		
		
		new Thread() {
           public void run() {
        	   final Linda linda = new linda.server.LindaClient("//localhost:4001/multi4001");
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
//               Tuple motif = new Tuple(Integer.class, String.class);
//               Tuple res = linda.take(motif);
//               System.out.println("(1) Resultat:" + res);
               Tuple t1 = new Tuple(194, 66);
               System.out.println("(1) write: " + t1);
               linda.write(t1);
               
               linda.debug("(1)");
           }
       }.start();
               
       new Thread() {
           public void run() {
        	   final Linda linda2 = new linda.server.LindaClient("//localhost:4001/multi4002"); 
               try {
            	   
                   Thread.sleep(3000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

               if(linda2==null){
            	   System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
               }else{
            	   System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
               }
               Tuple t1 = new Tuple(4, 5);
               System.out.println("(2) write: " + t1);
               linda2.write(t1);
               System.out.println("write t1 done");

               Tuple t11 = new Tuple(4, 5);
               if(t11==null){
            	   System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
               }
               System.out.println("(2) write: " + t11);
               linda2.write(t11);

               Tuple t2 = new Tuple("hello", 15);
               System.out.println("(2) write: " + t2);
               linda2.write(t2);

               Tuple t3 = new Tuple(4, "foo");
               System.out.println("(2) write: " + t3);
               linda2.write(t3);
                               
               linda2.debug("(2)");

               }
           }.start();
       }
               
   

}


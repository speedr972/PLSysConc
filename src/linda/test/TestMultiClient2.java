package linda.test;

import linda.Linda;
import linda.Tuple;

public class TestMultiClient2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("test Connexion 2");
		final Linda linda2 = new linda.server.LindaClient("//localhost:4002/multi4002"); 
        try {
     	   
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        if(linda2==null){
//     	   System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        }else{
//     	   System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
//        }
//        Tuple t1 = new Tuple(4, 5);
//        System.out.println("(2) write: " + t1);
//        linda2.write(t1);
//        System.out.println("write t1 done");
//
//        Tuple t11 = new Tuple(4, 5);
//        if(t11==null){
//     	   System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        }
//        System.out.println("(2) write: " + t11);
//        linda2.write(t11);
//
//        Tuple t2 = new Tuple("hello", 15);
//        System.out.println("(2) write: " + t2);
//        linda2.write(t2);

        Tuple t3 = new Tuple(4, "foo");
        System.out.println("(2) write: " + t3);
        linda2.write(t3);
        
        Tuple t4 = new Tuple(56, "world");
        System.out.println("(2) write: " + t4);
        linda2.write(t4);
                        
        linda2.debug("(2)");
	}

}

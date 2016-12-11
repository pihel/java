package Hash;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BloomFilterTest {
	
	@Before
    public void init() { 
    	//
    }
	
    @After
    public void tearDown() { 
    	//
    }
    
    @Test
    public void BloomFilter() {
    	//false positive test
    	for(int i = 0; i <= 50; i++) {
    		BloomFilter bf = new BloomFilter(ThreadLocalRandom.current().nextInt(1, 63), ThreadLocalRandom.current().nextInt(1, 10));
    		bf.debug = false;
    		
    		int elems = ThreadLocalRandom.current().nextInt(1, 100);
    		String[] arr = new String[elems];
    		
	 	    for(int j = 0; j < elems; j++) {
	 	    	arr[j] = "a" + ThreadLocalRandom.current().nextInt(1, 100);
	 	      	bf.add(arr[j]);
	 	    }
	 	    
	 	   for(int j = 0; j < ThreadLocalRandom.current().nextInt(1, 100); j++) {
	 		   String rand_str = "a" + ThreadLocalRandom.current().nextInt(1, 100);
	 		   
	 		   //строка должна быть в битовой карте
	 		   boolean src_val = false;
	 		   for(int k = 0; k < arr.length; k++){
	 			  if( rand_str == arr[k] ) {
	 				  src_val = true;
	 				  break;
	 			  }
	 		   }
	 		   
	 		   boolean h_val = bf.test(rand_str);
	 		   
	 		   //если элемент есть в массиве, то блуф фильтр всегда должен его найти
	 		   if(src_val && !h_val) {
	 			   assertEquals(src_val, h_val );
	 		   }
	 		   //обратая ситуация - элмента нет в массиве, но он находится в блум фильтре допустима и не проверяется
			}
    		
    		bf = null;
    	}
	} //BloomFilter

}

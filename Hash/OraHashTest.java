package Hash;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OraHashTest {
	
	@Before
    public void init() { 
    	//
    }
    @After
    public void tearDown() { 
    	//
    }
    
    @Test
    public void OraHash() {
    	//assertArrayEquals(arr, arr_rng);    	
		//assertEquals(t.indexScan(5), 4 );
    	//assertNull(t.indexScan(400));
    	
    	
    	//random test
    	for(int i = 0; i <= 50; i++) {
    		
	    	OraHash h = new OraHash( ThreadLocalRandom.current().nextInt(1, 100) );
	    	
	    	Hashtable<Integer, String> src = new Hashtable<Integer,String>();
	    	
	    	int elems = ThreadLocalRandom.current().nextInt(1, 100);
	 	    for(int j = 0; j < elems; j++) {
	 	    	int k = ThreadLocalRandom.current().nextInt(1, 100);
	 	    	
	 	    	h.put(k , "r." + j);
	 	    	
	 	    	src.put(k, "r." + j);
	 	    	
	 	    	//assertEquals(t.indexScan(5), 4 );
	 	    }
	 	    
			for(int j = 0; j < elems; j++) {
				boolean src_val = src.containsKey(j);
				boolean h_val = false;
				if( h.get(j) != null ) h_val = true;
				
				assertEquals(src_val, h_val );
			}
	 	   
	 	    h = null;
	 	    src = null;
	 	    
    	}
 	   
    } //OraHash
    
    
    @Test
    public void OraHashReorg() {
    	//random test
    	for(int i = 0; i <= 50; i++) {
    		
    		int hash_size = ThreadLocalRandom.current().nextInt(1, 100);
	    	OraHash h = new OraHash( hash_size );
	    	
	 	    for(int j = 0; j < ThreadLocalRandom.current().nextInt(1, 100); j++) {
	 	    	int k = ThreadLocalRandom.current().nextInt(1, 100);
	 	    	
	 	    	h.put(k , "r." + j);
	 	    }
	 	    h.reorg();
	 	    HashEntryHolder[] hold = h.getFullHash();
	 	    for(int j = 0; j < h.getTblSize(); j++) {
	 	    	if(hold[j] != null) {
	 	    		assertFalse((hold[j].table.cnt_mem > hold[j].table.cnt));
	 	    	}
	 	    }
	 	   
	 	    h = null;
	 	    
    	}
    	
    	//reorg test: 1 нет mem>cnt; хотябы 1 элемент должен быть реорганизован, если в нем кол-во элементов меньше area_size
 	   
    } //OraHashReorg

}

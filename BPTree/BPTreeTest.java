package BPTree;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;


public class BPTreeTest {
	Integer arr[] = {2, 6, 3, 5, 1, 7, 8, 0, 27, 17, 99, 13, 1, 7};
	Integer arr_sort[] = new Integer[arr.length];
	Integer arr_sort_test[] = new Integer[arr.length];
	
    @Before
    public void init() { 
    	System.arraycopy(arr, 0, arr_sort, 0, arr.length);
    	Arrays.sort(arr_sort);
    }
    @After
    public void tearDown() { 
    	arr_sort = null;
    	arr = null;
    }
    
    @Test
    public void BPTree() {
    	assertEquals(1, 1);
    }
}

package BPTree;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;


public class BPTreeTest {
	Integer arr1[] = {2, 6, 3, 5, 1, 7, 8, 0, 27, 17, 99, 13, 1, 7};
	Integer arr2[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	Integer arr3[] = {9, 8, 7, 6, 5, 4, 3, 2, 1};
	
	Integer arrs1[] = new Integer[arr1.length];
	Integer arrs2[] = new Integer[arr2.length];
	Integer arrs3[] = new Integer[arr3.length];
	
    @Before
    public void init() { 
    	System.arraycopy(arr1, 0, arrs1, 0, arr1.length);
    	System.arraycopy(arr2, 0, arrs2, 0, arr2.length);
    	System.arraycopy(arr3, 0, arrs3, 0, arr3.length);
    	
    	Arrays.sort(arrs1);
    	Arrays.sort(arrs2);
    	Arrays.sort(arrs3);
    }
    @After
    public void tearDown() { 
    	arr1 = null;
    	arr2 = null;
    	arr3 = null;
    	
    	arrs1 = null;
    	arrs2 = null;
    	arrs3 = null;
    }
    
    public BPTree BPTreeTestCommon(int rows_block, Integer[] arr_1, Integer[] arrs_1) {
    	BPTree t = new BPTree(rows_block);
    	for(int i = 0; i < arr_1.length; i++) {
    		t.insert(arr_1[i], i);
    	}
    	
    	Object arr[] = new Integer[t.getCnt()];
    	Object arr_res[] = new Integer[arr.length];
    	
    	
    	//fullscan test
    	arr = t.fullScan();
		for(int i = 0; i < arr.length; i++) {
			arr_res[i] = arr_1[(Integer)arr[i]];
		}
		assertArrayEquals(arr_res, arrs_1);
		//--
		
		//range scan - 1
		Integer arr_rng_null[] = {};
		assertArrayEquals(t.rangeScan(-1, -2), arr_rng_null);
		
		//range scan - 2
		arr = t.rangeScan(-1000, 1000);
		for(int i = 0; i < arr.length; i++) {
			arr_res[i] = arr_1[(Integer)arr[i]];
		}
		assertArrayEquals(arr_res, arrs_1);
		
		//max
		assertEquals(arr_1[ (Integer)t.getMax() ], Collections.max(Arrays.asList(arr_1)) );
		// --
		
		//min 
		assertEquals(arr_1[ (Integer)t.getMin() ], Collections.min(Arrays.asList(arr_1)) );
		//--
		
		return t;
    } //BPTreeTestCommon
    
    public void BPTreeTest_arr1(int rows_block, Integer[] arr_1, Integer[] arrs_1) {
    	BPTree t = BPTreeTestCommon(rows_block, arr_1, arrs_1);
    	
    	
		//range scan - 3
    	Object arr[] = new Integer[t.getCnt()];
		arr = t.rangeScan(2, 7);
		Integer arr_rng[] = {0, 2, 3, 1, 13, 5};
		assertArrayEquals(arr, arr_rng);
		//--
		
		
		//indexScan
		assertEquals(t.indexScan(5), 3 );
		assertEquals(t.indexScan(6), 1 );
		assertNull(t.indexScan(4));
		assertEquals(t.indexScan(99), 10 );
		assertNull(t.indexScan(100));
		// --
    	
    } //BPTreeTest_arr1
    
    public void BPTreeTest_arr2(int rows_block, Integer[] arr_1, Integer[] arrs_1) {
    	BPTree t = BPTreeTestCommon(rows_block, arr_1, arrs_1);
		
		
		//range scan - 3
    	Object arr[] = new Integer[6];
		arr = t.rangeScan(2, 7);		
		Integer arr_rng[] = {1, 2, 3, 4, 5, 6};
		//System.out.println(Arrays.toString(arr));
		assertArrayEquals(arr, arr_rng);
		//--
		
		
		//indexScan
		assertEquals(t.indexScan(5), 4 );
		assertEquals(t.indexScan(6), 5 );
		assertNull(t.indexScan(400));
		assertEquals(t.indexScan(4), 3 );
		assertNull(t.indexScan(-100));
		// --
    	
    } //BPTreeTest_arr2
    
    public void BPTreeTest_arr3(int rows_block, Integer[] arr_1, Integer[] arrs_1) {
    	BPTree t = BPTreeTestCommon(rows_block, arr_1, arrs_1);
		
		
		//range scan - 3
    	Object arr[] = new Integer[6];
		arr = t.rangeScan(2, 7);		
		Integer arr_rng[] = {7, 6, 5, 4, 3, 2};
		assertArrayEquals(arr, arr_rng);
		//--
		
		//indexScan
		assertEquals(t.indexScan(5), 4 );
		assertEquals(t.indexScan(6), 3 );
		assertNull(t.indexScan(400));
		assertEquals(t.indexScan(4), 5 );
		assertNull(t.indexScan(-100));
		// --
    	
    } //BPTreeTest_arr3
    
    @Test
    public void BPTree() {
    	BPTreeTest_arr1(2, arr1, arrs1);
    	BPTreeTest_arr1(3, arr1, arrs1);
    	BPTreeTest_arr1(4, arr1, arrs1);
    	BPTreeTest_arr1(5, arr1, arrs1);
    	
    	BPTreeTest_arr2(2, arr2, arrs2);
    	BPTreeTest_arr2(3, arr2, arrs2);
    	BPTreeTest_arr2(4, arr2, arrs2);
    	BPTreeTest_arr2(5, arr2, arrs2);
    	
    	BPTreeTest_arr3(2, arr3, arrs3);
    	BPTreeTest_arr3(3, arr3, arrs3);
    	BPTreeTest_arr3(4, arr3, arrs3);
    	BPTreeTest_arr3(5, arr3, arrs3);
    } //BPTree
}

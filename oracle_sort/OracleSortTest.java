package oracle_sort;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;


public class OracleSortTest {
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
    public void bubleSort() {
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	Sorts.bubleSort(arr_sort_test);
    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    }
    
    @Test
    public void insertSort() {
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	Sorts.insertSort(arr_sort_test);
    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    }
    
    @Test
    public void QSort() {
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	Sorts.QSort(arr_sort_test, 0, arr_sort_test.length-1);
    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    }
    
    @Test
    public void MergeSort() {
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	( new MergeSort() ).sort(arr_sort_test);
    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    }
    
    @Test
    public void radixSortUInt() {
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	Sorts.radixSortUInt(arr_sort_test);
    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    }
    
    @Test
    public void OracleSort() {
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	new OracleSort(5, 2).sort(arr_sort_test);
    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    }
}

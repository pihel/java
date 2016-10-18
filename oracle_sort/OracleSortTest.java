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
    	
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	new OracleSort(5000, 2).sort(arr_sort_test);    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    	
    	
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	new OracleSort(1, 2).sort(arr_sort_test);    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    	
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	new OracleSort(5, 1).sort(arr_sort_test);    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    	
    	
    	System.arraycopy(arr, 0, arr_sort_test, 0, arr.length);
    	new OracleSort(5, 100).sort(arr_sort_test);    	
    	assertArrayEquals(arr_sort_test, arr_sort);
    	
    	Integer arr1[] = {};
    	Integer arr2[] = {1};
    	Integer arr3[] = {1,2};
    	String arr4[] = {"2","1", "abc"};
    	
    	Integer arr11[] = {};
    	Integer arr22[] = {1};
    	Integer arr33[] = {1,2};
    	String arr44[] = {"1","2", "abc"};
    	
    	new OracleSort(5, 2).sort(arr1);  
    	Arrays.sort(arr11);
    	assertArrayEquals(arr1, arr11);
    	
    	new OracleSort(5, 2).sort(arr2);  
    	Arrays.sort(arr22);
    	assertArrayEquals(arr2, arr22);
    	
    	new OracleSort(5, 2).sort(arr3);  
    	Arrays.sort(arr33);
    	assertArrayEquals(arr3, arr33);
    }
    
    @Test
    public void binary_search() {
    	assertEquals( Sorts.binary_search(arr_sort, 7), Arrays.binarySearch(arr_sort, 7) );
    	assertEquals( Sorts.binary_search(arr_sort, -1), Arrays.binarySearch(arr_sort, -1) );
    	
    	Integer arr1[] = {};
    	Integer arr2[] = {1};
    	Integer arr3[] = {1,2};
    	assertEquals( Sorts.binary_search(arr1, -1), Arrays.binarySearch(arr1, -1) );
    	assertEquals( Sorts.binary_search(arr2, -1), Arrays.binarySearch(arr2, -1) );
    	assertEquals( Sorts.binary_search(arr3, -1), Arrays.binarySearch(arr3, -1) );
    }
}

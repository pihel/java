package algorithms;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Heap<T extends Comparable<T>> {
	
	
	void heapify(T a[], int n, int i) {
		int max, child_l, child_r;
		
		
		child_l = 2 * i + 1;
		child_r = child_l + 1;
		max = i;
		
		if (child_l < n && a[child_l].compareTo(a[max]) > 0) {
			max = child_l;
		}
		if (child_r < n && a[child_r].compareTo(a[max]) > 0) {
			max = child_r;
		}
		if (max != i) {
			T temp = a[i];
			a[i] = a[max];
			a[max] = temp;
			heapify(a, n, max);
		}
	}
	
	void dump(T a[]) {
		System.out.println(Arrays.toString(a));
	}
	
	void sort(T a[]) {
		buildheap(a);
		
		int len = a.length;
		
		for(int i = len-1; i >= 0; i--) {
			T temp = a[0];
			a[0] = a[i];
			a[i] = temp;
			
			heapify(a, i, 0);
		}
	}

	void buildheap(T a[]) {
		for (int i = a.length / 2 - 1; i >= 0; i--) {
			heapify(a, a.length, i);
		}
	}

	public static void main(String[] args) {
		Heap<Integer> h = new Heap<Integer>();
		
		Integer arr[] = {35, 33,	42,	10,	14,	19,	27,	44,	26,	31};
		h.dump(arr);
		h.buildheap(arr);
		h.dump(arr);
		h.sort(arr);
		h.dump(arr);		
	}

}

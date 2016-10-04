package oracle_sort;

import java.util.*;

class Node {
	Integer val;
	Node next;
 
	Node(Integer x) {
		val = x;
		next = null;
	}
}

class InsertSortList {
	
	public static Node arr2node(Integer[] arr) {
		Node root = null;
		Node prev = null;
		for(int i = arr.length - 1; i >= 0; i--) {
		    //последний элемент с конца - голова списка
			root = new Node(arr[i]);
			//предыдущий элемент в цикле - следующий в списке
			root.next = prev;
			prev = root;
		}
		/*for(int i = 0; i < arr.length; i++) {
			Node n = new Node(arr[i]);
			if(root == null) {
				//первый элемент - указатель на голову списка
				root = n;
			} else {
				//ставим следующего для предыдущего
				prev.next = n;
			}
			//текущий = предыдущий для след. итерации цикла
			prev = n;
		}*/
		return root;
	}
	
	public static void printList(Node r) {
		while(r != null) {
			System.out.print(r.val + ", ");
			r = r.next;
		}
	}
	
	public static Node sort(Node root) {
		if(root == null || root.next == null) return root;
		
		//новый отсортированный список
		Node sort_node = new Node(root.val);
		
		Node pointer = root.next;
		
		//идем вперед
		while(pointer != null) {
			Node inner_node = sort_node;
			Node next = pointer.next;
			
			//если элемент меньше начала, то это наше новое начало
			if(pointer.val <= sort_node.val) {
				Node swp = sort_node;
				sort_node = pointer;
				sort_node.next = swp;
			} else {
				//иначе идем с начала списка еще раз
				while(inner_node.next != null) {
					//находим место вставки
					if (pointer.val > inner_node.val && pointer.val <= inner_node.next.val) {
						Node oldNext = inner_node.next;
						inner_node.next = pointer;
						pointer.next = oldNext;
					}					
					inner_node = inner_node.next;
				}
				
				//дошли доконца - это наш новый конец
				if (inner_node.next == null && pointer.val > inner_node.val) {
					inner_node.next = pointer;
					pointer.next = null;
				}
			}
			
			pointer = next;
		}
		
		return sort_node;
	}
}

class Tree<T extends Comparable<T>> {
	public T val;
	public Tree<T> left;
	public Tree<T> right;
	
	Tree(T x) {
		val = x;
	}
	
   Tree( T[] arr) {
	   for(int i = 0; i < arr.length; i++) {
		   this.insert(new Tree<T>(arr[i]));
	   }
   }
	
   public void insert( Tree<T> aTree) {

	 if(val == null) {
		 val = aTree.val;
		 return;
	 }
		 
	 if (aTree.val.compareTo(val) < 0 ) {
	    if ( left != null ) {
	    	left.insert( aTree );
	    } else {
	    	left = aTree;
	    }
	 } else {
	    if ( right != null ) {
	    	right.insert( aTree );
	    }
	    else { 
	    	right = aTree;
	    }
	 }
   }
   
   public void traverse() {
	   if(left != null) {
		   left.traverse();
	   }
	   System.out.print(val + ", ");
	   
	   if(right != null) {
		   right.traverse();
	   }
   }
}

class MergeSort {
	
	int pga_size;
	
	MergeSort(int _pga_size) {
		pga_size = _pga_size;
	}
	
	@SuppressWarnings("rawtypes") 
	public void sort(Comparable[] arr) {
		if(arr.length <= pga_size) {
			if(pga_size == 1) {
				return;
			} else {
				OracleSort.QSort(arr, 0, arr.length - 1);
				return;
			}
		}
		
		Comparable[] left = new Comparable[arr.length / 2];
		Comparable[] right = new Comparable[arr.length - left.length];
	    System.arraycopy(arr, 0, left, 0, left.length);
	    System.arraycopy(arr, left.length, right, 0, right.length);
	     
	    sort(left);
	    sort(right);
	     
	    merge(left, right, arr);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	private void merge(Comparable[] left, Comparable[] right, Comparable[] arr) {
		int iFirst = 0;        
	    int iSecond = 0;         
	    int iMerged = 0;
	     
	    while (iFirst < left.length && iSecond < right.length) {
	        if (left[iFirst].compareTo(right[iSecond]) < 0) {
	        	arr[iMerged] = left[iFirst];
	            iFirst++;
	        } else {
	        	arr[iMerged] = right[iSecond];
	            iSecond++;
	        }
	        iMerged++;
	    }
	    
	    System.arraycopy(left, iFirst, arr, iMerged, left.length - iFirst);
	    System.arraycopy(right, iSecond, arr, iMerged, right.length - iSecond);
	}
}


public class OracleSort {
	
	//перемещаем самый большой элемент в конец, уменьшаем размер массива на 1, повторяем
	public static <T extends Comparable<T>> void bubleSort(T[] arr) {		
		for(int i = 0; i < arr.length - 1; i++) {
			boolean sorted = true;
			for(int j = 0; j < arr.length - i - 1; j++) {
				if(arr[j].compareTo(arr[j+1]) > 0) {
					T buf = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = buf;
					
					sorted = false;
				}
			}
			if(sorted) {
				break;
			}
		}
	}
	
	//идем вперед, текущий элемент записываем в буферную переменную, потом назад до элемента меньше нашего, смещаем элементы на 1 вправо замещая текущий элемент
	//текущий элемент вставляем на место меньше нашего + 1
	public static <T extends Comparable<T>> void insertSort(T[] arr) {
		for(int i = 0; i < arr.length; i++) {
			T val = arr[i];
			int key = i - 1;
			while(key >= 0 && val.compareTo(arr[key]) < 0) {
				arr[key+1] = arr[key];
				key--;
			}
			arr[key+1] = val;
		}
	}
	
	public static <T extends Comparable<T>> void QSort(T[] arr, int l, int r) {
		int i = l;
		int j = r;
		T c = arr[l + ( r - l ) / 2];
		while(i < j) {
			while(arr[i].compareTo(c) < 0) {
				i++;
			}
			while(arr[j].compareTo(c) > 0) {
				j--;
			}
			if(i <= j) {
				T t = arr[j];
				arr[j] = arr[i];
				arr[i] = t;
				i++;
				j--;
			}
		}
		if(l < j) {
			QSort(arr, l, j);
		}
		if(r > i) {
			QSort(arr, i, r);
		}
	}
	
	public static void radixSortUInt(Integer[] arr) {
		final int RADIX = 10;
		  // declare and initialize bucket[]
		  List<Integer>[] bucket = new ArrayList[RADIX];
		  for (int i = 0; i < bucket.length; i++) {
		    bucket[i] = new ArrayList<Integer>();
		  }
		 
		  // sort
		  boolean maxLength = false;
		  int tmp = -1, placement = 1;
		  while (!maxLength) {
		    maxLength = true;
		    // split input between lists
		    for (Integer i : arr) {
		      tmp = i / placement;
		      bucket[tmp % RADIX].add(i);
		      if (maxLength && tmp > 0) {
		        maxLength = false;
		      }
		    }
		    // empty lists into input array
		    int a = 0;
		    for (int b = 0; b < RADIX; b++) {
		      for (Integer i : bucket[b]) {
		    	  arr[a++] = i;
		      }
		      bucket[b].clear();
		    }
		    // move to next digit
		    placement *= RADIX;
		  }
	}

	public static void main(String[] args) {
		Integer arr[] = {2, 6, 3, 5, 1, /*-1,*/ 7, 8, 0};
		
		System.out.println(Arrays.toString(arr));
		
		//Arrays.sort(arr);
		//bubleSort(arr);
		//insertSort(arr);
		//InsertSortList.printList( InsertSortList.sort( InsertSortList.arr2node(arr) ) );
		
		//(new Tree(arr)).traverse();
		
		//QSort(arr, 0, arr.length-1);
		
		//( new MergeSort(5) ).sort(arr);
		
		radixSortUInt(arr);
		
		System.out.println(Arrays.toString(arr));
	}

}

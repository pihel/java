package oracle_sort;

import java.util.*;

class Node {
	Integer val;
	Node next;
	Node root;
 
	Node(Integer x) {
		val = x;
		next = null;
	}
	
	public Node(Integer[] arr) {
		root = null;
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
	} //Node
	
	public void print() {
		root = this;
		while(root != null) {
			System.out.print(root.val + ", ");
			root = root.next;
		}
	} //print
}

class InsertSortList {
	
	public static Node sort(Node root) {
		root = root.root;
		if(root == null) return root;
		
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
	} //sort
} //InsertSortList

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
} //Tree

class MergeSort {
	
	@SuppressWarnings("rawtypes") 
	protected boolean isSortArr(Comparable[] arr) {
		if(arr.length <= 1) return true;
		return false;
	}
	
	@SuppressWarnings("rawtypes") 
	public void sort(Comparable[] arr) {
		if(isSortArr(arr)) return;
		
		Comparable[] left = new Comparable[arr.length / 2];		
	    System.arraycopy(arr, 0, left, 0, left.length);
	    sort(left);
	    //swop to disk
	    
	    
	    Comparable[] right = new Comparable[arr.length - left.length];
	    System.arraycopy(arr, left.length, right, 0, right.length);
	    sort(right);
	     
	    merge(left, right, arr);
	} //sort
	
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
	} //merge
} //MergeSort

class Sorts {
	/*
	Сортировка пузырьком:
		* Сложность: N*N/2
		* Доп. Память: нет
		* Общий смысл:
			** перемещаем наибольший элемент вправо
			** повторяем еще раз, но не доходим до наибольшего элемента (отсортированных)
			** повторяем, пока все наибольшие элементы не будут справа
			
	*/
	public static <T extends Comparable<T>> void bubleSort(T[] arr) {
		//проходим по массиву
		for(int i = 0; i < arr.length - 1; i++) {
			//флаг отсортированности = не сделано ни одного обмена
			boolean sorted = true;
			
			//проходим от начала массива до конца - i с поиском элемента для обмена
			//все что правее конец-i уже отсортированно
			for(int j = 0; j < arr.length - i - 1; j++) {
				//сравниваем текущий элемент с соседним: больший элемент смещаем вправо
				//за каждый проход наибольший элемент уходит в крайнее правое положение = конец-i
				if(arr[j].compareTo(arr[j+1]) > 0) {
					T buf = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = buf;
					
					//если сделали хоть один обмен, то массив еще не отсортирован
					sorted = false;
				}
			}
			//если не было ни одного обмена за целый проход, значит массив отсортирован - выходим
			if(sorted) {
				break;
			}
		}
	} //bubleSort
	
	/*
	 Сортировка вставками: 
	 	* Сложность: N*N
	 	* Доп. память: нет
	 	* Общий смысл:
	 		** движеся вправо
	 		** для каждой итерации: обратно влево, пока не найдем элемент меньше текущего
	 		** попутно смещаем все элементы больше текущего вправо на 1
	 */
	public static <T extends Comparable<T>> void insertSort(T[] arr) {
		//движемся по массиву вправо
		for(int i = 0; i < arr.length; i++) {
			T val = arr[i];
			int key = i - 1;
			//движемся по массиву влево, пока не найдем элемент меньше текущего
			//все элементы > текущего смещаем вправа на один			
			while(key >= 0 && val.compareTo(arr[key]) < 0) {
				arr[key+1] = arr[key];
				key--;
			}
			//в найденное место: < текущего или первый (key=0), вставляем элемент i операции
			arr[key+1] = val;
		}
	} //insertSort
	
	/*
	 Быстрая сортировка: 
	 	* Сложность: N*Log(N)
	 	* Доп. память: нет
	 	* Минусы:
	 	* - Сильно зависит от выбора средней точки, при неправильно выборе (крайний элемент) сложность = N*N
	 	* - На большом наборе данных из-за рекурсии может вызвать переполнение стека
	 	* Общий смысл:
	 		** находим элемент в центре массива со средним значением
	 		** движемся с краев к центру, меняя значения местами, если влевой части значение больше середины или вправой больше
	 		** при доходе до середины, запускам рекурсивно для левой и правой части
	 */
	public static <T extends Comparable<T>> void QSort(T[] arr, int l, int r) {
		int i = l;
		int j = r;
		//средний элемент = левая граница + (правая граница - левая граница) / 2
		T c = arr[l + ( r - l ) / 2];
		
		// идем к центу, пока края не встретятся
		while(i < j) {
			//идем с левого края к центру, пока середина больше текущего значения (ищем элемент больше середины)
			while(arr[i].compareTo(c) < 0) {
				i++;
			}
			//с правого края к центру, пока середина меньше текущего значения (ищем элемент меньше середины)
			while(arr[j].compareTo(c) > 0) {
				j--;
			}
			//если левый индекс меньше правого индекса, то меняем местами и смещаемся
			if(i <= j) {
				T t = arr[j];
				arr[j] = arr[i];
				arr[i] = t;
				i++;
				j--;
			}
		}
		//рекурсивно запускаемся для левой и правой части
		if(l < j) {
			QSort(arr, l, j);
		}
		if(r > i) {
			QSort(arr, i, r);
		}
	} //QSort
	
	public static <T extends Integer> void radixSortUInt(T[] arr) {
		final int RADIX = 10;
		List<T>[] bucket = new ArrayList[RADIX];
		for (int i = 0; i < bucket.length; i++) {
		  bucket[i] = new ArrayList<T>();
		}
		boolean maxLength = false;
		int rank_val = -1;
		int placement = 1;
		
		while (!maxLength) {
		    maxLength = true;
		    
		    for (int i = 0; i < arr.length; i++) {
		    	rank_val = arr[i] / placement;
			    bucket[rank_val % RADIX].add(arr[i]);
			    if (maxLength && rank_val > 0) {
			    	maxLength = false;
			    }
		    }
		    
		    int a = 0;
		    for (int b = 0; b < RADIX; b++) {
		    	for (int i = 0; i < bucket[b].size(); i++) {
		    		arr[a++] = bucket[b].get(i);
		    	}
		    	bucket[b].clear();
		    }
		    placement *= RADIX;
		}
		  
	} //radixSortUInt
	
    public static <T extends Comparable<T>> int binary_search(T[] arr, T val) {
        int lo = 0;
        int hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            
            if (val.compareTo(arr[mid]) < 0) {
            	hi = mid - 1;
            } else if (val.compareTo(arr[mid]) > 0) {
            	lo = mid + 1;
            } else {
            	return mid;
            }
        }
        return -1;
    } //binary_search
}


public class OracleSort extends MergeSort {
	int sort_area_size = 1;
	int avg_key_size = 0;
	
	OracleSort(int _sort_area_size, int _avg_key_size) {
		sort_area_size = _sort_area_size;
		avg_key_size = _avg_key_size;
	} //OracleSort
	
	protected boolean isSortArr(Comparable[] arr) {
		
		if(arr.length <= sort_area_size) {		
			
			if( arr[0].getClass() == Integer.class && avg_key_size > 0 && avg_key_size < Math.log(arr.length) ) {
				/*System.out.println(avg_key_size);
				System.out.println(Math.log(arr.length));
				System.out.println(arr[0].getClass());*/
				
				Integer[] _arr = new Integer[arr.length]; //как привести Comparable к Integer?
				
				System.arraycopy(arr, 0, _arr, 0, arr.length);
				Sorts.radixSortUInt(_arr);
				System.arraycopy(_arr, 0, arr, 0, arr.length);
				_arr = null;
			} else {
				Sorts.QSort(arr, 0, arr.length-1);
			}
			return true;
		}
		return false;
	} //isSortArr

	public static void main(String[] args) {
		Integer arr[] = {2, 6, 3, 5, 1, /*-1,*/ 7, 8, 0, 27, 17, 99, 13, 1, 7};
		
		System.out.println(Arrays.toString(arr));
		
		//Arrays.sort(arr);
		//Sorts.bubleSort(arr);
		//Sorts.insertSort(arr);
		//InsertSortList.sort( new Node(arr) ).print();
		
		//(new Tree(arr)).traverse();
		
		//Sorts.QSort(arr, 0, arr.length-1);
		
		//( new MergeSort() ).sort(arr);
		
		//Sorts.radixSortUInt(arr);
		
		new OracleSort(5, 2).sort(arr);
		
		System.out.println(Arrays.toString(arr));
		
		System.out.println("7=" + Sorts.binary_search(arr, 7));
		System.out.println("73=" + Sorts.binary_search(arr, 73));
	}

}

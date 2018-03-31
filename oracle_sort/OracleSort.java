package oracle_sort;

import java.util.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/*
 * Односвязный список
 * */
class Node {
	Integer val;
	Node next;
	Node root;
 
	//создание элемента списка
	Node(Integer x) {
		val = x;
		next = null;
	}
	
	//вставка массива в список
	public Node(Integer[] arr) {
		root = null;
		Node prev = null;
		//идем с конца массива - так проще, т.к. у нас только указатель на следующий
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
	
	public void reverse() {
		Node prev = null;
		while(root != null) {
			Node rnext = root.next;
			root.next = prev;
			prev = root;
			root = rnext;
		}
		root = prev;
	} //reverse
	
	//печать списка
	public void print() {
		Node r = root;
		while(root != null) {
			System.out.print(root.val + ", ");
			root = root.next;
		}
		root = r;
		System.out.println("");
	} //print
}

/*
Бинарное дерево поиска:
	* Сложность: N*Log2(N)
	* Доп. Память: O(N)
		
*/
class Tree<T extends Comparable<T>> {
	public T val;
	public Tree<T> left;
	public Tree<T> right;
	
	Tree(T x) {
		val = x;
	}
	
	//вставка массива в список
   Tree( T[] arr) {
	   for(int i = 0; i < arr.length; i++) {
		   this.insert(new Tree<T>(arr[i]));
	   }
   }
	
   //вставка поддерева в дерево
   public void insert( Tree<T> aTree) {

	 //если корня нет - это наш новый корень
	 if(val == null) {
		 val = aTree.val;
		 return;
	 }
	
	 //если вставляемый элемент меньше текущего
	 if (aTree.val.compareTo(val) < 0 ) {
		//идем налево дерева
	    if ( left != null ) {
	    	//если в левой части есть дерево, то рекурсивно спускаемся
	    	left.insert( aTree );
	    } else {
	    	//иначе создаем новый лист
	    	left = aTree;
	    }
	 } else {
		//если больше, то движемся направо
	    if ( right != null ) {
	    	//если в правой части есть дерево, то спускаемся
	    	right.insert( aTree );
	    }
	    else { 
	    	//иначе создаем лист
	    	right = aTree;
	    }
	 }
   }
   
   //обход дерева
   public void traverse() {
	   if(left != null) {
		   //спускаемся налево, пока не дойдем до листа
		   left.traverse();
	   }
	   //доидя до листа раскручиваем рекурсию обратно
	   System.out.print(val + ", ");
	   
	   //пока не найдем поворот направо
	   if(right != null) {
		   //спускаемся на 1 элемента вправо
		   right.traverse();
	   }
   }
} //Tree


/*
Сортировка слиянием: 
	* Сложность: N*Log2(N)
	* Доп. память: O(N)
	* Общий смысл:
		** рекурсивно разбиваем масиив на 2 части, пока размер элемента не дойдет до N (тут =1 )
		** сливаем массивы в один последовательным проходом (в худшем случае N+M), сравнивая элементы
*/
class MergeSort {
	
	//проверка достаточности разбиения
	@SuppressWarnings("rawtypes") 
	protected boolean isSortArr(Comparable[] arr) {
		if(arr.length <= 1) return true;
		return false;
	}
	
	//разбитие массива на 2 равные части
	@SuppressWarnings("rawtypes") 
	public void sort(Comparable[] arr) {
		
		//если массив достаточно разбит, то прерываем рекурсию
		if(isSortArr(arr)) return;
		
		//левая половина
		Comparable[] left = new Comparable[arr.length / 2];		
	    System.arraycopy(arr, 0, left, 0, left.length);
	    sort(left);
	    //swop to disk
	    
	    //правая половина
	    Comparable[] right = new Comparable[arr.length - left.length];
	    System.arraycopy(arr, left.length, right, 0, right.length);
	    sort(right);
	     
	    //слияние
	    merge(left, right, arr);
	} //sort
	
	
	//слияние 2 упорядоченных массивов в один
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	private void merge(Comparable[] left, Comparable[] right, Comparable[] arr) {
		int iFirst = 0;        
	    int iSecond = 0;         
	    int iMerged = 0;
	     
	    //бежим, пока не дойдем до конца одного из массивов
	    while (iFirst < left.length && iSecond < right.length) {
	    	//если элемент в левом массиве больше, чем в правом
	        if (left[iFirst].compareTo(right[iSecond]) < 0) {
	        	//то добавляем элемент из левого
	        	arr[iMerged] = left[iFirst];
	        	//и двигаемся в левом на 1
	            iFirst++;
	        } else {
	        	//иначе добавляем элемент из правого
	        	arr[iMerged] = right[iSecond];
	        	//двигаемся в правом на 1
	            iSecond++;
	        }
	        //в любом случае увеличиваем результирующий массив
	        iMerged++;
	    }
	    
	    //оставшиеся элементы - больше последнего (максимального) элемента одного из массивов. Докопируем оставшиеся элементы.
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
	 Сортировка вставками для однонаправленного связанного списка: 
	 	* Сложность: N*N
	 	* Доп. память: нет
	 	* Общий смысл:
	 		** движеся вправо по списку i
	 		** если находим элемент меньше начала отсортированного списка, то делаем его началом отсортированного
	 		** иначе идем с начала списка еще раз j, ищем место вставки в i, где текущий элемент i меньше следующего, но больше текущего - меняем элементы местами
	 		** дошли до конца j , то это новый конец i
	 */
	public static Node InsertSortList(Node root) {
		root = root.root;
		if(root == null) return root;
		
		//новый отсортированный список
		Node sort_node = new Node(root.val);
		
		Node pointer = root.next;
		
		//идем вперед
		while(pointer != null) {
			Node inner_node = sort_node;
			Node next = pointer.next;
			
			//если элемент меньше текущего начала отсортированном, то это наше новое начало
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
	} //InsertSortList
	
	/*
	 Быстрая сортировка: 
	 	* Сложность: N*Log2(N)
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
	
	/*
	 Поразрядная сортировка: 
	 	* Сложность: N*K
	 		** где K число разрядов в сортиуемом элементе
	 	* Доп. память: O(N)
	 	* Минусы/Плюсы:
	 		* -/+ быстрей, если k < log2(n) и дольше в других случаях
	 		* - разный размер хэш массива для разных типов данных
	 	* Особенность:
	 		* Порядок сортировки зависит от направления: для цифр нужно справа-навлево, для строк: слева-направо
	 	* Общий смысл:
	 		* Идем по рязрядам числа справа-налево и помещаем элементы в массив по значению текущего разряда
	 			** т.е. элементы сначала упорядычиваются по последнему элементу
	 			** потом переупорядычиваваются по второму и т.д. пока полностью не упорядочим массив
	 */
	public static <T extends Integer> void radixSortUInt(T[] arr) {
		
		//хэш массив из 10 элементов для 10-ричного числа
		final int RADIX = 10;
		List<T>[] bucket = new ArrayList[RADIX];
		for (int i = 0; i < bucket.length; i++) {
			//список чисел, содержащих нужно число в разряде
			bucket[i] = new ArrayList<T>();
		}
		
		//признак, что все разряды перебраны
		boolean maxLength = false;
		//значение в разряде
		int rank_val = -1;
		//номер разряда
		int placement = 1;
		
		//пока не перебраны все разряды
		while (!maxLength) {
		    maxLength = true;
		    
		    //для каждого элемента массива
		    for (int i = 0; i < arr.length; i++) {
		    	//добавляем элемент в массив по значению в разряде
		    	rank_val = arr[i] / placement;
			    bucket[rank_val % RADIX].add(arr[i]);
			    
			    //если в разряде не пусто, то ставим флаг повторения цикла
			    if (maxLength && rank_val > 0) {
			    	maxLength = false;
			    }
		    }
		    
		    //разворачиваем двухуровневый массив обратно в последовательный
		    int a = 0;
		    for (int b = 0; b < RADIX; b++) {
		    	for (int i = 0; i < bucket[b].size(); i++) {
		    		arr[a++] = bucket[b].get(i);
		    	}
		    	bucket[b].clear();
		    }
		    //переходим к следующему разряду
		    placement *= RADIX;
		}
		  
	} //radixSortUInt
	
	/*
	 Бинарный посик: 
	 	* Сложность: log2(N)
	 	* Доп. память: нет
	 	* Общий смысл:
	 		* Идем по рязрядам числа справа-налево и помещаем элементы в массив по значению текущего разряда
	 			** т.е. элементы сначала упорядычиваются по последнему элементу
	 			** потом переупорядычиваваются по второму и т.д. пока полностью не упорядочим массив
	 */
    public static <T extends Comparable<T>> int binary_search(T[] arr, T val) {
        int lo = 0;
        int hi = arr.length - 1;
        //пока левая и правая границы не встретятся
        while (lo <= hi) {
        	//находим середину
            int mid = lo + (hi - lo) / 2;
            
            //если элемент меньше середины
            if (val.compareTo(arr[mid]) < 0) {
            	//то верхняя граница - 1 = середина
            	hi = mid - 1;
            } else if (val.compareTo(arr[mid]) > 0) {
            	//если больше, то нижняя граница = середина + 1
            	lo = mid + 1;
            } else {
            	//иначе нашли
            	return mid;
            }
        }
        //если границы встретились, то элемент не найден
        return -1;
    } //binary_search
}

/*
 * Комбинированная сортировка: слиянием + быстрая + поразрядная
 * 
 * */
public class OracleSort extends MergeSort {
	//размер памяти под сортировку
	int sort_area_size = 1;
	//размер ключа
	int avg_key_size = 0;
	
	
	final double log10_2 = Math.log(2);
	
	OracleSort(int _sort_area_size, int _avg_key_size) {
		sort_area_size = _sort_area_size;
		avg_key_size = _avg_key_size;
	} //OracleSort
	
	protected boolean isSortArr(Comparable[] arr) {
		
		if(arr.length < 2) return true;
		
		//если после разбиения массива размер меньше размера под сортировку
		if(arr.length <= sort_area_size) {		
			
			//если Integer и размер ключа меньше log2(Т), то делаем поразрядную сортировку
			if( arr[0].getClass() == Integer.class && avg_key_size > 0 && avg_key_size < ( Math.log(arr.length) / log10_2 ) ) {
				
				Integer[] _arr = new Integer[arr.length]; //как привести Comparable к Integer?
				
				System.arraycopy(arr, 0, _arr, 0, arr.length);
				Sorts.radixSortUInt(_arr);
				System.arraycopy(_arr, 0, arr, 0, arr.length);
				_arr = null;
			} else {
				//иначе быструю сортировку
				Sorts.QSort(arr, 0, arr.length-1);
			}
			return true;
		}
		return false;
	} //isSortArr

	public static void main(String[] args) {
		
		/*JUnitCore runner = new JUnitCore();
		Result result = runner.run(OracleSortTest.class);
		System.out.println("run tests: " + result.getRunCount());
		System.out.println("failed tests: " + result.getFailureCount());
		System.out.println("ignored tests: " + result.getIgnoreCount());
		System.out.println("success: " + result.wasSuccessful());*/
		
		Integer arr[] = {2, 6, 3, 5, 1, 7, 8, 0, 27, 17, 99, 13, 1, 7};
		
		System.out.println(Arrays.toString(arr));
		
		Node list = new Node(arr);
		list.print();
		list.print();
		System.out.println("reverse:");
		list.reverse();
		list.print();
		System.out.println("reverse:");
		list.reverse();
		list.print();
		
		//Arrays.sort(arr);
		//Sorts.bubleSort(arr);
		//Sorts.insertSort(arr);
		//Sorts.InsertSortList( new Node(arr) ).print();
		
		//(new Tree(arr)).traverse();
		
		//Sorts.QSort(arr, 0, arr.length-1);
		
		//( new MergeSort() ).sort(arr);
		
		//Sorts.radixSortUInt(arr);
		
		//new OracleSort(5, 2).sort(arr);
		
		//System.out.println(Arrays.toString(arr));
		
		//System.out.println("7=" + Sorts.binary_search(arr, 7));
		//System.out.println("73=" + Sorts.binary_search(arr, 73));
	}

}

package algorithms;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Heap<T extends Comparable<T>> {
	//свойства кучи:
	//1. значение в вершине больше любого потомка
	//2. взвешенность: глубина листьев не отличается больше чем на 1 слой
	//  т.к. дерево звешенное, то любая операция выполняется гарантировано за log(n)
	//3. заполняется слева направо, без пропусков
	
	//минус сортировки: не позволяет сортировать списки, т.к. нужен доступ по индексу
	
	//сложность одной итерации от 0 до log(n) (0, если вставляемый элемент уже на своем месте)
	void heapify(T a[], int n, int i) {
		int max, child_l, child_r;
		
		//удобным средством хранения кучи является массив, где:
		//N элемент в корне, будет иметь детей на 2N+1 и 2N+2 позиции - в этом случае все автоматом балансируется
		//на первой позиции будет максимальный элемент
		// см. картинку на https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D0%BE%D0%B8%D1%87%D0%BD%D0%B0%D1%8F_%D0%BA%D1%83%D1%87%D0%B0 или https://prog-cpp.ru/sort-pyramid/
		
		child_l = 2 * i + 1;
		child_r = child_l + 1;
		max = i;
		
		//проверяем условие дерева у левого и правого потомка
		if (child_l < n && a[child_l].compareTo(a[max]) > 0) {
			max = child_l;
		}
		//выбираем максимальный из потомков (max)
		if (child_r < n && a[child_r].compareTo(a[max]) > 0) {
			max = child_r;
		}
		//если не удволетворяет, то меняем местами
		if (max != i) {
			T temp = a[i];
			a[i] = a[max];
			a[max] = temp;
			//рекурсивно повторяем для детей
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

	//сложность превращения массива в кучу: N
	//хотя здесь происходит n/2 вызовов функции Heapify со сложностью log(n), можно показать, что время работы равно O(n)
	void buildheap(T a[]) {
		//движемся из середины к левому краю (правый край > N/2 это только листья, т.к. N/2*2+1 выходит за пределы массива)
		//движемся с середины, чтобы сначала заполнить листы, а потом ветви и перестроить листы рекурсивно
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

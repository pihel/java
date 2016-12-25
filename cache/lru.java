package cache;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

//двухсвязный список
class Node<Value> {
	//ключ списка
	int key;
	
	//абстрактное значение
	Value value;
	
	//счетчик обращений к элементу
	int cnt;
	
	//указатель на предыдущий элемент
	Node prev;
	
	//указатель на следующий элемент
	Node next;
	
	public Node(int key, Value value){
		this.key = key;
		this.value = value;
		this.cnt = 1;
	}
	
	//получить значение из списка
	public Value getValue() {
		//увеличиваем счетчик обращений
		cnt++;
		return value;
	}
	
	//установить значение
	public void setValue(Value val) {
		this.value = val;
		//также увеличиваем счетчик
		cnt++;
	} //setValue
} //Node


public class Lru<Value> {
	
	//доступной число элементов в кэше
	int capacity;
	
	//хэш массив элементов для быстрого доступа
	HashMap<Integer, Node> map;
	
	//указатель на начало (горячие элементы)
	Node head = null;
	
	//указатель на середину (начало холодных элементов)
	Node cold = null;
	
	//указатель на конец (самый редкоиспользуемый)
	Node end = null;
	
	//число элементов в кэше
	int cnt;
	
	
	//конкструктор с числом элементов в кэше
	public Lru (int capacity) {
		this.capacity = capacity;
		
		//хэш массив создаем с нужным числом секций = загруженности
		map = new HashMap<Integer, Node>(capacity);
	}
	
	//получить элемент из кэша
	public Value get(int key) {
		//быстрое извлечение их хэш массива
		if(map.containsKey(key)) {
			//и инкремент счетчика обращений
			return (Value) map.get(key).getValue();
		}
		
		return null;
	} //get
	
	//места достаточно, добавляем вначало
	protected void addHead(Node n) {
		
		//первый элемент
		if(this.head == null) {
			//устанавливаем начало и конец = элементу
			this.head = n;
			this.end = n;
		} else {
			
			//вставляем вначало
			
			//следующий для нового элемента = начало списка
			n.next = this.head;
			
			//предыдущий для начала списка = новый элемент
			this.head.prev = n;					
			this.head = n;
			
			//второй элемент
			if(this.end.prev == null) {
				//предыдущий для конца = новый элемент
				this.end.prev = n;
			}
		}				
		
		//устанавливаем середину
		if(cnt == capacity / 2) {
			this.cold = n;
		}
		
		//счетчик элементов + 1
		cnt++;
	} //addHead
	
	//вконце малопопулярный блок
	protected void addColdUnPop(Node n) {
		//удаляем конец
		
		//из хэш массива
		map.remove(this.end.key);
		
		//и делаем концом списка = предыдущий элемент
		this.end = this.end.prev;
		this.end.next = null;
		
		//у старой середины изменяем счетчик на 1
		this.cold.cnt = 1;
		
		//новый блок в середину = cold
		
		//проставляем ссылки у нового элемента
		n.prev = this.cold.prev;
		n.next = this.cold;
		
		//и разрываем связи и соседей
		n.prev.next = n;
		n.next.prev = n;
		this.cold = n;
	} //addColdUnPop
	
	//вконце популярный блок
	protected void addColdPop(int key, Value value) {
		//делим счетчик на пополам
		this.end.cnt = this.end.cnt / 2;		
		
		//открепляем конец
		Node n = this.end;
		
		//и делаем концом списка = предыдущий элемент
		this.end = this.end.prev;
		this.end.next = null;
		
		//конец перемещаем в начало
		n.prev = null;
		n.next = this.head;
		this.head.prev = n;					
		this.head = n;
		
		//смещаем середину на 1 влево
		this.cold.cnt = 1;
		this.cold = this.cold.prev;
		
		//рекурсивно пытаемся вставить вконец
		this.set(key, value);
	} //addColdPop
	
	public void set(int key, Value value) {
		//если элемент по ключу уже есть, то заменяем значение
		if(map.containsKey(key)) {
			map.get(key).setValue(value);
		} else {
			//новый элемент
			Node created = new Node(key, value);			
			
			//если в буфере еще есть место
			if(cnt < capacity) {
				//просто вставляем в начало
				addHead(created);			
			} else {
				//свободного места нет
				
				if(this.end.cnt <= 1) {
					//вконце малопопулярный блок
					addColdUnPop(created);
				} else {
					//вконце популярный блок
					addColdPop(key, value);					
					return;
				}
			} //else
			
			//добавляем элемент в хэш массив
			map.put(key, created);		
			
		} //else
	} //set
	
	//вывод двусвязного списка на экран
	public void dump() {
		System.out.print("cnt: " + this.cnt + " (cap: " + this.capacity + "): ");
		
		Node n = this.head;
		do {			
			if(n.prev != null && n.prev.next != null) System.out.print("> ");			
			System.out.print("[" + n.key + "] " + n.value + " (cnt=" + n.cnt + ")");			
			if( n == this.cold ) System.out.print(" {COLD}");			
			if(n.next != null && n.next.prev != null) System.out.print(" <-");			
			
			n = n.next;
		} while(n != null);
	} //dump
	
	public static void main(String[] args) {
		Lru l = new Lru(5);
		
		String r;
		for(int i = 0; i < 50; i++) {
			if(i % 5 == 0) {
				l.set(ThreadLocalRandom.current().nextInt(0, 10),"r." + i);
			}
			r = (String)l.get(ThreadLocalRandom.current().nextInt(0, 10));
		}
		
		/*l.set(1,"1");
		l.set(2,"2");
		l.set(3,"3");
		l.set(4,"4");	
		l.set(5,"5");
		
		String r = (String)l.get(5);
		r = (String)l.get(5);
		r = (String)l.get(1);
		r = (String)l.get(1);
		
		l.set(6,"6");
		l.set(7,"7");*/
		
		l.dump();
	} //main

} //Lru

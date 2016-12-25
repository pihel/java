package cache;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

class Node<Value> {
	int key;
	Value value;
	
	int cnt;
	
	Node prev;
	Node next;
	
	public Node(int key, Value value){
		this.key = key;
		this.value = value;
		this.cnt = 1;
	}
	
	public Value getValue() {
		cnt++;
		return value;
	}
	
	public void setValue(Value val) {
		this.value = val;
		cnt++;
	} //setValue
} //Node


public class Lru<Value> {
	
	int capacity;
	
	HashMap<Integer, Node> map;
	
	Node head = null;
	Node cold = null;
	Node end = null;
	
	int cnt;
	
	public Lru (int capacity) {
		this.capacity = capacity;
		
		map = new HashMap<Integer, Node>(capacity);
	}
	
	public Value get(int key) {
		if(map.containsKey(key)) {
			return (Value) map.get(key).getValue();
		}
		
		return null;
	} //get
	
	//места достаточно, добавляем вначало
	protected void addHead(Node n) {
		
		//первый элемент
		if(this.head == null) {
			this.head = n;
			this.end = n;
		} else {
			
			//вставляем вначало
			n.next = this.head;
			this.head.prev = n;					
			this.head = n;
			
			//второй элемент
			if(this.end.prev == null) {
				this.end.prev = n;
			}
		}				
		
		//устанавливаем середину
		if(cnt == capacity / 2) {
			this.cold = n;
		}
		
		cnt++;
	} //addHead
	
	//вконце малопопулярный блок
	protected void addColdUnPop(Node n) {
		//удаляем конец
		map.remove(this.end.key);
		this.end = this.end.prev;
		this.end.next = null;
		
		//у старой середины изменяем счетчик на 1
		this.cold.cnt = 1;
		
		//новый блок в середину = cold
		n.prev = this.cold.prev;
		n.next = this.cold;
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
		if(map.containsKey(key)) {
			map.get(key).setValue(value);
		} else {
			Node created = new Node(key, value);			
			
			if(cnt < capacity) {
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
			
			map.put(key, created);		
			
		} //else
	} //set
	
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

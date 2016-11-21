package Hash;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class ValueList<Value> {
	public Value value;
	public ValueList next;
	
	public ValueList(Value val) {
	    this.next = null;
	    this.value = val;
    } //ValueList
	
} //ValueList

class HashEntry<Value> {
    public int key;
    public ValueList root;

    public HashEntry(int key, Value value) {
	    this.key = key;
	    this.root = new ValueList( value );
    } //HashEntry
    
    public void add(Value value) {
    	ValueList old_root = this.root;
    	this.root = new ValueList( value );
    	this.root.next = old_root;
    } //add
} //HashEntry

//--------------------------------------------------

public class Hash<Value> {
	
	 //inital capacity  - начальная загрузка
	private int TABLE_SIZE;
	
	//число элементов в хэщ таблице
	private int cnt;
	
	//максимальная заполненность хэш таблицы - после этого нужно перестраивать
	private double max_load_factor = 0.75;
	 
	ArrayList<HashEntry> table;
	
	//признак хэш группы
	private boolean is_hash_group;

    public Hash(int cnt_elements) {
    	
    	//признак хэш массива
    	is_hash_group = false;
    	
    	//число хэш блоков = число элементов / максимальную загрузку
    	TABLE_SIZE = (int)Math.ceil((double)cnt_elements / max_load_factor );
    	
    	//TABLE_SIZE = 10; //TEST
    	
    	//резервируем память: начальный и конечный размер массива = TABLE_SIZE
        table = new ArrayList<HashEntry>(TABLE_SIZE);
        
        for (int i = 0; i < TABLE_SIZE; i++) {
        	table.add(i, null);
        }
    } //Hash   
    
    public Hash(HashEntry[] tbl, int hash_area_size) {
    	//признак хэш группы
    	is_hash_group = true;
    	
    	//кол-во хэш групп
    	TABLE_SIZE = (int)Math.ceil((double)tbl.length / hash_area_size / max_load_factor );

    	//кол-во хэшей в группе
    	int group_cnt = (int)Math.ceil( (double)tbl.length / (double)TABLE_SIZE / max_load_factor );
    	group_cnt = Math.min(hash_area_size, group_cnt );
    	
    	table = new ArrayList<HashEntry>(TABLE_SIZE);
    	for (int k = 0; k < TABLE_SIZE; k++) {
			HashEntry he = new HashEntry(k, new Hash(group_cnt) );
    		table.add(k, he);
    	}
    	
    	for(int i = 0; i < tbl.length; i++) {
    		this.put(tbl[i].key, (Value)tbl[i].root.value);
    	}
    } //Hash
    
    //заполненность хэш таблицы
    public double getLoadFactor() {
    	//для хэш группы = среднему из хэш таблиц
    	if(is_hash_group) {
    		double load_fact = 0;
        	for(int i = 0; i < TABLE_SIZE; i++) {    		
        		load_fact = load_fact + ((Hash)table.get(i).root.value).getLoadFactor();
        	}
    		return load_fact / TABLE_SIZE;
    	}
    	
    	//= число элементов / число хэш блоков
    	return (double)cnt / (double)TABLE_SIZE;
    } //getLoadFactor
    
    //кол-во проб хэш массива для получения элкмента
    public double getAvgProbeCnt() {
    	//кол-во проб ~= (loadFactor) / (1 - loadFactor)
    	
    	return getLoadFactor()  / (1 - getLoadFactor() );
    } //getAvgProbeCnt
    
    public int getCnt() {
    	return cnt;
    } //getCnt
    
    public int getTblSize() {
    	return TABLE_SIZE;
    } //getTblSize
    
    
    //получить хэш ключ по ключу поиска
    private int gethash(int key) {
    	//для хэш группы не предусмотрено коллизий, коллизия помещается в хэш массив под нужным индексом
    	if(is_hash_group) {
        	return (key % TABLE_SIZE);
        } 
    	
    	//хэш ключ таблицы
        int hash = (key % TABLE_SIZE);
        //первое значение ключа хэширования
        int initialHash = -1;
        
        //если хэш нашелся в таблице, но ключ не наш - это коллизия
        //или там уже лежит ключ от другой коллизии
        //двигаемся дальше, пока не найдем наш ключ
        while (hash != initialHash && table.get(hash) != null && table.get(hash).key != key) {
        	
        	//запишем хэш колллизии, чтобы не заходить в нее повторно для этого ключа
			if (initialHash == -1) {
				initialHash = hash;
		    }
			
			//смещаемся на ключ вправо по модулю TABLE_SIZE
			//т.е. если дошли до правого края, то возвращаемся к первому
		    hash = (hash + 1) % TABLE_SIZE;
        }
        
        //если прошли по кругу, то ключ не найден
        if(hash == initialHash) return -1;
        
        return hash;
    	
    } //gethash

    public HashEntry get(int key) {
    	//хэш ключ таблицы
        int hash = gethash(key);
        
        if(is_hash_group) {
        	return ((Hash)table.get(hash).root.value).get(key);
        }
          
        //значение по хэшу
        if (hash == -1 || table.get(hash) == null) {
        	return null;
    	} else {
    		return table.get(hash);
        }
    } //get

    private void put(int key, Value value) {    	
    	//хэш ключ таблицы
        int hash = gethash(key);
		
		//если хэш для вставки нашелся
		if (hash != -1) {
	    	if(is_hash_group) {
	    		((Hash)table.get(hash).root.value).put(key, value);
	    		cnt++;
	    		return;
	    	}
			
			//повторная вставка существующего ключа - расширяем список
	        if (table.get(hash) != null && table.get(hash).key == key) {
	        	table.get(hash).add(value);	        	
	        } else {
	        	//новый элемент
	        	table.set(hash, new HashEntry(key, value) );
	        	cnt++;
	        }
		} else {
			throw new RuntimeException("Overflow elemnt = " + key + " on hash table size = " + TABLE_SIZE);
		}
    } //put
    
    public void dump() {
    	dump(true);
    }//dump
    
    public void dump(boolean add_total) {
    	for(int i = 0; i < table.size(); i++) {    		
    		HashEntry he = table.get(i);
    		if(he == null) continue;
    		
    		if(!is_hash_group ) System.out.print("  ");
    		
    		System.out.print ("[hsh=" + i + "] (sz="+TABLE_SIZE+") k=" + he.key + " vls = { ");
    		
    		//если будут дочерний массив, то с новой строки внутренний массив
    		if(is_hash_group) System.out.println(" ");
    		
    		ValueList he_root = he.root;
    		do {
    			if( is_hash_group ) {
    				((Hash)he.root.value).dump(false);
    			} else {
    				System.out.print (he.root.value + ", ");
    			}
    			
    			he.root = he.root.next;
    		} while(he.root != null);
    		System.out.println ("}, ");
    		
    		//доп перенос строки - разделить главные массивы
    		if(is_hash_group) {
    			System.out.println (" ");
    		}
    		
    		//вернем указатель на начало
    		he.root = he_root;
		}
    	
    	if(add_total) { 
	    	System.out.println ( " " );
			System.out.println ( "cnt = " + getCnt() );	
			System.out.println ( "load factor = " + getLoadFactor() );
			System.out.println ( "avg probes = " + getAvgProbeCnt() );
    	}
    } //dump
    
    public static void dumpEntry(HashEntry he) {
    	if(he == null) return;
    	
		ValueList he_root = he.root;
		System.out.print ("[" + he.key + "] = ");
		do {
			System.out.print (he.root.value + ", ");		
			
			he.root = he.root.next;
		} while(he.root != null);
		he.root = he_root;
    	
    } //dumpEntry
	
	public static void main(String[] args) {		
		Hash h;
		
		/*h = new hash(7);
		h.put(1,1);
		h.put(1,1);
		h.put(2,2);
		h.put(3,3);
		h.put(11,11);
		System.out.println ("get(111) = " +  h.get(111) );*/
		
		/*h = new Hash(16);
		for(int i = 0; i < 10; i++) {
			h.put(ThreadLocalRandom.current().nextInt(0, 5) , "r." + i);
			//h.put(i , "r." + i);
		}*/
		
		
		HashEntry[] he = new HashEntry[10];		
		for(int i = 0; i < 10; i++) {
			he[i] = new HashEntry( ThreadLocalRandom.current().nextInt(0, 10) , "r." + i );
			//he[i] = new HashEntry( i , "r." + i );
		}
		h = new Hash(he, 5);
		
		for(int i = 0; i < 10; i++) {
			System.out.print ( "get(" + i + ") = " );
			Hash.dumpEntry ( h.get(i) );
			System.out.println ( " " );
		}
		
		h.dump();
		
	} //main

}

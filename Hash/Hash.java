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

    public Hash(int cnt_elements) {
    	
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
    	//кол-во хэш групп
    	int group_cnt = (int)Math.ceil( (double)tbl.length / (double)hash_area_size );
    	TABLE_SIZE = (int)Math.ceil(group_cnt / max_load_factor ) ;
    	
    	table = new ArrayList<HashEntry>(TABLE_SIZE);
    	for (int k = 0; k < TABLE_SIZE; k++) {
			HashEntry he = new HashEntry(k, new Hash(hash_area_size) );
    		table.add(k, he);
    	}
    	
    	for(int i = 0; i < tbl.length; i++) {
    		this.put(tbl[i].key, (Value)tbl[i].root.value, true);
    	}
    } //Hash
    
    //заполненность хэш таблицы
    public double getLoadFactor() {
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
    private int getHash(int key) {
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
    	
    } //getHash

    public ValueList get(int key) {
    	//хэш ключ таблицы
        int hash = getHash(key);
          
        //значение по хэшу
        if (hash == -1 || table.get(hash) == null) {
        	return null;
    	} else {
    		return table.get(hash).root;
        }
    } //get
    
    public void put(int key, Value value) {
    	this.put(key, value, false);
    }

    private void put(int key, Value value, boolean is_hash_group) {   	
    	//хэш ключ таблицы
        int hash = getHash(key);
		
		//если хэш для вставки нашелся
		if (hash != -1) {
	    	if(is_hash_group) {
	    		((Hash)table.get(hash).root.value).put(key, value, false);
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
		}
    } //put
    
    public void dump() {
    	for(int i = 0; i < table.size(); i++) {    		
    		HashEntry he = table.get(i);
    		if(he == null) continue;
    		
    		if( he.root.value.getClass() != Hash.class ) {
    			System.out.print("  ");
    		}
    		
    		int max_lvl = 0;
    		System.out.print (he.key + " = { ");
    		
    		//если будут дочерний массив, то с новой строки внутренний массив
    		if(he.root.value.getClass() == Hash.class ) System.out.println(" ");
    		
    		do {
    			if( he.root.value.getClass() == Hash.class ) {
    				((Hash)he.root.value).dump();
    				max_lvl = 1;
    			} else {
    				System.out.print (he.root.value + ", ");
    			}
    			
    			he.root = he.root.next;
    		} while(he.root != null);
    		System.out.println ("}, ");
    		
    		//доп перенос строки - разделить главные массивы
    		if(max_lvl > 0) {
    			System.out.println (" ");
    		}
		}
    } //dump
	
	public static void main(String[] args) {		
		Hash h;
		
		/*h = new Hash(16);
		h.put(1,1);
		h.put(1,1);
		h.put(11,11);
		h.put(2,2);		
		System.out.println ("get(111) = " +  h.get(111) );*/
		
		/*h = new Hash(16);
		for(int i = 0; i < 10; i++) {
			h.put(ThreadLocalRandom.current().nextInt(0, 10) , "r." + i);
		}*/
		
		HashEntry[] he = new HashEntry[10];		
		for(int i = 0; i < 10; i++) {
			he[i] = new HashEntry( ThreadLocalRandom.current().nextInt(0, 10) , "r." + i );
			//he[i] = new HashEntry( 1 , "r." + i );
		}
		h = new Hash(he, 4);
		
		h.dump();
		
		System.out.println ( " " );
		System.out.println ( "cnt = " + h.getCnt() );
		System.out.println ( "tbl size = " + h.getTblSize() );		
		System.out.println ( "load factor = " + h.getLoadFactor() );
		System.out.println ( "avg probes = " + h.getAvgProbeCnt() );
		
	} //main

}

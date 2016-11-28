package Hash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

//список повторяющихся значений
class ValueList<Value> {
	public Value value;
	public ValueList next;
	
	public ValueList(Value val) {
	    this.next = null;
	    this.value = val;
    } //ValueList
	
	public void dump() {
		ValueList e = this;
		System.out.print("<");
		do {
	        System.out.print(e.value);
	        e = e.next;
	        
	        if(e != null) {
	        	System.out.print(", ");
	        }
		} while(e != null);
		System.out.print(">");
    } //dump
	
} //ValueList
 

//список значений в хэш группе
class HashEntry<Value> {
	
    public int key;
    public ValueList value;
    public HashEntry next;
    
    public int cnt;
    public int cnt_mem;
 
    HashEntry(int key, Value value) {
          this.key = key;
          this.value = new ValueList( value );
          this.next = null;
          
          cnt++;
    }
    
    public void add(Value val) {
    	ValueList old_root = this.value;
    	this.value = new ValueList( val );
    	this.value.next = old_root;
    	
    	cnt++;
    } //add
    
    public void dump() {
		HashEntry e = this;
		System.out.print("{ ");
		do {
	        System.out.print("[" + e.key + ": ");
	        e.value.dump();
	        System.out.print("], ");
	        e = e.next;
		} while(e != null);
		System.out.print("}");
    } //dump
    
    public HashEntry reverse() {
		// 1 -> 2 -> 3 -> 4 -> 5
		
		HashEntry rev = this;
		HashEntry prev = null;
		HashEntry next = null;
		
		while(rev != null) {
			
		    //запомнил ссылку на следующий эл.
		    next = rev.next;
		   
		    //заменим следующий = предыдущий
		    rev.next = prev;
		   
		    //если нет следующего - выходим (или возвращаем предыдущего после цикла, тогда не нужно сравнение)
		    //if(next == null)  return rev;
		   
		    // если есть следующий то подготовим переменные для следующей итерации
	  	    //предыдущий = текущий
		    prev = rev;
		    //текущий = следующий
		    rev = next;
		               
		}
		return prev;
    }
} //HashEntry

class HashEntryHolder<Value> {
	public HashEntry table;
	
	public int cnt;
	public int cnt_mem;
	public int hash;
	public boolean is_reorg = false;
	
	HashEntryHolder(int key, Value value, int hash) {
		this.table = new HashEntry(key, value);	
		this.hash = hash;
    } //HashEntryHolder
	
	public void addCnt() {
		cnt++;
		
		if(OraHash.free_hash_area_size > 0) { 
			OraHash.free_hash_area_size--;
			cnt_mem++;
		}
	}
	
	public void dump() {
		System.out.print ("[" + this.hash + "] (cnt: " + cnt + ", mem: " + cnt_mem);
		if(is_reorg) System.out.print (", reorg");
		System.out.print (") = ");
	} //dump
} //HashEntryHolder
 
//--------------------------------------------------
 
class Hash<Value> {
    
	//inital capacity  - начальная загрузка
    private static int TABLE_SIZE;
    
    //число элементов в хэш таблице
  	public int cnt;
  	
  	//число уникальных значений в хэш таблице
  	public int uniq_cnt;
  	
  	//число занятых хэшей
  	public int hash_cnt;

  	HashEntryHolder[] holder;
    
    public Hash() {    	
    	this(10);
    } //Hash
 
    public Hash(int _table_size) {
    	if(_table_size < 1) _table_size = 1;
    	
	    TABLE_SIZE = _table_size;
	    
	    holder = new HashEntryHolder[TABLE_SIZE];
	    Arrays.fill(holder, null);
	    
    } //Hash
    
    public static int getHash(int key) {
    	return (key % TABLE_SIZE);
    } //getHash
    
    public HashEntryHolder[] getFullHash() {
    	return this.holder;
    } //getFullHash
 
    public ValueList get(int key) {
		int hash = getHash(key);
		  
		if (holder[hash] == null) {
			return null;
		} else {
		    HashEntry<ValueList> entry = holder[hash].table;
		    
		    while (entry != null && entry.key != key) {
		    	entry = entry.next;
		    }
		    if (entry == null) {
		    	return null;
		    } else {
		    	return entry.value;
		    }
		}
    } //get
 
    public void put(int key, Value value) {
	  int hash = getHash(key);
	  
	  if (holder[hash] == null) {
		  holder[hash] = new HashEntryHolder(key, value, hash);
          hash_cnt++;
          uniq_cnt++;
          holder[hash].addCnt();
		} else {
			HashEntry entry = holder[hash].table;
			while (entry.next != null && entry.key != key) {
			    entry = entry.next;
			}
			if (entry.key == key) {
			    entry.add(value);
			} else {
				entry.next = new HashEntry(key, value);
				uniq_cnt++;
			}
			holder[hash].addCnt();
		}
      
	  cnt++;
    } //put
    
    //заполненность хэш таблицы
    public double getLoadFactor() {    	
    	//= число элементов / число хэш блоков
    	return (double)this.uniq_cnt / (double)TABLE_SIZE;
    } //getLoadFactor
    
    //кол-во проб хэш массива для получения элкмента
    public double getAvgProbeCnt() {
    	//кол-во проб ~= (loadFactor) / (1 - loadFactor)
    	
    	double probes = getLoadFactor()  / (1 - getLoadFactor() );
    	
    	if(probes > cnt || probes <= 0) {
    		return cnt;
    	}
    	
    	return probes;
    } //getAvgProbeCnt
    
    public int getTblSize() {
    	return TABLE_SIZE;
    } //getTblSize
    
    public void dump() {
    	dump(true);
    } //dump
    
    public void dump(boolean add_total) {
    	
    	for(int i = 0; i < TABLE_SIZE; i++) {
			if(holder[i] != null) {
				holder[i].dump();
				holder[i].table.dump();
				System.out.println(" ");
			}
		}
    	
    	if(add_total) { 
	    	System.out.println ( " " );	    	
	    	System.out.println ( "tbl size = " + getTblSize() );	
	    	System.out.println ( "cnt = " + this.cnt );	
	    	System.out.println ( "uniq_cnt = " + this.uniq_cnt );	
	    	System.out.println ( "hash_cnt = " + this.hash_cnt );	
			System.out.println ( "load factor = " + getLoadFactor() );
			System.out.println ( "avg probes = " + getAvgProbeCnt() );
    	}
    } //dump
} //Hash

class OraHash<Value> extends Hash<Value> {
	
	//битовая карта - принадлежность элемента к хэш массиву без запроса к самой таблице
	private boolean bitmap[];
	public static int bitmap_size = 30;
	public static int hash_area_size;
	public static int free_hash_area_size;
	
    public OraHash(int _hash_area_size) {
    	
    	super((int) ( _hash_area_size * 0.8 ));
    	
    	hash_area_size = _hash_area_size;
    	free_hash_area_size = hash_area_size;
    	
    	bitmap = new boolean[bitmap_size];
    	Arrays.fill(bitmap, false);
    } //OraHash
    
    public static int getBitmapHash(int key) {
    	return (key % bitmap_size);
    } //getHash
    
    public void put(int key, Value value) {
    	super.put(key, value);
    	
    	bitmap[getBitmapHash(key)] = true;
    } //put
    
    public ValueList get(int key) {
    	int hash = getBitmapHash(key);
    	
    	if(!bitmap[getBitmapHash(key)]) return null;
    	
    	return super.get(key);
    } //get
    
    public void reorg() {
    	int hash_max = -1;
    	int elems_in_mem = 0;
    	for(int i = 0; i < this.holder.length; i++) {
    		if( this.holder[i] != null && 
    				this.holder[i].cnt_mem > elems_in_mem && 
    				this.holder[i].cnt_mem < this.holder[i].cnt && 
    				!this.holder[i].is_reorg &&
    				this.holder[i].cnt <= hash_area_size
    				) {
    			elems_in_mem = this.holder[i].cnt_mem;
    			hash_max = i;
    		}
    	}
    	if(hash_max < 0) return;
    	
    	//System.out.println ( "max free mem hash = " + hash_max);
    	
    	this.holder[hash_max].is_reorg = true;
    	
    	for(int i = 0; i < this.holder.length; i++) {
    		if( i != hash_max && 
    				this.holder[i] != null && 
    				this.holder[i].cnt_mem > 0) {
    			
    			int elems_in_disk = this.holder[hash_max].cnt - this.holder[hash_max].cnt_mem;
    			
    			if(elems_in_disk <= 0) break;
    			
    			if(elems_in_disk > this.holder[i].cnt_mem) {
    				elems_in_disk = this.holder[i].cnt_mem;
    			}
    			this.holder[hash_max].cnt_mem = this.holder[hash_max].cnt_mem + elems_in_disk;
				this.holder[i].cnt_mem = this.holder[i].cnt_mem - elems_in_disk;
				
    		} //if
    	}
    	
    	reorg();
    	
    } //reorg
    
    public void dump(boolean add_total) {
    	super.dump(add_total);
    	
    	if(add_total) {
    		System.out.println ( "free mem = " + free_hash_area_size);
    		System.out.print ( "bitmap = ");
    		for(int i = 0; i < bitmap_size; i++) {
    			System.out.print("[" + i + "]=" + bitmap[i] + ", ");
    		}
    	}
    	System.out.println (" ");
    } //dump

    public static void main(String[] args) {
	   /*HashEntry e = new HashEntry(1, -1);
	   HashEntry root = e;
	   for(int i = 2; i <= 5; i++)  {
           e.next = new HashEntry(i, -i);
           e = e.next;
	   }
	   root.dump();
	   System.out.println(" ");
	   root.dump();
	   
	   System.out.println(" ");
	   System.out.println("------");
	   
	   HashEntry rev = root.reverse();
	   rev.dump();
	   System.out.println(" ");
	   rev.dump();*/
    	
    	/*OraHash h = new OraHash(13);
       h.put(1,1);
       h.put(1,1);
       h.put(2,2);
       h.put(3,3);
       h.put(11,11);
       h.dump();*/
	   
       OraHash h = new OraHash(1);
	   for(int i = 0; i < 50; i++) {
	       h.put(ThreadLocalRandom.current().nextInt(0, 3) , "r." + i);
	       //h.put(i , "r." + i);
	   }
	   h.reorg();
	   h.dump();
                   
    } //main
 
}
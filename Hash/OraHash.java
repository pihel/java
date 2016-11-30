package Hash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

//список повторяющихся значений внутри уникального значения HashEntry
class ValueList<Value> {
	//абстрактное значение
	public Value value;
	//ссылка на следующий элемент списка
	public ValueList next;
	
	public ValueList(Value val) {
	    this.next = null;
	    this.value = val;
    } //ValueList
	
	//обход списка с выводом на экран
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
 

//список уникальных значений (ValueList - список повторов) в хэш секции
class HashEntry<Value> {
	
	//ключ секции
    public int key;
    
    //список повторяющихся значений
    public ValueList value;
    
    //следующий элемент списка
    public HashEntry next;
 
    HashEntry(int key, Value value) {
          this.key = key;
          this.value = new ValueList( value );
          this.next = null;
    }
    
    public void add(Value val) {
    	ValueList old_root = this.value;
    	this.value = new ValueList( val );
    	this.value.next = old_root;    	
    } //add
    
    //обход списка с выводом на экран
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
    
    
    //разворот списка в обратную сторону (не используется)
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

//обертка хэш секции, для возможности хранения дополнительных параметров
class HashEntryHolder<Value> {
	//ссылка на хэш секцию
	public HashEntry table;
	
	//количество элементов в секции (уникальных и неуникальных)
	public int cnt;
	
	//количество элементов в памяти
	public int cnt_mem;
	
	//ключ хэш секции
	public int hash;
	
	//признак просмотра секции при реорганизации в памяти
	public boolean is_reorg = false;
	
	HashEntryHolder(int key, Value value, int hash) {
		this.table = new HashEntry(key, value);	
		this.hash = hash;
    } //HashEntryHolder
	
	//инкремент числа элементов
	public void addCnt() {
		cnt++;
		
		//если элемент еще влезает в выделенную память
		if(OraHash.free_hash_area_size > 0) { 
			//уменьшаем размер доступной памяти
			OraHash.free_hash_area_size--;
			//увеличиваем счетчик числа элементов в памяти
			cnt_mem++;
		}
	} //addCnt
	
	//вывод секции на экран
	public void dump() {
		System.out.print ("[" + this.hash + "] (cnt: " + cnt + ", mem: " + cnt_mem);
		if(is_reorg) System.out.print (", reorg");
		System.out.print (") = ");
		table.dump();
	} //dump
	
} //HashEntryHolder
 
//--------------------------------------------------
 
//хэш массив
class Hash<Value> {
    
	//inital capacity  - начальная загрузка (число секций в таблице)
    private static int TABLE_SIZE;
    
    //число элементов в хэш таблице
  	public int cnt;
  	
  	//число уникальных значений в хэш таблице
  	public int uniq_cnt;
  	
  	//число занятых хэшей
  	public int hash_cnt;

  	//статический массив хэш секций
  	HashEntryHolder[] holder;
    
  	//поумолчанию
    public Hash() {    	
    	this(10);
    } //Hash
 
    //создание хэш таблицы _table_size размера
    public Hash(int _table_size) {
    	if(_table_size < 1) _table_size = 1;
    	
	    TABLE_SIZE = _table_size;
	    
	    //инициализация массива null значениями
	    holder = new HashEntryHolder[TABLE_SIZE];
	    Arrays.fill(holder, null);
	    
    } //Hash
    
    //хэширование ключа
    public static int getHash(int key) {
    	//остаток от деления на число секций
    	return (key % TABLE_SIZE);
    } //getHash
    
    //получить указатель на массив секций
    public HashEntryHolder[] getFullHash() {
    	return this.holder;
    } //getFullHash
 
    //получить список значений по ключу
    public ValueList get(int key) {
    	//получаем номер хэш секции
		int hash = getHash(key);
		
		//хэш секции нет - элемент не найден
		if (holder[hash] == null) {
			return null;
		} else {
			//получаем уникальный список значение в секции
		    HashEntry<ValueList> entry = holder[hash].table;
		    
		    //последовательно обходим список, пока не найдем наш ключ
		    while (entry != null && entry.key != key) {
		    	entry = entry.next;
		    }
		    //если не нашлось, то это коллизия - возвращаем пусто
		    if (entry == null) {
		    	return null;
		    } else {
		    	//нашлось - возвращаем список повторяющихся значений
		    	return entry.value;
		    }
		}
    } //get
 
    //поместим пару ключ-значение в хэш массив
    public void put(int key, Value value) {
      //получаем номер хэш секции
	  int hash = getHash(key);
	  
	  //если секция пустая
	  if (holder[hash] == null) {
		  //инициализируем объектом секции
		  holder[hash] = new HashEntryHolder(key, value, hash);
		  //увеличиваем счетчик занятых секций
          hash_cnt++;
          
          //увеличиваем оставшиеся значения: общее число, уникальное число и число в памяти
          uniq_cnt++;
          holder[hash].addCnt();
		} else {
			//нужная секция уже есть
			HashEntry entry = holder[hash].table;
			
			//обходим список уникальных значений для поиска нашего ключа
			while (entry.next != null && entry.key != key) {
			    entry = entry.next;
			}
			
			//если есть, то добавляем значение в список повторяющися значений
			if (entry.key == key) {
			    entry.add(value);
			} else {
				//иначе создаем новое уникальное значение
				entry.next = new HashEntry(key, value);
				//увеличиваем счетчики
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
    	
    	//пропорционально заполненности
    	double probes = getLoadFactor();
    	
    	if(probes > cnt || probes <= 0) {
    		return cnt;
    	}
    	
    	return probes;
    } //getAvgProbeCnt
    
    //кол-во хэш секций
    public int getTblSize() {
    	return TABLE_SIZE;
    } //getTblSize
    
    //вывод хэш массива на экран поумолчанию
    public void dump() {
    	dump(true);
    } //dump
    
  //вывод хэш массива на экран с дополнительной информацией
    public void dump(boolean add_total) {
    	
    	//обходим секции
    	for(int i = 0; i < TABLE_SIZE; i++) {
			if(holder[i] != null) {
				//если не пустая, то выводим
				holder[i].dump();
				System.out.println(" ");
			}
		}
    	
    	
    	//общая информация о хэш массиве
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


//Oracle реализация хэш массива
class OraHash<Value> extends Hash<Value> {
	
	//битовая карта - принадлежность элемента к хэш массиву без запроса к самой таблице
	private boolean bitmap[];
	
	//кол-во элементов в битовом массиве
	public static int bitmap_size = 30;
	
	//размер памяти под хэширование
	public static int hash_area_size;
	
	//размер свободной памяти под хэширование
	public static int free_hash_area_size;
	
	//создание хэш массива, с выделением памяти размером _hash_area_size
    public OraHash(int _hash_area_size) {
    	
    	//инициализруем хэш массив размеро 0,8 от доступной памяти
    	super((int) ( _hash_area_size * 0.8 ));
    	
    	hash_area_size = _hash_area_size;
    	free_hash_area_size = hash_area_size;
    	
    	//инициализируем битовый массив
    	bitmap = new boolean[bitmap_size];
    	Arrays.fill(bitmap, false);
    } //OraHash
    
    //хэширование ключа для битовой карты
    public static int getBitmapHash(int key) {
    	//остаток от деления по числу элементов в битовой карте
    	return (key % bitmap_size);
    } //getHash
    
    //поместить пару ключ-значение в хэш массив
    public void put(int key, Value value) {
    	//помещаем в хэш массив
    	super.put(key, value);
    	
    	//проставляем флаг занятости в битовой карте
    	bitmap[getBitmapHash(key)] = true;
    } //put
    
    
    //получить список значений по ключу
    public ValueList get(int key) {
    	
    	//быстрая дополнительная проверка по битовой карте, без обращения к хэш таблице
    	int hash = getBitmapHash(key);
    	
    	if(!bitmap[getBitmapHash(key)]) return null;
    	
    	return super.get(key);
    } //get
    
    
    //реорганизация хэш секций в памяти, с целью поместить как можно больше целых секций в памяти
    public void reorg() {
    	
    	//1 цикл - ищем секцию максимально размещенную в памяти
    	int hash_max = -1;
    	int elems_in_mem = 0;
    	for(int i = 0; i < this.holder.length; i++) {
    		if( this.holder[i] != null && //есть секция
    				this.holder[i].cnt_mem > elems_in_mem && //элементов в памяти секции больше, чем в предыдущем кандидате 
    				this.holder[i].cnt_mem < this.holder[i].cnt &&  //не все элементы в памяти
    				!this.holder[i].is_reorg && //секция еще не была просмотрена
    				this.holder[i].cnt <= hash_area_size //число элементов меньше выделенной памяти
    				) {
    			elems_in_mem = this.holder[i].cnt_mem;
    			hash_max = i; //новый кандидат для реоганизации
    		}
    	}
    	//кандидат для реорганизации не нашелся
    	if(hash_max < 0) return;
    	
    	//System.out.println ( "max free mem hash = " + hash_max);
    	
    	//помечаем секцию реорганизованной
    	this.holder[hash_max].is_reorg = true;
    	
    	
    	//2 цикл - выгружаем другие секции из памяти и загружаем элементы в память целевой секции
    	for(int i = 0; i < this.holder.length; i++) {
    		if( i != hash_max && //просматриваемая секция не кандидат
    				this.holder[i] != null &&  //секция существует
    				this.holder[i].cnt_mem > 0) { //у секции есть элементы в памяти
    			
    			//определяем число элементов целевой секции на диске
    			int elems_in_disk = this.holder[hash_max].cnt - this.holder[hash_max].cnt_mem;
    			
    			if(elems_in_disk <= 0) break;
    			
    			//если число элементов на диске, больше доступного числа элементов в памяти у донора
    			if(elems_in_disk > this.holder[i].cnt_mem) {
    				//то максимум что можно забрать = число элементов в памяти донора
    				elems_in_disk = this.holder[i].cnt_mem;
    			}
    			
    			//выгружаем элементы из памяти донора
				this.holder[i].cnt_mem = this.holder[i].cnt_mem - elems_in_disk;
				
				//загружаем элементы в память у целевой секции
				this.holder[hash_max].cnt_mem = this.holder[hash_max].cnt_mem + elems_in_disk;
				
    		} //if
    	}
    	
    	//рекурсивно повторяем для других секции, возможно есть еще секции которые можно разместить целиком в памяти
    	reorg();
    	
    } //reorg
    
    
    //вывод Oracle хэш массива на экран
    public void dump(boolean add_total) {
    	super.dump(add_total);
    	
    	//с дополнительной информацией о размере хэш ареи и битового массива
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
	   
       //Oracle хэш массив с выделением памяти под 13 элементов, хэш массив создается на 10
       OraHash h = new OraHash(13);
       //записываем 50 случайных пар
	   for(int i = 0; i < 50; i++) {
	       h.put(ThreadLocalRandom.current().nextInt(0, 50) , "r." + i);
	       //h.put(i , "r." + i);
	   }
	   //реорганизуем секции в памяти
	   h.reorg();
	   h.dump();
                   
    } //main
 
}
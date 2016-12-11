package Hash;

import java.util.concurrent.ThreadLocalRandom;

public class BloomFilter {

  //long переменная в 64бита под битовый массив
  private long data;
  
  //кол-во хэш функций
  private int hash_nums;
  
  //масска для проверки элемента к массиву
  public long hashMask;
  
  //битов в битовой карте = числу битов в long
  private int bit_array_size = Long.SIZE;
  
  //примесь для слуайного хэширования
  private int seed = ThreadLocalRandom.current().nextInt(1, bit_array_size);
  
  //счетчик числа элементов в блумфильтре
  private int cnt = 0;
  
  //вывод доп. информации во время работы
  public boolean debug = true;

  //задаем число бит в битовой карте, и кол-во хэш функций
  BloomFilter(int num_bits, int num_hashs) {
	  
	//кол-во хэш функций
    this.hash_nums = num_hashs;
    
    //битовая масска из единиц = размеру числу бит
    this.hashMask = (long)( 1L << num_bits ) -1 ;
    
    //битовая карта
    this.data = 0L;
    
    //размер битового массива
    this.bit_array_size = num_bits;
  } //BloomFilter

  //хэшировани = номер бита в битовом массиве
  public long hashCode(String s, int hash_num) {
    long result = 1;
    
    //для каждого байта в строке
    for (int i = 0; i < s.length(); ++i) {
      //применяем хэш функцию под номером hash_num и обрезаем по маске
    	
	  //простая хэш функция = ascii значение буквы * примесь * номер функции * хэш от предыдущей функции & обрезка по маске
      //1 = (1 * 1 + 58)
      //1 = ( 0001 * 0001 + 11 0001 ) & 1111 1111 1111 1111 
      result = ((hash_num + seed) * result + s.charAt(i)) & this.hashMask;
    }
    
    //номер бита ограничим размером битовой маски
    return result % bit_array_size;
  }
  
  //установить index бит в битовой карте
  public void setBit(long index) {
	  //= битовая карта OR 1 смещенное влево на index
	  this.data = this.data | (1L << index );
  } //setbit
  
  //получить значение бита на index месте
  public long getBit(long index) {
	  //=битовая карта смещенная вправо на index мест (>>> пустые места справа заполняются 0)
	  // & 01 - проверка только крайнего левого бита (все остальные игнорируются)
	  return ( this.data >>> index ) & 1;
  } //getBit
  
  //добавить элемент в блум фильтр
  public void add(String s) {
	//++ счетчик элементов
	cnt++;
	//для каждой хэш функции
	for(int i = 1; i <= hash_nums; i++) {
	  //расчитаем номер индекса в битовой карте и установим его
	  long index = hashCode(s, i);
	  setBit(index);
	  
	  //дебаг вывод
	  if(debug) {
		  if(i == 1) System.out.println(s + " ("+cnt+")");
		  System.out.print(i+" ) [" + index + "] ");
		  dump(false);
	  }
	}
  } //add
  
  //проверка наличия элемента в блум фильтре
  public boolean test(String s) {
	//для каждой хэш функции
    for(int i = 1; i <= hash_nums; i++) {
      //определяем номер бита в битовой карте
      long index = hashCode(s, i);
      
      //если хотябы одна проверка не прошла - элемента нет
      if( getBit(index) == 0L ) return false;
    }
    
    //иначе элемент вероятно есть
    return true;
  } //test
  
  public void dump() {
	  dump(true);
  }
  
  private void dump(boolean show_total) {
	  System.out.println(this.data + " = " + Long.toBinaryString(this.data | (this.hashMask + 1)).substring(1));
	  
	  if(show_total) {
		  System.out.println("getFalsePossb = " + getFalsePossb());
		  System.out.println("getOptimalFncCnt = " + getOptimalFncCnt());
	  }
  } //dump
  
  //вероятность ложного срабатывания
  public double getFalsePossb() {
	  if(cnt == 0) return 0;
	  return 1 / Math.pow(Math.E, bit_array_size * Math.log(2) * Math.log(2) / cnt );
  } //getFalsePossb
  
  //оптимальное число функций хэширования
  public int getOptimalFncCnt() {
	  if(cnt == 0) return 1;
	  return (int)Math.ceil( bit_array_size / cnt * Math.log(2) );
  } //getOptimalFncCnt

  public static void main(String[] args) {
    /*BloomFilter bf = new BloomFilter(63, 3);
    
    String[] arr = new String[100];
    for(int i = 0; i < 5; i++) {
    	arr[i] = "a" + i;
    	bf.add(arr[i]);
    }
    
    for(int i = 0; i < arr.length; i++) {
    	if(arr[i] != null) {
	    	arr[i] = "a" + i;
	    	System.out.println(arr[i] + "=" + bf.test(arr[i]));
    	}
    }
    System.out.println("b0=" + bf.test("b0"));
    bf.dump();*/
	  
  	for(int i = 0; i <= 50; i++) {
		BloomFilter bf = new BloomFilter(ThreadLocalRandom.current().nextInt(1, 63), ThreadLocalRandom.current().nextInt(1, 10));
		bf.debug = false;
		
		int elems = ThreadLocalRandom.current().nextInt(1, 100);
		String[] arr = new String[elems];
		
 	    for(int j = 0; j < elems; j++) {
 	    	arr[i] = "a" + i;
 	      	bf.add(arr[i]);
 	    }
		
		bf = null;
	}
    
    
  } //main

}

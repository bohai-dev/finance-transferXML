package com.bohai.finance;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        
        for(int i=1 ; i<=9 ;i++){
            System.out.println();
            for(int j = i ; j<= 9 ;j++){
                System.out.print(i+"*"+j +"="+i*j+"  ");
            }
        }
        
    }
    
    
    public void printSysEnv(){
        Set<Entry<Object, Object>> entrySet = System.getProperties().entrySet();
        Iterator<Entry<Object, Object>> iterator = entrySet.iterator();  
        while( iterator.hasNext() ){  
            System.err.println(iterator.next().toString());  
        }  
    }
    
    
}

package com.bohai.finance;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        
        Set<Entry<Object, Object>> entrySet = System.getProperties().entrySet();
        Iterator<Entry<Object, Object>> iterator = entrySet.iterator();  
        while( iterator.hasNext() ){  
            System.err.println(iterator.next().toString());  
        }  
        
    }
}

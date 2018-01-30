package com.bohai.finance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BigdeciamlTest {
	
	public static void main(String[] args) {
		
		
		Map<String, BigDecimal> map = new HashMap<>();
		
		map.put("b", new BigDecimal("0"));
		
		BigDecimal bg = map.get("b");
		bg = bg.add(new BigDecimal("100"));
		
		System.out.println(bg);
		System.out.println(map.get("b"));
		
	}

}

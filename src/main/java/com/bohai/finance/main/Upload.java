package com.bohai.finance.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class Upload {

    public static void main(String[] args) throws Exception {
        
        URL url = new URL("http://12.65.178.131:3390/service/XChangeServlet?account=01&groupcode=00");
        //URL url = new URL("http://101.95.0.114:8090/bohai-dataCenter");
        
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        
        BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
        
        
        InputStream inputStream = new FileInputStream(new File("C:\\Users\\BHQH-CXYWB\\Desktop\\2017-12-12凭证.xml"));
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
                
        byte[] bs = document.asXML().getBytes();
       
        out.write(bs);
       
        out.close();
       
        //获取服务器返回的信息
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
       
        byte[] b = new byte[in.available()];
       
        String s = "";
       
        //in.read(b);
        
        int tmp = 0;
        StringBuffer sb = new StringBuffer();
        while((tmp=in.read(b))!=-1){
            sb.append(new String(b,0,tmp));
            System.out.println("每次读取字节数量:"+tmp);
            System.out.println("文件中剩余字节数:"+in.available());
         }
        System.out.println(sb.toString());
        
    }
}

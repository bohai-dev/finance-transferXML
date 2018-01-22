package com.bohai.finance.main;

import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.bohai.finance.util.DataConfig;

/**
 * 初始化数据
 * @author BHQH-CXYWB
 *
 */
public class Init {
    
    
    public static void main(String[] args) throws IOException {
        
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");
        
        root.addElement("建设银行")
            .addAttribute("bankName", "建设银行")
            .addAttribute("subjectCode", "10020101")
            .addAttribute("subjectName", "建设银行")
            .addAttribute(DataConfig.BANK_ACCOUNT_NO, "21201504000050000508");
            
        
        root.addElement("工商银行")
        .addAttribute(DataConfig.BANK_BANK_NAME, "工商银行")
        .addAttribute(DataConfig.BANK_SUBJECT_CODE, "10020102")
        .addAttribute(DataConfig.BANK_SUBJECT_NAME, "工商银行")
        .addAttribute(DataConfig.BANK_ACCOUNT_NO, "1001358129004606933");
        
        root.addElement("交通银行")
        .addAttribute("bankName", "交通银行")
        .addAttribute("subjectCode", "10020103")
        .addAttribute("subjectName", "交通银行")
        .addAttribute("accountNo", "212060120018000486088");
        
        root.addElement("中国银行")
        .addAttribute("bankName", "中国银行")
        .addAttribute("subjectCode", "10020104")
        .addAttribute("subjectName", "中国银行")
        .addAttribute("accountNo", "292156326670");
        
        root.addElement("农业银行")
        .addAttribute("bankName", "农业银行")
        .addAttribute("subjectCode", "10020105")
        .addAttribute("subjectName", "农业银行")
        .addAttribute("accountNo", "34646001040001362");
        
        root.addElement("民生银行")
        .addAttribute("bankName", "民生银行")
        .addAttribute("subjectCode", "10020106")
        .addAttribute("subjectName", "民生银行")
        .addAttribute("accountNo", "627002671");
        
        root.addElement("中信银行")
        .addAttribute("bankName", "中信银行")
        .addAttribute("subjectCode", "10020107")
        .addAttribute("subjectName", "中信银行")
        .addAttribute("accountNo", "7201110187000000351");
        
        root.addElement("招商银行")
        .addAttribute("bankName", "招商银行")
        .addAttribute("subjectCode", "10020108")
        .addAttribute("subjectName", "招商银行")
        .addAttribute("accountNo", "955109019910126");
        
        root.addElement("兴业银行")
        .addAttribute("bankName", "兴业银行")
        .addAttribute("subjectCode", "10020109")
        .addAttribute("subjectName", "兴业银行")
        .addAttribute("accountNo", "532110113461002015");
            
        root.addElement("浦发银行")
        .addAttribute("bankName", "浦发银行")
        .addAttribute("subjectCode", "10020110")
        .addAttribute("subjectName", "浦发银行")
        .addAttribute("accountNo", "752001538070020010");
        
        root.addElement("光大银行")
        .addAttribute("bankName", "光大银行")
        .addAttribute("subjectCode", "10020111")
        .addAttribute("subjectName", "光大银行")
        .addAttribute("accountNo", "358301880000334210020");
        
        root.addElement("平安银行")
        .addAttribute("bankName", "平安银行")
        .addAttribute("subjectCode", "10020112")
        .addAttribute("subjectName", "平安银行")
        .addAttribute("accountNo", "19900002600020");
        
      //设置文件编码  
        OutputFormat xmlFormat = new OutputFormat();  
        xmlFormat.setEncoding("UTF-8"); 
        // 设置换行 
        xmlFormat.setNewlines(true); 
        // 生成缩进 
        xmlFormat.setIndent(true); 
        // 使用4个空格进行缩进, 可以兼容文本编辑器 
        xmlFormat.setIndent("    "); 

        // lets write to a file
        try (FileWriter fileWiter = new FileWriter(DataConfig.BANK_DATA_URL)) {
            XMLWriter writer = new XMLWriter(fileWiter,xmlFormat);
            writer.write(document);
            writer.close();
        }
        
        Document deptDocument = DocumentHelper.createDocument();
        Element deptRoot = deptDocument.addElement("root");
        
        deptRoot.addElement("大连分公司")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "01-大连分公司")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "31")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "31-0002");
        
        deptRoot.addElement("长春营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "02-长春营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "35")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "35-0002");
        
        deptRoot.addElement("北京营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "03-北京营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "34")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "34-0002");
        
        deptRoot.addElement("郑州营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "05-郑州营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "46")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "46-0002");
        
        deptRoot.addElement("上海营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "06-上海营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "43")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "43-0002");
        
        
        deptRoot.addElement("重庆营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "08-重庆营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "37")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "37-0002");
        
        deptRoot.addElement("广州营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "09-广州营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "39")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "39-0002");
        
        deptRoot.addElement("福州营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "10-福州营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "38")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "38-0002");
        
        deptRoot.addElement("营销总部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "11-营销总部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "2006") //应付货币保证金
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "应付货币保证金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "00")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "00-0002");
        
        deptRoot.addElement("沈阳营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "12-沈阳营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "45")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "45-0002");
        
        deptRoot.addElement("创新业务部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "15-创新业务部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "AA")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "AA-0002");
        
        deptRoot.addElement("营销一部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "18-营销一部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "2006")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "应付货币保证金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "00")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "00-0002");
        
        deptRoot.addElement("济南营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "21-济南营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "41")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "41-0002");
        
        deptRoot.addElement("营销四部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "26-营销四部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "2006")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "应付货币保证金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "00")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "00-0002");
        
        deptRoot.addElement("营销五部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "28-营销五部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "2006")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "应付货币保证金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "00")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "00-0002");
        
        deptRoot.addElement("北方事业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "29-北方事业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "47")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "47-0002");
        
        deptRoot.addElement("成都营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "31-成都营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "36")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "36-0002");
        
        deptRoot.addElement("深圳营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "32-深圳营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "44")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "44-0002");
        
        deptRoot.addElement("杭州营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "36-杭州营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "40")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "40-0002");
        
        deptRoot.addElement("厦门营业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "37-厦门营业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "42")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "42-0002");
        
        deptRoot.addElement("华东事业部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "39-华东事业部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "48")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "48-0002");
        
        deptRoot.addElement("资产管理业务部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "16-资产管理业务部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "51")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "51-0002");
        
        deptRoot.addElement("华东事业二部")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "40-华东事业二部")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "48")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "48-0002");
        
        deptRoot.addElement("江苏分公司")
        .addAttribute(DataConfig.DEPT_DEPT_NAME, "41-江苏分公司")
        .addAttribute(DataConfig.DEPT_SUBJECT_CODE, "22410101")
        .addAttribute(DataConfig.DEPT_SUBJECT_NAME, "自有资金")
        .addAttribute(DataConfig.DEPT_ASS_CODE, "0005")
        .addAttribute(DataConfig.DEPT_ASS_VALUE, "32")
        .addAttribute(DataConfig.DEPT_BOOK_NO, "32-0002");
        

        // lets write to a file
        try (FileWriter fileWiter = new FileWriter(DataConfig.DEPT_DATA_URL)) {
            XMLWriter writer = new XMLWriter(fileWiter,xmlFormat);
            writer.write(deptDocument);
            writer.close();
        }
    }

}

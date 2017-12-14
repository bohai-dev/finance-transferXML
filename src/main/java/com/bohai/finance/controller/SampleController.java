package com.bohai.finance.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.bohai.finance.model.Bank;
import com.bohai.finance.service.VoucherService;
import com.bohai.finance.util.ApplicationConfig;
import com.bohai.finance.util.DateFormatterUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SampleController implements Initializable{
    
    static Logger logger = Logger.getLogger(SampleController.class);

    @FXML
    private Button fileButton;

    @FXML
    private TextField textField;
    
    @FXML
    private Button transferButton;
    
    @FXML
    private TextArea textArea;
    
    @FXML
    private MenuItem bankItem;
    
    @FXML
    private MenuItem deptItem;
    
    @FXML
    private TabPane tabPane;
    
    
    
    
    @FXML
    private DatePicker beginDate;
    
    @FXML
    private DatePicker endDate;
    
    /**
     * 营业部文件
     */
    @FXML
    private Button businessFileButton;
    
    @FXML
    private TextField businessTextField;
    
    @FXML
    private Button businessGenerateButton;
    
    
    private File businessFile;
    
    private File file;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }

    /**
     * 点击按钮选择文件
     * @param event
     */
    @FXML
    public void chooseFile(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择文件");
        //读取上次目录
        String lastUploadDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY);
        
        if(lastUploadDirectory != null){
            chooser.setInitialDirectory(new File(lastUploadDirectory));
        }
        file = chooser.showOpenDialog(new Stage());
        if(file != null) {
            textField.setText(file.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, file.getParent());
        }else {
            textField.setText("");
        }
    }
    
    
    /**
     * 点击按钮选择营业部文件
     * @param event
     */
    @FXML
    public void chooseBusinessFile(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择文件");
        //读取上次目录
        String lastUploadDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY);
        
        if(lastUploadDirectory != null){
            chooser.setInitialDirectory(new File(lastUploadDirectory));
        }
        businessFile = chooser.showOpenDialog(new Stage());
        if(businessFile != null) {
            businessTextField.setText(businessFile.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, businessFile.getParent());
        }else {
            businessTextField.setText("");
        }
    }
    
    
    
    /**
     * 生成凭证
     * @param event
     */
    @FXML
    public void generateVoucher(ActionEvent event){
        
        if(file == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd")+"凭证.xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = chooser.showSaveDialog(new Stage());
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                VoucherService voucherService = new VoucherService();
                try {
                    Map<String, Bank> map = voucherService.generateXML(file, file1.getAbsolutePath());
                    
                    BigDecimal totalIn = new BigDecimal("0");
                    BigDecimal totalOut = new BigDecimal("0");
                    if(map != null){
                        for (Entry<String, Bank> m :map.entrySet())  {
                            Bank bank = m.getValue();
                            textArea.appendText(bank.getBankName()+" 入金："+bank.getIn()+" ,出金："+bank.getOut()+"\n");
                            totalIn = totalIn.add(bank.getIn());
                            totalOut = totalOut.add(bank.getOut());
                        }
                        textArea.appendText("总计入金："+totalIn+" ,出金："+totalOut+"\n");
                    }
                    
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("生成总部凭证失败"+e);
                    textArea.appendText("生成总部凭证失败："+e.getMessage()+"\n");
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成总部凭证失败！");
                    warning.showAndWait();
                }
            }
        }
    }
    
    /**
     * 按月生成营业部凭证
     * @param event
     */
    @FXML
    public void generateBusinessVoucher(ActionEvent event){
        
        if(businessFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd")+"凭证.xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = chooser.showSaveDialog(new Stage());
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                VoucherService voucherService = new VoucherService();
                try {
                    
                    voucherService.generateBusinessXML(businessFile, file1.getAbsolutePath());
                    
                    
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("生成营业部凭证文件失败"+e);
                    textArea.appendText("生成营业部凭证文件失败："+e.getMessage()+"\n");
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成营业部凭证文件失败！");
                    warning.showAndWait();
                }
            }
        }
    }
    
    
    @FXML
    public void createBankTab() throws IOException{
        
        List<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if(tab.getText().equals("银行账户")){
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        
        Tab child = (Tab) FXMLLoader.load(getClass().getResource("/com/bohai/finance/view/BankTab.fxml"));
        tabPane.getTabs().add(child);
        tabPane.getSelectionModel().select(child);
    }
    
    @FXML
    public void createDeptTab() throws IOException{
        
        List<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if(tab.getText().equals("营业部")){
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        
        Tab child = (Tab) FXMLLoader.load(getClass().getResource("/com/bohai/finance/view/BusinessDeptTab.fxml"));
        tabPane.getTabs().add(child);
        tabPane.getSelectionModel().select(child);
    }
    
}

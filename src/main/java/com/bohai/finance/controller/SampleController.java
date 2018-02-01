package com.bohai.finance.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.bohai.finance.model.Bank;
import com.bohai.finance.service.CFFEXDeclareService;
import com.bohai.finance.service.ChargeLossService;
import com.bohai.finance.service.ChargeVoucherService;
import com.bohai.finance.service.InvestorService;
import com.bohai.finance.service.ProfitVoucherService;
import com.bohai.finance.service.RoyaltyService;
import com.bohai.finance.service.VoucherService;
import com.bohai.finance.util.ApplicationConfig;

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
    private DatePicker headBeginDate;
    
    @FXML
    private DatePicker headEndDate;
    
    
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
    
    /**
     * 手续费凭证kongjian
     */
    @FXML
    private DatePicker chargeBeginDate;
    
    @FXML
    private DatePicker chargeEndDate;
    
    @FXML
    private TextField chargeTextField;
    
    @FXML
    private Button chargeFileButton;
    
    @FXML
    private Button chargeGenerateButton;
    
    private File chargeFile;
    
    /**
     * 盈亏凭证控件
     */
    @FXML
    private DatePicker profitBeginDate;
    
    @FXML
    private DatePicker profitEndDate;
    
    @FXML
    private TextField profitTextField;
    
    @FXML
    private Button profitFileButton;
    
    @FXML
    private Button profitGenerateButton;
    
    private File profitFile;
    
    /**
     * 其他盈亏
     */
    @FXML
    private Button otherGenerateButton;
    
    /**
     * 中金所申报费凭证控件
     */
    @FXML
    private DatePicker declareBeginDate;
    
    @FXML
    private DatePicker declareEndDate;
    
    @FXML
    private TextField declareTextField;
    
    @FXML
    private Button declareFileButton;
    
    @FXML
    private Button declareGenerateButton;
    
    @FXML
    private TextField investorTextField;
    
    @FXML
    private Button investorFileButton;
    
    private File declareFile;
    
    private File investorFile;
    
    /**
     * 盈亏凭证控件
     */
    @FXML
    private DatePicker royaltyBeginDate;
    
    @FXML
    private DatePicker royaltyEndDate;
    
    @FXML
    private TextField royaltyTextField;
    
    @FXML
    private Button royaltyFileButton;
    
    @FXML
    private Button royaltyGenerateButton;
    
    private File royaltyFile;
    
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
        
        try {
            file = chooser.showOpenDialog(new Stage());
        } catch (Exception e) {
            //由于不同操作系统，文件路径格式不一致
            logger.error("打开目录失败",e);
            chooser.setInitialDirectory(null);
            file = chooser.showOpenDialog(new Stage());
        }
        
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
        
        try {
            
            businessFile = chooser.showOpenDialog(new Stage());
        } catch (Exception e) {
            //由于不同操作系统，文件路径格式不一致
            logger.error("打开目录失败",e);
            chooser.setInitialDirectory(null);
            businessFile = chooser.showOpenDialog(new Stage());
        }
        
        if(businessFile != null) {
            businessTextField.setText(businessFile.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, businessFile.getParent());
        }else {
            businessTextField.setText("");
        }
    }
    
    
    /**
     * 点击按钮选择手续费文件
     * @param event
     */
    @FXML
    public void chooseChargeFile(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择手续费文件");
        //读取上次目录
        String lastUploadDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY);
        
        if(lastUploadDirectory != null){
            chooser.setInitialDirectory(new File(lastUploadDirectory));
        }
        
        try {
            
            chargeFile = chooser.showOpenDialog(new Stage());
        } catch (Exception e) {
            //由于不同操作系统，文件路径格式不一致
            logger.error("打开目录失败",e);
            chooser.setInitialDirectory(null);
            chargeFile = chooser.showOpenDialog(new Stage());
        }
        
        if(chargeFile != null) {
            chargeTextField.setText(chargeFile.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, chargeFile.getParent());
        }else {
            chargeTextField.setText("");
        }
    }
    
    
    /**
     * 点击按钮选择盈亏文件
     * @param event
     */
    @FXML
    public void chooseProfitFile(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择手续费文件");
        //读取上次目录
        String lastUploadDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY);
        
        if(lastUploadDirectory != null){
            chooser.setInitialDirectory(new File(lastUploadDirectory));
        }
        
        try {
            
            profitFile = chooser.showOpenDialog(new Stage());
        } catch (Exception e) {
            //由于不同操作系统，文件路径格式不一致
            logger.error("打开目录失败",e);
            chooser.setInitialDirectory(null);
            profitFile = chooser.showOpenDialog(new Stage());
        }
        
        if(profitFile != null) {
            profitTextField.setText(profitFile.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, profitFile.getParent());
        }else {
            profitTextField.setText("");
        }
    }
    
    /**
     * 点击按钮选择盈亏文件
     * @param event
     */
    @FXML
    public void chooseInvestorFile(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择投资者文件");
        //读取上次目录
        String lastUploadDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY);
        
        if(lastUploadDirectory != null){
            chooser.setInitialDirectory(new File(lastUploadDirectory));
        }
        
        try {
            
            investorFile = chooser.showOpenDialog(new Stage());
        } catch (Exception e) {
            //由于不同操作系统，文件路径格式不一致
            logger.error("打开目录失败",e);
            chooser.setInitialDirectory(null);
            investorFile = chooser.showOpenDialog(new Stage());
        }
        
        if(investorFile != null) {
            investorTextField.setText(investorFile.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, investorFile.getParent());
        }else {
            investorTextField.setText("");
        }
    }
    
    /**
     * 点击按钮选择分项资金文件
     * @param event
     */
    @FXML
    public void chooseDeclareFile(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择手续费文件");
        //读取上次目录
        String lastUploadDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY);
        
        if(lastUploadDirectory != null){
            chooser.setInitialDirectory(new File(lastUploadDirectory));
        }
        
        try {
            
            declareFile = chooser.showOpenDialog(new Stage());
        } catch (Exception e) {
            //由于不同操作系统，文件路径格式不一致
            logger.error("打开目录失败",e);
            chooser.setInitialDirectory(null);
            declareFile = chooser.showOpenDialog(new Stage());
        }
        
        if(declareFile != null) {
            declareTextField.setText(declareFile.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, declareFile.getParent());
        }else {
            declareTextField.setText("");
        }
    }
    
    /**
     * 点击按钮选择权利金收支文件
     * @param event
     */
    @FXML
    public void chooseRoyaltyFile(ActionEvent event) {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择手续费文件");
        //读取上次目录
        String lastUploadDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY);
        
        if(lastUploadDirectory != null){
            chooser.setInitialDirectory(new File(lastUploadDirectory));
        }
        
        try {
            
            royaltyFile = chooser.showOpenDialog(new Stage());
        } catch (Exception e) {
            //由于不同操作系统，文件路径格式不一致
            logger.error("打开目录失败",e);
            chooser.setInitialDirectory(null);
            royaltyFile = chooser.showOpenDialog(new Stage());
        }
        
        if(royaltyFile != null) {
            royaltyTextField.setText(royaltyFile.getAbsolutePath());
            //缓存本次上传目录
            ApplicationConfig.setProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY, royaltyFile.getParent());
        }else {
            royaltyTextField.setText("");
        }
    }
    
    /**
     * 生成凭证
     * @param event
     */
    @FXML
    public void generateVoucher(ActionEvent event){
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(headBeginDate.getValue() == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择起始日期！");
            warning.showAndWait();
        }else if (headEndDate.getValue() == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择结束日期！");
            warning.showAndWait();
        }else if(file == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            String date = headBeginDate.getValue().format(dateTimeFormatter)+"-"+headEndDate.getValue().format(dateTimeFormatter);
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName("总部出入金凭证"+date+".xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = null;
            try {
                file1 = chooser.showSaveDialog(new Stage());
            } catch (Exception e1) {
                logger.error("打开目录失败",e1);
                chooser.setInitialDirectory(null);
                file1 = chooser.showSaveDialog(new Stage());
            }
            
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                VoucherService voucherService = new VoucherService();
                try {
                    
                    Map<String, Bank> map = voucherService.generateXML(file, file1.getAbsolutePath(),date);
                    
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
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(beginDate.getValue() == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择起始日期！");
            warning.showAndWait();
        }else if (endDate.getValue() == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择结束日期！");
            warning.showAndWait();
        }else if(businessFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            String date = beginDate.getValue().format(dateTimeFormatter)+"-"+endDate.getValue().format(dateTimeFormatter);
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName("营业部出入金凭证"+date+".xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = null;
            try {
                file1 = chooser.showSaveDialog(new Stage());
            } catch (Exception e1) {
                logger.error("打开目录失败",e1);
                chooser.setInitialDirectory(null);
                file1 = chooser.showSaveDialog(new Stage());
            }
            
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                VoucherService voucherService = new VoucherService();
                try {
                    
                    voucherService.generateBusinessXML(businessFile, file1.getAbsolutePath(),date);
                    
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
    
    
    /**
     * 生成手续费凭证
     * @param event
     */
    @FXML
    public void generateChargeVoucher(ActionEvent event){
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(chargeBeginDate.getValue() == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择起始日期！");
            warning.showAndWait();
        }else if (chargeEndDate.getValue() == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择结束日期！");
            warning.showAndWait();
        }else if(chargeFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            String date = chargeBeginDate.getValue().format(dateTimeFormatter)+"-"+chargeEndDate.getValue().format(dateTimeFormatter);
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName("手续费凭证"+date+".xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = null;
            try {
                file1 = chooser.showSaveDialog(new Stage());
            } catch (Exception e1) {
                logger.error("打开目录失败",e1);
                chooser.setInitialDirectory(null);
                file1 = chooser.showSaveDialog(new Stage());
            }
            
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                ChargeVoucherService chargeVoucherService = new ChargeVoucherService();
                try {
                    
                    
                    chargeVoucherService.generateVoucher(chargeFile, file1.getAbsolutePath(),date);
                    
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("生成手续费凭证文件失败"+e);
                    textArea.appendText("生成手续费凭证文件失败："+e.getMessage()+"\n");
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成手续费凭证文件失败！");
                    warning.showAndWait();
                }
            }
        }
    }
    
    
    /**
     * 生成盈亏凭证
     * @param event
     */
    @FXML
    public void generateProfitVoucher(ActionEvent event){
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(profitBeginDate.getValue() == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择起始日期！");
            warning.showAndWait();
        }else if (profitEndDate.getValue() == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择结束日期！");
            warning.showAndWait();
        }else if(profitFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            String date = profitBeginDate.getValue().format(dateTimeFormatter)+"-"+profitEndDate.getValue().format(dateTimeFormatter);
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName("盈亏凭证"+date+".xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = null;
            try {
                file1 = chooser.showSaveDialog(new Stage());
            } catch (Exception e1) {
                logger.error("打开目录失败",e1);
                chooser.setInitialDirectory(null);
                file1 = chooser.showSaveDialog(new Stage());
            }
            
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                ProfitVoucherService profitVoucherService = new ProfitVoucherService();
                try {
                    profitVoucherService.generateVoucher(profitFile, file1.getAbsolutePath(),date);
                    
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("生成盈亏凭证文件失败"+e);
                    textArea.appendText("生成盈亏凭证文件失败："+e.getMessage()+"\n");
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成盈亏凭证文件失败！");
                    warning.showAndWait();
                }
            }
        }
    }
    
    /**
     * 生成其他凭证
     * @param event
     */
    @FXML
    public void generateOtherVoucher(ActionEvent event){
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(profitBeginDate.getValue() == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择起始日期！");
            warning.showAndWait();
        }else if (profitEndDate.getValue() == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择结束日期！");
            warning.showAndWait();
        }else if(profitFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            String date = profitBeginDate.getValue().format(dateTimeFormatter)+"-"+profitEndDate.getValue().format(dateTimeFormatter);
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName("其他客户盈亏手续费凭证"+date+".xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = null;
            try {
                file1 = chooser.showSaveDialog(new Stage());
            } catch (Exception e1) {
                logger.error("打开目录失败",e1);
                chooser.setInitialDirectory(null);
                file1 = chooser.showSaveDialog(new Stage());
            }
            
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                //ProfitVoucherService profitVoucherService = new ProfitVoucherService();
                ChargeLossService test = new ChargeLossService();
                try {
                    
                    //profitVoucherService.generateVoucher(profitFile, file1.getAbsolutePath(),date);
                    test.chargeLoss(profitFile, file1.getAbsolutePath(),date);
                    
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("生成其他盈亏凭证文件失败"+e);
                    textArea.appendText("生成其他盈亏凭证文件失败："+e.getMessage()+"\n");
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成其他盈亏凭证文件失败！");
                    warning.showAndWait();
                }
            }
        }
    }
    
    /**
     * 生成中金所申报费凭证
     * @param event
     */
    @FXML
    public void generateDeclareVoucher(ActionEvent event){
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(declareBeginDate.getValue() == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择起始日期！");
            warning.showAndWait();
        }else if (declareEndDate.getValue() == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择结束日期！");
            warning.showAndWait();
        }else if(investorFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择投资者文件！");
            warning.showAndWait();
        }else if(declareFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择分项资金文件！");
            warning.showAndWait();
        }else {
            
            InvestorService investorService = new InvestorService();
            try {
                investorService.saveInvestors(investorFile);
            } catch (Exception e2) {
                logger.error("保存投资者信息失败"+e2);
                textArea.appendText("保存投资者信息失败："+e2.getMessage()+"\n");
                Alert warning = new Alert(Alert.AlertType.ERROR,"保存投资者信息失败！");
                warning.showAndWait();
                return;
            }
            
            String date = declareBeginDate.getValue().format(dateTimeFormatter)+"-"+declareEndDate.getValue().format(dateTimeFormatter);
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName("中金所申报费凭证"+date+".xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = null;
            try {
                file1 = chooser.showSaveDialog(new Stage());
            } catch (Exception e1) {
                logger.error("打开目录失败",e1);
                chooser.setInitialDirectory(null);
                file1 = chooser.showSaveDialog(new Stage());
            }
            
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                CFFEXDeclareService cffexDeclareService = new CFFEXDeclareService();
                try {
                    
                    //profitVoucherService.generateVoucher(profitFile, file1.getAbsolutePath(),date);
                    cffexDeclareService.generateVoucher(declareFile, file1.getAbsolutePath(),date);
                    
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("生成中金所申报费凭证文件失败"+e);
                    textArea.appendText("生成中金所申报费凭证文件失败："+e.getMessage()+"\n");
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成中金所申报费凭证文件失败！");
                    warning.showAndWait();
                }
            }
        }
    }
    
    /**
     * 生成其他凭证
     * @param event
     */
    @FXML
    public void generateRoyaltyVoucher(ActionEvent event){
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if(royaltyBeginDate.getValue() == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择起始日期！");
            warning.showAndWait();
        }else if (royaltyEndDate.getValue() == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择结束日期！");
            warning.showAndWait();
        }else if(royaltyFile == null){
            Alert warning = new Alert(Alert.AlertType.WARNING,"请先选择文件！");
            warning.showAndWait();
        }else {
            
            String date = royaltyBeginDate.getValue().format(dateTimeFormatter)+"-"+royaltyEndDate.getValue().format(dateTimeFormatter);
            //文件选择器
            FileChooser chooser = new FileChooser();
            chooser.setTitle("保存文件");
            chooser.setInitialFileName("权利金收支凭证"+date+".xml");
            
            //获取上次保存目录
            String lastOutDirectory = ApplicationConfig.getProperty(ApplicationConfig.LAST_OUT_DIRECTORY);
            if(lastOutDirectory != null){
                chooser.setInitialDirectory(new File(lastOutDirectory));
            }
            
            File file1 = null;
            try {
                file1 = chooser.showSaveDialog(new Stage());
            } catch (Exception e1) {
                logger.error("打开目录失败",e1);
                chooser.setInitialDirectory(null);
                file1 = chooser.showSaveDialog(new Stage());
            }
            
            if(file1 != null) {
                
                //缓存本次生成目录
                ApplicationConfig.setProperty(ApplicationConfig.LAST_OUT_DIRECTORY, file1.getParent());
                RoyaltyService service = new RoyaltyService();
                try {
                    
                    service.generateVoucher(royaltyFile, file1.getAbsolutePath(),date);
                    
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("生成权利金收支凭证文件失败"+e);
                    textArea.appendText("生成权利金收支凭证文件失败："+e.getMessage()+"\n");
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成权利金收支凭证文件失败！");
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

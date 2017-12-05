package com.bohai.finance.controller;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import com.bohai.finance.service.VoucherService;
import com.bohai.finance.util.ApplicationConfig;
import com.bohai.finance.util.DateFormatterUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SampleController implements Initializable{

    @FXML
    private Button fileButton;

    @FXML
    private TextField textField;
    
    @FXML
    private Button transferButton;
    
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
        if(ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY) != null){
            chooser.setInitialDirectory(new File(ApplicationConfig.getProperty(ApplicationConfig.LAST_UPLOAD_DIRECTORY)));
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
                    voucherService.generateXML(file, file1.getAbsolutePath());
                    Alert warning = new Alert(Alert.AlertType.INFORMATION,"生成成功！");
                    warning.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert warning = new Alert(Alert.AlertType.ERROR,"生成失败！");
                    warning.showAndWait();
                }
            }
        }
    }

    
}

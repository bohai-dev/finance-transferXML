package com.bohai.finance.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.bohai.finance.model.Bank;
import com.bohai.finance.service.BankService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class BankController implements Initializable{
    
    @FXML
    private TableView<Bank> tableView;
    
    @FXML
    private TableColumn<Bank,String> bankCol;
    
    @FXML
    private TableColumn<Bank,String> subjectCol;
    
    @FXML
    private TableColumn<Bank,String> subjectNameCol;
    
    @FXML
    private TableColumn<Bank,String> accountCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        
        try {
            BankService bankService = new BankService();
            List<Bank> bankList = null;
            
            bankList = bankService.queryBanks();
            
            ObservableList<Bank> list = FXCollections.observableArrayList(bankList);
            
            bankCol.setCellValueFactory(new PropertyValueFactory<Bank,String>("bankName"));
            bankCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            subjectCol.setCellValueFactory(new PropertyValueFactory<Bank,String>("subjectCode"));
            subjectCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            subjectNameCol.setCellValueFactory(new PropertyValueFactory<Bank,String>("subjectName"));
            subjectNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            accountCol.setCellValueFactory(new PropertyValueFactory<Bank,String>("accountNo"));
            accountCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            tableView.setItems(list);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        accountCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bank,String>>() {
            
            @Override
            public void handle(CellEditEvent<Bank, String> event) {
                
                //原值
                System.out.println("原值"+event.getOldValue());
                //新值
                System.out.println("新值"+event.getNewValue());
                
                event.getRowValue().setAccountNo(event.getNewValue());
                
                BankService bankService = new BankService();
                try {
                    bankService.updateAccountNoByBankName(event.getRowValue().getBankName(), event.getNewValue());
                } catch (Exception e) {
                    
                    e.printStackTrace();
                    //TODO
                }
                
            }
        });
    }
    

}

package com.bohai.finance.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.bohai.finance.model.BusinessDepartment;
import com.bohai.finance.service.DeptService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class BusinessDepartmentController implements Initializable{
    
    @FXML
    private TableView<BusinessDepartment> tableView;
    
    @FXML
    private TableColumn<BusinessDepartment, String> deptNameCol;
    
    @FXML
    private TableColumn<BusinessDepartment, String> subjectCodeCol;
    
    @FXML
    private TableColumn<BusinessDepartment, String> accountNameCol;
    
    @FXML
    private TableColumn<BusinessDepartment, String> assCodeCol;
    
    @FXML
    private TableColumn<BusinessDepartment, String> assValueCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        try {
            DeptService deptService = new DeptService();
            
            List<BusinessDepartment> depts = deptService.queryDepts();
            
            ObservableList<BusinessDepartment> list = FXCollections.observableArrayList(depts);
            
            deptNameCol.setCellValueFactory(new PropertyValueFactory<BusinessDepartment, String>("deptName"));
            deptNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            subjectCodeCol.setCellValueFactory(new PropertyValueFactory<BusinessDepartment, String>("subjectCode"));
            subjectCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            accountNameCol.setCellValueFactory(new PropertyValueFactory<BusinessDepartment, String>("subjectName"));
            accountNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            assCodeCol.setCellValueFactory(new PropertyValueFactory<BusinessDepartment, String>("assCode"));
            assCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            assValueCol.setCellValueFactory(new PropertyValueFactory<BusinessDepartment, String>("assValue"));
            assValueCol.setCellFactory(TextFieldTableCell.forTableColumn());
            
            tableView.setItems(list);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    

}

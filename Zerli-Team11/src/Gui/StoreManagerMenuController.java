/************************************************************************** 
 * Copyright (�) Zerli System 2017-2018 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Edo Notes <Edoono24@gmail.com>
 * 			  Tomer Arzuan <Tomerarzu@gmail.com>
 * 			  Matan Sabag <19matan@gmail.com>
 * 			  Ido Kalir <idotehila@gmail.com>
 * 			  Elinor Faddoul<elinor.faddoul@gmail.com
 **************************************************************************/
package Gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.TreeMap;

import Entities.Customer;
import Entities.User;
import Login.WelcomeController;
import client.ChatClient;
import Server.EchoServer;
import client.ClientConsole;
import common.Msg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class StoreManagerMenuController implements Initializable
{
	@FXML
	ComboBox cmbSelectReport1;
	@FXML
	ComboBox cmbS1;
	@FXML
	ComboBox cmbQ1;
	@FXML
	Button Breport1;
	@FXML
	Button BNU;
	@FXML
	TextField CustomerIDtext;
	@FXML
	Button SettelementAccount;
	
	ObservableList <String >quarterly = FXCollections.observableArrayList("1","2","3","4");
	ObservableList<String> ReportsList=FXCollections.observableArrayList("Quarter's Incomes","Quarter's Order","Customers Complaints","Customer Satisfication");
	ObservableList<String> ShopList=FXCollections.observableArrayList("Haifa","Ako","Tel Aviv");
	public ClientConsole client;
	public ChatClient chat;
	private Msg LogoutMsg=new Msg();
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		cmbSelectReport1.setItems(ReportsList);
		cmbQ1.setItems(quarterly);
		//cmbS1.setItems(ShopList);
	}
	@FXML
	public void RegisterBtn(ActionEvent event) throws IOException
	{	
		{((Node)event.getSource()).getScene().getWindow().hide();//Hide Menu
		Stage primaryStage=new Stage();
		Parent root=FXMLLoader.load(getClass().getResource("/Gui/NewUserRegistration.fxml"));
		Scene serverScene = new Scene(root);
		serverScene.getStylesheets().add(getClass().getResource("NewUserRegistration.css").toExternalForm());
		primaryStage.setScene(serverScene);
		primaryStage.show();}

	}
	
	@FXML
	public void askReport(ActionEvent event) throws Exception
{		TreeMap<String, String> directory = new TreeMap<String, String>();
		Msg userToCheck=new Msg(Msg.qSELECTALL,"checkUserExistence");
		userToCheck.setClassType("report");// create a new msg
		String[] date= {"'2011-01-01' AND '2011-3-31'", "'2011-4-01' AND '2011-12-06'","'2011-07-01' AND '2011-09-31'", "'2011-10-01' AND '2011-12-31'"}; 
		String cmd = "";
		String cmd_count ="SELECT orders.type,count(*) as count FROM zerli.orders WHERE date BETWEEN";
		String cmd_sum = "SELECT orders.type,sum(price) as sum FROM zerli.orders WHERE date BETWEEN";
			if((String)cmbSelectReport1.getValue()=="Quarter's Incomes") cmd=cmd_sum;
			if((String)cmbSelectReport1.getValue()=="Quarter's Order") cmd=cmd_count;

		System.out.println((String)cmbQ1.getValue());
		int q2 =Integer.parseInt((String)cmbQ1.getValue());
		cmd += date[q2-1];
		cmd +=" and orders.shop = '" + cmbS1.getValue() +"' group by orders.type ;";
		System.out.println(cmd);
		
		userToCheck.setQueryQuestion(cmd);
//		userToCheck.setQueryExist(true);
		
		client=new ClientConsole(EchoServer.HOST,EchoServer.DEFAULT_PORT);
		client.accept((Object)userToCheck);
		directory = (TreeMap<String, String> )client.msg;
		System.out.println("good 2" +directory);
		

		
		Stage primaryStage=new Stage();
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("report_order.fxml").openStream());
		report_orderController report=loader.getController();
		report.setdirectory(directory);
		report.load_dir(directory);
		Scene serverScene = new Scene(root);
		serverScene.getStylesheets().add(getClass().getResource("report_order.css").toExternalForm());
		primaryStage.setScene(serverScene);
		primaryStage.show();

		
		
		
	}
	
	@FXML
	public void SettelementAccountBut(ActionEvent event) throws Exception
	{
		Customer CustomerDB= new Customer();
		CustomerDB.setCustomerID(Integer.parseInt(CustomerIDtext.getText()));
		
		Msg exsitCustomer = new Msg(Msg.qSELECT, "searchCustomerInDB"); // create a new msg
		exsitCustomer.setSentObj(CustomerDB); // put the Survey into msg
		exsitCustomer.setClassType("customer");
		
		client = new ClientConsole("127.0.0.1",5555);/////����� ��� welcomeController �� ������ ����
		client.accept((Object) exsitCustomer); //adding the survey to DB
		
		exsitCustomer = (Msg) client.get_msg();
		Customer returnCustomer = (Customer) exsitCustomer.getReturnObj();
		if(returnCustomer.getCustomerID()!=0) 
		{
			
			Stage primaryStage=new Stage();
			((Node)event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/Gui/SettlementAccount.fxml").openStream());
			SettlementAccountController settlementController= (SettlementAccountController)loader.getController();
			settlementController.getCustomerIdANDuserName(Integer.toString(returnCustomer.getCustomerID()), returnCustomer.getUserName());
			Scene Scene = new Scene(root);
			Scene.getStylesheets().add(getClass().getResource("SettlementAccount.css").toExternalForm());
			primaryStage.setScene(Scene);
			primaryStage.show();
		}
		
		
	}
	
	
	
	
	@FXML
	public void LogoutBtn(ActionEvent event)
	{
		LogoutMsg.setqueryToDo("update user");
		LogoutMsg.setSentObj(User.currUser);
		LogoutMsg.setQueryQuestion(Msg.qUPDATE);
		LogoutMsg.setColumnToUpdate("ConnectionStatus");
		LogoutMsg.setValueToUpdate("Offline");	
		LogoutMsg.setClassType("User");
		ClientConsole client=new ClientConsole(WelcomeController.IP, WelcomeController.port);
		try {
			client.accept(LogoutMsg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* Update the connection status of the user from online  to offline */
		
		Stage primaryStage=new Stage();
		Parent root;
		try {
			
			((Node)event.getSource()).getScene().getWindow().hide();
			root = FXMLLoader.load(getClass().getResource("/Login/Login.fxml"));
			Scene loginScene = new Scene(root);
			loginScene.getStylesheets().add(getClass().getResource("/Login/login_application.css").toExternalForm());
			primaryStage.setScene(loginScene);
			primaryStage.show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
}


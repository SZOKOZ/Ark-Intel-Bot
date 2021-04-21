package arkintelbot.gui;

/**
 * Sample Skeleton for 'serverFormAdd.fxml' Controller Class
 */

import java.net.URL;
import java.util.ResourceBundle;

import arkintelbot.gui.model.ServerDiscordRelay;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ServerFormAdd {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="serverAliasTextField"
    private TextField serverAliasTextField; // Value injected by FXMLLoader
    
    @FXML // fx:id="serverPortTextField"
    private TextField serverPortTextField; // Value injected by FXMLLoader

    @FXML // fx:id="serverIPTextField"
    private TextField serverIPTextField; // Value injected by FXMLLoader

    @FXML // fx:id="relayChannelTextField"
    private TextField relayChannelTextField; // Value injected by FXMLLoader

    @FXML // fx:id="rconPortTextField"
    private TextField rconPortTextField; // Value injected by FXMLLoader

    @FXML // fx:id="serverFormAddButton"
    private Button serverFormAddButton; // Value injected by FXMLLoader

    private ObservableList<ServerDiscordRelay> serversList = null;
    
    @FXML
    void onServerFormAdd(ActionEvent event) 
    {
    	/*Reset the background color of the text fields*/
    	String whitecss = "-fx-control-inner-background: white";
    	serverAliasTextField.setStyle(whitecss);
    	serverIPTextField.setStyle(whitecss);
    	serverPortTextField.setStyle(whitecss);
    	rconPortTextField.setStyle(whitecss);
    	relayChannelTextField.setStyle(whitecss);
    	
    	boolean aliasValid = false, ipValid = false, portValid = false, relayValid = false;
    	String redcss = "-fx-control-inner-background: red";
    	String serverAlias = serverAliasTextField.getText().trim();
    	if (serverAlias.isEmpty())
    	{
    		serverAliasTextField.setStyle(redcss);
    	}
    	else
    	{
    		aliasValid = true;
    	}
    	
    	String serverIP = serverIPTextField.getText().trim();
    	if (serverIP.isEmpty())
    	{
    		serverIPTextField.setStyle(redcss);
    	}
    	else
    	{
    		ipValid = true;
    	}
    	
    	int serverPort = 0;
    	try
    	{
    		serverPort = Integer.parseInt(serverPortTextField.getText().trim());
    		portValid = true;
    	}
    	catch(NumberFormatException nfe)
    	{
    		serverPortTextField.setStyle(redcss);
    	}
    	
    	int rconPort = 0;
    	try
    	{
    		rconPort = Integer.parseInt(rconPortTextField.getText().trim());
    	}
    	catch(NumberFormatException nfe)
    	{
    		
    	}
    	
    	String relayChannel = relayChannelTextField.getText().trim();
    	if (relayChannel.isEmpty())
    	{
    		relayChannelTextField.setStyle(redcss);
    	}
    	else
    	{
    		relayValid = true;
    	}
    	
    	if (aliasValid && ipValid && portValid && relayValid)
    	{
    		serversList.add(new ServerDiscordRelay(serverAlias, serverIP, serverPort, rconPort, relayChannel));
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() 
    {
    	assert serverAliasTextField != null : "fx:id=\"serverAliasTextField\" was not injected: check your FXML file 'serverFormAdd.fxml'.";
        assert serverPortTextField != null : "fx:id=\"serverPortTextField\" was not injected: check your FXML file 'serverFormAdd.fxml'.";
        assert serverIPTextField != null : "fx:id=\"serverIPTextField\" was not injected: check your FXML file 'serverFormAdd.fxml'.";
        assert relayChannelTextField != null : "fx:id=\"relayChannelTextField\" was not injected: check your FXML file 'serverFormAdd.fxml'.";
        assert rconPortTextField != null : "fx:id=\"rconPortTextField\" was not injected: check your FXML file 'serverFormAdd.fxml'.";
        assert serverFormAddButton != null : "fx:id=\"serverFormAddButton\" was not injected: check your FXML file 'serverFormAdd.fxml'.";

    }
    
    void initServerList(ObservableList<ServerDiscordRelay> serverList)
    {
    	this.serversList = serverList;
    }
}

package arkintelbot.gui;

import java.io.IOException;

/**
 * Sample Skeleton for 'MasterBotGUIDocument.fxml' Controller Class
 */

import java.net.URL;
import java.util.ResourceBundle;

import arkintelbot.api.ArkIntelBotAPI;
import arkintelbot.gui.model.ServerDiscordRelay;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MasterBotGUIController extends Application{

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="mainTabPane"
    private TabPane mainTabPane; // Value injected by FXMLLoader

    @FXML // fx:id="bmChannelTextField"
    private TextField bmChannelTextField; // Value injected by FXMLLoader

    @FXML // fx:id="taiChannelTextField"
    private TextField taiChannelTextField; // Value injected by FXMLLoader

    @FXML // fx:id="consoleTextArea"
    private TextArea consoleTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="svChannelTextField"
    private TextField svChannelTextField; // Value injected by FXMLLoader

    @FXML // fx:id="teiChannelTextField"
    private TextField teiChannelTextField; // Value injected by FXMLLoader

    @FXML // fx:id="eaChannelTextField"
    private TextField eaChannelTextField; // Value injected by FXMLLoader

    @FXML // fx:id="tokenTextField"
    private TextField tokenTextField; // Value injected by FXMLLoader

    @FXML // fx:id="serversTableView"
    private TableView<ServerDiscordRelay> serversTableView; // Value injected by FXMLLoader

    @FXML // fx:id="toggleOnButton"
    private AnchorPane toggleOnButton; // Value injected by FXMLLoader

    @FXML // fx:id="toggleButtonBg"
    private Rectangle toggleButtonBg; // Value injected by FXMLLoader

    @FXML // fx:id="tmiChannelTextField"
    private TextField tmiChannelTextField; // Value injected by FXMLLoader

    @FXML // fx:id="toggleButonCircle"
    private Circle toggleButonCircle; // Value injected by FXMLLoader

    private BooleanProperty buttonToggle = new SimpleBooleanProperty(false);
    TranslateTransition translateAnim = new TranslateTransition(Duration.seconds(0.25));
    FillTransition fillAnim = new FillTransition(Duration.seconds(0.25));
    ParallelTransition parallelAnim = new ParallelTransition(translateAnim, fillAnim);
    
    private ObservableList<ServerDiscordRelay> intelServersList = FXCollections.observableArrayList();
    
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ArkIntelBotAPI arkIntelBot;
    @FXML
    void onClickToggleOnButton(MouseEvent event) 
    {
    	buttonToggle.set(!buttonToggle.get());
    }

    @FXML
    void onAddNewClick(ActionEvent event) 
    {
    	Stage formStage = new Stage(StageStyle.DECORATED);
    	formStage.initModality(Modality.NONE);
    	formStage.initOwner(primaryStage);
    	formStage.setTitle("Add Server");
    	
    	try
    	{
    		FXMLLoader loader = new FXMLLoader();
    		loader.setLocation(getClass().getResource("view/serverFormAdd.fxml"));
    		rootLayout = (AnchorPane) loader.load();
    		ServerFormAdd formController = (ServerFormAdd)loader.getController();
    		formController.initServerList(intelServersList);
    		
    		// Show the scene containing the root layout.
    		Scene scene = new Scene(rootLayout);
    		formStage.setScene(scene);
    		formStage.show();
    	}
    	catch(IOException ioe)
    	{
    		ioe.printStackTrace();
    	}
    	
    }

    @FXML
    void onDeleteClick(ActionEvent event) 
    {
    	serversTableView.getSelectionModel().getSelectedItems().forEach(intelServersList::remove);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert mainTabPane != null : "fx:id=\"mainTabPane\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert bmChannelTextField != null : "fx:id=\"bmChannelTextField\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert taiChannelTextField != null : "fx:id=\"taiChannelTextField\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert consoleTextArea != null : "fx:id=\"consoleTextArea\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert svChannelTextField != null : "fx:id=\"svChannelTextField\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert teiChannelTextField != null : "fx:id=\"teiChannelTextField\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert eaChannelTextField != null : "fx:id=\"eaChannelTextField\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert tokenTextField != null : "fx:id=\"tokenTextField\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert serversTableView != null : "fx:id=\"serversTableView\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert toggleOnButton != null : "fx:id=\"toggleOnButton\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert toggleButtonBg != null : "fx:id=\"toggleButtonBg\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert tmiChannelTextField != null : "fx:id=\"tmiChannelTextField\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        assert toggleButonCircle != null : "fx:id=\"toggleButonCircle\" was not injected: check your FXML file 'MasterBotGUIDocument.fxml'.";
        
        translateAnim.setNode(toggleButonCircle);
        fillAnim.setShape(toggleButtonBg);
        buttonToggle.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) 
			{
				if (newValue == Boolean.TRUE)
				{
					translateAnim.setToX(60);
					fillAnim.setToValue(Color.LAWNGREEN);
				}
				else
				{
					translateAnim.setToX(0);
					fillAnim.setToValue(Color.WHITE);
				}
				
				parallelAnim.play();
			}
        	
        });
        
        serversTableView.getColumns().forEach(column -> {
        	if (column.getText().equals("Server Name"))
        		column.setCellValueFactory(new PropertyValueFactory<>("alias"));
        	else if(column.getText().equals("IP"))
        		column.setCellValueFactory(new PropertyValueFactory<>("ip"));
        	else if(column.getText().equals("Port"))
        		column.setCellValueFactory(new PropertyValueFactory<>("port"));
        	else if(column.getText().equals("Query Port (Optional)"))
        		column.setCellValueFactory(new PropertyValueFactory<>("queryPort"));
        	else if(column.getText().equals("Discord Channel ID or Name"))
        		column.setCellValueFactory(new PropertyValueFactory<>("discordRelay"));
        });
        serversTableView.setItems(intelServersList);
    }

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Ark Intel Bot");

        initRootLayout();
	}
	
	 /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try 
        {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("view/MasterBotGUIDocument.fxml"));
            rootLayout = (AnchorPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) 
    {
        launch(args);
    }
}

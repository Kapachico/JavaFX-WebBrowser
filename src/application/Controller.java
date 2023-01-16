package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Controller implements Initializable{

	@FXML
	private WebView myWV;
	
	@FXML
	private TextField searchTF;
	
	@FXML
	private Button reload,zoomInBtn,zoomOutBtn,resetBtn,forwardBtn,backwardBtn,newWindowBtn,btnBookmark;
	
	@FXML
	HBox hbBookmark;
	
	private WebEngine engine; 
	private WebHistory history;
	private double zoom;
	private ImageView reloadImv=new ImageView(new Image("reload-icon.png"));
	private ImageView zoomOutImv=new ImageView(new Image("zoomOut.png"));
	private ImageView zoomInImv=new ImageView(new Image("ZoomIn.png"));
	private ImageView resetZoomImv=new ImageView(new Image("resetZoom.png"));
	private ImageView fowardImv=new ImageView(new Image("forward-icon.png"));
	private ImageView backwardImv=new ImageView(new Image("backward-icon.png"));
	private int j=1; //For adding labels in history pane

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		engine=myWV.getEngine();
		zoom=1;
		searchTF.setText("www.google.com");
		
		//====================Button Setup and Design=================//
		reload.setGraphic(reloadImv);
		zoomInBtn.setGraphic(zoomInImv);
		zoomOutBtn.setGraphic(zoomOutImv);
		resetBtn.setGraphic(resetZoomImv);
		forwardBtn.setGraphic(fowardImv);
		backwardBtn.setGraphic(backwardImv);
		
		reloadImv.setFitWidth(20);
		reloadImv.setFitHeight(15);
		zoomOutImv.setFitWidth(20);
		zoomOutImv.setFitHeight(15);
		zoomInImv.setFitWidth(20);
		zoomInImv.setFitHeight(15);
		resetZoomImv.setFitWidth(20);
		resetZoomImv.setFitHeight(15);
		fowardImv.setFitWidth(20);
		fowardImv.setFitHeight(15);
		backwardImv.setFitWidth(20);
		backwardImv.setFitHeight(15);
		//====================Button Setup and Design=================//
		
		
		go();
	}
	public void go() {
		engine.load("http://"+searchTF.getText());
	}
	public void reload() {
		engine.reload();
	}
	public void zoomIn(){
		zoom+=0.10;
		myWV.setZoom(zoom);
	}
	public void zoomOut(){
		zoom-=0.10;
		myWV.setZoom(zoom);
	}
	public void resetZoom() {
		zoom=1;
		myWV.setZoom(zoom);
	}
	
	public void back() {
		try {
			history=engine.getHistory();
			ObservableList<WebHistory.Entry> entries = history.getEntries();
			history.go(-1);
			searchTF.setText(entries.get(history.getCurrentIndex()).getUrl());
		} catch (IndexOutOfBoundsException e) {
			System.out.println("There is no previous History yet");
		}
	}
	public void forward() {
		try {
			history=engine.getHistory();
			ObservableList<WebHistory.Entry> entries = history.getEntries();
			history.go(1);
			searchTF.setText(entries.get(history.getCurrentIndex()).getUrl());
		} catch (IndexOutOfBoundsException e) {
			System.out.println("There is no forward History Yet");
		}
	}
	
	
	public void newWindow() throws IOException {
		Parent root= FXMLLoader.load(getClass().getResource("web.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage newStage=new Stage();
		newStage.setScene(scene);
		newStage.show();
	}
	
	public void bookmark() {
		if (hbBookmark.getChildren().size()< 5) {
			String url=searchTF.getText();
			String name = (searchTF.getText()).substring(4,10);//www.
			Button btn=new Button(name);
			btn.setOnAction(e->{
				searchTF.setText(url);
				engine.load("http://"+searchTF.getText());
			});
			hbBookmark.getChildren().add(btn);
		}else{
			System.out.println("A maximum of 5 bookmarks are allowed!");
		}
		
	}
	
	public void getHistory() {
		history=engine.getHistory();
		ObservableList<WebHistory.Entry> entries = history.getEntries();
		
		GridPane gp=new GridPane();
		BorderPane bp=new BorderPane();
		Label lbURL=new Label("URL");
		Label lbDate=new Label("Last Visited");
		lbURL.setId("lbhistory");
		lbDate.setId("lbhistory");
		HBox hb= new HBox(250);
		hb.getChildren().addAll(lbURL,lbDate);
		hb.setPadding(new Insets(20,100,50,100));
		bp.setCenter(gp);
		bp.setTop(hb);
		gp.setHgap(100);
		bp.setId("historyBp");
		
		gp.setPadding(new Insets(20,40,50,70));
		Scene historyScene=new Scene(bp,600,600);
		historyScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage historySt=new Stage();
		historySt.setScene(historyScene);
		historySt.setTitle("History Window");
		historySt.show();
		try{
			for(WebHistory.Entry entry:entries) {
			gp.add(new Label(entry.getUrl()), 0, j);
			gp.add(new Label((entry.getLastVisitedDate()).toString()), 1, j);
			j+=1;
		}
		}catch(Exception e) {
			System.out.println("Please Wait for the page to load before requesting History");
		}
	}
}

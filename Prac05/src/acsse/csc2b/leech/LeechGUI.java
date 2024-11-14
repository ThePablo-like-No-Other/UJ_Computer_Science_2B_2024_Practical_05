/*@author P_Nneko_221044447
 * @version
 * Prac05_2024
 */
package acsse.csc2b.leech;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LeechGUI extends Pane {
//GUI nodes
	private VBox vbox;
	private Button DownloadBtn;
	private Button FileListBtn;
	private Button ConnectBtn;

	private Label hostAddressLbl;
	private Label portNumLbl;
	private Label fileListLbl;
	private Label fileIDLbl;

	private TextField TxtfileID;
	private TextArea TxtAfileList;
	private TextField hostAddressTxt;
	private TextField portNumTxt;

	private LeechClient leech_client;

	/**
	 * Constructor
	 * 
	 * @param stage the main stage
	 */
	public LeechGUI(Stage stage) {
		stage.setTitle("Leech");

		// initialize variables
		vbox = new VBox();
		leech_client = new LeechClient();

		DownloadBtn = new Button("Download File");
		FileListBtn = new Button("Request File List");
		ConnectBtn = new Button("Connect to Host");// once connected used as GUI Feedback to User

		hostAddressLbl = new Label("Enter Host Address");
		portNumLbl = new Label("Enter Port Number");
		fileListLbl = new Label("List of Files");
		fileIDLbl = new Label("Enter File ID:");

		TxtfileID = new TextField();
		TxtAfileList = new TextArea();
		hostAddressTxt = new TextField();
		portNumTxt = new TextField();

		ConnectBtn.setOnAction(e -> {
			// call the client's connectToPort method
			ConnectBtn.setText(// If connection Made will Be Shown on Button
					leech_client.connectToPort(Integer.parseInt(portNumTxt.getText()), hostAddressTxt.getText()));

			ConnectBtn.setText("Leech Connection made to Seed");

		});

		// when clicking the download button
		DownloadBtn.setOnAction(e -> {
			Thread t1 = new Thread(() -> {
				ConnectBtn
						.setText(LeechClient.downloadFile(TxtfileID.getText(), Integer.parseInt(portNumTxt.getText())));
			});
			t1.start();
		});

		FileListBtn.setOnAction(e -> {
			Thread t2 = new Thread(() -> {
				TxtAfileList.setText(leech_client.getFileList(1234));
			});
			t2.start();
		});

		// add all nodes to the vbox
		vbox.getChildren().addAll(hostAddressLbl, hostAddressTxt, portNumLbl, portNumTxt, ConnectBtn, fileListLbl,
				TxtAfileList, FileListBtn, fileIDLbl, TxtfileID, DownloadBtn);

		vbox.setPadding(new Insets(20, 20, 20, 20));
	}

	public VBox getVbox() {
		return this.vbox;
	}
}

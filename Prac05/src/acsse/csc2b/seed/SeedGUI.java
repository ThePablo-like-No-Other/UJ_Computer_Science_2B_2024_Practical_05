/*@author P_Nneko_221044447
 * @version
 * Prac05_2024
 */
package acsse.csc2b.seed;

//---------------
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//seed mode uploads/sends the files
public class SeedGUI extends Pane {

	private VBox vbox;
	private SeedClient objSeedClient;
	private Stage mainStage;

	private Button getShowlistBtn;
	private Button addFileBtn;
	private Button sendFileListBtn;
	private Button sendFileBtn;

	private TextField fileIDTxt;
	private TextArea listOfFilesTxt;

	private Label listOfFilesLbl;
	private Label fileIDLbl;

	File chosenFile;

	public SeedGUI(Stage stage) {
		mainStage = stage;

		// Instantiate seedClient object
		objSeedClient = new SeedClient(6524);

		stage.setTitle("Seeder");

		vbox = new VBox();
		addFileBtn = new Button("Add File");
		sendFileListBtn = new Button("Send Files List");
		sendFileBtn = new Button("Send File");
		getShowlistBtn = new Button("Show File List");

		listOfFilesLbl = new Label("List of Available Files");
		fileIDLbl = new Label("Enter File ID:");
		listOfFilesTxt = new TextArea();
		fileIDTxt = new TextField();

		getShowlistBtn.setOnAction(e -> {// call the showFileList method
			Thread t = new Thread(() -> {
				listOfFilesLbl.setText(GetFileList());
			});
			t.start();
		});

		addFileBtn.setOnAction(e -> {
			Thread t = new Thread(() -> {
				FileChooser chooser = new FileChooser();
				File f = new File("data/seeder files");
				chooser.setInitialDirectory(f); // set initial directory of FileChooser
				chosenFile = chooser.showOpenDialog(mainStage); // get the file
				// update listOfFilesLbl
				listOfFilesLbl.setText(GetFileList());
			});
			t.start();
		});

		sendFileListBtn.setOnAction(e -> {
			Thread t = new Thread(() -> {
				try {
					// send request
					String request = "LIST" + " " + GetFileList();

					DatagramPacket packet = new DatagramPacket(request.getBytes(), request.getBytes().length,
							objSeedClient.getIPAddress(), 4321);// UDP protocol transfer
					objSeedClient.getSocket().send(packet);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
			t.start();
		});

		sendFileBtn.setOnAction(e -> {
			Thread t = new Thread(() -> {
				// get the directory and list the files
				File dir = new File("data/seeder files");
				File[] listOfFiles = dir.listFiles();

				// if there's no chosenFile
				if (chosenFile == null) {
					// select the file to send from the list of files
					File fileToSend = listOfFiles[Integer.parseInt(fileIDTxt.getText())];
					try {
						// send the id of the file that we are sending
						DatagramPacket id = new DatagramPacket(fileIDTxt.getText().getBytes(),
								fileIDTxt.getText().getBytes().length, objSeedClient.getIPAddress(), 4321 // 4321 is the
																											// default
						// port number of the
						// Leecher when in
						// SeedMode
						);
						objSeedClient.getSocket().send(id);

						// send the filename first
						DatagramPacket fname = new DatagramPacket(fileToSend.getName().getBytes(),
								fileToSend.getName().getBytes().length, objSeedClient.getIPAddress(), 4321);
						objSeedClient.getSocket().send(fname);

						// send the file itself
						DatagramPacket packet = SeedClient.sendFile(fileIDTxt.getText(), objSeedClient.getIPAddress(),
								4321);
						objSeedClient.getSocket().send(packet);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					// if there is a chosen file to send
				} else if (chosenFile != null && Integer.parseInt(fileIDTxt.getText()) == listOfFiles.length) {
					try {
						// send the id of chosen file
						DatagramPacket id = new DatagramPacket(fileIDTxt.getText().getBytes(),
								fileIDTxt.getText().getBytes().length, objSeedClient.getIPAddress(), 4321);
						objSeedClient.getSocket().send(id);

						// send filename
						DatagramPacket fname = new DatagramPacket(chosenFile.getName().getBytes(),
								chosenFile.getName().getBytes().length, objSeedClient.getIPAddress(), 4321);
						objSeedClient.getSocket().send(fname);

						// send the file
						DatagramPacket packet = SeedClient.sendParticularFile(chosenFile, objSeedClient.getIPAddress(),
								4321);
						objSeedClient.getSocket().send(packet);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});
			t.start();
		});

		// add all nodes to the vbox
		vbox.getChildren().addAll(listOfFilesLbl, getShowlistBtn, listOfFilesTxt, addFileBtn, sendFileListBtn,
				fileIDLbl, fileIDTxt, sendFileBtn);

		// set vbox padding
		vbox.setPadding(new Insets(20, 20, 20, 20));
	}

	/**
	 * getter for vbox
	 * 
	 * @return the GUI's vbox
	 */
	public VBox getVbox() {
		return this.vbox;
	}

	/**
	 * Get the local file list of Seeder
	 * 
	 * @return the list of files
	 */
	private String GetFileList() {

		File dir = new File("data/seed/SeedFiles");// cr8t File object to access Directory/folder data/seed/SeedFiles

		File[] listOfFiles = dir.listFiles();// Capture Array of Files files in directory

		String list = ""; // the file list to return

		// Creating Own ID-system/reference for each file via numbering
		int counter = 0; // keeps track of the file id

		for (File f : listOfFiles) {

			if (!f.isDirectory()) {// if the f is not a directory then implies is a actual file thus add to list
				list += ("ID: " + "(" + counter + ")" + ", File: " + f.getName() + "\n");
				counter += 1;
			}
		}

		listOfFilesTxt.setText(list);
		return list;
	}
}

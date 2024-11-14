
import acsse.csc2b.leech.LeechGUI;
import acsse.csc2b.seed.SeedClient;
import acsse.csc2b.seed.SeedGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
	SeedGUI seed = null;
	LeechGUI leech = null;// Star up seed so that leech has someone to connect to

	SeedClient seed_Client = null;
	Scene SelectedScene = null;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {

		// Start Up GUI
		// Create buttons
		Button seedButton = new Button("Seed");
		Button leechButton = new Button("Leech");
		// Layout - HBox to place buttons next to each other
		HBox hbox = new HBox(10, seedButton, leechButton);
		Scene scene = new Scene(hbox, 300, 100);

		seedButton.setOnAction(e -> Selected_Seed(SelectedScene, primaryStage, seed));
		leechButton.setOnAction(e -> Selected_Leech(SelectedScene, primaryStage, leech, seed_Client));

		// Set the stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("P2P File Transfer");
		primaryStage.show();

	}

	/*
	 * when button press appropriate Mode will be activate
	 * 
	 * @paramter Primary stage and
	 * 
	 * @paramter the scene
	 * 
	 * @paramter Appropriate SelectedClient
	 */
	private void Selected_Leech(Scene SelectedScene, Stage primaryStage, LeechGUI leech, SeedClient seed_Client) {
		System.out.println("Leech selected");
		seed_Client = new SeedClient();// A leech only survives by having a host here is the host
		leech = new LeechGUI(primaryStage);
		SelectedScene = new Scene(leech.getVbox(), 300, 400);
		primaryStage.setScene(SelectedScene);

		return;
	}

	private void Selected_Seed(Scene SelectedScene, Stage primaryStage, SeedGUI seed) {
		System.out.println("Seed Activated");

		seed = new SeedGUI(primaryStage);
		SelectedScene = new Scene(seed.getVbox(), 300, 400);
		primaryStage.setScene(SelectedScene);

		return;
	}

}

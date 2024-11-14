/*@author P_Nneko_221044447
 * @version
 * Prac05_2024
 */
package acsse.csc2b.leech;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.StringTokenizer;

//class  connects to a seeder, request a file list, and download files.
public class LeechClient {

	private static DatagramSocket ds;
	private InetAddress ip;

	// default port to send the data over when in leech mode
	private static final int DEFAULT_PORT = 4321;

	// client socket will run on default port
	public LeechClient() {
		try {
			ds = new DatagramSocket(DEFAULT_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	// second constructor for when the client will run on a specified port
	public LeechClient(int portNum) {
		try {
			ds = new DatagramSocket(portNum);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * main method to run the client server
	 * 
	 * @param args
	 */
	public static void LeeachActivate(String[] args) {
		boolean running = true;
		// cr8t leech client
		LeechClient leecher = new LeechClient();
		System.out.println("Leecher running");

		while (running) {
			try {
				// Receive the initial command
				byte[] data = new byte[2048];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				leecher.getLeecSocket().receive(packet);// Connect to Seeder: Send Packet to seed->Seed ought to send
														// Packet confirmation back
				int seedPortNum = packet.getPort(); // take the seeders port number

				// Get seed cmd
				String SeedRepsonse = new String(packet.getData());
				// Handle data received from Seed
				StringTokenizer responseToken = new StringTokenizer(SeedRepsonse.trim());
				String request = responseToken.nextToken();
				String str = SeedRepsonse.replace(request, ""); // remove the request and keep the file list

				System.out.println(request);// print on console

				// request can be LIST or the integer ID number of the requested file
				if (request.equals("LIST")) {

					System.out.println("Response from Seed -> Here is list of Avaible Files " + str.substring(1));

				} else if (request.contains("FILE")) {//
					downloadFile(request, seedPortNum);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * connect the client to a port.
	 * 
	 * @param portNum the port number
	 * 
	 * @param hostName the host name
	 * 
	 * @return The returned string will be used in the GUI prompt.
	 */
	public String connectToPort(int portNum, String hostName) {
		try {
			ds = new DatagramSocket(portNum);
			this.ip = InetAddress.getByName(hostName);
			return "Connected to " + hostName + " on port " + portNum;// -----change to boolean
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Unable to Connect to port " + portNum;
	}

	/**
	 * download the file.
	 * 
	 * @param fileId        id of requested file
	 * @param senderPortNum the port number of the sender
	 * @return returns a string to show the status of the request in the GUI
	 */
	public static String downloadFile(String fileId, int senderPortNum) {
		System.out.println("Sending Request...");
		String status = "File Not Recieved";

		try {
			// requesting the file
			InetAddress ip = InetAddress.getByName("localhost");
			String request = "FILE" + " " + fileId;
			DatagramPacket packet = new DatagramPacket(request.getBytes(), request.getBytes().length, ip,
					senderPortNum);
			ds.send(packet);
			System.out.println("Request Sent!");

			// Receiving the FILE command ,filename/index first Filename will be used in the
			// FileOutputStream.
			byte[] fnameData = new byte[2048];
			DatagramPacket recievedNamePacket = new DatagramPacket(fnameData, fnameData.length);
			ds.receive(recievedNamePacket);
			String fname = new String(recievedNamePacket.getData()).trim();

			// Receiving the file
			byte[] data = new byte[2048];
			DatagramPacket recievedPacket = new DatagramPacket(data, data.length);

			FileOutputStream fos = new FileOutputStream("data/leecher/leecher Files/" + fname);

			ds.receive(recievedPacket);
			data = recievedPacket.getData();
			status = "File received successfully";

			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * gets the list of files available in the directory
	 * 
	 * @param senderPortNum the port number of the sender
	 * @return the list of files. this will be used in the GUI
	 */
	public String getFileList(int senderPortNum) {

		System.out.println("Sending Request...");
		String flist = "Unable to get File List";

		try (DatagramSocket socket = new DatagramSocket();) {
			InetAddress ip = InetAddress.getByName("localhost");

			// send the request
			String request = "LIST";
			DatagramPacket packet = new DatagramPacket(request.getBytes(), request.getBytes().length, ip,
					senderPortNum);
			socket.send(packet);// send command

			// result in Receive file list
			byte[] data = new byte[2048];
			DatagramPacket recievedPacket = new DatagramPacket(data, data.length);
			socket.receive(recievedPacket);

			System.out.println("File List Recieved");
			return new String(recievedPacket.getData());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return flist;
	}

	public DatagramSocket getLeecSocket() {
		return ds;
	}

	public InetAddress getleechIp() {
		return ip;
	}
}

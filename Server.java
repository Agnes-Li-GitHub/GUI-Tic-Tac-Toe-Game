import java.io.*;  
import java.net.*;

/**
 * The Main program implements the tic tac toe game (server side).
 * @author Li Sing Yee Agnes
 * @version 1.0
 * @since 2023-05-09
 */

public class Server implements Runnable{
	public static Server server;
	public BufferedReader p1_reader;
	public PrintWriter p1_writer;
	public BufferedReader p2_reader;
	public PrintWriter p2_writer;
	public String readline1 = "";
	public String readline2 = "";
	public String readline = "";
	public String response = "";
	boolean game_continue = true;
	String all_9_label[] = {"z", "z", "z", "z", "z", "z", "z", "z", "z"};
	int round = 1;
	int current_player = 1;

	/**
	 * This is the main program, which only call the method go(). 
	 * @param args Unused.
	 */
	public static void main(String[] args) {  
		server = new Server();  
		server.go(); 
	}

	/**
	 * This method is to connect to the client
	 */
	public void go() {  
		try{ 
			ServerSocket serverSock = new ServerSocket(5000);
			Socket sock1 = serverSock.accept(); 
			InputStreamReader p1_streamReader = new InputStreamReader(sock1.getInputStream());  
			p1_reader = new BufferedReader(p1_streamReader); 
			p1_writer = new PrintWriter(sock1.getOutputStream(), true); 		
			response = "x";
			p1_writer.println(response);

			Socket sock2 = serverSock.accept(); 
			InputStreamReader p2_streamReader = new InputStreamReader(sock2.getInputStream());  
			p2_reader = new BufferedReader(p2_streamReader);  
			p2_writer = new PrintWriter(sock2.getOutputStream(), true); 		
			response = "o";
			p2_writer.println(response);

			Thread player1 = new Thread(server);  
			Thread player2 = new Thread(server);
			player1.start();
			player2.start();
			player1.join();
			player2.join();
			serverSock.close();
		} 
		catch (Exception e) { e.printStackTrace();}
	}	

	/**
	 * This method is to run the game, and how to deal with different messages that the server receive.
	 */
	@Override
	public void run()  {
		while(game_continue) {
			try {
				if (check_full()) {
					if (check_result() == "n") {
						response = "Draw.";
						p1_writer.println(response);
						p2_writer.println(response);
						game_continue = false;
					}
				}
				else {
					if (check_result() == "x") {
						game_continue = false;
					}
					else if (check_result() == "o") {
						game_continue = false;
					}
					else {
						if (current_player == 1 && round > 1) {
							p1_writer.println("Your opponent has moved, now is your turn.");
							p2_writer.println("Valid move, wait for your opponent.");
							System.out.println("Waiting for player 1, round 1+");
							readline = p1_reader.readLine();
							current_player = 2;
						}					
						else if (current_player == 2) {
							p1_writer.println("Valid move, wait for your opponent.");
							p2_writer.println("Your opponent has moved, now is your turn.");
							System.out.println("Waiting for player 2, round 1+");
							readline = p2_reader.readLine();
							current_player = 1;
							round += 1;
						}
						// special case for first move
						else if (current_player == 1 && round == 1) {
							p1_writer.println("Your first move");
							p2_writer.println("Not your move");
							System.out.println("Waiting for player 1, round 1");
							readline = p1_reader.readLine();
							current_player = 2;
						}
						// message Mx4
						if (readline.substring(0,1).equals("M")) {
							all_9_label[Integer.parseInt(readline.substring(2))] = readline.substring(1,2);
							System.out.println("server receive " + readline);
							System.out.println("server receive " + readline2);
							p1_writer.println(readline);
							p2_writer.println(readline);
						}
					}
				}				
			}
			catch (Exception e) { 
				System.out.println("Catch error, game ends");
				p1_writer.println("Game Ends. One of the players left.");
				p2_writer.println("Game Ends. One of the players left.");
				e.printStackTrace();
				break;
			}
		}
	}

	private String check_result() {
		String winner;
		if (all_9_label[0].equals(all_9_label[1]) && all_9_label[1].equals(all_9_label[2]) && !all_9_label[0].equals("z")) {
			winner = all_9_label[0];
		}    
		else if (all_9_label[3].equals(all_9_label[4]) && all_9_label[4].equals(all_9_label[5])&& !all_9_label[3].equals("z") ) {
			winner = all_9_label[3];
		} 
		else if (all_9_label[6].equals(all_9_label[7]) && all_9_label[7].equals(all_9_label[8])&& !all_9_label[6].equals("z") ) {
			winner = all_9_label[6];
		} 
		else if (all_9_label[0].equals(all_9_label[3]) && all_9_label[3].equals(all_9_label[6])&& !all_9_label[0].equals("z") ) {
			winner = all_9_label[0];
		} 
		else if (all_9_label[1].equals(all_9_label[4]) && all_9_label[4].equals(all_9_label[7])&& !all_9_label[1].equals("z") ) {
			winner = all_9_label[1];
		} 
		else if (all_9_label[2].equals(all_9_label[5]) && all_9_label[5].equals(all_9_label[8])&& !all_9_label[2].equals("z") ) {
			winner = all_9_label[2];
		} 
		else if (all_9_label[0].equals(all_9_label[4]) && all_9_label[4].equals(all_9_label[8])&& !all_9_label[0].equals("z") ) {
			winner = all_9_label[0];
		} 
		else if (all_9_label[2].equals(all_9_label[4]) && all_9_label[4].equals(all_9_label[6])&& !all_9_label[2].equals("z") ) {
			winner = all_9_label[2];
		} 
		else {
			winner = "n";
		}

		if (winner.equals("x")){
			response = "Congratulations. You win.";
			p1_writer.println(response);
			response = "You lose.";
			p2_writer.println(response);
			return "x";
		}
		else if (winner.equals("o")) {
			response = "You lose.";
			p1_writer.println(response);
			response = "Congratulations. You win.";
			p2_writer.println(response);
			return "o";
		}
		else {
			response = "Draw.";
			return "n";
		}
	}

	private boolean check_full(){
		for (int i =0; i<9; i++) {
			if (all_9_label[i].equals("x") || all_9_label[i].equals("o")) { }
			else { return false;  }
		}
		return true;
	}	 
}
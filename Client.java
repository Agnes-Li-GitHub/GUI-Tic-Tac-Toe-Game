import java.io.*;  
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*; 

/**
 * The Main program implements the tic tac toe game. 
 * User needs to input their names in order to start. 
 * During the game, they can select the places to put their symbol (x/o).
 * @author Li Sing Yee Agnes
 * @version 1.0
 * @since 2023-05-09
 */
public class Client implements ActionListener{  
	static BufferedReader reader;
	static PrintWriter writer;
	String readline = "";
	String response = "";
	Socket sock;
	JLabel labels_array[] = new JLabel[9];
	String all_9_label[] = {"z", "z", "z", "z", "z", "z", "z", "z", "z"};
	String symbol;
	String player_name;
	boolean game_continue = true;
	boolean isenabled = false;
	
	/**
	 * This is the main program, which only call the method go(). 
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		Client c = new Client();
			c.go();		
	} 

	/**
	 * This method is used to implement the layout of the card games.
	 * Also, it helps to connect to the server
	 */
	public void go() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Tic Tac Toe");
		JPanel Title_panel = new JPanel();
		JPanel Board_panel = new JPanel();
		Board_panel.setLayout(new GridLayout(3,3));
		JPanel Message_panel = new JPanel();
		frame.add(Title_panel, BorderLayout.NORTH);
		frame.add(Board_panel, BorderLayout.CENTER);
		frame.add(Message_panel, BorderLayout.SOUTH);
		JLabel message_label = new JLabel("Enter your player name...");
		Title_panel.add(message_label);

		JTextField txt_inputname = new JTextField(25);
		JButton btn_submit = new JButton("Submit");
		Message_panel.add(txt_inputname);
		Message_panel.add(btn_submit);
		
		for (int i = 0; i<9; i++) {
			JLabel label_9 = new JLabel();
			label_9.setHorizontalAlignment(SwingConstants.CENTER);
			label_9.setBorder(BorderFactory.createLineBorder(Color.black));
			Board_panel.add(label_9);
			labels_array[i] = label_9;
			final String temp = Integer.toString(i);
			/**
			 * This is the Mouse Listener for 9 JLabel.
			 * @param MouseEvent e, which is a click 
			 * It check if the client is allowed to click 
			 */
			label_9.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					if ((all_9_label[Integer.parseInt(temp)].equals("z")) && (isenabled) && (player_name != null)) {
						System.out.println("client send " + "M" + symbol + temp);
						all_9_label[Integer.parseInt(temp)] = symbol;
						writer.println("M" + symbol + temp);
					}
				}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseReleased(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
			});
		}

		JMenuBar menuBar = new JMenuBar(); 
		JMenu menu_control = new JMenu ("Control"); 
		JMenu menu_help = new JMenu ("Help"); 
		JMenuItem menuItem_exit = new JMenuItem("Exit"); 
		JMenuItem menuItem_instruction = new JMenuItem("Instruction"); 
		menu_control.add(menuItem_exit);
		menu_help.add(menuItem_instruction);
		menuBar.add(menu_control);
		menuBar.add(menu_help);
		frame.setJMenuBar(menuBar);
		frame.setSize(400, 400);
		frame.setVisible(true);

		/**
		 * This is the Action Listener for any sudden close window action.
		 * @param  WindowEvent e, which is a click 
		 */
		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	System.out.println("I close the window");
                writer.println("Game Ends. One of the players left.");
            }
        });
		
		/**
		 * This is the Action Listener for the exit button in menu.
		 * @param ActionEvent e, which is a click 
		 */
		menuItem_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		/**
		 * This is the Action Listener to get the instructions for the game.
		 * @param ActionEvent e, which is a click 
		 */
		menuItem_instruction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, 
					"Some information about the game:" + "\n" + "Criteria for a valid move:" + "\n" + "- The move is not occupied by any mark." + "\n" + "- The move is made in the player's turn." + "\n" + "- The move is made within the 3 x 3 board" + "\n" + "The game would continue and switch among the opposite player until it reaches either one of the following conditions:" + "\n" + "- Player 1 wins." + "\n" + "- Player 2 wins." + "\n" + "- Draw."
				);
			}
		});	

		/**
		 * This is the Action Listener to submit the player name.
		 * @param ActionEvent e, which is a click 
		 */
		btn_submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (! txt_inputname.getText().equals("")){
					player_name = txt_inputname.getText();
					message_label.setText("WELCOME " + player_name);
					frame.setTitle("Tic Tac Toe - Player: " + player_name);
					btn_submit.setEnabled(false); 
					txt_inputname.setEnabled(false); 
				}				
			}
		});
		
		try {   
			sock = new Socket("127.0.0.1", 5000);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream())); 
			writer = new PrintWriter(sock.getOutputStream(), true); 	
		} 
		catch (Exception e) { e.printStackTrace();}
		
		while(game_continue) {
			try {		
				readline = reader.readLine();	
				System.out.println("Client recevie " + readline);
				System.out.println(player_name + " " + isenabled);
				// case 1, playing
				if (readline.substring(0,1).equals("M")) {
					int location = Integer.parseInt(readline.substring(2)); 
					labels_array[location].setText(readline.substring(1,2));		
					labels_array[location].setFont(new Font("Serif", Font.PLAIN, 40));
					if (readline.substring(1,2).equals("x")) {
						labels_array[location].setForeground(Color.GREEN);
						all_9_label[location] = readline.substring(1,2);
					}
					else {
						labels_array[location].setForeground(Color.RED);
						all_9_label[location] = readline.substring(1,2);
					}
				}
				// case 2, just to tell client their symbol
				else if (readline.equals("x") || readline.equals("o")) {
					symbol = readline;
				}
				// case 3, end game
				else if (readline.equals("Congratulations. You win.") || readline.equals("You lose.") || readline.equals("Draw.")) {
					JOptionPane.showMessageDialog(frame, readline);
					game_continue = false;
					break;
				}				
				// case 4 message label  
				else if (readline.equals("Your opponent has moved, now is your turn.")) {
					isenabled = true;
					message_label.setText(readline);
					// move to mouse event listener
				}
				// case 5 message label
				else if (readline.equals("Valid move, wait for your opponent.")) {
					isenabled = false;
					message_label.setText(readline);
				}
				// case 6, round 1 special case
				else if (readline.equals("Not your move")) {
					isenabled = false;
				}
				// case 6, round 1 special case
				else if (readline.equals("Your first move")) {
					isenabled = true;
					// move to mouse event listener
				}
				// case 7 someone quit
				else if (readline.equals("Game Ends. One of the players left.")) {
					isenabled = false;
					JOptionPane.showMessageDialog(frame, readline);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	/**
	 * This is the actionPerformed method. 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {		
	}
}
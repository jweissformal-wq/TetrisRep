package Gam;

import java.awt.*;//imports for GUI
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Board {
    private JFrame frame; //actual frame
    private BoardPanel panel; //board itself
    private Timer gameTimer;  // Timer to update the game state

    public Board() {
        frame = new JFrame("Tetris"); //title and create
        panel = new BoardPanel(); //create the panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //prevents computer from being fried
        frame.getContentPane().add(panel); //gets the panel to main area of frame
        frame.pack();//sizes window
        frame.setLocationRelativeTo(null);// centers the window
        frame.setVisible(true); //visible

        // Set up a timer to update the game state every 500 milliseconds
        gameTimer = new Timer(500, (ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
            }
        });
        gameTimer.start();  // Start the timer immediately
    }

    // Method to update the game state, called by the timer every 500ms
    private void updateGame() {
        panel.dropPiece();  // Drop the current piece down automatically
    }

    public static void main(String[] args) {
        new Board();
    }
}

package Gam;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel { // gets all JPanel stuff

	private Piece currentPiece; // the piece currently on
	private Piece heldPiece; // piece being held
	private Piece nextPiece; // piece that's next
	private int[][] grid = new int[20][10]; // board grid
	private int pieceX = 4; // starts middle of the top of the board
	private int pieceY = 0; // 0 is top of the board
	private boolean gameOver = false; // gameover status
	private boolean canHold = true; // can still hold a piece?
	private int score = 0; // current score
	private final int[] pointsForRows = { 0, 100, 300, 500, 800 }; // amount of points for each row cleared
	private List<Integer> clearedRows = new ArrayList<>(); // row indexes for flashing effect before gone
	private Timer flashTimer; // timer for flashing
	private boolean isFlashing = false; // if flash animation
	private int flashStep = 0; // times the cleared rows flash before being removed.
	private final int FLASH_DURATION = 5; // how long the flash effect lasts (in timer ticks)
	public int speed = 800;
	Timer gameTimer;

	public BoardPanel() { // constructor
		this.setBackground(Color.BLACK); // background and frame size/info
		this.setPreferredSize(new Dimension(500, 600));
		this.setFocusable(true); // can see

		KeyBindings(this); // links keybindings

		spawnNextPiece(); // next and new pieces
		spawnNewPiece();

		// create timer with 500ms increments
		gameTimer = new Timer(speed, new ActionListener() {
			@Override // the actionPerformed() method is called, and so calls drop
			public void actionPerformed(ActionEvent e) {
				dropPiece(); // make the current piece drop down
			}
		});

		// dropping the piece every 500ms
		gameTimer.start();
	}

	@Override
	protected void paintComponent(Graphics g) { // draw game elements
		super.paintComponent(g); // ensure proper panel rendering
		if (gameOver) { // if game over display it
			g.setColor(Color.RED);
			g.drawString("Game Over!", 100, 250);
		} else { // otherwise draw grid, pieces, next, held, and score
			drawGrid(g);
			drawPiece(g);
			drawHeldPiece(g);
			drawNextPiece(g);
			drawScore(g);
		}
	}

	private void drawScore(Graphics g) { // draw the score
		g.setColor(Color.WHITE); // white text (black background)
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.drawString("Score: " + score, 365, 20);
	}

	private void drawGrid(Graphics g) { // draw game grid
		for (int r = 0; r < 20; r++) { // loop each row and column of grid
			for (int c = 0; c < 10; c++) {
				if (grid[r][c] != 0) { // fill occupied cells with blue (cohesion)
					g.setColor(Color.BLUE);
					g.fillRect(c * 30, r * 30, 30, 30);
				}
				g.setColor(Color.DARK_GRAY); // draw grid cell borders
				g.drawRect(c * 30, r * 30, 30, 30);
			}
		}

		// Flash the cleared rows
		if (isFlashing) {
			g.setColor(new Color(255, 0, 0, 100)); // Light red with some transparency
			for (int i = 0; i < clearedRows.size(); i++) {
				int row = clearedRows.get(i);
				if (flashStep % 2 == 0) { // alternating colors
					g.fillRect(0, row * 30, 300, 30);
				}
			}
		}
	}

	private void drawPiece(Graphics g) { //draws given piece
		if (currentPiece != null) {
			g.setColor(currentPiece.getColor());
			int[][] shape = currentPiece.getShape();
			for (int i = 0; i < shape.length; i++) { //loop through the shape'slength
				int blockX = pieceX + shape[i][0]; //fill based on xy
				int blockY = pieceY + shape[i][1];
				g.fillRect(blockX * 30, blockY * 30, 30, 30);
			}
		}
	}

	private void drawHeldPiece(Graphics g) { //draw held piece
		int boxX = 320; //x position
		int boxY = 50; //y position
		int boxSize = 120; //size of box

		g.setColor(Color.WHITE); //white border
		g.drawRect(boxX, boxY, boxSize, boxSize);

		if (heldPiece != null) { //draw only if held
			g.setColor(heldPiece.getColor()); //set color

			int[][] shape = heldPiece.getShape(); //get shape
			int[] bounds = getBounds(shape); //bounds for centering (white box fits all way up to long piece, whatever you wanna call it)

			//calculate width and height of piece in pixels
			int width = (bounds[2] - bounds[0] + 1) * 30;
			int height = (bounds[3] - bounds[1] + 1) * 30;

			//center it
			int offsetX = (boxSize - width) / 2 - bounds[0] * 30;
			int offsetY = (boxSize - height) / 2 - bounds[1] * 30;

			//draw each block of piece
			for (int i = 0; i < shape.length; i++) {
				int blockX = boxX + offsetX + shape[i][0] * 30;
				int blockY = boxY + offsetY + shape[i][1] * 30;
				g.fillRect(blockX, blockY, 30, 30);
			}
		}
	}

	private void drawNextPiece(Graphics g) { //next piece (a little repetitive of held but still)
		int boxX = 320; //x pos
		int boxY = 190; //y pos
		int boxSize = 120; //size of box

		g.setColor(Color.WHITE); //white border
		g.drawRect(boxX, boxY, boxSize, boxSize);

		if (nextPiece != null) { //only draw if next piece
			g.setColor(nextPiece.getColor()); 

			int[][] shape = nextPiece.getShape(); //get shape block cords
			int[] bounds = getBounds(shape); //get bounds to center

			int width = (bounds[2] - bounds[0] + 1) * 30; //calculate width and height
			int height = (bounds[3] - bounds[1] + 1) * 30;

			int offsetX = (boxSize - width) / 2 - bounds[0] * 30; //center it
			int offsetY = (boxSize - height) / 2 - bounds[1] * 30;

			for (int i = 0; i < shape.length; i++) { //draw each block of piece
				int blockX = boxX + offsetX + shape[i][0] * 30;
				int blockY = boxY + offsetY + shape[i][1] * 30;
				g.fillRect(blockX, blockY, 30, 30);
			}
		}
	}


	private void KeyBindings(BoardPanel panel) { //key bindings
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "leftMove");
		this.getActionMap().put("leftMove", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.movePiece(-1, 0); //left click, left move
			}
		});

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "rightMove");
		this.getActionMap().put("rightMove", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.movePiece(1, 0); //right click right move
			}
		});

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "downMove");
		this.getActionMap().put("downMove", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.dropPiece(); //down click move faster
			}
		});

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "rotate");
		this.getActionMap().put("rotate", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.rotatePiece(); //rotate = up click
			}
		});

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"), "holdPiece");
		this.getActionMap().put("holdPiece", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.holdPiece(); //hold if h pressed
			}
		});
	}

	private void spawnNewPiece() { //spawns a new piece
		if (nextPiece != null) { //if given next piece
			currentPiece = nextPiece; //set current to previous next
			spawnNextPiece(); //spawn a next piece now to replace
			pieceX = 4; //middle of top board
			pieceY = 0;
			canHold = true; //can now hold this piece

			if (!isValidMove(pieceX, pieceY, currentPiece)) { //if no move
				gameOver = true; //end game
			}
		}
		repaint(); //refresh
	}

	private void spawnNextPiece() { //get random piece to do this
		nextPiece = Piece.getRandomPiece();
	}

	private void movePiece(int x, int y) { //moves piece x and y based on given input (assuming game isnt over and is a valid move)
		if (!gameOver && isValidMove(pieceX + x, pieceY + y, currentPiece)) {
			pieceX += x;
			pieceY += y;
			repaint(); //refresh
		}
	}

	private boolean isValidMove(int newX, int newY, Piece piece) { //checks for valid move given possible new x and y and the piece
		if (piece == null) { //if no piece, not valid
			return false;
		}

		int[][] shape = piece.getShape(); //get piece shape
		for (int i = 0; i < shape.length; i++) {
			int x = newX + shape[i][0]; //calculate new x and y
			int y = newY + shape[i][1];
			if (x < 0 || x >= 10 || y >= 20 || (y >= 0 && grid[y][x] != 0)) {
				return false;
			} //collision check (overlap and existing piece) and bounds
		}
		return true; //return valid
	}

	private void placePiece() {  //place piece on grid at current pos
		int[][] shape = currentPiece.getShape(); //get shape of piece
		for (int i = 0; i < shape.length; i++) {
			int x = pieceX + shape[i][0]; //calculate x and y
			int y = pieceY + shape[i][1];
			if (y >= 0) { //only place piece if in grid, add to grid position
				grid[y][x] = 1;
			}
		}

		removeFullRows(); //remove any full rows
		repaint(); //refresh
	}

	private void rotatePiece() {
		if (!gameOver) {
			currentPiece.rotate(); //rotate if game not over

			int[][] kicks = { { 0, 0 }, { 1, 0 }, { -1, 0 }, { 0, -1 }, { 1, -1 }, { -1, -1 }, { 0, 1 } };
			//array of possible offsets if rotating where it would be conflicting will borders
			boolean rotatedSuccessfully = false; //check for good rotation

			for (int i = 0; i < kicks.length; i++) { //try each kick to find valid pos
				int newX = pieceX + kicks[i][0]; //calculate new x and y
				int newY = pieceY + kicks[i][1];

				if (isValidMove(newX, newY, currentPiece)) { //if rotated position is valid
					pieceX = newX; //set x and y
					pieceY = newY;
					rotatedSuccessfully = true; //successful rotation
					break;
				}
			}

			if (!rotatedSuccessfully) {
				currentPiece.rotateBack(); // Undo rotation if no valid pos
			}

			repaint(); //refresh it
		}
	}

	public void dropPiece() {
	    //check if gameTimer is initialized before changing the delay
	    if (gameTimer != null && speed > 100) {
	        speed -= 0.1; // increase the falling speed (decrease delay)
	        gameTimer.setDelay(speed); // update timer delay
	    }

	    if (!gameOver) {
	        // check if the piece can drop
	        if (isValidMove(pieceX, pieceY + 1, currentPiece)) {
	            pieceY++; // drop the piece one row down
	        } else {
	            placePiece(); // place the piece on the board if it can't drop further
	            spawnNewPiece(); // 'spawn' a new piece
	        }
	        repaint(); // refresh
	    }
	}


	private void holdPiece() { //holds piece method
		if (!gameOver && canHold) { //if game is not over and you can hold it
			if (heldPiece == null) { //no current held piece
				heldPiece = currentPiece; //just set current as held and get a new piece
				spawnNewPiece();
			} else {
				Piece temp = currentPiece; //temp piece to hold current
				currentPiece = heldPiece; //swap current and held
				heldPiece = temp;
				pieceX = 4; //set x and y for held to top middle of board
				pieceY = 0;
			}
			canHold = false; //now cant hold
		}
	}

	private void removeFullRows() {
		clearedRows.clear(); // Reset from previous clears
		List<Integer> fullRows = new ArrayList<>(); //list to store indexes of full rows

		for (int i = 0; i < 20; i++) { //check each row if full
			boolean full = true; //set full status to true
			for (int j = 0; j < 10; j++) {
				if (grid[i][j] == 0) { //if any cell in that row is empty
					full = false; //not full
					break; 
				}
			}
			if (full) { //if its full still
				fullRows.add(i); //add i to the index
			}
		}

		if (!fullRows.isEmpty()) { // if there are full rows
			score += pointsForRows[fullRows.size()]; //add to score based on num cleared rows
			for (int i = 0; i < fullRows.size(); i++) { //add cleared rows to list of cleared rows
				clearedRows.add(fullRows.get(i));
			}

			for (int i = 0; i < fullRows.size(); i++) { //move down rows
				int rowIndex = fullRows.get(i);
				for (int r = rowIndex; r > 0; r--) { //shift rows down from bottom to top
					grid[r] = grid[r - 1].clone(); //copy row above to current
				}
				grid[0] = new int[10]; //clear the top row
			}

			startFlashEffect(); // clear animation
		}

		repaint(); // refresh
	}

	private void startFlashEffect() {
		isFlashing = true; //flash status true
		flashStep = 0; //initialize 0 steps

		if (flashTimer != null) { //if already active stop it before a new one
			flashTimer.stop();
		}

		flashTimer = new Timer(100, new ActionListener() { //create a new timer (100 ms until flash update)
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        updateFlashEffect(); 
		    }
		});
		flashTimer.start();//start timer
	}

	private void updateFlashEffect() {
		flashStep++; //increment flash step

		if (flashStep >= FLASH_DURATION) { //if flash long enough stop
			flashStep = 0; //reset
			isFlashing = false;
			clearedRows.clear(); //clear after done flashing
			flashTimer.stop();
		}

		repaint(); //reset
	}

	private int[] getBounds(int[][] shape) { //calculates bounding of given shape
		int minX = Integer.MAX_VALUE; //initalize max and min vals of xy
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE; 
		int maxY = Integer.MIN_VALUE;

		for (int i = 0; i < shape.length; i++) { //go through each block in shape to find max min
			minX = Math.min(minX, shape[i][0]); //find leftmost x
			maxX = Math.max(maxX, shape[i][0]);//rightmost x
			minY = Math.min(minY, shape[i][1]);//top most y
			maxY = Math.max(maxY, shape[i][1]);//bottom most y
		}

		return new int[] { minX, minY, maxX, maxY }; //return array representing bounding box
	}
}

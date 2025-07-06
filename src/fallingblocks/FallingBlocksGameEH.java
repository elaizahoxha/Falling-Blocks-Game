package fallingblocks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

// Falling Blocks Game 
// By: Elaiza Hoxha and Thomas Sinn
// Description: A game where the player avoids falling blocks. The blocks increase in speed as the player progresses through levels.

public class FallingBlocksGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer; // Timer to update the game
    private int playerX = 250; // Player's x position
    private final int playerWidth = PLAYER_WIDTH;
    private final int playerHeight = PLAYER_HEIGHT;
    private final int windowWidth = WINDOW_WIDTH;
    private final int windowHeight = WINDOW_HEIGHT;
    private ArrayList<Block> blocks; // List of blocks
    private Random random; // Random generator for block positions
    private final Color[] blockColors = {
        new Color(255, 204, 204),  // Light Pink
        new Color(204, 255, 204),  // Light Green
        new Color(204, 204, 255),  // Light Blue
        new Color(252, 240, 167),  // Light Yellow
        new Color(255, 204, 255)   // Light Purple
    };
    private final Color backgroundColor = new Color(255, 250, 240);  // Background color
    private final Color playerColor = new Color(255, 182, 193);  // Player color
    private int level = 1; // Current level
    private int blocksDodged = 0; // Number of blocks dodged
    private int blockSpeed = 2;  // Speed of blocks
    private boolean moveLeft = false; // Flag to move left
    private boolean moveRight = false; // Flag to move right

    // Constants for the game
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 20;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 800;
    private static final int TIMER_DELAY = 20;
    private static final int MOVE_SPEED = 10;

    public FallingBlocksGame() {
        this.setPreferredSize(new Dimension(windowWidth, windowHeight));
        this.setBackground(backgroundColor); // Set the background color
        this.setFocusable(true);
        this.addKeyListener(this); // Add key listener for player input

        timer = new Timer(TIMER_DELAY, this); // Initialize the timer
        blocks = new ArrayList<>(); // Initialize the list of blocks
        random = new Random(); // Initialize the random generator

        timer.start(); // Start the timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the player
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(playerColor);
        g2d.fill(new RoundRectangle2D.Float(playerX, windowHeight - playerHeight - 10, playerWidth, playerHeight, 10, 10));

        // Draw the blocks
        for (Block block : blocks) {
            g2d.setColor(block.color);
            g2d.fillRect(block.x, block.y, block.width, block.height);
        }

        // Draw the current level
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Level: " + level, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move the player
        if (moveLeft && playerX > 0) {
            playerX -= MOVE_SPEED;
        }
        if (moveRight && playerX < windowWidth - playerWidth) {
            playerX += MOVE_SPEED;
        }

        // Move the blocks down the screen
        for (Block block : blocks) {
            block.y += block.speed;
        }

        // Remove blocks that have fallen out of the screen
        blocks.removeIf(block -> block.y > windowHeight);

        // Add new blocks randomly
        if (random.nextInt(20) == 0) {
            blocks.add(new Block(random.nextInt(windowWidth - 20), 0, 20, 20, blockSpeed, getRandomBlockColor()));
        }

        // Check for collisions with the player
        for (Block block : blocks) {
            if (block.intersects(new Rectangle(playerX, windowHeight - playerHeight - 10, playerWidth, playerHeight))) {
                timer.stop(); // Stop the game
                int option = JOptionPane.showConfirmDialog(this, "Game Over! You reached level " + level + ". Do you want to restart?", "Game Over", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    resetGame();
                } else {
                    System.exit(0); // Exit the game
                }
            }
        }

        // Increase difficulty as more blocks are dodged
        blocksDodged++;
        if (blocksDodged % 50 == 0) {
            level++;
            blockSpeed++;  // Increase the speed of falling blocks with each level
        }

        repaint(); // Redraw the game
    }

    private void resetGame() {
        playerX = 250;
        blocks.clear();
        level = 1;
        blocksDodged = 0;
        blockSpeed = 2;
        moveLeft = false;
        moveRight = false;
        timer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft = true; // Move left
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight = true; // Move right
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft = false; // Stop moving left
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight = false; // Stop moving right
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    private class Block extends Rectangle {
        int speed;
        Color color;

        Block(int x, int y, int width, int height, int speed, Color color) {
            super(x, y, width, height);
            this.speed = speed;
            this.color = color;
        }
    }

    // Get a random color for a block
    private Color getRandomBlockColor() {
        return blockColors[random.nextInt(blockColors.length)];
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Falling Blocks Game");
        FallingBlocksGame gamePanel = new FallingBlocksGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

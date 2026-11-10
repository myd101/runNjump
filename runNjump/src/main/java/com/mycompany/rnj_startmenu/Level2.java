/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.rnj_startmenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
/**
 *
 * @author mamdi
 */
public class Level2 extends javax.swing.JFrame {
// Player variables (Chunky 8-bit scale)
    private int playerX = 100;
    private int playerY = 340; // Positioned above the higher ground
    private final int SCALE = 4;
    private int playerWidth = 8 * SCALE;  // 32 pixels wide
    private int playerHeight = 16 * SCALE; // 64 pixels tall
    private int xVelocity = 0;
    private int yVelocity = 0;
    
    // Movement speeds & Physics
    private final int MOVE_SPEED = 5;
    private final int JUMP_STRENGTH = -15;
    private final int GRAVITY = 1;
    private final int GROUND_Y = 440; // Higher ground level matching retro style
    
    // Enemy timer management (2 seconds = ~120 frames at 60 FPS)
    private int enemyTimer = 0;
    private Random random = new Random();
    
    // 2-Minute Countdown Timer for Level 2 (2 mins * 60 seconds * 60 FPS = 7200 frames)
    private int remainingFrames = 2 * 60 * 60;
    private JLabel timerLabel;
    
    // Moving Platforms with 2D simultaneous movement (increased speed for Level 2) and optional active enemy
    private class MovingPlatform {
        Rectangle rect;
        int vx, vy; // Velocity components for both horizontal and vertical movement
        Rectangle enemyRect; // Enemy riding on this platform
        boolean hasEnemy = false;

        public MovingPlatform(int x, int y, int width, int height, int vx, int vy) {
            this.rect = new Rectangle(x, y, width, height);
            this.vx = vx;
            this.vy = vy;
            this.enemyRect = new Rectangle(0, 0, 32, 24);
        }

        public void update() {
            rect.x += vx;
            rect.y += vy;

            // Bounce off left and right screen boundaries
            if (rect.x <= 0 || rect.x + rect.width >= 800) {
                vx = -vx;
            }
            // Bounce off top area and ground level
            if (rect.y <= 60 || rect.y + rect.height >= GROUND_Y - 10) {
                vy = -vy;
            }

            // Keep enemy locked on top of the moving platform if active
            if (hasEnemy) {
                enemyRect.x = rect.x + (rect.width / 2) - 16;
                enemyRect.y = rect.y - enemyRect.height;
            }
        }
    }

    private MovingPlatform[] platforms;
    
    // Open Door at top right
    private final Rectangle openDoor = new Rectangle(700, 80, 50, 80);
    
    // Key states
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean isGrounded = false;
    
    private Timer gameTimer;
    private GamePanel gamePanel; 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Level2.class.getName());

    /**
     * Creates new form Level2
     */
    public Level2() {
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Initialize all 5 platforms with increased speed for Level 2
        platforms = new MovingPlatform[] {
            new MovingPlatform(100, 320, 128, 24, 3, -2),
            new MovingPlatform(300, 240, 128, 24, -3, 2),
            new MovingPlatform(450, 180, 128, 24, 2, 3),
            new MovingPlatform(200, 120, 128, 24, -2, -3),
            new MovingPlatform(550, 100, 128, 24, 3, -2)
        };

        // Assign initial enemies to 2 random blocks
        shuffleEnemies();

        initGamePanel();
    }

    // Helper method to return to the StartPanel menu
    private void returnToMenu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        dispose();
        
        java.awt.EventQueue.invokeLater(() -> {
            StartPanel startMenu = new StartPanel();
            startMenu.setVisible(true);
        });
    }

    // Helper method to transition to Level 3
    private void goToLevel3() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        dispose(); // Close Level 2 window
        
        java.awt.EventQueue.invokeLater(() -> {
            Level3 level3Frame = new Level3();
            level3Frame.setVisible(true);
        });
    }

    private void initGamePanel() {
        // Initialize the custom game panel
        gamePanel = new GamePanel();
        gamePanel.setFocusable(true);

        // Add key listener for WASD, Space, and Escape
        gamePanel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                    leftPressed = true;
                }
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                    rightPressed = true;
                }
                if (key == KeyEvent.VK_SPACE) {
                    if (isGrounded) {
                        yVelocity = JUMP_STRENGTH;
                        isGrounded = false;
                    }
                }
                if (key == KeyEvent.VK_ESCAPE) {
                    returnToMenu();
                }
            }

            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                    leftPressed = false;
                }
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                    rightPressed = false;
                }
            }
        });

        // Create a top control panel aligned to the LEFT with text button and timer label
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(92, 148, 252));
        
        JButton btnBack = new JButton("Back to Menu");
        btnBack.setFocusable(false); // Prevents the button from stealing keyboard focus from the game
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                returnToMenu();
            }
        });
        topPanel.add(btnBack);

        // Setup 2-Minute Timer Display Label for Level 2
        timerLabel = new JLabel("TIME: 02:00");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Courier New", Font.BOLD, 16));
        topPanel.add(timerLabel);

        // Setup frame content pane
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(gamePanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        // Start game loop timer (~60 FPS)
        gameTimer = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateGame();
                gamePanel.repaint();
            }
        });
        gameTimer.start();

        // Request focus so key events register immediately
        gamePanel.requestFocusInWindow();
    }

    private void updateGame() {
        // Handle Countdown Timer logic (2-minute countdown)
        if (remainingFrames > 0) {
            remainingFrames--;
            int totalSeconds = remainingFrames / 60;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            timerLabel.setText(String.format("TIME: %02d:%02d", minutes, seconds));
            
            if (remainingFrames <= 0) {
                returnToMenu(); // Automatically returns to start panel when time runs out
                return;
            }
        }

        // Handle enemy respawn timer (Every 2 seconds = 120 frames at 60 FPS)
        enemyTimer++;
        if (enemyTimer >= 120) {
            enemyTimer = 0;
            shuffleEnemies(); // Disappear and reappear on different blocks
        }

        // Horizontal movement logic
        xVelocity = 0;
        if (leftPressed) {
            xVelocity = -MOVE_SPEED;
        }
        if (rightPressed) {
            xVelocity = MOVE_SPEED;
        }
        playerX += xVelocity;

        // Apply gravity & vertical movement
        yVelocity += GRAVITY;
        playerY += yVelocity;

        // Update all moving platforms and their enemies
        for (MovingPlatform mp : platforms) {
            mp.update();
        }

        // Assume airborne until collision check proves otherwise
        isGrounded = false;

        // Floor collision check
        if (playerY + playerHeight >= GROUND_Y) {
            playerY = GROUND_Y - playerHeight;
            yVelocity = 0;
            isGrounded = true;
        }

        // Player bounding box for collisions
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        // Platform & Enemy collision checks
        for (MovingPlatform mp : platforms) {
            // Check if player touches an active enemy -> send back to ground
            if (mp.hasEnemy && playerRect.intersects(mp.enemyRect)) {
                playerY = GROUND_Y - playerHeight;
                yVelocity = 0;
                isGrounded = true;
                break;
            }

            // Check platform collision (carrying both horizontal and vertical momentum)
            Rectangle p = mp.rect;
            if (playerX + playerWidth > p.x && playerX < p.x + p.width) {
                int feetY = playerY + playerHeight;
                if (feetY >= p.y && feetY <= p.y + 15 && yVelocity >= 0) {
                    playerY = p.y - playerHeight;
                    yVelocity = 0;
                    isGrounded = true;
                    // Carry both horizontal and vertical platform momentum
                    playerX += mp.vx;
                    playerY += mp.vy;
                }
            }
        }

        // Check if player reaches the open door at top right -> Transition to Level 3
        if (playerRect.intersects(openDoor)) {
            goToLevel3();
            return;
        }

        // Screen boundary limits
        if (playerX < 0) playerX = 0;
        if (playerX > gamePanel.getWidth() - playerWidth) {
            playerX = gamePanel.getWidth() - playerWidth;
        }
    }

    private void shuffleEnemies() {
        for (MovingPlatform mp : platforms) {
            mp.hasEnemy = false;
        }
        int count = 0;
        while (count < 2) {
            int idx = random.nextInt(platforms.length);
            if (!platforms[idx].hasEnemy) {
                platforms[idx].hasEnemy = true;
                count++;
            }
        }
    }

    // Inner panel class for chunky 8-bit retro rendering
    private class GamePanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            // Emulate chunky 8-bit pixels by disabling antialiasing entirely
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            int width = getWidth();
            int height = getHeight();

            // 1. Classic 8-Bit Sky Blue Background
            g2d.setColor(new Color(92, 148, 252));
            g2d.fillRect(0, 0, width, height);

            // 2. Chunky Pixel Clouds
            g2d.setColor(Color.WHITE);
            g2d.fillRect(80, 40, 24 * SCALE, 6 * SCALE);
            g2d.fillRect(100, 28, 12 * SCALE, 4 * SCALE);
            
            g2d.fillRect(520, 60, 30 * SCALE, 6 * SCALE);
            g2d.fillRect(540, 48, 16 * SCALE, 4 * SCALE);

            // 3. Chunky Pixel Hills (Retro block stair steps)
            g2d.setColor(new Color(0, 168, 0));
            g2d.fillRect(40, GROUND_Y - (16 * SCALE), 8 * SCALE, 16 * SCALE);
            g2d.fillRect(48, GROUND_Y - (24 * SCALE), 16 * SCALE, 24 * SCALE);
            g2d.fillRect(64, GROUND_Y - (32 * SCALE), 8 * SCALE, 32 * SCALE);
            
            g2d.fillRect(520, GROUND_Y - (20 * SCALE), 10 * SCALE, 20 * SCALE);
            g2d.fillRect(530, GROUND_Y - (34 * SCALE), 18 * SCALE, 34 * SCALE);
            g2d.fillRect(548, GROUND_Y - (20 * SCALE), 10 * SCALE, 20 * SCALE);

            // 4. Chunky 8-Bit Brick Floor Tiles (High ground position)
            int tileSize = 8 * SCALE; // 32x32 pixel tiles
            for (int x = 0; x < width; x += tileSize) {
                for (int y = GROUND_Y; y < height; y += tileSize) {
                    // Brick Base Color
                    g2d.setColor(new Color(200, 76, 12));
                    g2d.fillRect(x, y, tileSize, tileSize);
                    
                    // Thick Black Retro Mortar Grid Lines
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, tileSize, tileSize);
                    g2d.drawLine(x, y + (tileSize / 2), x + tileSize, y + (tileSize / 2));
                    
                    // Inner chunky pixel brick highlights
                    g2d.setColor(new Color(230, 115, 40));
                    g2d.fillRect(x + SCALE, y + SCALE, (tileSize / 2) - (SCALE / 2), (tileSize / 2) - (SCALE / 2));
                    g2d.fillRect(x + (tileSize / 2) + (SCALE / 2), y + (tileSize / 2) + (SCALE / 2), (tileSize / 2) - (SCALE / 2), (tileSize / 2) - (SCALE / 2));
                }
            }

            // 5. Draw Open Door at the top right
            g2d.setColor(Color.BLACK);
            g2d.drawRect(openDoor.x, openDoor.y, openDoor.width, openDoor.height);
            g2d.setColor(new Color(255, 215, 0, 220));
            g2d.fillRect(openDoor.x + 4, openDoor.y + 4, openDoor.width - 8, openDoor.height - 4);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(openDoor.x + 8, openDoor.y + 40, 6, 6);

            // 6. Draw all 5 Moving Platforms as Chunky 8-Bit Brick Blocks and render enemies if active
            for (MovingPlatform mp : platforms) {
                for (int bx = mp.rect.x; bx < mp.rect.x + mp.rect.width; bx += tileSize) {
                    g2d.setColor(new Color(200, 76, 12));
                    g2d.fillRect(bx, mp.rect.y, tileSize, mp.rect.height);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(bx, mp.rect.y, tileSize, mp.rect.height);
                    g2d.setColor(new Color(230, 115, 40));
                    g2d.fillRect(bx + SCALE, mp.rect.y + SCALE, tileSize - (SCALE * 2), 2 * SCALE);
                }

                // Draw Chunky 8-Bit Enemies only on platforms chosen by the timer
                if (mp.hasEnemy) {
                    g2d.setColor(new Color(180, 40, 40)); // Retro Red Enemy
                    g2d.fillRect(mp.enemyRect.x, mp.enemyRect.y, mp.enemyRect.width, mp.enemyRect.height);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(mp.enemyRect.x, mp.enemyRect.y, mp.enemyRect.width, mp.enemyRect.height);
                    
                    // Enemy Eyes
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(mp.enemyRect.x + 4, mp.enemyRect.y + 6, 6, 6);
                    g2d.fillRect(mp.enemyRect.x + 22, mp.enemyRect.y + 6, 6, 6);
                }
            }

            // 7. Draw Completely White Chunky 8-Bit Pixel Character
            g2d.setColor(Color.WHITE);
            // Head block
            g2d.fillRect(playerX + (2 * SCALE), playerY, 4 * SCALE, 4 * SCALE);
            // Torso block
            g2d.fillRect(playerX + (2 * SCALE), playerY + (4 * SCALE), 4 * SCALE, 5 * SCALE);
            // Arms block
            g2d.fillRect(playerX, playerY + (5 * SCALE), 8 * SCALE, 2 * SCALE);
            // Legs blocks
            g2d.fillRect(playerX + SCALE, playerY + (9 * SCALE), 2 * SCALE, 5 * SCALE);
            g2d.fillRect(playerX + (5 * SCALE), playerY + (9 * SCALE), 2 * SCALE, 5 * SCALE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            Level2 frame = new Level2();
            frame.setVisible(true);
            if (frame.gamePanel != null) {
                frame.gamePanel.requestFocusInWindow();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

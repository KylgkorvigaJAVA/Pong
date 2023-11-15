package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PongGame extends JPanel {

    private static final int TITLE_SCREEN_STATE = 0;
    private static final int PVP_STATE = 1;
    private static final int PVC_STATE = 2;

    private Paddle paddleLeft;
    private Paddle paddleRight;
    private Paddle computerPaddle;
    private Ball ball;
    private int playerLeftScore = 0;
    private int playerRightScore = 0;
    private int gameState;

    public PongGame() {
        gameState = TITLE_SCREEN_STATE;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e);
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        Timer timer = new Timer(10, e -> {
            if(gameState == TITLE_SCREEN_STATE) {

                repaint();
            } else if(gameState == PVP_STATE) {
                updatePaddles();
                handleBallOffScreen();
                ball.move();
                ball.checkPaddleCollision(paddleLeft);
                ball.checkPaddleCollision(paddleRight);
                ball.checkWallCollision(getHeight());
                repaint();
            } else if(gameState == PVC_STATE) {
                updatePvCPaddle();
                handleBallOffScreen();
                ball.move();
                ball.checkPaddleCollision(paddleRight);
                ball.checkPaddleCollision(computerPaddle);
                ball.checkWallCollision(getHeight());
                repaint();
            }
        });
        timer.start();
    }

    private void handleKeyPress(KeyEvent e) {
        if(gameState == TITLE_SCREEN_STATE) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                startGamePvP();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGamePvC();
            }
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_S:
                    paddleLeft.keyPressed(e.getKeyCode());
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                    paddleRight.keyPressed(e.getKeyCode());
                    break;
            }
        }
        repaint();
    }

    private void handleKeyRelease(KeyEvent e) {
        // Handle key release during the game
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
                paddleLeft.keyReleased(e.getKeyCode());
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                paddleRight.keyReleased(e.getKeyCode());
                break;
        }
        repaint();
    }

    private void updatePaddles() {
        paddleLeft.update();
        paddleRight.update();
    }

    private void updatePvCPaddle() {
        if(computerPaddle != null) {
            computerPaddle.updateForComputer(ball.getY());
        }
    }

    private void handleBallOffScreen() {
        int ballX = ball.getX();
        int ballDiameter = ball.getDiameter();
        int screenWidth = getWidth();

        if(ballX < 0) {
            playerLeftScore++;
            resetBall();
        } else if(ballX > screenWidth - ballDiameter) {
            playerRightScore++;
            resetBall();
        }
    }

    private void resetBall() {
        ball.setX(getWidth()/2);
        ball.setY(getWidth()/2);
    }

    private void startGamePvP() {
        paddleLeft = new Paddle(14, 240);
        paddleRight = new Paddle(760,240);
        ball = new Ball(400,300);
        gameState = PVP_STATE;
        repaint();
    }

    private void startGamePvC() {
        paddleLeft = new Paddle(14,240);
        computerPaddle = new Paddle(20, getHeight() / 2 - 80/2);
        ball = new Ball(400,300);
        gameState = PVC_STATE;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(gameState == TITLE_SCREEN_STATE) {
            drawTitleScreen(g);
        } else if (gameState == PVP_STATE) {
            drawGameScreen(g);
            paddleLeft.draw(g);
            paddleRight.draw(g);
            ball.draw(g);
        } else if (gameState == PVC_STATE) {
            drawGameScreen(g);
            paddleLeft.draw(g);
            computerPaddle.draw(g);
            ball.draw(g);
        }
    }

    private void drawTitleScreen(Graphics g) {
        drawGameScreen(g);  // Draw the basic game screen

        // Draw the title text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 40));
        g.drawString("Pong", 340, 100);

        // Draw instructions
        g.setFont(new Font("Monospaced", Font.PLAIN, 20));
        g.drawString("Press Spacebar for PvP", 280, 200);
        g.drawString("Press Enter for PvC", 290, 250);
    }

    private void drawGameScreen(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0,0,getWidth(),getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(8.0f));
        g2d.drawLine(0,0,getWidth(),0);
        g2d.drawLine(0,getHeight()-1, getWidth(),getHeight() - 1);

        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());

//        paddleLeft.draw(g);
//        paddleRight.draw(g);
//        ball.draw(g);

        g.setFont(new Font("Monospaced", Font.PLAIN, 40));
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(playerLeftScore),405,45);
        g.drawString(String.valueOf(playerRightScore),355, 45);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pong");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800,600);

            frame.setLocationRelativeTo(null);
            PongGame pongGame = new PongGame();
            frame.add(pongGame);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth, boardHeight, tileSize = 30;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;

    int velocityX, velocityY;
    Timer gameLoop;
    boolean gameOver = false;
    boolean isPaused = false;

    int ogSpeed = 150;
    int score = 0;
    int highScore = 0;
    int speed = ogSpeed;



    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        initGame();
    }

    public void initGame() {
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        gameOver = false;
        isPaused = false;
        score = 0;
        speed = ogSpeed;

        gameLoop = new Timer(speed, this);
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Game Over! Score: " + score, boardWidth / 2 - 80, boardHeight / 2);
            g.drawString("High Score: " + highScore, boardWidth / 2 - 80, boardHeight / 2 + 30);
            g.drawString("Press 'R' to Restart", boardWidth / 2 - 70, boardHeight / 2 + 60);
            return;
        }

        if (isPaused) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("PAUSED", boardWidth / 2 - 40, boardHeight / 2);
            return;
        }

        g.setColor(Color.darkGray);
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        g.setColor(Color.green);
        for (Tile snakePart : snakeBody) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, boardWidth / 2 - 50, 20);
        g.drawString("High Score: " + highScore, boardWidth / 2 - 50, 40);
        g.drawString("Snake Speed: " + (ogSpeed - speed), boardWidth / 2 - 50, 60);
    }

    public void placeFood() {
        boolean valid;
        do {
            valid = true;
            food.x = random.nextInt(boardWidth / tileSize);
            food.y = random.nextInt(boardHeight / tileSize);
            if (collision(snakeHead, food)) {
                valid = false;
            }
            for (Tile snakePart : snakeBody) {
                if (collision(snakePart, food)) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);
    }

    public void move() {
        if (isPaused || gameOver) return;

        int prevX = snakeHead.x;
        int prevY = snakeHead.y;

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize ||
                snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
            updateHighScore();
            return;
        }

        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                updateHighScore();
                return;
            }
        }

        if (!snakeBody.isEmpty()) {
            snakeBody.add(0, new Tile(prevX, prevY));
            if (!collision(snakeHead, food)) {
                snakeBody.remove(snakeBody.size() - 1);
            } else {
                increaseScore();
                placeFood();
            }
        } else if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(prevX, prevY));
            increaseScore();
            placeFood();
        }
    }

    public void increaseScore() {
        score++;
        if (score > highScore) highScore = score;

        if (score % 5 == 0 && speed > 20) {
            speed -= 10;
            gameLoop.setDelay(speed);
        }
    }

    public void updateHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) gameLoop.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_S && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_A && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_D && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            initGame();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isPaused = !isPaused;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}

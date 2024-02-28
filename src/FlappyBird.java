import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int width = 360;
    int height = 640;

    //Image
    Image birdImg;
    Image backgroundImg;
    Image bottomPipeImg;
    Image topPipeImg;

    //Bird
    int birdX = width/8;
    int birdY = height/2;
    int birdWidth = 34;
    int birdHeight = 24;



    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = width;
    int pipeY = 0;
    int pipeWidth = 64; // scale 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;

        Pipe(Image img) {
            this.img = img;
        }
        boolean passed = false;
    }

    // Game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;

    // Timer
    Timer gameLoop;
    Timer placePipesTimer;

    boolean isGameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottomPipe.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./topPipe.png")).getImage();

        // Create bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // Game loop
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = height/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + topPipe.height + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw background
        g.drawImage(backgroundImg, 0, 0, width, height, null);

        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (isGameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), width/2 - 50, height/2 - 50);
        }
        else {
            g.drawString("Score: " + String.valueOf((int) score), 10, 25);
        }
    }

    public void move() {
        // Move bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(0, bird.y);

        // Move pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (isCollision(bird, pipe)) {
                isGameOver = true;
            }
        }

        if (bird.y > height) {
            isGameOver = true;
        }
    }

    public boolean isCollision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   // a's left edge is to the left of b's right edge
                a.x + a.width > b.x &&  // a's right edge is to the right of b's left edge
                a.y < b.y + b.height && // a's top edge is above b's bottom edge
                a.y + a.height > b.y;   // a's bottom edge is below b's top edge
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (isGameOver) {
            gameLoop.stop();
            placePipesTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (isGameOver) {
                // reset game
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                isGameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
}

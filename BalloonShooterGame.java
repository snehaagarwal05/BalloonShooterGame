import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BalloonShooterGame extends JPanel implements ActionListener, KeyListener {

    static final int WIDTH = 900;
    static final int HEIGHT = 600;

    int gunY = HEIGHT / 2;

    List<Bullet> bullets = new ArrayList<>();
    List<Balloon> balloons = new ArrayList<>();

    javax.swing.Timer gameTimer;
    javax.swing.Timer spawnTimer;

    int score = 0;
    Random rand = new Random();

    boolean bigGunMode = false;
    String secretCode = "";

    public BalloonShooterGame() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        gameTimer = new javax.swing.Timer(25, this);
        gameTimer.start();

        spawnTimer = new javax.swing.Timer(1000, e -> spawnBalloon());
        spawnTimer.start();
    }

    void spawnBalloon() {
        int y = rand.nextInt(HEIGHT - 150) + 75;
        balloons.add(new Balloon(WIDTH, y));
    }

    void activateHack() {
        bigGunMode = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        for (Bullet b : bullets) b.x += 12;
        for (Balloon b : balloons) b.x -= 2;

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Iterator<Balloon> balloonIterator = balloons.iterator();
            int hitCount = 0;

            while (balloonIterator.hasNext()) {
                Balloon balloon = balloonIterator.next();
                if (bullet.getBounds().intersects(balloon.getBounds())) {
                    balloonIterator.remove();
                    hitCount++;
                }
            }

            if (hitCount > 0) {
                score += hitCount;
                bulletIterator.remove();
            }
        }

        bullets.removeIf(b -> b.x > WIDTH);
        balloons.removeIf(b -> b.x < -100);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int gunWidth = bigGunMode ? 100 : 50;

        g.setColor(Color.GRAY);
        g.fillRect(10, gunY + 20, 20, 40); 
        g.fillRect(30, gunY + 25, gunWidth, 15); 
        g.fillRect(30 + gunWidth, gunY + 28, 10, 10);

        g.setColor(Color.YELLOW);
        for (Bullet b : bullets) {
            g.fillRect(b.x, b.y, 10, 5);
        }

        for (Balloon b : balloons) {
            b.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Score: " + score, 20, 30);

        if (bigGunMode) {
            g.setColor(Color.RED);
            g.drawString("HACK MODE", WIDTH/2 - 60, 40);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // Secret Code Detection
        secretCode += KeyEvent.getKeyText(e.getKeyCode()).toUpperCase();
        if (secretCode.length() > 4) {
            secretCode = secretCode.substring(secretCode.length() - 4);
        }
        if (secretCode.equals("HACK")) {
            activateHack();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            gunY -= 20;
            if (gunY < 0) gunY = 0;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            gunY += 20;
            if (gunY > HEIGHT - 100) gunY = HEIGHT - 100;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            if (bigGunMode) {
                // 5 bullets (spread vertically)
                bullets.add(new Bullet(130, gunY + 10));
                bullets.add(new Bullet(130, gunY + 20));
                bullets.add(new Bullet(130, gunY + 30));
                bullets.add(new Bullet(130, gunY + 40));
                bullets.add(new Bullet(130, gunY + 50));
            } else {
                bullets.add(new Bullet(90, gunY + 30));
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Balloon Shooter Game");
        BalloonShooterGame game = new BalloonShooterGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static class Bullet {
        int x, y;

        Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, 10, 5);
        }
    }

    static class Balloon {
        int x, y;
        Color color;

        Balloon(int x, int y) {
            this.x = x;
            this.y = y;

            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN,
                    Color.MAGENTA, Color.ORANGE};
            color = colors[new Random().nextInt(colors.length)];
        }

        void draw(Graphics g) {

            g.setColor(color);
            g.fillOval(x - 25, y - 25, 50, 50);

            g.setColor(color.darker());
            int[] xPoints = {x - 5, x + 5, x};
            int[] yPoints = {y + 25, y + 25, y + 35};
            g.fillPolygon(xPoints, yPoints, 3);

            g.setColor(Color.WHITE);
            g.drawLine(x, y + 35, x, y + 55);
        }

        Rectangle getBounds() {
            return new Rectangle(x - 25, y - 25, 50, 80);
        }
    }
}

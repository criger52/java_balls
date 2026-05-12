package catchballs;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Container;
import java.util.List;
import java.util.ArrayList;

public class CatchGameTable extends JPanel implements GameConstants {

    private JLabel statusLabel;
    private int basketX = WINDOW_WIDTH / 2 - BASKET_WIDTH / 2;
    private List<Ball> balls = new ArrayList<>();
    private int score = 0;
    private int lives = LIVES_START;
    private boolean gameRunning = true;
    private boolean paused = false;
    private int currentSpeedLevel = 1;
    private int combo = 0; // комбо-счетчик для уникальности

    Dimension preferredSize = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public CatchGameTable() {
        setFocusable(true);
        setBackground(BACKGROUND_COLOR);
    }

    void addPanelToFrame(Container container) {
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(this);
        statusLabel = new JLabel("N - новая игра | P - пауза | Q - выход");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(0, 0, 0, 150));
        container.add(statusLabel);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Фон с градиентом
        for (int i = 0; i < WINDOW_HEIGHT; i++) {
            int r = 20 + i / 30;
            int gb = 25 + i / 40;
            g.setColor(new Color(r, gb, 45));
            g.drawLine(0, i, WINDOW_WIDTH, i);
        }

        // Нарисовать все шарики
        for (Ball ball : balls) {
            if (ball.isActive()) {
                ball.draw(g);
            }
        }

        // Нарисовать корзинку
        g.setColor(BASKET_COLOR);
        g.fillRoundRect(basketX, BASKET_Y, BASKET_WIDTH, BASKET_HEIGHT, 10, 10);

        // Ободок корзинки
        g.setColor(new Color(255, 220, 100));
        g.drawRoundRect(basketX, BASKET_Y, BASKET_WIDTH, BASKET_HEIGHT, 10, 10);

        // Счет и жизни
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(TEXT_COLOR);
        g.drawString("Счет: " + score, 20, 50);
        g.drawString("Жизни: " + lives, 20, 85);

        // Комбо (уникальная фишка)
        if (combo > 2) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(new Color(255, 200, 0));
            g.drawString("COMBO x" + combo + "!", WINDOW_WIDTH - 120, 50);
        }

        // Сообщение о паузе или конце игры
        if (paused) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.YELLOW);
            String pauseText = "ПАУЗА";
            int textWidth = g.getFontMetrics().stringWidth(pauseText);
            g.drawString(pauseText, WINDOW_WIDTH/2 - textWidth/2, WINDOW_HEIGHT/2);
        } else if (!gameRunning) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.RED);
            String endText = (score >= WIN_SCORE) ? "ПОБЕДА!" : "ИГРА ОКОНЧЕНА";
            int textWidth = g.getFontMetrics().stringWidth(endText);
            g.drawString(endText, WINDOW_WIDTH/2 - textWidth/2, WINDOW_HEIGHT/2);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            String newGameText = "Нажми N для новой игры";
            int newGameWidth = g.getFontMetrics().stringWidth(newGameText);
            g.drawString(newGameText, WINDOW_WIDTH/2 - newGameWidth/2, WINDOW_HEIGHT/2 + 50);
        }

        // Скорость игры
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Скорость: " + currentSpeedLevel, WINDOW_WIDTH - 100, 85);

        requestFocus();
    }

    // Методы для обновления состояния из движка
    public void setBasketX(int x) {
        this.basketX = x;
        repaint();
    }

    public int getBasketX() {
        return basketX;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
        repaint();
    }

    public void setScore(int score) {
        this.score = score;
        repaint();
    }

    public void setLives(int lives) {
        this.lives = lives;
        repaint();
    }

    public void setGameRunning(boolean running) {
        this.gameRunning = running;
        repaint();
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        repaint();
    }

    public void setCombo(int combo) {
        this.combo = combo;
        repaint();
    }

    public void setCurrentSpeedLevel(int level) {
        this.currentSpeedLevel = level;
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Catch the Balls - Поймай шарики!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        CatchGameTable table = new CatchGameTable();
        table.addPanelToFrame(frame.getContentPane());

        GameEngine engine = new GameEngine(table);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
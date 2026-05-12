package catchballs;

import java.awt.Graphics;
import java.awt.Color;

public class Ball {
    private int x, y;           // координаты
    private int size;           // размер
    private boolean isGood;     // true - зеленый (+очки), false - красный (-жизни)
    private int speed;          // скорость падения
    private boolean active;     // активен ли шарик на экране

    public Ball(int x, int y, int size, boolean isGood, int speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.isGood = isGood;
        this.speed = speed;
        this.active = true;
    }

    // Движение вниз
    public void fall() {
        y += speed;
    }

    // Проверка, упал ли ниже экрана
    public boolean isOutOfScreen() {
        return y > GameConstants.WINDOW_HEIGHT;
    }

    // Проверка столкновения с корзинкой
    public boolean collidesWith(int basketX, int basketY, int basketW, int basketH) {
        return (x + size > basketX &&
                x < basketX + basketW &&
                y + size > basketY &&
                y < basketY + basketH);
    }

    // Отрисовка шарика
    public void draw(Graphics g) {
        if (!active) return;

        if (isGood) {
            g.setColor(GameConstants.GOOD_BALL_COLOR);
        } else {
            g.setColor(GameConstants.BAD_BALL_COLOR);
        }
        g.fillOval(x, y, size, size);

        // Блик для красоты
        g.setColor(new Color(255, 255, 255, 100));
        g.fillOval(x + 5, y + 5, 5, 5);
    }

    // Геттеры и сеттеры
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isGood() { return isGood; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
package catchballs;

import java.awt.Color;

public interface GameConstants {
    // Размеры окна
    int WINDOW_WIDTH = 500;
    int WINDOW_HEIGHT = 600;

    // Размеры корзинки
    int BASKET_WIDTH = 80;
    int BASKET_HEIGHT = 20;
    int BASKET_Y = WINDOW_HEIGHT - 80;

    // Размеры шарика
    int BALL_SIZE = 25;

    // Скорости
    int BALL_FALL_SPEED = 3;
    int MAX_FALL_SPEED = 12;
    int SPEED_INCREMENT = 1; // увеличение скорости каждые 10 очков

    // Игровые параметры
    int LIVES_START = 3;
    int WIN_SCORE = 60;
    int BALL_GENERATION_DELAY = 800; // миллисекунды между появлением шариков

    // Цвета
    Color BACKGROUND_COLOR = new Color(20, 25, 45);  // темно-синий космос
    Color BASKET_COLOR = new Color(255, 180, 50);   // золотая корзина
    Color GOOD_BALL_COLOR = new Color(50, 230, 80); // зеленый + очки
    Color BAD_BALL_COLOR = new Color(230, 50, 50);  // красный - жизни
    Color TEXT_COLOR = Color.WHITE;

    // Скорость игрового цикла (мс)
    int GAME_LOOP_SLEEP = 16; // ~60 FPS
}
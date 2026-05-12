package catchballs;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameEngine implements Runnable, MouseMotionListener, KeyListener, GameConstants {

    private CatchGameTable table;
    private List<Ball> balls;
    private Random random = new Random();
    private int score = 0;
    private int lives = LIVES_START;
    private boolean gameRunning = true;
    private boolean paused = false;
    private int currentFallSpeed = BALL_FALL_SPEED;
    private int speedLevel = 1;
    private int combo = 0;
    private long lastBallSpawnTime = 0;

    public GameEngine(CatchGameTable table) {
        this.table = table;
        this.balls = new ArrayList<>();

        // Подключаем обработчики
        table.addMouseMotionListener(this);
        table.addKeyListener(this);

        // Запускаем игровой поток
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!gameRunning || paused) return;

        int mouseX = e.getX() - BASKET_WIDTH / 2;
        if (mouseX < 0) mouseX = 0;
        if (mouseX > WINDOW_WIDTH - BASKET_WIDTH) mouseX = WINDOW_WIDTH - BASKET_WIDTH;

        table.setBasketX(mouseX);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();

        if (key == 'n' || key == 'N') {
            startNewGame();
        } else if (key == 'q' || key == 'Q') {
            System.exit(0);
        } else if (key == 'p' || key == 'P') {
            togglePause();
        }
    }

    private void togglePause() {
        if (!gameRunning) return;
        paused = !paused;
        table.setPaused(paused);
        table.setStatusMessage(paused ? "ПАУЗА | N - новая игра, P - продолжить" : "N - новая игра | P - пауза | Q - выход");
    }

    private void startNewGame() {
        score = 0;
        lives = LIVES_START;
        combo = 0;
        currentFallSpeed = BALL_FALL_SPEED;
        speedLevel = 1;
        gameRunning = true;
        paused = false;
        balls.clear();

        table.setScore(score);
        table.setLives(lives);
        table.setGameRunning(true);
        table.setPaused(false);
        table.setCombo(0);
        table.setCurrentSpeedLevel(1);
        table.setStatusMessage("Игра началась! Лови зеленые шарики!");
    }

    private void spawnBall() {
        if (!gameRunning || paused) return;

        int x = random.nextInt(WINDOW_WIDTH - BALL_SIZE);
        boolean isGood = random.nextBoolean();

        // Чем выше скорость, тем больше красных шариков
        if (speedLevel >= 5) {
            isGood = random.nextDouble() < 0.4; // 40% зеленых на высокой скорости
        } else if (speedLevel >= 3) {
            isGood = random.nextDouble() < 0.5; // 50% зеленых
        }

        Ball ball = new Ball(x, 0, BALL_SIZE, isGood, currentFallSpeed);
        balls.add(ball);
    }

    private void updateBalls() {
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);

            if (!ball.isActive()) {
                continue;
            }

            ball.fall();

            // Проверка столкновения с корзинкой
            if (ball.collidesWith(table.getBasketX(), BASKET_Y, BASKET_WIDTH, BASKET_HEIGHT)) {
                if (ball.isGood()) {
                    // Зеленый шарик - добавляем очки с комбо
                    int points = 10 + (combo / 2);
                    score += points;
                    combo++;

                    // Эффект комбо
                    if (combo >= 3 && combo % 3 == 0) {
                        table.setStatusMessage("🔥 КОМБО x" + combo + "! +" + points + " очков!");
                    }

                    table.setScore(score);
                    table.setCombo(combo);

                    // Увеличение скорости каждые 10 очков
                    if (score / 10 > speedLevel - 1) {
                        increaseSpeed();
                    }
                } else {
                    // Красный шарик - теряем жизнь
                    lives--;
                    combo = 0; // сброс комбо
                    table.setLives(lives);
                    table.setCombo(0);
                    table.setStatusMessage("💔 Потеряна жизнь! Осталось: " + lives);

                    // Проверка на проигрыш
                    if (lives <= 0) {
                        gameRunning = false;
                        table.setGameRunning(false);
                        table.setStatusMessage("Игра окончена! Нажми N для новой игры");
                    }
                }
                ball.setActive(false);
                continue;
            }

            // Проверка, упал ли шарик ниже экрана
            if (ball.isOutOfScreen()) {
                if (!ball.isGood()) {
                    // Красный шарик упал - ничего не происходит
                    ball.setActive(false);
                } else {
                    // Зеленый шарик пропустили - теряем жизнь
                    lives--;
                    combo = 0;
                    table.setLives(lives);
                    table.setCombo(0);
                    table.setStatusMessage("❌ Пропущен зеленый! -1 жизнь");

                    if (lives <= 0) {
                        gameRunning = false;
                        table.setGameRunning(false);
                        table.setStatusMessage("Игра окончена! Нажми N для новой игры");
                    }
                }
                ball.setActive(false);
            }
        }

        // Удаляем неактивные шарики
        balls.removeIf(ball -> !ball.isActive());

        // Проверка на победу
        if (score >= WIN_SCORE) {
            gameRunning = false;
            table.setGameRunning(false);
            table.setStatusMessage("ПОБЕДА! Нажми N для новой игры");
        }
    }

    private void increaseSpeed() {
        speedLevel++;
        currentFallSpeed = Math.min(MAX_FALL_SPEED, BALL_FALL_SPEED + (speedLevel - 1) * SPEED_INCREMENT);
        table.setCurrentSpeedLevel(speedLevel);

        // Обновляем скорость всех существующих шариков
        for (Ball ball : balls) {
            // Создаем новый шарик со старой скоростью нельзя
            // проще просто установить новую скорость для всех
        }
        table.setStatusMessage("⚡ СКОРОСТЬ ПОВЫШЕНА! Уровень " + speedLevel);
    }

    @Override
    public void run() {
        lastBallSpawnTime = System.currentTimeMillis();

        while (true) {
            if (gameRunning && !paused) {
                long currentTime = System.currentTimeMillis();

                // Создание новых шариков
                if (currentTime - lastBallSpawnTime >= BALL_GENERATION_DELAY) {
                    spawnBall();
                    lastBallSpawnTime = currentTime;
                }

                // Обновление позиций шариков
                updateBalls();

                // Обновление отображения
                table.setBalls(balls);
            }

            try {
                Thread.sleep(GAME_LOOP_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
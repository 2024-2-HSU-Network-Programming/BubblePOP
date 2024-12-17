package client;

public class GameScore {
    private int score;

    public GameScore() {
        this.score = 0;
    }

    // 제거된 버블 개수에 따른 점수 계산
    public int calculateScore(int bubbleCount) {
        return switch (bubbleCount) {
            case 1 -> 10;  // 1개: 10점
            case 2 -> 30;  // 2개: 30점
            case 3 -> 40;  // 3개: 40점
            case 4 -> 50;  // 4개: 50점
            case 5 -> 70;  // 5개: 70점
            case 6 -> 90;  // 6개: 90점
            case 7 -> 110; // 7개: 110점
            case 8 -> 130; // 8개: 130점
            case 9 -> 150; // 9개: 150점
            case 10 -> 170; // 10개: 170점
            default -> bubbleCount > 10 ? 200 : 0; // 10개 초과: 200점
        };
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

class GameTimer extends Thread {
    private int remainingTime = 40; // 30초 타이머
    private boolean isRunning = true;
    private final OriginalGameScreen gameScreen;

    public GameTimer(OriginalGameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void run() {
        while (isRunning && remainingTime > 0) {
            try {
                Thread.sleep(1000); // 1초 대기
                remainingTime--;
                gameScreen.updateTimer(remainingTime);

                if (remainingTime == 0) {
                    gameScreen.endGame(); // 시간 종료 시 게임 종료
                    isRunning = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopTimer() {
        isRunning = false;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
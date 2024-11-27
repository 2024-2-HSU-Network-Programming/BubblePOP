package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GameScreen {
    public static void main(String[] args) {
        // 프레임 생성
        JFrame frame = new JFrame("게임화면");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 672);
        frame.setResizable(false);

        // 배경 패널 설정
        GamePanel backgroundPanel = new GamePanel();
        backgroundPanel.setLayout(null); // 배경 위에 컴포넌트 배치를 위해 절대 레이아웃 설정

        // 예제 컴포넌트 추가 (플레이어 영역)
        JLabel player1Label = new JLabel("Player 1");
        player1Label.setBounds(160, 20, 100, 30); // 위치와 크기 설정
        player1Label.setForeground(Color.WHITE);
        player1Label.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(player1Label);

        JLabel player2Label = new JLabel("Player 2");
        player2Label.setBounds(650, 20, 100, 30); // 위치와 크기 설정
        player2Label.setForeground(Color.WHITE);
        player2Label.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(player2Label);

        // 배경 패널 추가
        frame.add(backgroundPanel);
        frame.setVisible(true);

        // 키보드 이벤트 추가
        frame.addKeyListener(backgroundPanel);
    }
}

class GamePanel extends JPanel implements KeyListener {
    private BufferedImage arrowImage; // 화살표 이미지

    private BufferedImage cc1; // 발사대 상단 이미지

    private BufferedImage dd1, dd2, dd3, dd4, dd5, dd6, dd7, dd8, dd9, dd10, dd11, dd12; // 발사대 하단 이미지
    private BufferedImage gameBottom; // 게임 하단 이미지

    private BufferedImage b1, b2, b3, b4, b5, b6, b7; // 버블 이미지
    private double angle = 0 ; // 화살표의 현재 각도 (라디안 값)
    private final double maxAngle = Math.toRadians(50); // 최대 각도 (50도)
    private final double minAngle = Math.toRadians(-50); // 최소 각도 (-50도)
    private final double rotationSpeed = Math.toRadians(4); // 회전 속도

    private boolean isBubbleMoving = false; // 구슬이 발사 중인지 여부
    private int bubbleX, bubbleY; // 발사되는 구슬의 현재 위치
    private final int bubbleSpeed = 8; // 구슬의 이동 속도

    private double dx, dy; // 구슬의 이동 방향 멤버 변수로 ...


    private int currentBubbleType = 1; // 발사대 구슬의 타입 (1~7)
    private int[][] board = { // 구슬 상태를 저장하는 배열
            {1, 2, 3, 4, 5, 6, 7, 1},   // 첫 번째 줄 (8개)
            {1, 2, 3, 4, 5, 6, 7, 0},   // 두 번째 줄 (7개 + 오른쪽 빈칸)
            {1, 2, 3, 4, 5, 6, 7, 1},   // 세 번째 줄 (8개)
            {1, 2, 3, 4, 5, 6, 7, 0}    // 네 번째 줄 (7개 + 오른쪽 빈칸)
    };

    public GamePanel() {
        try {
            // 화살표 이미지 로드
            arrowImage = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/arrow.png"));
            cc1 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/cc1.png"));
            dd1 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd1.png"));
            dd2 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd2.png"));
            dd3 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd3.png"));
            dd4 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd4.png"));
            dd5 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd5.png"));
            dd6 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd6.png"));
            dd7 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd7.png"));
            dd8 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd8.png"));
            dd9 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd9.png"));
            dd10 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd10.png"));
            dd11 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd11.png"));
            dd12 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd12.png"));
            b1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble1.png"));
            b2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble2.png"));
            b3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble3.png"));
            b4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble4.png"));
            b5 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble5.png"));
            b6 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble6.png"));
            b7 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble7.png"));
            gameBottom = ImageIO.read(getClass().getClassLoader().getResource("assets/game/gamebottom.png"));

            // 발사대 구슬 초기 위치 설정
            bubbleX = 212; // 발사대 중심 X 좌표
            bubbleY = 525; // 발사대 중심 Y 좌표

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경 이미지 로드
        ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("assets/game/two_player_background.png"));
        g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);

        // 구슬 배열을 기반으로 그리기
        int startX, startY = 65; // 시작 Y 위치
        int spacing = 48; // 구슬 간 간격

        for (int row = 0; row < board.length; row++) {
            startX = (row % 2 == 0) ? 43 : 67; // 홀수 줄과 짝수 줄의 X 위치 조정
            for (int col = 0; col < board[row].length; col++) {
                int bubbleType = board[row][col];
                if (bubbleType != 0) { // 0은 빈 공간
                    BufferedImage bubbleImage = getBubbleImage(bubbleType);
                    if (bubbleImage != null) {
                        g.drawImage(bubbleImage, startX + col * spacing, startY, null);
                    }
                }
            }
            startY += 48; // 다음 줄로 이동
        }

        // 발사대 하단 이미지 출력
        BufferedImage currentImage = getImageForAngle();
        if (currentImage != null) {
            int ddX = 152; // 발사대 하단의 X 위치
            int ddY = 557; // 발사대 하단의 Y 위치
            g.drawImage(currentImage, ddX, ddY, null);
        }

        // 발사대 상단 이미지 출력
        if (cc1 != null) {
            int cc1X = 191; // 발사대 상단의 X 위치
            int cc1Y = 516; // 발사대 상단의 Y 위치
            g.drawImage(cc1, cc1X, cc1Y, null);
        }

        // 화살표 이미지 출력
        if (arrowImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 부드럽게 렌더링

            int arrowX = 208; // 화살표의 X 위치
            int arrowY = 470; // 화살표의 Y 위치
            int arrowWidth = arrowImage.getWidth();
            int arrowHeight = arrowImage.getHeight();

            // 중심점을 기준으로 회전
            g2d.rotate(angle, arrowX + arrowWidth / 2.0, arrowY + arrowHeight / 2.0);
            g2d.drawImage(arrowImage, arrowX, arrowY, arrowWidth, arrowHeight, null);
            g2d.rotate(-angle, arrowX + arrowWidth / 2.0, arrowY + arrowHeight / 2.0); // 회전 복구
        }

        // 발사대 구슬 출력
        if (!isBubbleMoving) { // 구슬이 발사 중이 아닐 때만 출력
            BufferedImage bubbleImage = getBubbleImage(currentBubbleType);
            if (bubbleImage != null) {
                g.drawImage(bubbleImage, bubbleX, bubbleY, null);
            }
        }

        // 발사 중인 구슬 출력
        if (isBubbleMoving) {
            BufferedImage bubbleImage = getBubbleImage(currentBubbleType);
            if (bubbleImage != null) {
                g.drawImage(bubbleImage, bubbleX, bubbleY, null);
            }
        }

        // 게임 하단 이미지 출력
        if (gameBottom != null) {
            int gameBottomX = 5; // 하단의 X 위치
            int gameBottomY = 516; // 하단의 Y 위치
            g.drawImage(gameBottom, gameBottomX, gameBottomY, null);
        }
    }

    // 버블 이미지를 반환하는 메서드 추가
    private BufferedImage getBubbleImage(int bubbleNumber) {
        return switch (bubbleNumber) {
            case 1 -> b1;
            case 2 -> b2;
            case 3 -> b3;
            case 4 -> b4;
            case 5 -> b5;
            case 6 -> b6;
            case 7 -> b7;
            default -> null;
        };
    }

    private BufferedImage getImageForAngle() {
        // 각도를 -50도에서 50도로 변환하여 이미지 인덱스를 계산
        double degrees = Math.toDegrees(angle); // 라디안을 도(degree)로 변환
        int index = (int) ((degrees + 50) / 100 * 12); // -50~50 -> 0~12 범위로 매핑

        // 인덱스 범위를 확인
        if (index < 0) index = 0;
        if (index > 11) index = 11;

        // 인덱스에 따른 이미지 반환
        return switch (index) {
            case 0 -> dd1;
            case 1 -> dd2;
            case 2 -> dd3;
            case 3 -> dd4;
            case 4 -> dd5;
            case 5 -> dd6;
            case 6 -> dd7;
            case 7 -> dd8;
            case 8 -> dd9;
            case 9 -> dd10;
            case 10 -> dd11;
            case 11 -> dd12;
            default -> dd1; // 기본값
        };
    }
    // 키 입력 처리 메서드 수정
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { // 화살표 왼쪽 회전
            angle -= rotationSpeed;
            if (angle < minAngle) {
                angle = minAngle; // 최소 각도 제한
            }
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // 화살표 오른쪽 회전
            angle += rotationSpeed;
            if (angle > maxAngle) {
                angle = maxAngle; // 최대 각도 제한
            }
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) { // 스페이스바로 구슬 발사
            if (!isBubbleMoving) {
                isBubbleMoving = true;
                double startX = bubbleX; // 발사 시작 위치의 X 좌표
                double startY = bubbleY; // 발사 시작 위치의 Y 좌표

                // 초기 각도를 -Math.PI/2로 설정 (12시 방향) ,,!!!!!!
                 dx = Math.cos(angle - Math.PI/2) * bubbleSpeed;
                 dy = Math.sin(angle - Math.PI/2) * bubbleSpeed;

                new Thread(() -> {
                    // 게임 보드의 좌우 경계 설정
                    int boardStartX = 43; // 가장 왼쪽 경계
                    int boardEndX = boardStartX + (board[0].length - 1) * 48; // 가장 오른쪽 경계 //48-> 구슬 크기

                    while (isBubbleMoving) {

                        // 구슬 이동
                        bubbleX += dx; // X 방향 이동
                        bubbleY += dy; // Y 방향 이동

                        // 좌우 벽에 부딪히면 반사
                        if (bubbleX <= boardStartX) {
                            bubbleX = boardStartX; // 경계값에 부딪혔을 때 위치 조정
                            dx = -dx; // X 방향 반전
                        } else if (bubbleX >= boardEndX) {
                            bubbleX = boardEndX; // 경계값에 부딪혔을 때 위치 조정
                            dx = -dx; // X 방향 반전
                        }

                        // 구슬이 Y축 상단에 도달하거나 특정 조건에서 발사 종료
                        if (bubbleY < 0) {
                            isBubbleMoving = false;

                            // 발사대 초기 위치로 재설정
                            bubbleX = 212;
                            bubbleY = 525;

                            // 다음 구슬 타입으로 변경 (1~7 순환)
                            currentBubbleType = (currentBubbleType % 7) + 1;
                        }

                        repaint();
                        try {
                            Thread.sleep(16); // 프레임 딜레이
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

            }
        }
    }



    @Override
    public void keyReleased(KeyEvent e) {
        // 아무 작업도 하지 않음 (키를 뗐을 때 회전 멈춤)
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

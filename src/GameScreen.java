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

    private BufferedImage dd1; // 발사대 하단 이미지
    private BufferedImage dd2; // 발사대 하단 이미지
    private BufferedImage dd3; // 발사대 하단 이미지
    private BufferedImage dd4; // 발사대 하단 이미지
    private BufferedImage dd5; // 발사대 하단 이미지
    private BufferedImage dd6; // 발사대 하단 이미지
    private BufferedImage dd7; // 발사대 하단 이미지
    private BufferedImage dd8; // 발사대 하단 이미지
    private BufferedImage dd9; // 발사대 하단 이미지
    private BufferedImage dd10; // 발사대 하단 이미지
    private BufferedImage dd11; // 발사대 하단 이미지
    private BufferedImage dd12; // 발사대 하단 이미지

    private BufferedImage gameBottom; // 게임 하단 이미지

    private BufferedImage b1; // 버블1 이미지



    private double angle = 0; // 화살표의 현재 각도 (라디안 값)
    private final double maxAngle = Math.toRadians(50); // 최대 각도 (70도)
    private final double minAngle = Math.toRadians(-50); // 최소 각도 (-70도)
    private final double rotationSpeed = Math.toRadians(4); // 회전 속도

    public GamePanel() {
        try {
            // 화살표 이미지 로드
            arrowImage = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/arrow.png"));
            cc1 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/cc1.png"));
            // 발사대 하단 이미지 로드
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

            //게임 하단 이미지 로드
            gameBottom = ImageIO.read(getClass().getClassLoader().getResource("assets/game/gamebottom.png"));

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

        // 화살표 각도에 따라 dd 이미지 선택
        BufferedImage currentImage = getImageForAngle();

        // 상단 1번째 줄
        if (b1 != null) {
            int startX = 43; // 시작 X 위치
            int startY = 65; // 시작 Y 위치
            int spacing = 48; // 간격

            for (int i = 0; i < 8; i++) {
                g.drawImage(b1, startX + i * spacing, startY, null);
            }
        }
        // 상단 1번째 줄
        if (b1 != null) {
            int startX = 67; // 시작 X 위치
            int startY = 113; // 시작 Y 위치
            int spacing = 48; // 간격

            for (int i = 0; i < 7; i++) {
                g.drawImage(b1, startX + i * spacing, startY, null);
            }
        }

        // 선택된 dd 이미지를 화면에 출력
        if (currentImage != null) {
            int ddX = 152; // dd 이미지의 X 위치
            int ddY = 557; // dd 이미지의 Y 위치
            g.drawImage(currentImage, ddX, ddY, null);
        }

        // cc1 이미지를 화면에 출력
        if (cc1 != null) {
            int cc1X = 191; // cc1의 X 위치
            int cc1Y = 516; // cc1의 Y 위치
            g.drawImage(cc1, cc1X, cc1Y, null);
        }

        // 화살표 이미지를 회전시켜 그리기
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

        // cc1 이미지를 화면에 출력
        if (gameBottom != null) {
            int gameBottomX = 5; // cc1의 X 위치
            int gameBottomY = 516; // cc1의 Y 위치
            g.drawImage(gameBottom, gameBottomX, gameBottomY, null);
        }


    }

    private BufferedImage getImageForAngle() {
        // 각도를 -50도에서 50도로 변환하여 이미지 인덱스를 계산
        double degrees = Math.toDegrees(angle); // 라디안을 도(degree)로 변환
        int index = (int) ((degrees + 50) / 100 * 12); // -50~50 -> 0~12 범위로 매핑

        // 인덱스 범위를 확인
        if (index < 0) index = 0;
        if (index > 11) index = 11;

        // 인덱스에 따른 이미지 반환
        switch (index) {
            case 0: return dd1;
            case 1: return dd2;
            case 2: return dd3;
            case 3: return dd4;
            case 4: return dd5;
            case 5: return dd6;
            case 6: return dd7;
            case 7: return dd8;
            case 8: return dd9;
            case 9: return dd10;
            case 10: return dd11;
            case 11: return dd12;
            default: return dd1; // 기본값
        }
    }




    @Override
    public void keyPressed(KeyEvent e) {
        // 왼쪽 방향키 입력 처리
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            angle -= rotationSpeed;
            if (angle < minAngle) {
                angle = minAngle; // 최소 각도 제한
            }
            repaint();
        }
        // 오른쪽 방향키 입력 처리
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            angle += rotationSpeed;
            if (angle > maxAngle) {
                angle = maxAngle; // 최대 각도 제한
            }
            repaint();
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

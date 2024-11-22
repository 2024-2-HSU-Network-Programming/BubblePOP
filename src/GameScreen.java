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
    private BufferedImage cc1; // 화살표 이미지
    private double angle = 0; // 화살표의 현재 각도 (라디안 값)
    private final double maxAngle = Math.toRadians(70); // 최대 각도 (70도)
    private final double minAngle = Math.toRadians(-70); // 최소 각도 (-70도)
    private final double rotationSpeed = Math.toRadians(4); // 회전 속도

    public GamePanel() {
        try {
            // 화살표 이미지 로드
            arrowImage = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/arrow.png"));
            cc1 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/cc1.png"));
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

        // cc1 이미지를 화면에 출력
        if (cc1 != null) {
            int cc1X = 191; // cc1의 X 위치
            int cc1Y = 501; // cc1의 Y 위치
            g.drawImage(cc1, cc1X, cc1Y, null); // cc1 이미지를 해당 위치에 출력
        }

        // 화살표 이미지를 회전시켜 그리기
        if (arrowImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 부드럽게 렌더링

            int arrowX = 215; // 화살표의 X 위치
            int arrowY = 455; // 화살표의 Y 위치
            int arrowWidth = arrowImage.getWidth();
            int arrowHeight = arrowImage.getHeight();

            // 중심점을 기준으로 회전
            g2d.rotate(angle, arrowX + arrowWidth / 2.0, arrowY + arrowHeight / 2.0);
            g2d.drawImage(arrowImage, arrowX, arrowY, arrowWidth, arrowHeight, null);
            g2d.rotate(-angle, arrowX + arrowWidth / 2.0, arrowY + arrowHeight / 2.0); // 회전 복구
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

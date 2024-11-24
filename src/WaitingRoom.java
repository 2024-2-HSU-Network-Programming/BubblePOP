import javax.swing.*;
import java.awt.*;

public class WaitingRoom {
    public static void main(String[] args) {
        JFrame frame = new JFrame("대기방");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 672);        frame.setLayout(null); // 절대 레이아웃 사용
        frame.getContentPane().setBackground(Color.decode("#181329"));
        // 프레임 크기 고정
        frame.setResizable(false);

        // 한글 폰트 설정
        Font koreanFont = new Font("맑은 고딕", Font.PLAIN, 14);

        // 로비로 나가기 버튼
        JButton exitButton = new JButton("로비로 나가기");
        exitButton.setBounds(25, 50, 550, 50);
        exitButton.setBackground(Color.GREEN);
        exitButton.setFont(koreanFont);
        frame.add(exitButton);

        // 방 제목
        JLabel roomTitle = new JLabel("나랑 한판 할 사람~~~~(방 이름)", SwingConstants.CENTER); //중앙에
        roomTitle.setBounds(0, 100, 600, 50);
        roomTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        roomTitle.setOpaque(false); // 투명한 배경
        roomTitle.setForeground(Color.WHITE); // 글자색만!
        frame.add(roomTitle);

        // 플레이어 패널 1
        JPanel playerPanel1 = createPlayerPanel("아이디 1", koreanFont);
        playerPanel1.setBounds(50, 170, 200, 250);
        frame.add(playerPanel1);

        // 플레이어 패널 2
        JPanel playerPanel2 = createPlayerPanel("아이디 2", koreanFont);
        playerPanel2.setBounds(350, 170, 200, 250);
        frame.add(playerPanel2);

        // 준비 1
        JLabel readyLabel1 = new JLabel("준비 완료"); //중앙에
        readyLabel1.setBounds(115, 435, 200, 40);
        readyLabel1.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        readyLabel1.setOpaque(false); // 투명한 배경
        readyLabel1.setForeground(Color.WHITE); // 글자색만!
        frame.add(readyLabel1);

        // 준비 2
        JLabel readyLabel2 = new JLabel("준비 완료"); //중앙에
        readyLabel2.setBounds(415, 435, 200, 40);
        readyLabel2.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        readyLabel2.setOpaque(false); // 투명한 배경
        readyLabel2.setForeground(Color.WHITE); // 글자색만!
        frame.add(readyLabel2);

        // READY 버튼
        JButton readyButton = new JButton("READY");
        readyButton.setBounds(25, 500, 550, 50);
        readyButton.setBackground(Color.YELLOW);
        readyButton.setForeground(Color.BLACK);
        readyButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        frame.add(readyButton);

        // START 버튼
        JButton startButton = new JButton("START");
        startButton.setBounds(25, 550, 550, 50);
        startButton.setBackground(Color.RED);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        frame.add(startButton);

        // 채팅 패널
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBounds(630, 50, 300, 550);
        chatPanel.setBackground(Color.LIGHT_GRAY);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(koreanFont);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        JTextField chatInputField = new JTextField();
        chatInputField.setFont(koreanFont);
        JButton sendButton = new JButton("전송");
        sendButton.setFont(koreanFont);
        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        frame.add(chatPanel);

        frame.setVisible(true);
    }

    private static JPanel createPlayerPanel(String playerName, Font font) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        JLabel avatarLabel = new JLabel(); // 이미지가 없으므로 빈 라벨
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(avatarLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(font);
        panel.add(nameLabel, BorderLayout.SOUTH);

        return panel;
    }
}

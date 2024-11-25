package client;

import javax.swing.*;
import java.awt.*;

public class WaitingRoom {
    public static void main(String[] args) {
        JFrame frame = new JFrame("대기방");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 672);
        frame.setLayout(null); // 절대 레이아웃 사용
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
        JPanel playerPanel1 = createPlayerPanel("아이디 1","assets/chracter/mainPlayer1_1.png", koreanFont);
        playerPanel1.setBounds(50, 170, 200, 250);
        frame.add(playerPanel1);

        // 플레이어 패널 2
        JPanel playerPanel2 = createPlayerPanel("아이디 2", "assets/chracter/mainPlayer2_1.png",koreanFont);
        playerPanel2.setBounds(350, 170, 200, 250);
        frame.add(playerPanel2);

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

        // START 버튼 클릭 이벤트 추가
        startButton.addActionListener(e -> {
            frame.dispose(); // 현재 대기방 프레임 닫기
            GameScreen.main(new String[]{}); // client.GameScreen 실행
        });

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

    private static JPanel createPlayerPanel(String playerName, String imagePath, Font font) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        JLabel avatarLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(imagePath));
            Image scaledImage = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("이미지를 불러오지 못했습니다: " + imagePath);
        }
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(avatarLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(font);
        panel.add(nameLabel, BorderLayout.SOUTH);

        return panel;
    }
}

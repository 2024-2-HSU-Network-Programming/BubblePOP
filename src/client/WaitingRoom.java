package client;

import shared.ChatMsg;

import javax.swing.*;
import java.awt.*;

public class WaitingRoom {
    private String userId;
    private ManageNetwork network;
    private JFrame frame;

    public WaitingRoom(String roomNumber, String roomName, String userId, ManageNetwork network) {
        this.userId = userId;
        this.network = network;

        // 프레임 초기화
        frame = new JFrame("대기방");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 672);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.decode("#181329"));
        frame.setResizable(false);

        // 한글 폰트 설정
        Font koreanFont = new Font("맑은 고딕", Font.PLAIN, 14);

        // 로비로 나가기 버튼
        JButton exitButton = new JButton("로비로 나가기");
        exitButton.setBounds(25, 50, 550, 50);
        exitButton.setBackground(Color.GREEN);
        exitButton.setFont(koreanFont);
        exitButton.addActionListener(e -> {
            // 서버에 방 나가기 메시지 전송
            ChatMsg leaveRoomMsg = new ChatMsg(userId, ChatMsg.MODE_LEAVE_ROOM,
                    roomNumber + "|" + userId);
            network.sendMessage(leaveRoomMsg);
            frame.dispose();
            //new LobbyFrame(userId, network);
        });
        frame.add(exitButton);

        // 방 제목
        JLabel roomTitle = new JLabel(roomName, SwingConstants.CENTER);
        roomTitle.setBounds(0, 100, 600, 50);
        roomTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        roomTitle.setOpaque(false);
        roomTitle.setForeground(Color.WHITE);
        frame.add(roomTitle);

        // 플레이어 패널 1 (현재 유저)
        JPanel playerPanel1 = createPlayerPanel(userId, "assets/chracter/mainPlayer1_1.png", koreanFont);
        playerPanel1.setBounds(50, 170, 200, 250);
        frame.add(playerPanel1);

        // 플레이어 패널 2
        JPanel playerPanel2 = createPlayerPanel("대기중", "assets/chracter/mainPlayer2_1.png", koreanFont);
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

        // START 버튼 클릭 이벤트
        startButton.addActionListener(e -> {
            frame.dispose();
            GameScreen.main(new String[]{});
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
    }

    private JPanel createPlayerPanel(String playerName, String imagePath, Font font) {
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

    public void show() {
        frame.setVisible(true);
    }
}
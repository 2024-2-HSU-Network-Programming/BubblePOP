package client;

import server.RoomManager;
import shared.ChatMsg;

import javax.swing.*;
import java.awt.*;

public class WaitingRoom {
    private String userId;
    private ManageNetwork network;
    private JFrame frame;
    private JLabel player2NameLabel;
    private String roomId;
    private JButton readyButton;
    private JButton startButton;
    private JLabel player1ReadyLabel;  // 플레이어1 준비상태 라벨
    private JLabel player2ReadyLabel;  // 플레이어2 준비상태 라벨
    private boolean isReady = false;   // 준비상태 플래그
    private String roomOwner;          // 방장 ID 저장
    private boolean player1Ready = false;  // 자신의 준비 상태
    private boolean player2Ready = false;  // 상대방의 준비 상태
    private GameUser gameUser;


    // 채팅 관련 필드
    private JTextArea chatArea;
    private JTextField chatInputField;
    private JButton sendButton;

    private ImageIcon userCharacterIcon = new ImageIcon(getClass().getResource("/client/assets/game/user_character.png"));
    private ImageIcon logoIcon = new ImageIcon(getClass().getResource("/client/assets/logo/logo.png"));

    public WaitingRoom(String roomNumber, String roomName, String userId, ManageNetwork network) {
        this.userId = userId;
        this.network = network;
        this.roomId = roomNumber;
        this.gameUser = GameUser.getInstance();

        // ChatMsg에서 받은 방 생성 정보로부터 방장 ID를 설정
        String ownerFromServer = RoomManager.getInstance().getGameRoom(roomNumber).getRoomOwner();
        this.roomOwner = ownerFromServer;  // 서버에서 받은 실제 방장 ID로 설정

        network.setWaitingRoom(this);

        frame = new JFrame("대기방");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 672);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.decode("#181329"));
        frame.setResizable(false);

        Font koreanFont = new Font("맑은 고딕", Font.PLAIN, 14);

        // 로비로 나가기 버튼
        JButton exitButton = new JButton("로비로 나가기");
        exitButton.setBounds(25, 50, 550, 50);
        exitButton.setBackground(Color.GREEN);
        exitButton.setFont(koreanFont);
        exitButton.addActionListener(e -> {
            ChatMsg leaveRoomMsg = new ChatMsg(userId, ChatMsg.MODE_LEAVE_ROOM,
                    roomNumber + "|" + userId);
            network.sendMessage(leaveRoomMsg);
            frame.dispose();

            SwingUtilities.invokeLater(() -> {
                new LobbyFrame();
            });
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

        // 플레이어1 준비상태 라벨 추가
        player1ReadyLabel = new JLabel("준비", SwingConstants.CENTER);
        player1ReadyLabel.setForeground(Color.WHITE);
        player1ReadyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        player1ReadyLabel.setBounds(100, 430, 100, 30);
        frame.add(player1ReadyLabel);

        // 플레이어 패널 2
        JPanel playerPanel2 = createPlayerPanel("대기중", "assets/chracter/mainPlayer2_1.png", koreanFont);
        for (Component comp : playerPanel2.getComponents()) {
            if (comp instanceof JLabel && comp != playerPanel2.getComponent(0)) {
                player2NameLabel = (JLabel)comp;
                break;
            }
        }
        playerPanel2.setBounds(350, 170, 200, 250);
        frame.add(playerPanel2);

        // 플레이어2 준비상태 라벨 추가
        player2ReadyLabel = new JLabel("준비", SwingConstants.CENTER);
        player2ReadyLabel.setForeground(Color.WHITE);
        player2ReadyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        player2ReadyLabel.setBounds(400, 430, 100, 30);
        frame.add(player2ReadyLabel);

        // READY 버튼
        readyButton = new JButton("READY");
        readyButton.setBounds(25, 500, 550, 50);
        readyButton.setBackground(Color.YELLOW);
        readyButton.setForeground(Color.BLACK);
        readyButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        readyButton.addActionListener(e -> {
            isReady = !isReady;  // 준비상태 토글
            player1Ready = isReady;  // 자신의 준비 상태 업데이트
            if (isReady) {
                player1ReadyLabel.setForeground(Color.YELLOW);
                ChatMsg readyMsg = new ChatMsg(userId, ChatMsg.MODE_TX_ROOMCHAT,
                        roomId + "|" + userId + ": 준비완료");
                network.sendMessage(readyMsg);
            } else {
                player1ReadyLabel.setForeground(Color.WHITE);
                ChatMsg notReadyMsg = new ChatMsg(userId, ChatMsg.MODE_TX_ROOMCHAT,
                        roomId + "|" + userId + ": 준비해제");
                network.sendMessage(notReadyMsg);
            }
            updateStartButton();  // START 버튼 상태 업데이트
        });
        frame.add(readyButton);

        // START 버튼
        startButton = new JButton("START");
        startButton.setBounds(25, 550, 550, 50);
        startButton.setBackground(Color.RED);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        startButton.setEnabled(userId.equals(roomOwner));  // 방장인 경우에만 활성화

        startButton.addActionListener(e -> {
            if (player1Ready && player2Ready) {
                // 게임 시작 메시지를 모든 클라이언트에게 전송
                ChatMsg startMsg = new ChatMsg(userId, ChatMsg.MODE_GAME_START, roomId);
                network.sendMessage(startMsg);

                // 현재 대기방 닫기
                dispose();
//
//                // 게임 화면 시작
//                SwingUtilities.invokeLater(() -> {
//                    OriginalGameScreen gameScreen = new OriginalGameScreen(userId, network, roomOwner.equals(userId));
//                    gameScreen.setVisible(true);
//                });
            }
        });

        frame.add(startButton);

        // 채팅 패널
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBounds(630, 50, 300, 550);
        chatPanel.setBackground(Color.LIGHT_GRAY);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(koreanFont);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputField = new JTextField();
        chatInputField.setFont(koreanFont);
        sendButton = new JButton("전송");
        sendButton.setFont(koreanFont);

        chatInputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        frame.add(chatPanel);
    }

    // 기존 메서드들...

    public void updatePlayer2Name(String newPlayerName) {
        System.out.println("Updating player2 name to: " + newPlayerName);
        if (!newPlayerName.equals(userId)) {
            SwingUtilities.invokeLater(() -> {
                System.out.println("이전 이름: " + player2NameLabel.getText());
                player2NameLabel.setText(newPlayerName);
                System.out.println("변경된 이름: " + player2NameLabel.getText());

                player2NameLabel.invalidate();
                frame.validate();
                frame.repaint();
            });
        }
    }

    // 다른 플레이어의 준비상태 업데이트
    public void updatePlayer2Ready(boolean ready) {
        SwingUtilities.invokeLater(() -> {
            player2Ready = ready;  // 상대방의 준비 상태 업데이트
            player2ReadyLabel.setForeground(ready ? Color.YELLOW : Color.WHITE);
            updateStartButton();  // START 버튼 상태 업데이트
        });

    }

    private void sendMessage() {
        String message = chatInputField.getText().trim();
        if (!message.isEmpty()) {
            ChatMsg chatMsg = new ChatMsg(userId, ChatMsg.MODE_TX_ROOMCHAT,
                    roomId + "|" + userId + ": " + message);
            network.sendMessage(chatMsg);
            chatInputField.setText("");
        }
    }

    public void receiveMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            // "준비완료" 메시지 처리
            if (message.contains("준비완료") && !message.startsWith(userId)) {
                updatePlayer2Ready(true);
            }
            // "준비해제" 메시지 처리
            else if (message.contains("준비해제") && !message.startsWith(userId)) {
                updatePlayer2Ready(false);
            }
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
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

    public void dispose() {
        frame.dispose();
    }

    // START 버튼 활성화 상태를 업데이트하는 메서드 추가
    private void updateStartButton() {
        if (userId.equals(roomOwner)) {
            startButton.setEnabled(player1Ready && player2Ready);
        }
    }

    public String getRoomOwner() {
        return roomOwner;
    }

}
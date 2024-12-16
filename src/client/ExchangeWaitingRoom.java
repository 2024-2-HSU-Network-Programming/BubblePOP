package client;

import server.RoomManager;
import shared.ChatMsg;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ExchangeWaitingRoom {
    private String userId;
    private ManageNetwork network;
    private JFrame frame;
    private JLabel player2NameLabel;
    private String roomId;
    private JButton readyButton;
    private JButton exchangeButton;
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
    // 필드 추가
    private JLabel selectedItemLabel; // 선택한 아이템 이미지 라벨
    private JButton selectItemButton; // 아이템 선택 버튼
    private String selectedItemPath; // 선택한 아이템 이미지 경로

    private ImageIcon userCharacterIcon = new ImageIcon(getClass().getResource("/client/assets/game/user_character.png"));
    private ImageIcon logoIcon = new ImageIcon(getClass().getResource("/client/assets/logo/logo.png"));

    public ExchangeWaitingRoom(String roomNumber, String roomName, String userId, ManageNetwork network) {
        this.userId = userId;
        this.network = network;
        this.roomId = roomNumber;
        this.gameUser = GameUser.getInstance();

        // ChatMsg에서 받은 방 생성 정보로부터 방장 ID를 설정
        String ownerFromServer = RoomManager.getInstance().getExchangeRoom(roomNumber).getRoomOwner();
        this.roomOwner = ownerFromServer;  // 서버에서 받은 실제 방장 ID로 설정

        network.setExchangeWaitingRoom(this);

        frame = new JFrame("교환방");
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

//        // 플레이어1 아이템 패널
//        String[] player1Items = gameUser.getItemList();
//        JPanel player1ItemPanel = createItemPanel(player1Items, koreanFont);
//        player1ItemPanel.setBounds(50, 530, 200, 100);
//        frame.add(player1ItemPanel);

//        // 플레이어1 준비상태 라벨 추가
//        player1ReadyLabel = new JLabel("교환아이템", SwingConstants.CENTER);
//        player1ReadyLabel.setForeground(Color.WHITE);
//        player1ReadyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
//        player1ReadyLabel.setBounds(100, 430, 100, 30);
//        frame.add(player1ReadyLabel);
        // 교환하기 버튼 아래에 아이템 선택 UI 추가
        selectItemButton = new JButton("아이템 선택");
        selectItemButton.setBounds(250, 500, 100, 50);
        selectItemButton.setBackground(Color.white);
        selectItemButton.setForeground(Color.black);
        selectItemButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        frame.add(selectItemButton);

// 선택된 아이템 이미지 라벨
        selectedItemLabel = new JLabel("선택한 아이템", SwingConstants.CENTER);
        selectedItemLabel.setBounds(100, 450, 100, 100);
        selectedItemLabel.setBackground(Color.LIGHT_GRAY);
        selectedItemLabel.setOpaque(true);
        frame.add(selectedItemLabel);

        selectItemButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            // 초기 파일 경로 설정
            File initialDirectory = new File("/Users/yebin/BubblePOP/src/client/assets/item");
            fileChooser.setCurrentDirectory(initialDirectory);

            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Image files", "png", "jpg", "jpeg", "gif"
            ));
            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedItemPath = selectedFile.getAbsolutePath();

                /// 유저1의 화면에 이미지 표시
                ImageIcon selectedImage = new ImageIcon(selectedItemPath);
                addUserImage(userId, selectedImage);


                // 선택한 이미지를 서버로 전송
                sendSelectedItemImage();
            }
        });


        // 플레이어 패널 2
        JPanel playerPanel2 = createPlayerPanel("대기중", "assets/chracter/mainPlayer2_1.png", koreanFont);
        for (Component comp : playerPanel2.getComponents()) {
            if (comp instanceof JLabel && comp != playerPanel2.getComponent(0)) {
                player2NameLabel = (JLabel)comp;
                break;
            }
        }

        // 플레이어2 아이템 패널
//        String[] player2Items = gameUser.getItemList();
//        JPanel player2ItemPanel = createItemPanel(player2Items, koreanFont);
//        player2ItemPanel.setBounds(150, 480, 200, 100);
//        frame.add(player2ItemPanel);
        playerPanel2.setBounds(350, 170, 200, 250);
        frame.add(playerPanel2);

        // 플레이어2 준비상태 라벨 추가
        player2ReadyLabel = new JLabel("", SwingConstants.CENTER);
        player2ReadyLabel.setForeground(Color.WHITE);
        player2ReadyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        player2ReadyLabel.setBounds(400, 430, 100, 30);
        frame.add(player2ReadyLabel);

        // 교환하기 버튼
        exchangeButton = new JButton("교환하기");
        exchangeButton.setBounds(250, 430, 100, 50);
        exchangeButton.setBackground(Color.RED);
        exchangeButton.setForeground(Color.white);
        exchangeButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        exchangeButton.setEnabled(userId.equals(roomOwner));  // 방장인 경우에만 활성화

        exchangeButton.addActionListener(e -> {
            // 교환 요청 데이터 생성
            String exchangeData = userId + "|" + player2NameLabel.getText();

            // 서버로 교환 요청 전송
            ChatMsg exchangeRequest = new ChatMsg(userId, ChatMsg.MODE_EXCHANGEITEM, exchangeData);
            network.sendMessage(exchangeRequest);

        });

        frame.add(exchangeButton);

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

    private JPanel createItemPanel(String[] items, Font font) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(0, 1)); // 아이템 수만큼 세로로 나열
        itemPanel.setBackground(Color.LIGHT_GRAY);

        for (String item : items) {
            String[] parts = item.split("\\|");
            String itemTitle = parts[0];
            int itemQuantity = Integer.parseInt(parts[1]);
            System.out.println(itemTitle);

            JPanel singleItemPanel = new JPanel(new BorderLayout());
            singleItemPanel.setBackground(Color.WHITE);

            // 아이템 이미지
            String imagePath = GameUser.getItemImagePath(itemTitle);
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

            // 아이템 이름과 수량
            JLabel textLabel = new JLabel(itemTitle + ": " + itemQuantity);
            textLabel.setFont(font);
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // 이미지와 텍스트 결합
            singleItemPanel.add(imageLabel, BorderLayout.WEST);
            singleItemPanel.add(textLabel, BorderLayout.CENTER);

            itemPanel.add(singleItemPanel);
        }

        return itemPanel;
    }

    // 기존 메서드들...

//    public void updatePlayer2Name(String newPlayerName) {
//        System.out.println("Updating player2 name to: " + newPlayerName);
//        if (!newPlayerName.equals(userId)) {
//            SwingUtilities.invokeLater(() -> {
//                System.out.println("이전 이름: " + player2NameLabel.getText());
//                player2NameLabel.setText(newPlayerName);
//                System.out.println("변경된 이름: " + player2NameLabel.getText());
//
//                player2NameLabel.invalidate();
//                frame.validate();
//                frame.repaint();
//            });
//        }
//    }
    public void updatePlayer2Name(String newPlayerName) {
        System.out.println("Updating player2 name to: " + newPlayerName);
        if (!newPlayerName.equals(userId)) {
            SwingUtilities.invokeLater(() -> {
                player2NameLabel.setText(newPlayerName);
                frame.validate();
                frame.repaint();
            });
        }
    }

    // 플레이어2 아이템 데이터를 업데이트하는 메서드
    public void updatePlayer2Items(String[] items) {
        System.out.println("player2 items: " + items);
        SwingUtilities.invokeLater(() -> {
            JPanel player2ItemPanel = createItemPanel(items, new Font("맑은 고딕", Font.PLAIN, 12));
            player2ItemPanel.setBounds(400, 480, 200, 100);

            frame.add(player2ItemPanel);
            frame.repaint();
            frame.validate();
        });
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
//    private void sendSelectedItemImage() {
//        if (selectedItemPath != null) {
//            ImageIcon imageIcon = new ImageIcon(selectedItemPath);
//            ChatMsg imageMsg = new ChatMsg(userId, ChatMsg.MODE_TX_IMAGE, "SelectedItem", imageIcon);
//            network.sendMessage(imageMsg);
//        }
//    }
private void sendSelectedItemImage() {
    if (selectedItemPath != null) {
        ImageIcon imageIcon = new ImageIcon(selectedItemPath);
        ChatMsg imageMsg = new ChatMsg(userId, ChatMsg.MODE_TX_IMAGE, "SelectedItem", imageIcon);
        network.sendMessage(imageMsg);
    }
}

    // 선택된 아이템 이미지 업데이트
    public void updateSelectedItemImage(ImageIcon image) {
        selectedItemLabel.setIcon(new ImageIcon(image.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        selectedItemLabel.setText(""); // 텍스트 제거
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
            exchangeButton.setEnabled(player1Ready && player2Ready);
        }
    }

    public String getRoomOwner() {
        return roomOwner;
    }

    public String getPlayer2Name() {
        return player2NameLabel.getText();
    }
    public String getRoomId() {
        return this.roomId;
    }

    public void addUserImage(String userId, ImageIcon image) {
        SwingUtilities.invokeLater(() -> {
            ImageIcon scaledImage = new ImageIcon(image.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));

            if (this.userId.equals(userId)) {
                // 본인의 이미지 -> 왼쪽에 표시
                selectedItemLabel.setIcon(scaledImage);
                selectedItemLabel.setText(""); // 텍스트 제거
            } else {
                // 상대방의 이미지 -> 오른쪽에 표시
                player2ReadyLabel = new JLabel(scaledImage);
                player2ReadyLabel.setText("");
                player2ReadyLabel.setBounds(400, 450, 100, 100); // 오른쪽 위치에 배치
                player2ReadyLabel.setBackground(Color.LIGHT_GRAY);
                player2ReadyLabel.setOpaque(true);
                frame.add(player2ReadyLabel);
                frame.repaint();
            }
        });
    }


}
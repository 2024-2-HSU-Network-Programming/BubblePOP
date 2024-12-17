package client;

import shared.ChatMsg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LobbyFrame extends JFrame {
    private JPanel lobbyPane;
    private JPanel lobbyLeftPane;
    private JPanel lobbyRightPane;
    private JPanel lobbyCenterPane;
    private RoomListTapPane roomListTapPane;
    private JButton btnItemStore;
    private JButton btnCreateRoom;
    private JTextArea t_globalChat;
    private JTextField tf_globalChat;
    private JButton btnSendGlobalChat;

    private ImageIcon userCharacterIcon = new ImageIcon(getClass().getResource("/client/assets/game/user_character.png"));
    private ImageIcon logoIcon = new ImageIcon(getClass().getResource("/client/assets/logo/logo.png"));

    private BufferedImage changeBubble, linebomb, bomb;

    private String userId;
    private ManageNetwork network;
    GameUser user = GameUser.getInstance();
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public LobbyFrame() {
        setTitle("Lobby");
        setBounds(100,100, 960, 672);
        setResizable(false); // 크기 고정
//        this.userId = userId;
//        this.network = network;

        try {
            changeBubble = ImageIO.read(getClass().getResourceAsStream("/client/assets/item/change-bubble.png"));
            linebomb = ImageIO.read(getClass().getResourceAsStream("/client/assets/item/line-explosion.png"));
            bomb = ImageIO.read(getClass().getResourceAsStream("/client/assets/item/bomb.png"));
        } catch (IOException e) {
            e.printStackTrace();
            changeBubble = null;
            linebomb = null;
            bomb = null;
        }


        lobbyPane = new JPanel();
        lobbyPane.setLayout(new BorderLayout());
        lobbyPane.setBackground(Color.BLACK);
        lobbyPane.add(LobbyLeftPanel(), BorderLayout.WEST);
        lobbyPane.add(LobbyRightPanel(), BorderLayout.EAST);
        lobbyPane.add(LobbyCenterPanel(), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(lobbyPane);
        setVisible(true); // 프레임 보이도록

        this.ois=user.getNet().getOIS();
        this.oos=user.getNet().getOOS();
    }

    // getter 메서드 추가
    public String getUserId() {
        return user.getId();
    }

    public ManageNetwork getNetwork() {
        return network;
    }

    private JPanel LobbyCenterPanel() {
        lobbyCenterPane = new JPanel();
        lobbyCenterPane.setLayout(null);
        lobbyCenterPane.setBackground(Color.BLACK);

//        // 유저 정보 패널
//        JPanel userInfoPanel = new JPanel();
//        userInfoPanel.setLayout(new GridLayout(3, 1));
//        userInfoPanel.setBounds(20, 20, 400, 100);
//        userInfoPanel.setBackground(new Color(52, 74, 119));  // 배경색 설정

        // 유저 ID
        JLabel userIdLabel = new JLabel("User ID: " + user.getId());
        userIdLabel.setForeground(Color.WHITE);
        userIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userIdLabel.setBounds(100, 230, 200, 30); // 위치와 크기 설정
        lobbyCenterPane.add(userIdLabel);

        // 보유 코인
        JLabel coinLabel = new JLabel("Coins:    " + user.getCoin());
        coinLabel.setForeground(Color.WHITE);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coinLabel.setBounds(100, 260, 200, 30);
        lobbyCenterPane.add(coinLabel);


        // 이미지 크기 조정 및 추가
        addScaledImage(lobbyCenterPane, changeBubble, 50, 300, 40, 40); // changeBubble
        addScaledImage(lobbyCenterPane, linebomb, 180, 300, 40, 40); // linebomb
        addScaledImage(lobbyCenterPane, bomb, 300, 300, 40, 40); // bomb

        // 구슬전환 아이템 갯수 getChangeBubbleColor
        JLabel changeBubbleNum = new JLabel("x " + user.getChangeBubbleColor());
        changeBubbleNum.setForeground(Color.WHITE);
        changeBubbleNum.setFont(new Font("Arial", Font.BOLD, 16));
        changeBubbleNum.setBounds(90, 305, 90, 30);
        lobbyCenterPane.add(changeBubbleNum);

// 한줄 제거 아이템 갯수 getLineExplosion
        JLabel linebombNum = new JLabel("x " + user.getLineExplosion());
        linebombNum.setForeground(Color.WHITE);
        linebombNum.setFont(new Font("Arial", Font.BOLD, 16));
        linebombNum.setBounds(225, 305, 95, 30);
        lobbyCenterPane.add(linebombNum);

// 폭탄 구슬 아이템 갯수 getBomb
        JLabel bombNum = new JLabel("x " + user.getBomb());
        bombNum.setForeground(Color.WHITE);
        bombNum.setFont(new Font("Arial", Font.BOLD, 16));
        bombNum.setBounds(350, 305, 90, 30);
        lobbyCenterPane.add(bombNum);




        // 전체 채팅 칸 추가
        t_globalChat = new JTextArea();
        t_globalChat.setBounds(20,20,400,150);
        lobbyCenterPane.add(t_globalChat);

        t_globalChat.setText("                                      *** 전체 채팅 ***\n          ");
        //addGlobalChatMessage(userId + "님이 로그인했습니다!");
        t_globalChat.setEditable(false); // 입력 비활성화

        // 전체 채팅 입력칸 추가
        tf_globalChat = new JTextField();
        tf_globalChat.setBounds(20, 200, 330, 30);
        lobbyCenterPane.add(tf_globalChat);

        btnSendGlobalChat = new JButton("보내기");
        btnSendGlobalChat.setBounds(360,200, 70, 30);
        btnSendGlobalChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatMsg msg = new ChatMsg(user.getId(), ChatMsg.MODE_TX_STRING, user.getId() + "님: " + tf_globalChat.getText());
                user.getNet().sendMessage(msg);
            }
        });
        lobbyCenterPane.add(btnSendGlobalChat);

        JLabel lb_userCharacter = new JLabel(userCharacterIcon);
        lb_userCharacter.setBounds(120,330, 200,350);
        lobbyCenterPane.add(lb_userCharacter);

        return lobbyCenterPane;
    }

    private JPanel LobbyLeftPanel() {
        lobbyLeftPane = new JPanel();
        lobbyLeftPane.setPreferredSize(new Dimension(260, 672)); // 원하는 크기 설정
        lobbyLeftPane.setBackground(new Color(52,74,119));
        lobbyLeftPane.setLayout(new BorderLayout());

        roomListTapPane = new RoomListTapPane(LobbyFrame.this, network);
        lobbyLeftPane.add(roomListTapPane, BorderLayout.CENTER);

        return lobbyLeftPane;
    }

    private JPanel LobbyRightPanel() {
        lobbyRightPane = new JPanel();
        lobbyRightPane.setPreferredSize(new Dimension(260, 672)); // 원하는 크기 설정
        lobbyRightPane.setBackground(new Color(52,74,119));
        lobbyRightPane.setLayout(null);

        // 이미지 크기 조절
        Image scaledImage = logoIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH); // 150x150 크기로 조절
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // 이미지 라벨
        JLabel lb_logo = new JLabel(scaledIcon);
        lb_logo.setBounds(35,20, 200,150);
        lobbyRightPane.add(lb_logo);

        btnItemStore = new JButton("아이템 상점");
        btnCreateRoom = new JButton("방 만들기");
        //btnExchangeRoom = new JButton("교환방 만들기");

        btnItemStore.setBounds(40, 340, 180,85);
        //btnExchangeRoom.setBounds(40, 440, 180,85);
        btnCreateRoom.setBounds(40, 540, 180,85);

        btnItemStore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ItemStoreFrame(user.getNet(), LobbyFrame.this);
            }
        });

        btnCreateRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MakeRoomDialog(LobbyFrame.this, user.getNet());
            }
        });


        lobbyRightPane.add(btnItemStore);
        lobbyRightPane.add(btnCreateRoom);
        //lobbyRightPane.add(btnExchangeRoom);


        return lobbyRightPane;
    }
    public void addGlobalChatMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            t_globalChat.append(message + "\n");
        });
    }

    public void updateRoomList(String roomInfo) {
        SwingUtilities.invokeLater(() -> {
            t_globalChat.append("새로운 대기방: " + roomInfo + "\n");
        });
    }
    // roomListTapPane 가져오기
    public RoomListTapPane getRoomListPane() {
        return roomListTapPane;
    }

    // 아이템 표시를 위한 헬퍼 메소드
//    private void addItemDisplay(JPanel panel, String imagePath, int count) {
//        ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
//        Image scaledImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
//        ImageIcon scaledIcon = new ImageIcon(scaledImage);
//
//        JPanel itemContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
//        itemContainer.setLayout(null);
//        itemContainer.setBackground(new Color(52, 74, 119));
//
//        JLabel iconLabel = new JLabel(scaledIcon);
//        JLabel countLabel = new JLabel("x " + count);
//        countLabel.setForeground(Color.WHITE);
//        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
//        countLabel .setBounds(10, 10, 200, 30);
//
//        itemContainer.add(iconLabel);
//        itemContainer.add(countLabel);
//        panel.add(itemContainer);
//    }

    public void updateCoinDisplay() {
        SwingUtilities.invokeLater(() -> {
            // 코인 라벨 업데이트
            for (Component comp : lobbyCenterPane.getComponents()) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().startsWith("Coins:")) {
                    ((JLabel) comp).setText("Coins:    " + user.getCoin());
                    break;
                }
            }
        });
    }

    private void addScaledImage(JPanel panel, BufferedImage image, int x, int y, int width, int height) {
        if (image != null) {
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); // 스케일 조정
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setBounds(x, y, width, height); // 위치와 크기 설정
            panel.add(imageLabel);
        }
    }


    public void updateItemDisplay() {
        SwingUtilities.invokeLater(() -> {
            for (Component comp : lobbyCenterPane.getComponents()) {
                if (comp instanceof JLabel) { //아이템 수량 라벨 동적으로 찾아서 업데이트!
                    JLabel label = (JLabel) comp;
                    String text = label.getText();
                    if (text != null && text.startsWith("x ")) {  // null 체크 추가
                        if (label.getBounds().x == 90) { // 구슬 색변경
                            label.setText("x " + user.getChangeBubbleColor());
                        } else if (label.getBounds().x == 225) { //라인 폭발
                            label.setText("x " + user.getLineExplosion());
                        } else if (label.getBounds().x == 350) {//폭탄
                            label.setText("x " + user.getBomb());
                        }
                    }
                }
            }
        });
    }



//    public void refreshUserInfo() {
//        SwingUtilities.invokeLater(() -> {
//            getContentPane().removeAll();
//            lobbyPane.removeAll();
//            lobbyPane.add(LobbyLeftPanel(), BorderLayout.WEST);
//            lobbyPane.add(LobbyRightPanel(), BorderLayout.EAST);
//            lobbyPane.add(LobbyCenterPanel(), BorderLayout.CENTER);
//            revalidate();
//            repaint();
//        });
//    }

    public static void main(String[] args) {
        ObjectInputStream in = null; // 초기화 필요
        ObjectOutputStream out = null; // 초기화 필요
        Socket socket = null; // 테스트 목적으로 null 사용
        //new LobbyFrame("TestUser", new ManageNetwork(in, out, socket, null));
    }
}

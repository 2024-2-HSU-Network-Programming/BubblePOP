package client;

import shared.ChatMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyFrame extends JFrame {
    private JPanel lobbyPane;
    private JPanel lobbyLeftPane;
    private JPanel lobbyRightPane;
    private JPanel lobbyCenterPane;
    private RoomListTapPane roomListTapPane;
    private JButton btnItemStore;
    private JButton btnCreateRoom;
    private JButton btnExchangeRoom;
    private JTextArea t_globalChat;
    private JTextField tf_globalChat;
    private JButton btnSendGlobalChat;

    private ImageIcon userCharacterIcon = new ImageIcon(getClass().getResource("/client/assets/game/user_character.png"));
    private ImageIcon logoIcon = new ImageIcon(getClass().getResource("/client/assets/logo/logo.png"));

    private String userId;
    private ManageNetwork network;

    public LobbyFrame(String userId, ManageNetwork network) {
        setTitle("Lobby");
        setBounds(100,100, 960, 672);
        setResizable(false); // 크기 고정
        this.userId = userId;
        this.network = network;

        lobbyPane = new JPanel();
        lobbyPane.setLayout(new BorderLayout());
        lobbyPane.setBackground(Color.BLACK);
        lobbyPane.add(LobbyLeftPanel(), BorderLayout.WEST);
        lobbyPane.add(LobbyRightPanel(), BorderLayout.EAST);
        lobbyPane.add(LobbyCenterPanel(), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(lobbyPane);
        setVisible(true); // 프레임 보이도록
    }

    private JPanel LobbyCenterPanel() {
        lobbyCenterPane = new JPanel();
        lobbyCenterPane.setLayout(null);
        lobbyCenterPane.setBackground(Color.BLACK);
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
                ChatMsg msg = new ChatMsg(userId, ChatMsg.MODE_TX_STRING, userId + "님: " + tf_globalChat.getText());
                network.sendMessage(msg);
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

        roomListTapPane = new RoomListTapPane();
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
        btnCreateRoom = new JButton("대기방 만들기");
        btnExchangeRoom = new JButton("교환방 만들기");

        btnItemStore.setBounds(40, 340, 180,85);
        btnExchangeRoom.setBounds(40, 440, 180,85);
        btnCreateRoom.setBounds(40, 540, 180,85);

        btnItemStore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { new ItemStoreFrame(); }
        });

        btnCreateRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MakeRoomDialog(LobbyFrame.this, network);
            }
        });

        btnExchangeRoom.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ExchangeRoom.main(new String[]{});
            }
        });

        lobbyRightPane.add(btnItemStore);
        lobbyRightPane.add(btnCreateRoom);
        lobbyRightPane.add(btnExchangeRoom);


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

//    public static void main(String[] args) {
//        new LobbyFrame(null, new ManageNetwork());
//    }
}

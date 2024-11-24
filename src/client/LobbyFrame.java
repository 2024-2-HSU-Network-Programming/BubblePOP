package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyFrame extends JFrame {
    private JPanel lobbyPane;
    private JPanel lobbyLeftPane;
    private JPanel lobbyRightPane;
    private RoomListTapPane roomListTapPane;
    private JButton btnItemStore;
    private JButton btnCreateRoom;



    public LobbyFrame() {
        setTitle("Lobby");
        setBounds(100,100, 960, 672);
        setResizable(false); // 크기 고정

        lobbyPane = new JPanel();
        lobbyPane.setLayout(new BorderLayout());
        lobbyPane.setBackground(Color.BLACK);
        lobbyPane.add(LobbyLeftPanel(), BorderLayout.WEST);
        lobbyPane.add(LobbyRightPanel(), BorderLayout.EAST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(lobbyPane);
        setVisible(true); // 프레임 보이도록
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

        btnItemStore = new JButton("아이템 상점");
        btnCreateRoom = new JButton("대기방 만들기");

        btnItemStore.setBounds(40, 400, 180,85);
        btnCreateRoom.setBounds(40, 500, 180,85);

        btnCreateRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MakeRoomDialog();
            }
        });

        lobbyRightPane.add(btnItemStore);
        lobbyRightPane.add(btnCreateRoom);

        return lobbyRightPane;
    }

    public static void main(String[] args) {
        new LobbyFrame();
    }
}

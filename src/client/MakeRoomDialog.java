package client;

import shared.ChatMsg;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MakeRoomDialog extends JDialog {
    private MakeRoomPanel makeRoomPanel = new MakeRoomPanel();
    private JPasswordField roomPassword;
    private JTextField roomTitle;
    LobbyFrame lobbyFrame;
    private JComboBox<String> roomTypeComboBox;

    private ObjectOutputStream out;
    private ManageNetwork network;
    GameUser user = GameUser.getInstance();

    public MakeRoomDialog(LobbyFrame lobbyFrame, ManageNetwork network) {
        this.network = network;
        this.lobbyFrame = lobbyFrame; // 추후 통신시 삭제
        setTitle("대기방 만들기");
        setBounds(400, 300, 360, 350);
        setModal(true);
        makeRoomPanel.setLayout(null);
        makeRoomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(makeRoomPanel, BorderLayout.CENTER);

        setVisible(true);

    }

    public void sendObject(Object ob) {
        try {
            out.writeObject(ob);
            out.flush();
        } catch (IOException e) {
            System.out.println("SendObject Error");
        }
    }

    class MakeRoomPanel extends JPanel {

        public MakeRoomPanel() {
            // 방 유형 선택
            JLabel lblRoomType = new JLabel("방 유형");
            lblRoomType.setBounds(45, 10, 100, 20);
            add(lblRoomType);

            roomTypeComboBox = new JComboBox<>(new String[]{"대기방 생성", "교환방 생성"});
            roomTypeComboBox.setBounds(45, 35, 282, 30);
            add(roomTypeComboBox);

            // 방 제목 입력
            JLabel lblRoomTitle = new JLabel("방 제목");
            lblRoomTitle.setBounds(45,70, 100,20);
            add(lblRoomTitle);
            roomTitle = new JTextField();
            roomTitle.setBounds(45, 95, 282, 30);
            add(roomTitle);

            // 방 비밀번호 설정 체크박스
            JCheckBox check = new JCheckBox("");
            check.setBounds(45, 140, 282, 20);
            add(check);
            JLabel lblSettingPassword = new JLabel("비밀번호 설정");
            lblSettingPassword.setBounds(80,140, 100,20);
            add(lblSettingPassword);

            Color enableColor = new Color(225, 225, 225);

            check.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == check) {
                        if (roomPassword.getBackground() == enableColor) {
                            roomPassword.setEnabled(true);
                            roomPassword.setBackground(Color.white);
                        } else {
                            roomPassword.setEnabled(false);
                            roomPassword.setBackground(enableColor);
                        }

                    }
                }
            });

            // 방 비밀번호 입력
            roomPassword = new JPasswordField(10);
            roomPassword.setBounds(45, 165, 282, 50);
            roomPassword.setEnabled(false);
            roomPassword.setBackground(enableColor);
            roomPassword.setEchoChar('*');
            add(roomPassword);

            JButton btnCreate = new JButton("완료");
            btnCreate.setBounds(130, 250, 100,40);
            add(btnCreate);

            btnCreate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String roomType = roomTypeComboBox.getSelectedItem().toString();
                    String roomName = roomTitle.getText().trim();
                    String password = new String(roomPassword.getPassword()).trim();
                    System.out.println("password: " + password);
                    if (password.isEmpty()) { // 비밀번호가 비어 있는 경우
                        password = " "; // 비밀번호 없이 공개 방 생성
                    }
                    if(roomType.equals("대기방 생성")) {
                        // 대기방 정보 생성
                        String roomInfo = roomName + "|" + password;

                        ChatMsg roomObj = new ChatMsg(user.getId(), ChatMsg.MODE_TX_CREATEROOM, roomInfo);
                        System.out.println("user name: " + network.getName());
                        user.getNet().sendMessage(roomObj);
                        dispose(); // 다이얼로그 닫기
                        lobbyFrame.dispose();

                        //WaitingRoom.main(new String[]{});

                    } else {
                        // 교환방 정보 생성
                        String exchangeRoomInfo = roomName + "|" + password;
                        ChatMsg roomObj = new ChatMsg(user.getId(), ChatMsg.MODE_TX_CREATEEXCHANGEROOM, exchangeRoomInfo);
                        System.out.println("user name: " + network.getName());
                        user.getNet().sendMessage(roomObj);
                        dispose(); // 다이얼로그 닫기
                        lobbyFrame.dispose();
                        return;
                    }
                }
            });
        }
    }
}

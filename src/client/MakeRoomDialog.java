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
    //LobbyFrame lobbyFrame; // 추후 통신시 삭제

    private ObjectOutputStream out;
    private ManageNetwork network;
    public MakeRoomDialog(LobbyFrame lobbyFrame, ManageNetwork network) {
        this.network = network;
        //this.lobbyFrame = lobbyFrame; // 추후 통신시 삭제
        setTitle("대기방 만들기");
        setBounds(400, 300, 360, 280);
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
            // 방 제목 입력
            JLabel lblRoomTitle = new JLabel("방 제목");
            lblRoomTitle.setBounds(45,10, 100,20);
            add(lblRoomTitle);
            roomTitle = new JTextField();
            roomTitle.setBounds(45, 33, 282, 50);
            add(roomTitle);

            // 방 비밀번호 설정 체크박스
            JCheckBox check = new JCheckBox("");
            check.setBounds(45, 100, 282, 20);
            add(check);
            JLabel lblSettingPassword = new JLabel("비밀번호 설정");
            lblSettingPassword.setBounds(80,100, 100,20);
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
            roomPassword.setBounds(45, 140, 282, 50);
            roomPassword.setEnabled(false);
            roomPassword.setBackground(enableColor);
            roomPassword.setEchoChar('*');
            add(roomPassword);

            JButton btnCreate = new JButton("완료");
            btnCreate.setBounds(130, 200, 100,40);
            add(btnCreate);

            btnCreate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String roomName = roomTitle.getText();
                    String password = new String(roomPassword.getPassword());

                    // 대기방 정보 생성
                    String roomInfo = roomName + "|" + password;

                    ChatMsg roomObj = new ChatMsg("yebin", ChatMsg.MODE_TX_CREATEROOM, roomInfo);
//                    try {
//                        out.writeObject(roomObj);
//                        out.flush();
//                    } catch (IOException ex) {
//                        System.out.println("대기방 생성 메시지 전송 실패: " + ex.getMessage());
//                    }
                    network.sendMessage(roomObj);
                    dispose(); // 다이얼로그 닫기

                }
            });

        }

    }
}

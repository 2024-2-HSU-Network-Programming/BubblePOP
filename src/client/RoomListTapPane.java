package client;

import server.GameRoom;
import server.RoomManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class RoomListTapPane extends JTabbedPane {
    private JPanel tab1;
    private JPanel tab2;
    private JPanel roomPane;

    // roomPane 속성
    private JLabel lblRoomNumber;
    private JLabel lblRoomName;
    private JLabel lblPasswordStatus;
    private JLabel lblParticipants;
    private int nextY = 20; // 다음 RoomPane의 Y 좌표
    private final int roomPaneHeight = 50; // RoomPane 높이
    private int gap = 10;
    private LobbyFrame lobbyFrame;
    private ObjectInputStream in;

    public RoomListTapPane(LobbyFrame lobbyFrame) {
        tab1 = new JPanel();
        this.lobbyFrame = lobbyFrame;
        //tab1.setLayout(new BoxLayout(tab1, BoxLayout.Y_AXIS)); // 세로로 여러 방 추가 가능
        tab1.setLayout(null);
        tab1.setBackground(new Color(40,49,69));
        tab1.setPreferredSize(new Dimension(215, 572)); // 전체 크기 고정

        // RoomManager에서 초기 방 리스트를 가져와 추가
        refreshRoomList();

        // 초기 방 추가
//        addRoomPane(1, "초보자 방");
//        addRoomPane(2, "고수 방");
//        addRoomPane(3, "같이 게임해요");
        //tab1.add(Box.createVerticalStrut(10)); // 간격 추가
        addTab("대기방", tab1);
        startListeningForRooms();
        //addTab("교환방", tab2);

    }

    private JPanel RoomPane(int roomNumber, String roomName) {
        roomPane = new JPanel();
        roomPane.setBackground(new Color(60,188,233));
        roomPane.setLayout(null); // 절대 레이아웃 사용
        roomPane.setBounds(20, nextY, 180, roomPaneHeight); // 패널 크기 설정

        // 방 번호 라벨
        lblRoomNumber = new JLabel(roomName);
        lblRoomNumber.setBounds(20, 20, 120, 20); // 위치와 크기 설정
        lblRoomNumber.setForeground(Color.WHITE); // 흰색 텍스트
        roomPane.add(lblRoomNumber);

//        // 방 이름 라벨
//        lblRoomName = new JLabel(roomName);
//        lblRoomName.setBounds(20, 50, 200, 20); // 위치와 크기 설정
//        lblRoomName.setForeground(Color.WHITE); // 흰색 텍스트
//        roomPane.add(lblRoomName);

        // 클릭 이벤트 추가
        roomPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("RoomPane clicked: " + roomName);
                enterRoom(roomNumber, roomName);
            }
        });

        return roomPane;
    }

    // 새로운 RoomPane 추가 메서드
    public void addRoomPane(int roomNumber, String roomName) {
        JPanel newRoomPane = RoomPane(roomNumber, roomName);
        tab1.add(newRoomPane); // tab1에 추가
        nextY += roomPaneHeight + gap; // 다음 RoomPane의 Y 좌표 갱신
        tab1.setPreferredSize(new Dimension(215, nextY + gap)); // 패널 크기 갱신
        tab1.revalidate(); // 레이아웃 업데이트
        tab1.repaint(); // 화면 갱신
    }
    // RoomManager로부터 실시간으로 방 정보를 가져와 갱신
    public void refreshRoomList() {
        tab1.removeAll(); // 기존 RoomPane 제거
        nextY = 20; // 초기 Y 좌표로 리셋

        List<GameRoom> roomList = RoomManager.getRoomList();
        for (GameRoom room : roomList) {
            addRoomPane(room.getRoomId(), room.getRoomName());
        }

        tab1.revalidate();
        tab1.repaint();
    }

    private void enterRoom(int roomNumber, String roomName) {
        System.out.println("Entering Room: " + roomName + " (ID: " + roomNumber + ")");

        // 대기방 화면으로 이동하고 로비 창 닫기
        SwingUtilities.invokeLater(() -> {
            WaitingRoom waitingRoom = new WaitingRoom(
                    String.valueOf(roomNumber),  // roomNumber
                    roomName,                    // roomName
                    lobbyFrame.getUserId(),      // userId
                    lobbyFrame.getNetwork()      // network
            );
            waitingRoom.show();  // 대기방 표시
            lobbyFrame.dispose(); // 로비 창 닫기
        });
    }

    // 방 정보를 수신하는 스레드
    private void startListeningForRooms() {
//        new Thread(() -> {
//            try {
//                while (true) {
//                    Object obj = in.readObject(); // 서버로부터 객체 수신
//                    if (obj instanceof ChatMsg) {
//                        ChatMsg chatMsg = (ChatMsg) obj;
//                        if (chatMsg.getMode() == 101) { // 방 생성 메시지라면
//                            // 방 이름과 번호를 파싱하여 추가
//                            String[] roomInfo = chatMsg.getMessage().split(", ");
//                            String roomName = roomInfo[0].split(": ")[1];
//                            String password = roomInfo[1].split(": ")[1]; // 비밀번호
//                            int roomNumber = tab1.getComponentCount() + 1; // 새 방 번호
//                            addRoomPane(roomNumber, roomName);
//                        }
//                    }
//                }
//            } catch (ClassNotFoundException | IOException e) {
//                System.out.println("Room 수신 오류: " + e.getMessage());
//            }
//        }).start();
    }

}

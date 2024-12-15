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
    private final int roomPaneHeight = 80; // RoomPane 높이
    private int gap = 10;
    private LobbyFrame lobbyFrame;
    private ObjectInputStream in;
    private ManageNetwork network;
    GameUser user = GameUser.getInstance();

    public RoomListTapPane(LobbyFrame lobbyFrame, ManageNetwork network) {
        tab1 = new JPanel();
        this.lobbyFrame = lobbyFrame;
        this.network = network;
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

        //addTab("교환방", tab2);

    }

    private JPanel RoomPane(int roomNumber, String roomName, String password, int userListSize) {
        roomPane = new JPanel();
        roomPane.setBackground(Color.WHITE);
        roomPane.setLayout(null); // 절대 레이아웃 사용
        roomPane.setBounds(20, nextY, 200, roomPaneHeight); // 패널 크기 설정

        // 방 번호 라벨

        lblRoomNumber = new JLabel(String.valueOf(roomNumber));
        lblRoomNumber.setBounds(20, 20, 20, 20); // 위치와 크기 설정
        lblRoomNumber.setForeground(Color.black); // 흰색 텍스트
        roomPane.add(lblRoomNumber);

        // 방 이름 라벨
        lblRoomName = new JLabel(roomName);
        lblRoomName.setBounds(50, 20, 200, 20); // 위치와 크기 설정
        lblRoomName.setForeground(Color.black); // 흰색 텍스트
        roomPane.add(lblRoomName);

        // 공개, 비공개 라벨
        String passwordStatus = "";
        if(password.equals("")) {
            passwordStatus = "공개";
        } else {
            passwordStatus = "비공개";
        }
        lblPasswordStatus = new JLabel(passwordStatus);
        lblPasswordStatus.setBounds(20, 50, 200, 20); // 위치와 크기 설정
        lblPasswordStatus.setForeground(Color.black); // 흰색 텍스트
        roomPane.add(lblPasswordStatus);

        // 참여 인원수 라벨
        lblParticipants = new JLabel(Integer.toString(userListSize) + "/2");
        lblParticipants.setBounds(120, 50, 80, 20); // 위치와 크기 설정
        lblParticipants.setForeground(Color.black); // 흰색 텍스트
        roomPane.add(lblParticipants);

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
    public void addRoomPane(int roomNumber, String roomName, String password, int userListSize) {
        System.out.println("addRoomPane 호출: RoomID=" + roomNumber + ", RoomName=" + roomName);
        JPanel newRoomPane = RoomPane(roomNumber, roomName, password, userListSize);
        tab1.add(newRoomPane); // tab1에 추가
        nextY += roomPaneHeight + gap; // 다음 RoomPane의 Y 좌표 갱신
        tab1.setPreferredSize(new Dimension(215, nextY + gap)); // 패널 크기 갱신
        tab1.revalidate(); // 레이아웃 업데이트
        tab1.repaint(); // 화면 갱신
        System.out.println("RoomPane 갱신 완료: " + tab1.getComponentCount() + "개의 방");
    }
    // RoomManager로부터 실시간으로 방 정보를 가져와 갱신
    public void refreshRoomList() {
        tab1.removeAll(); // 기존 RoomPane 제거
        tab1.setLayout(null); // 레이아웃을 명시적으로 재설정
        nextY = 20; // 초기 Y 좌표로 리셋
        List<GameRoom> rooms = RoomManager.getInstance().getAllRooms();
        System.out.println("RoomManager에서 반환된 방 개수: " + rooms.size());

        for (GameRoom room : rooms) {
            System.out.println("Room 갱신 중: RoomID=" + room.getRoomId() + ", RoomName=" + room.getRoomName());
            addRoomPane(room.getRoomId(), room.getRoomName(), room.getRoomPassword(), room.getUserListSize());
        }

        System.out.println(RoomManager.getInstance().getAllRooms());
        tab1.revalidate();
        tab1.repaint();
    }

    private void enterRoom(int roomNumber, String roomName) {
        System.out.println("Entering Room: " + roomName + " (ID: " + roomNumber + ")");
        String currentUser = user.getId(); // 현재 사용자를 가져오는 로직 추가 필요

        //boolean success = RoomManager.addUserToRoom(roomNumber, currentUser);
        boolean success = RoomManager.getInstance().addUserToRoom(roomNumber, currentUser);

        if (success) {
            System.out.println("User successfully entered the room.");
            SwingUtilities.invokeLater(() ->
                    {
                        WaitingRoom waitingRoom = new WaitingRoom(Integer.toString(roomNumber), roomName, currentUser, network);
                        waitingRoom.show();
                    }

            );
        } else {
            JOptionPane.showMessageDialog(null, "방이 가득 찼습니다!", "입장 실패", JOptionPane.ERROR_MESSAGE);
        }

        // 방 상태 갱신
        refreshRoomList();

//        // 대기방 화면으로 이동하고 로비 창 닫기
//        SwingUtilities.invokeLater(() -> {
//            WaitingRoom waitingRoom = new WaitingRoom(
//                    String.valueOf(roomNumber),  // roomNumber
//                    roomName,                    // roomName
//                    lobbyFrame.getUserId(),      // userId
//                    lobbyFrame.getNetwork()      // network
//            );
//            waitingRoom.show();  // 대기방 표시
//            lobbyFrame.dispose(); // 로비 창 닫기
//        });
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

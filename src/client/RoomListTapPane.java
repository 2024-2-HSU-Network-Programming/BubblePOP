package client;

import server.GameRoom;
import server.ExchangeRoom;
import server.RoomManager;
import shared.ChatMsg;

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
        addTab("대기방", tab1);

        tab2 = new JPanel();
        tab2.setLayout(null);
        tab2.setBackground(new Color(60, 70, 90));
        tab2.setPreferredSize(new Dimension(215, 572));
        addTab("교환방", tab2);

        // RoomManager에서 초기 방 리스트를 가져와 추가
        refreshRoomList();

        // 초기 방 추가
//        addRoomPane(1, "초보자 방");
//        addRoomPane(2, "고수 방");
//        addRoomPane(3, "같이 게임해요");


    }

    private JPanel RoomPane(int roomNumber, String roomName, String password, int userListSize, boolean isExchangeRoom) {
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
        if(password.equals(" ")) {
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

        String finalPasswordStatus = passwordStatus;
        // 클릭 이벤트 추가
        roomPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("RoomPane clicked: " + roomName);
                handleRoomClick(roomNumber, roomName, finalPasswordStatus, isExchangeRoom);
//                //enterRoom(roomNumber, roomName);
//                if(finalPasswordStatus.equals("비공개")) {
//                    // 비밀번호 입력 다이얼로그
//                    String inputPassword = JOptionPane.showInputDialog(
//                            null,
//                            "비밀번호를 입력하세요:",
//                            "비공개 방 입장",
//                            JOptionPane.PLAIN_MESSAGE
//                    );
//                    if (inputPassword != null) { // 사용자가 비밀번호를 입력했을 때
//                        // 서버로 입력된 비밀번호를 검증하는 로직 추가 필요
//                        boolean isPasswordCorrect = checkPassword(roomNumber, inputPassword);
//                        if (isPasswordCorrect) {
//                            enterRoom(roomNumber, roomName); // 비밀번호 검증 성공 시 방 입장
//                        } else {
//                            JOptionPane.showMessageDialog(
//                                    null,
//                                    "비밀번호가 올바르지 않습니다!",
//                                    "입장 실패",
//                                    JOptionPane.ERROR_MESSAGE
//                            );
//                        }
//                    }
//                } else {
//                    enterRoom(roomNumber, roomName);
//                }

            }
        });

        return roomPane;
    }
    private void handleRoomClick(int roomNumber, String roomName, String passwordStatus, boolean isExchangeRoom) {
        if (passwordStatus.equals("비공개")) {
            // 비밀번호 입력 다이얼로그
            String inputPassword = JOptionPane.showInputDialog(
                    null,
                    "비밀번호를 입력하세요:",
                    "비공개 방 입장",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (inputPassword == null || inputPassword.isBlank()) {
                return; // 입력이 취소되었거나 비어있는 경우
            }
            // 비밀번호 확인
            boolean isPasswordCorrect = isExchangeRoom
                    ? checkExchangeRoomPassword(roomNumber, inputPassword)
                    : checkPassword(roomNumber, inputPassword);
            if (!isPasswordCorrect) {
                JOptionPane.showMessageDialog(
                        null,
                        "비밀번호가 올바르지 않습니다!",
                        "입장 실패",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
        // 방 입장 처리
        if (isExchangeRoom) {
            enterExchangeRoom(roomNumber, roomName);
        } else {
            enterRoom(roomNumber, roomName);
        }
    }

    // 비밀번호 체크 함수
    private boolean checkPassword(int roomNumber, String inputPassword) {
        // RoomManager에서 해당 방 가져오기
        GameRoom room = RoomManager.getInstance().getGameRoom(String.valueOf(roomNumber));
        // 서버로 비밀번호 검증 요청
//        ChatMsg passwordCheckMsg = new ChatMsg(
//                GameUser.getInstance().getId(),
//                ChatMsg.MODE_PASSWORD_CHECK,
//                roomNumber + "|" + inputPassword
//        );
        if (room == null) {
            // 방이 존재하지 않을 경우 처리
            System.out.println("방을 찾을 수 없습니다: RoomID=" + roomNumber);
            JOptionPane.showMessageDialog(
                    null,
                    "방을 찾을 수 없습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // 비밀번호 비교
        String roomPassword = room.getRoomPassword();
        System.out.println("입력된 비밀번호: " + inputPassword + ", 방 비밀번호: " + roomPassword);
        return roomPassword.equals(inputPassword);
        // 서버에서 검증 결과를 받아오는 로직 필요
        // 예를 들어, 서버에서 결과를 콜백으로 받을 수 있다면 콜백 함수에서 결과를 처리합니다.
        //return network.waitForPasswordCheckResult(roomNumber); // 이 메서드는 서버 응답 대기 후 결과를 반환
    }
    // 교환방 비밀번호 체크 함수
    private boolean checkExchangeRoomPassword(int roomNumber, String inputPassword) {
        ExchangeRoom room = RoomManager.getInstance().getExchangeRoom(String.valueOf(roomNumber));
        if (room == null) {
            System.out.println("교환방을 찾을 수 없습니다: RoomID=" + roomNumber);
            JOptionPane.showMessageDialog(
                    null,
                    "교환방을 찾을 수 없습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        System.out.println("입력된 비밀번호: " + inputPassword + ", 방 비밀번호: " + room.getRoomPassword());
        return room.getRoomPassword().equals(inputPassword);
    }

    // 새로운 RoomPane 추가 메서드
    public void addRoomPane(int roomNumber, String roomName, String password, int userListSize, JPanel tab, boolean isExchangeRoom) {
        System.out.println("addRoomPane 호출: RoomID=" + roomNumber + ", RoomName=" + roomName);
        JPanel newRoomPane = RoomPane(roomNumber, roomName, password, userListSize, isExchangeRoom);
        tab.add(newRoomPane); // 선택한 탭에 추가
        nextY += roomPaneHeight + gap; // 다음 RoomPane의 Y 좌표 갱신
        tab.setPreferredSize(new Dimension(215, nextY + gap)); // 패널 크기 갱신
        tab.revalidate(); // 레이아웃 업데이트
        tab.repaint(); // 화면 갱신
        System.out.println("RoomPane 갱신 완료: " + tab.getComponentCount() + "개의 방");
    }

    // RoomManager로부터 실시간으로 방 정보를 가져와 갱신
    public void refreshRoomList() {
        SwingUtilities.invokeLater(() -> {
            tab1.removeAll(); // 기존 RoomPane 제거
            tab1.setLayout(null);
            nextY = 20; // 초기 Y 좌표로 리셋

            List<GameRoom> rooms = RoomManager.getInstance().getAllRooms();
            System.out.println("RoomManager에서 반환된 방 개수: " + rooms.size());

            for (GameRoom room : rooms) {
                System.out.println("Room 갱신 중: RoomID=" + room.getRoomId() + ", RoomName=" + room.getRoomName());
                addRoomPane(room.getRoomId(), room.getRoomName(), room.getRoomPassword(), room.getUserListSize(), tab1, false);
            }

            tab1.revalidate();
            tab1.repaint();

            // 교환방 갱신
            tab2.removeAll(); // 기존 RoomPane 제거
            tab2.setLayout(null); // 레이아웃을 명시적으로 재설정
            nextY = 20; // 초기 Y 좌표로 리셋
            List<ExchangeRoom> exchangeRooms = RoomManager.getInstance().getAllExchangeRooms(); // 교환방만 가져오기
            System.out.println("RoomManager에서 반환된 교환방 개수: " + exchangeRooms.size());
            for (ExchangeRoom room : exchangeRooms) {
                System.out.println("교환방 갱신 중: RoomID=" + room.getRoomId() + ", RoomName=" + room.getRoomName() + "UserListSize= " + room.getUserListSize());
                addRoomPane(room.getRoomId(), room.getRoomName(), room.getRoomPassword(), room.getUserListSize(), tab2, true);
            }
            tab2.revalidate();
            tab2.repaint();
        });

    }

    private void enterRoom(int roomNumber, String roomName) {
        System.out.println("Entering Room: " + roomName + " (ID: " + roomNumber + ")");
        String currentUser = user.getId();

        // 먼저 입장 메시지를 서버로 전송
        ChatMsg enterMsg = new ChatMsg(currentUser, ChatMsg.MODE_ENTER_ROOM, roomNumber + "|" + currentUser);
        user.getNet().sendMessage(enterMsg);

        boolean success = RoomManager.getInstance().addUserToRoom(roomNumber, currentUser);

        if (success) {
            System.out.println("User successfully entered the room.");
            SwingUtilities.invokeLater(() -> {
                WaitingRoom waitingRoom = new WaitingRoom(
                        String.valueOf(roomNumber),
                        roomName,
                        currentUser,
                        user.getNet()
                );
                waitingRoom.show();
                lobbyFrame.dispose();
            });
        } else {
            JOptionPane.showMessageDialog(null, "방이 가득 찼습니다!", "입장 실패", JOptionPane.ERROR_MESSAGE);
        }

        refreshRoomList();
    }

//    private void enterExchangeRoom(int roomNumber, String roomName) {
//        System.out.println("Entering Exchange Room: " + roomName + " (ID: " + roomNumber + ")");
//        String currentUser = user.getId();
//
//        // 서버로 입장 메시지 전송
//        ChatMsg enterMsg = new ChatMsg(currentUser, ChatMsg.MODE_ENTER_EXCHANGEROOM, roomNumber + "|" + currentUser);
//        user.getNet().sendMessage(enterMsg);
//        boolean success = RoomManager.getInstance().addUserToExchangeRoom(roomNumber, currentUser);
//        if (success) {
//            System.out.println("User successfully entered the exchange room.");
//            SwingUtilities.invokeLater(() -> {
//                ExchangeWaitingRoom exchangeRoom = new ExchangeWaitingRoom(
//                        String.valueOf(roomNumber),
//                        roomName,
//                        currentUser,
//                        user.getNet()
//                );
//                exchangeRoom.show();
//                lobbyFrame.dispose();
//            });
//        } else {
//            JOptionPane.showMessageDialog(null, "방이 가득 찼습니다!", "입장 실패", JOptionPane.ERROR_MESSAGE);
//        }
//        refreshRoomList();
//    }
private void enterExchangeRoom(int roomNumber, String roomName) {
    System.out.println("Entering Exchange Room: " + roomName + " (ID: " + roomNumber + ")");
    String currentUser = user.getId();

    // 서버로 입장 메시지 전송
    ChatMsg enterMsg = new ChatMsg(currentUser, ChatMsg.MODE_ENTER_EXCHANGEROOM, roomNumber + "|" + currentUser);
    user.getNet().sendMessage(enterMsg);

    // 교환방 UI는 서버 응답을 기반으로 동기화
    System.out.println("입장 요청 메시지를 서버로 전송했습니다.");
    boolean success = RoomManager.getInstance().addUserToExchangeRoom(roomNumber, currentUser);

    if (success) {
        System.out.println("User successfully entered the room.");
        SwingUtilities.invokeLater(() -> {
            ExchangeWaitingRoom waitingRoom = new ExchangeWaitingRoom(
                    String.valueOf(roomNumber),
                    roomName,
                    currentUser,
                    user.getNet(),
                    lobbyFrame  // LobbyFrame 인스턴스 전달
            );
            waitingRoom.show();
            lobbyFrame.dispose();
        });

    }
}


}

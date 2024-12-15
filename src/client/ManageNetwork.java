package client;

import server.GameRoom;
import server.RoomManager;
import shared.ChatMsg;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ManageNetwork extends Thread{
    private GameUser gameUser;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private LobbyFrame lobbyFrame;
    private int roomCount = RoomManager.getRoomListSize();

    public ManageNetwork(ObjectInputStream in, ObjectOutputStream out, Socket socket) {
        this.ois = in;
        this.oos = out;
        this.socket = socket;
        //this.lobbyFrame = lobbyFrame;
    }

    public ObjectInputStream getOIS() {
        return this.ois;
    }

    public ObjectOutputStream getOOS() {
        return this.oos;
    }

    @Override
    public void run() {
        while(true) {
            Object objCm = null;
            String msg = null;
            ChatMsg cm;

            try {
                objCm = ois.readObject();
                System.out.println("데이터 수신 완료!" + objCm);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if(objCm != null) {
                if(objCm instanceof ChatMsg) {
                    cm = (ChatMsg) objCm;
                    System.out.println(cm.getMode());
                    switch (cm.getMode()) {
                        case ChatMsg.MODE_LOGIN:
                            lobbyFrame = new LobbyFrame();
                            if (lobbyFrame != null) {
                                lobbyFrame.addGlobalChatMessage(cm.getMessage());
                            }
                            //lobbyFrame = new LobbyFrame(this, cm.getUserId());
                            //lobbyFrame.addGlobalChatMessage(cm.getUserId() + "님이 로그인했습니다!");
                            break;
                        case ChatMsg.MODE_LOGOUT:
                            System.out.println("서버에서 연결 종료 메시지 수신: " + cm.getMessage());
                            closeConnection();
                            return; // 쓰레드 종료
                        case ChatMsg.MODE_TX_STRING:
                            System.out.println("서버에서 스트링값 수신: " + cm.getMessage());
                            lobbyFrame.addGlobalChatMessage(cm.getMessage());
                            break;
                        case ChatMsg.MODE_TX_CREATEROOM:
                            if (lobbyFrame != null) {
                                String[] roomInfo = cm.getMessage().split("\\|");
                                if (roomInfo.length >= 4) {
                                    int roomId = Integer.parseInt(roomInfo[0]);
                                    String ownerName = roomInfo[1];
                                    String roomName = roomInfo[2];
                                    String roomPassword = roomInfo[3];
                                    RoomManager.getInstance().createRoom(ownerName, roomName, roomPassword); // 방 생성
                                    lobbyFrame.getRoomListPane().refreshRoomList(); // UI 갱신
                                    // UI 갱신 작업은 SwingUtilities.invokeLater를 통해 실행
                                    SwingUtilities.invokeLater(() -> {
                                        lobbyFrame.getRoomListPane().addRoomPane(roomId, roomName, roomPassword, 1);
                                        System.out.println("Adding RoomPane: RoomID=" + roomId + ", RoomName=" + roomName);

                                        lobbyFrame.getRoomListPane().refreshRoomList(); // 강제 UI 갱신
                                        lobbyFrame.updateRoomList("새로운 대기방 " + roomName + "에 들어오세요!\n");
                                    });
                                    // 방 생성자인 경우에만 WaitingRoom 오픈
                                    if (ownerName.equals(lobbyFrame.getUserId())) {
                                        WaitingRoom waitingRoom = new WaitingRoom(
                                                String.valueOf(roomId),
                                                roomName,
                                                ownerName,
                                                this
                                        );
                                        waitingRoom.show();
                                        lobbyFrame.dispose();
                                    }
                                }
                            }
                            break;
                        case ChatMsg.MODE_LEAVE_ROOM:
                            String[] leaveInfo = cm.getMessage().split("\\|");
                            int leavingRoomId = Integer.parseInt(leaveInfo[0]);
                            String leavingUser = leaveInfo[1];

                            // UI 업데이트 로직 추가
                            if (lobbyFrame != null) {
                                lobbyFrame.updateRoomList(leavingUser + "님이 방에서 나갔습니다.");
                                lobbyFrame.getRoomListPane().refreshRoomList(); // 방 목록 새로고침
                            }
                            break;

                        default:
                            System.out.println("알 수 없는 모드: " + cm.getMode());
                    }
                }
            } else {
                System.out.println("받은 객체가 없음");
            }


        }
    }
    // 메시지를 서버로 전송하는 메서드
    public synchronized void sendMessage(ChatMsg msg) {
        try {
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            System.err.println("메시지 전송 오류: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("클라이언트 소켓 종료 오류: " + e.getMessage());
        }
        System.out.println("클라이언트 연결 종료");
    }
}

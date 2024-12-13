package client;

import server.RoomManager;
import shared.ChatMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ManageNetwork extends Thread{
    private GameUser gameUser;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private LobbyFrame lobbyFrame;
    private int roomCount = RoomManager.getRoomListSize();
    public ManageNetwork(ObjectInputStream in, ObjectOutputStream out, Socket socket, LobbyFrame lobbyFrame) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.lobbyFrame = lobbyFrame;
    }

    @Override
    public void run() {
        while(true) {
            Object objCm = null;
            String msg = null;
            ChatMsg cm;

            try {
                objCm = in.readObject();
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
                        case ChatMsg.MODE_TX_CREATEROOM:
                            System.out.println("새로운 대기방 생성 !: " + cm.getMessage());
                            if (lobbyFrame != null) {
                                String[] roomInfo = cm.getMessage().split("\\|");

                                // 서버에서 할당된 실제 roomId 사용
                                if (roomInfo.length >= 3) {
                                    int roomId = Integer.parseInt(roomInfo[0]);
                                    String ownerName = roomInfo[1];
                                    String roomName = roomInfo[2];

                                    lobbyFrame.getRoomListPane().addRoomPane(roomId, roomName);
                                    lobbyFrame.updateRoomList("새로운 대기방 " + roomName + "에 들어오세요!");

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
            out.writeObject(msg);
            out.flush();
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

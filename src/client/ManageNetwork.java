package client;

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
    private WaitingRoom waitingRoom;
    private OriginalGameScreen originalGameScreen;

    public ManageNetwork(ObjectInputStream in, ObjectOutputStream out, Socket socket) {
        this.ois = in;
        this.oos = out;
        this.socket = socket;
        //this.lobbyFrame = lobbyFrame;
    }
    public void setWaitingRoom(WaitingRoom waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public ObjectInputStream getOIS() {
        return this.ois;
    }

    public ObjectOutputStream getOOS() {
        return this.oos;
    }

    @Override
    public void run() {
        // lobbyFrame 초기화를 여기서 한 번만 수행
        lobbyFrame = new LobbyFrame();

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
                            // 로비 프레임은 이미 생성되어 있으므로 메시지만 추가
                            if (lobbyFrame != null) {
                                lobbyFrame.addGlobalChatMessage(cm.getMessage());
                            }
                            break;

                        case ChatMsg.MODE_TX_STRING:
                            if (lobbyFrame != null) {
                                System.out.println("서버에서 스트링값 수신: " + cm.getMessage());
                                lobbyFrame.addGlobalChatMessage(cm.getMessage());
                                lobbyFrame.getRoomListPane().refreshRoomList(); // UI 갱신 추가
                            }
                            break;

                        case ChatMsg.MODE_TX_CREATEROOM:
                            if (lobbyFrame != null) {
                                String[] roomInfo = cm.getMessage().split("\\|");
                                if (roomInfo.length >= 4) {
                                    int roomId = Integer.parseInt(roomInfo[0]);
                                    String ownerName = roomInfo[1];
                                    String roomName = roomInfo[2];
                                    String roomPassword = roomInfo[3];

                                    // 방 생성 및 UI 갱신
                                    RoomManager.getInstance().createRoom(ownerName, roomName, roomPassword);
                                    SwingUtilities.invokeLater(() -> {
                                        lobbyFrame.getRoomListPane().refreshRoomList();
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
                        case ChatMsg.MODE_TX_CREATEEXCHANGEROOM:
                        if (lobbyFrame != null) {
                            String[] exchangeRoomInfo = cm.getMessage().split("\\|");
                            if (exchangeRoomInfo.length >= 4) {
                                int roomId = Integer.parseInt(exchangeRoomInfo[0]);
                                String ownerName = exchangeRoomInfo[1];
                                String roomName = exchangeRoomInfo[2];
                                String roomPassword = exchangeRoomInfo[3];

                                // 방 생성 및 UI 갱신
                                RoomManager.getInstance().createExchangeRoom(ownerName, roomName, roomPassword);
                                SwingUtilities.invokeLater(() -> {
                                    lobbyFrame.getRoomListPane().refreshRoomList();
                                    lobbyFrame.updateRoomList("새로운 교환방 " + roomName + "에 들어오세요!\n");
                                });

                                // 방 생성자인 경우에만 WaitingRoom 오픈
//                                if (ownerName.equals(lobbyFrame.getUserId())) {
//                                    ExchangeWaitingRoom exchangeWaitingRoom = new ExchangeWaitingRoom(
//                                            String.valueOf(roomId),
//                                            roomName,
//                                            ownerName,
//                                            this
//                                    );
//                                    // 교환 대기방을 `ManageNetwork`와 연결
//                                    this.setExchangeWaitingRoom(exchangeWaitingRoom);
//                                    exchangeWaitingRoom.show();
//                                    lobbyFrame.dispose();
//                                }
                            }
                        }
                        break;

                        case ChatMsg.MODE_PASSWORD_CHECK:
                            String[] resultData = cm.getMessage().split("\\|");
                            int roomId = Integer.parseInt(resultData[0]);
                            boolean isPasswordCorrect = Boolean.parseBoolean(resultData[1]);
                            // 결과를 전역 변수나 콜백 함수로 전달
                            //setPasswordCheckResult(roomId, isPasswordCorrect); // 결과 저장
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
                        case ChatMsg.MODE_ENTER_ROOM:
                            String[] enterInfo = cm.getMessage().split("\\|");
                            int enterRoomId = Integer.parseInt(enterInfo[0]);
                            String enteringUser = enterInfo[1];

                            System.out.println("Received ENTER_ROOM message: " + cm.getMessage());

                            // waitingRoom이 null이 아닐 때만 업데이트
                            if (waitingRoom != null) {
                                waitingRoom.updatePlayer2Name(enteringUser);
                                System.out.println("Updated waiting room with user: " + enteringUser);
                            } else {
                                System.out.println("WaitingRoom is null");
                            }
                            break;
                        case ChatMsg.MODE_ENTER_EXCHANGEROOM:
                            String[] exchangeEnterInfo = cm.getMessage().split("\\|");
                            int exchangeRoomId = Integer.parseInt(exchangeEnterInfo[0]);
                            String exchangeUserName = exchangeEnterInfo[1];
                            System.out.println("해당 교환방 유저 수: " + RoomManager.getExchangeRoom(Integer.toString((exchangeRoomId))).getUserListSize());
                            // 교환 대기방이 열려 있는 경우
//                            if (exchangeWaitingRoom != null && exchangeWaitingRoom.getRoomId().equals(String.valueOf(exchangeRoomId))) {
//                                exchangeWaitingRoom.updatePlayerName(exchangeUserName); // 대기방 UI 업데이트
//                                System.out.println("교환 대기방 업데이트: " + exchangeUserName + " 추가됨.");
//                            } else {
//                                System.out.println("교환 대기방이 활성화되지 않았습니다.");
//                            }
                            break;
                        case ChatMsg.MODE_TX_ROOMCHAT:
                            if (waitingRoom != null) {
                                String[] roomChatData = cm.getMessage().split("\\|", 2);
                                if (roomChatData.length == 2) {
                                    waitingRoom.receiveMessage(roomChatData[1]);
                                }
                            }
                            break;
                        case ChatMsg.MODE_BUYITEM:
                            String[] itemData = cm.getMessage().split("\\|");
                            String itemName = itemData[0];
                            int quantity = Integer.parseInt(itemData[1]);
                            int totalCost = Integer.parseInt(itemData[2]);

                            // 사용자 데이터 업데이트
                            GameUser.getInstance().addItem(itemName, quantity, totalCost);
                            System.out.println("아이템 구매 완료: " + itemName + " " + quantity + "개");
                            System.out.println("코인: " + GameUser.getInstance().getCoin() + "\n");
                            break;
                        case ChatMsg.MODE_SELLITEM:
                            itemData = cm.getMessage().split("\\|");
                            itemName = itemData[0];
                            quantity = Integer.parseInt(itemData[1]);
                            totalCost = Integer.parseInt(itemData[2]);
                            // 사용자 데이터 업데이트
                            GameUser.getInstance().sellItem(itemName, quantity, totalCost);
                            System.out.println("아이템 판매 완료: " + itemName + " " + quantity + "개");
                            System.out.println("코인: " + GameUser.getInstance().getCoin() + "\n");
                            break;
                        case ChatMsg.MODE_GAME_START:
                            if (waitingRoom != null) {
                                SwingUtilities.invokeLater(() -> {
                                    // gameUser가 null이면 초기화
                                    if (gameUser == null) {
                                        gameUser = GameUser.getInstance();
                                    }

                                    // player2의 이름 가져오기
                                    String player2Name = waitingRoom.getPlayer2Name(); // 이 메서드를 WaitingRoom에 추가해야 함

                                    // 대기방 창 닫기
                                    waitingRoom.dispose();

                                    // 게임 화면 시작 (player2Name 전달)
                                    OriginalGameScreen gameScreen = new OriginalGameScreen(
                                            gameUser.getId(),
                                            this,
                                            waitingRoom.getRoomOwner().equals(gameUser.getId()),
                                            player2Name  // 상대방 이름 전달
                                    );
                                    gameScreen.setVisible(true);
                                    originalGameScreen = gameScreen;
                                });
                            }
                            break;
                        case ChatMsg.MODE_TX_GAME:
                            // 게임 상태 업데이트 처리
                            if (originalGameScreen != null) {
                                // 메시지를 보낸 사용자가 현재 사용자가 아닐 때만 업데이트
                                String senderId = cm.getUserId();
                                if (!senderId.equals(gameUser.getId())) {
                                    originalGameScreen.updateOpponentState(cm.getMessage());
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
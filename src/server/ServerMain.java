package server;

import client.GameUser;
import shared.ChatMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class ServerMain extends JFrame {
    private int port;
    private JTextArea t_display;
    private Vector<String> connectedUsers; // 접속 중인 사용자 목록
    private Vector<ClientHandler> users;
    private Map<Socket, ObjectOutputStream> clientStreams;

    private ChatMsg cmsg;
    private Socket clientSocket;

    public ServerMain(int port) {
        super("BubblePOP Server");
        this.port = port;
        //users = new Vector<>();

        connectedUsers = new Vector<>();
        clientStreams = new HashMap<>(); // 초기화

        buildGUI();

        this.setBounds(800, 300, 500, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /* UI 부분 */
    public void buildGUI() {
        this.setLayout(new BorderLayout());
        this.add(createDisplayPanel(), BorderLayout.CENTER);
        this.add(createControlPanel(), BorderLayout.SOUTH);
    }

    private JPanel createDisplayPanel() {
        t_display = new JTextArea();
        t_display.setEditable(false);

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.add(t_display, BorderLayout.CENTER);

        return displayPanel;
    }

    public JPanel createControlPanel() {
        JButton sendButton = new JButton("서버 종료");

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(sendButton, BorderLayout.CENTER);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return controlPanel;
    }

    /* 클라이언트와의 통신 코드 시작 */
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            t_display.append("서버가 시작되었습니다.\n");
            while(true) {
                clientSocket = serverSocket.accept(); // 클라이언트의 연결 요청을 수락함
                t_display.append("클라이언트가 연결되었습니다.\n");
                //receiveMessages(clientSocket); // 연결되었으니 clientSocket에서 전달한 문자열을 받아야되겠지?
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                // 클라이언트와 통신 처리
                new Thread(() -> handleClient(clientSocket)).start();
                //clientHandler.start();
            }
        } catch(IOException e) {
            System.err.println("서버 오류 > " + e.getMessage());
            t_display.append("서버 오류: " + e.getMessage() + "\n");
        }
    }
    // 브로드캐스트
    public synchronized void broadcasting(ChatMsg msg) {
        synchronized (clientStreams) {
            for (ObjectOutputStream out : clientStreams.values()) {
                try {
                    out.writeObject(msg);
                    out.flush();
                } catch (IOException e) {
                    t_display.append("브로드캐스트 오류: " + e.getMessage() + "\n");
                }
            }
        }
    }

    // 클라이언트 연결 해제
    public synchronized void removeClient(ClientHandler clientHandler) {
        users.remove(clientHandler);
        t_display.append("클라이언트 접속 종료. 현재 참가자 수: " + users.size() + "\n");
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            synchronized (clientStreams) {
                clientStreams.put(clientSocket, out); // 클라이언트 추가
            }

            // ChatMsg 객체 수신
//            ChatMsg cmsg = (ChatMsg) in.readObject();
//            String userName = cmsg.getUserId(); // ChatMsg에서 사용자 ID 추출
//

            // 클라이언트와 계속 통신
            while (true) {
                ChatMsg msg = (ChatMsg) in.readObject(); // 메시지 수신
                String userName = msg.getUserId();
                switch (msg.getMode()) {
                    case ChatMsg.MODE_LOGIN:
                        //중복 사용자 검사
                        synchronized (connectedUsers) {
                            if (connectedUsers.contains(userName)) {
                                t_display.append("중복된 사용자: " + userName);
                                t_display.append("접속 거부: 이미 접속 중입니다.");
                                clientSocket.close(); // 연결 종료
                                return;
                            } else {
                                connectedUsers.add(userName); // 사용자 추가
                                t_display.append("새 사용자 접속: " + userName + "\n");
                                t_display.append("현재 참가자 수: " + connectedUsers.size());

                                // 현재 존재하는 모든 대기방 정보를 전송
                                for (GameRoom room : RoomManager.getInstance().getAllRooms()) {
                                    ChatMsg existingRoomMsg = new ChatMsg("Server", ChatMsg.MODE_TX_CREATEROOM,
                                            room.getRoomId() + "|" + room.getRoomOwner() + "|" + room.getRoomName() + "|" + room.getRoomPassword());
                                    out.writeObject(existingRoomMsg);
                                }

                                // 현재 존재하는 모든 교환방 정보를 전송
                                for (ExchangeRoom room : RoomManager.getInstance().getAllExchangeRooms()) {
                                    ChatMsg existingExchangeRoomMsg = new ChatMsg("Server", ChatMsg.MODE_TX_CREATEEXCHANGEROOM,
                                            room.getRoomId() + "|" + room.getRoomOwner() + "|" + room.getRoomName() + "|" + room.getRoomPassword());
                                    out.writeObject(existingExchangeRoomMsg);
                                }

                                ChatMsg welcomeMsg = new ChatMsg(userName, ChatMsg.MODE_LOGIN, userName + "님 환영합니다!");
                                broadcasting(welcomeMsg);
                            }
                        }
                        break;

                    case ChatMsg.MODE_TX_STRING:
                        t_display.append(userName + ": " + msg.getMessage() + "\n");
                        broadcasting(msg); // 다른 사용자들에게 메시지 전송
                        break;
                    case ChatMsg.MODE_LOGOUT:
                        t_display.append("사용자 로그아웃: " + userName + "\n");
                        synchronized (connectedUsers) {
                            connectedUsers.remove(userName);
                        }
                        broadcasting(new ChatMsg("Server", ChatMsg.MODE_LOGOUT, userName + "님이 로그아웃했습니다."));
                        return; // 통신 종료
                    case ChatMsg.MODE_PASSWORD_CHECK:
                        String[] passwordData = msg.getMessage().split("\\|");
                        int roomId = Integer.parseInt(passwordData[0]);
                        String inputPassword = passwordData[1];
                        GameRoom room = RoomManager.getInstance().getGameRoom(String.valueOf(roomId));
                        boolean isPasswordCorrect = room != null && room.getRoomPassword().equals(inputPassword);
                        ChatMsg passwordCheckResult = new ChatMsg(
                                "Server",
                                ChatMsg.MODE_PASSWORD_CHECK,
                                roomId + "|" + isPasswordCorrect
                        );
                        out.writeObject(passwordCheckResult); // 클라이언트로 결과 전송
                        break;
                    case ChatMsg.MODE_TX_CREATEROOM:
                        String[] roomData = msg.getMessage().split("\\|");
                        if (roomData.length < 2) { // 배열 크기 점검
                            t_display.append("잘못된 방 생성 요청: " + msg.getMessage() + "\n");
                            break;
                        }
                        String roomName = roomData[0];
                        String password = roomData[1];

                        // 서버에서 방 생성
                        GameRoom newRoom = RoomManager.getInstance().createRoom(userName, roomName, password);

                        // 디버깅용 메시지
                        t_display.append("\n방 생성 - RoomID: " + newRoom.getRoomId() + ", RoomName: " + roomName + "\n");

                        // 생성된 방 정보를 모든 클라이언트에 브로드캐스트
                        ChatMsg roomBroadcastMsg = new ChatMsg(userName, ChatMsg.MODE_TX_CREATEROOM,
                                newRoom.getRoomId() + "|" + userName + "|" + roomName + "|" + password);

                        broadcasting(roomBroadcastMsg); // 모든 클라이언트에 전송
                        break;
                    case ChatMsg.MODE_TX_CREATEEXCHANGEROOM:
                        String[] exchangeRoomData = msg.getMessage().split("\\|");
                        if (exchangeRoomData.length < 2) { // 배열 크기 점검
                            t_display.append("잘못된 방 생성 요청: " + msg.getMessage() + "\n");
                            break;
                        }
                        String exchangeRoomName = exchangeRoomData[0];
                        String exchangeRoomPw = exchangeRoomData[1]; // 비밀번호가 없을 경우 빈 문자열로 처리
                        // 서버에서 방 생성
                        ExchangeRoom newExchangeRoom = RoomManager.getInstance().createExchangeRoom(userName, exchangeRoomName, exchangeRoomPw);
                        // 디버깅용 메시지
                        t_display.append("\n방 생성 - RoomID: " + newExchangeRoom.getRoomId() + ", RoomName: " + exchangeRoomName + "\n");
                        // 생성된 방 정보를 모든 클라이언트에 브로드캐스트
                        ChatMsg exchangeRoomBroadcastMsg = new ChatMsg(userName, ChatMsg.MODE_TX_CREATEEXCHANGEROOM,
                                newExchangeRoom.getRoomId() + "|" + userName + "|" + exchangeRoomName + "|" + exchangeRoomPw);
                        broadcasting(exchangeRoomBroadcastMsg); // 모든 클라이언트에 전송
                        break;
                    case ChatMsg.MODE_LEAVE_ROOM:
                        String[] leaveRoomData = msg.getMessage().split("\\|");
                        roomId = Integer.parseInt(leaveRoomData[0]);
                        String leavingUser = leaveRoomData[1];

                        RoomManager.leaveRoom(roomId, leavingUser);

                        // 모든 클라이언트에 방 나가기 정보 브로드캐스트
                        ChatMsg leaveRoomMsg = new ChatMsg("Server", ChatMsg.MODE_LEAVE_ROOM,
                                roomId + "|" + leavingUser);
                        broadcasting(leaveRoomMsg);
                        break;
                    case ChatMsg.MODE_ENTER_ROOM:
                        String[] enterRoomData = msg.getMessage().split("\\|");
                        int enterRoomId = Integer.parseInt(enterRoomData[0]);
                        String enteringUser = enterRoomData[1];

                        // 방에 유저 추가 시도
                        boolean success = RoomManager.addUserToRoom(enterRoomId, enteringUser);

                        if(success) {
                            // 서버 로그
                            t_display.append(enteringUser + "님이 " + enterRoomId + "번 방에 입장했습니다.\n");

                            // 해당 방의 모든 유저 정보 가져오기
                            room = RoomManager.getInstance().getGameRoom(String.valueOf(enterRoomId));
                            if(room != null) {
                                // 약간의 딜레이를 주어 두 번째 클라이언트의 WaitingRoom이 생성될 시간을 줌
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                // 방의 모든 유저들에 대해 메시지 전송
                                List<String> users = room.getUserList();
                                for(String user : users) {
                                    ChatMsg enterRoomMsg = new ChatMsg("Server", ChatMsg.MODE_ENTER_ROOM,
                                            enterRoomId + "|" + user);
                                    broadcasting(enterRoomMsg);
                                }
                            }
                        }
                        break;
                    case ChatMsg.MODE_ENTER_EXCHANGEROOM:
                        String[] enterExchangeRoomData = msg.getMessage().split("\\|");
                        int exchangeRoomId = Integer.parseInt(enterExchangeRoomData[0]);
                        enteringUser = enterExchangeRoomData[1];

                        // 교환방에 사용자 추가
                        success = RoomManager.addUserToExchangeRoom(exchangeRoomId, enteringUser);
                        if (success) {
                            ExchangeRoom exchangeRoom = RoomManager.getInstance().getExchangeRoom(String.valueOf(exchangeRoomId));
                            if (exchangeRoom != null) {
                                // 모든 유저 정보와 함께 브로드캐스트
                                StringBuilder userList = new StringBuilder();
                                for (String user : exchangeRoom.getUserList()) {
                                    userList.append(user).append(",");
                                }
                                // 마지막 쉼표 제거
                                if (userList.length() > 0) {
                                    userList.setLength(userList.length() - 1);
                                }

                                ChatMsg updateMsg = new ChatMsg(
                                        "Server",
                                        ChatMsg.MODE_ENTER_EXCHANGEROOM,
                                        exchangeRoomId + "|" + userList.toString() + "|" + GameUser.getInstance().getItemList()
                                );
                                broadcasting(updateMsg);
                            }
                        } else {
//                            ChatMsg errorMsg = new ChatMsg("Server", ChatMsg.MODE_ERROR, "방이 가득 찼습니다.");
//                            out.writeObject(errorMsg);
                        }
                        break;
                    // 서버가 수신한 이미지 메시지를 다른 클라이언트에게 브로드캐스트
                    case ChatMsg.MODE_TX_IMAGE:
                        // 이미지 데이터 수신
                        t_display.append("이미지 수신: " + msg.getMessage() + "\n");

                        // SelectedItem|filename 형식에서 파일명 추출
                        String[] imageInfo = msg.getMessage().split("\\|");
                        if (imageInfo.length == 2 && imageInfo[0].equals("SelectedItem")) {
                            // 해당 유저가 있는 교환방 찾기
                            for (ExchangeRoom room2 : RoomManager.getInstance().getAllExchangeRooms()) {
                                if (room2.getUserList().contains(msg.getUserId())) {
                                    // 선택한 아이템 저장
                                    room2.setSelectedItem(msg.getUserId(), imageInfo[1]);
                                    break;
                                }
                            }
                        }

                        // 모든 클라이언트에 이미지 브로드캐스트
                        synchronized (clientStreams) {
                            for (ObjectOutputStream clientOut : clientStreams.values()) {
                                try {
                                    clientOut.writeObject(new ChatMsg(msg.getUserId(), ChatMsg.MODE_TX_IMAGE,
                                            msg.getMessage(), msg.getImage()));
                                    clientOut.flush();
                                } catch (IOException e) {
                                    t_display.append("이미지 브로드캐스트 오류: " + e.getMessage() + "\n");
                                }
                            }
                        }
                        break;
                    case ChatMsg.MODE_EXCHANGEITEM:
                        String[] exchangeData = msg.getMessage().split("\\|");
                        String sender = exchangeData[0];
                        String senderItem = exchangeData[1];  // sender의 아이템

                        // 해당 교환방의 다른 사용자를 찾아서 아이템 전송
                        ExchangeRoom exchangeRoom = null;
                        for (ExchangeRoom room2 : RoomManager.getInstance().getAllExchangeRooms()) {
                            if (room2.getUserList().contains(sender)) {
                                exchangeRoom = room2;
                                break;
                            }
                        }

                        if (exchangeRoom != null) {
                            // 상대방 찾기
                            String receiver = exchangeRoom.getUserList().stream()
                                    .filter(user -> !user.equals(sender))
                                    .findFirst()
                                    .orElse(null);

                            if (receiver != null) {
                                // receiver의 선택한 아이템 가져오기
                                String receiverItem = exchangeRoom.getSelectedItem(receiver);

                                // 양방향 교환 정보를 포함한 메시지 생성
                                // 형식: sender|receiver|senderItem|receiverItem
                                String exchangeResult = sender + "|" + receiver + "|" + senderItem + "|" + receiverItem;
                                ChatMsg exchangeMsg = new ChatMsg("Server", ChatMsg.MODE_EXCHANGEITEM, exchangeResult);
                                broadcasting(exchangeMsg);

                                t_display.append("아이템 교환 완료: " + sender + "의 " + senderItem + "와 " +
                                        receiver + "의 " + receiverItem + " 교환됨\n");
                            }
                        }
                        break;

//                    case ChatMsg.MODE_ENTER_EXCHANGEROOM:
//                        String[] enterExchangeRoomData = msg.getMessage().split("\\|");
//                        int enterExchangeRoomId = Integer.parseInt(enterExchangeRoomData[0]);
//                        enteringUser = enterExchangeRoomData[1];
//
//                        // 방에 유저 추가 시도
//                        success = RoomManager.addUserToRoom(enterExchangeRoomId, enteringUser);
//
//                        if(success) {
//                            // 서버 로그
//                            t_display.append(enteringUser + "님이 " + enterExchangeRoomId + "번 방에 입장했습니다.\n");
//
//                            // 해당 방의 모든 유저 정보 가져오기
//                            ExchangeRoom exchangeRoom = RoomManager.getInstance().getExchangeRoom(String.valueOf(enterExchangeRoomId));
//                            if(exchangeRoom != null) {
//                                // 약간의 딜레이를 주어 두 번째 클라이언트의 WaitingRoom이 생성될 시간을 줌
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                // 방의 모든 유저들에 대해 메시지 전송
//                                List<String> users = exchangeRoom.getUserList();
//                                for(String user : users) {
//                                    ChatMsg enterRoomMsg = new ChatMsg("Server", ChatMsg.MODE_ENTER_EXCHANGEROOM,
//                                            enterExchangeRoomId + "|" + user);
//                                    broadcasting(enterRoomMsg);
//                                }
//                            }
//                        }
//                        break;

                    case ChatMsg.MODE_TX_ROOMCHAT:
                        String[] roomChatData = msg.getMessage().split("\\|");
                        int roomChatId = Integer.parseInt(roomChatData[0]);
                        String chatMessage = msg.getMessage().split("\\|", 2)[1];

                        // 서버에서 해당 방의 모든 유저에게 한 번만 브로드캐스트
                        room = RoomManager.getInstance().getGameRoom(String.valueOf(roomChatId));
                        if (room != null) {
                            // 방 전체에 대해 한 번만 브로드캐스트
                            ChatMsg roomChatMsg = new ChatMsg(userName, ChatMsg.MODE_TX_ROOMCHAT,
                                    roomChatId + "|" + chatMessage);
                            broadcasting(roomChatMsg);

                            // 서버 로그에 출력
                            printMessage("[방 " + roomChatId + "] " + chatMessage);
                        }
                        break;
                    case ChatMsg.MODE_GAME_START:
                        // 게임 시작 메시지를 모든 클라이언트에게 브로드캐스트
                        t_display.append("게임 시작: " + msg.getMessage() + "\n");
                        broadcasting(msg);
                        break;
                    case ChatMsg.MODE_BUYITEM:
                        String[] itemData = msg.getMessage().split("\\|");
                        String itemName2 = itemData[0];
                        int quantity = Integer.parseInt(itemData[1]);
                        int totalCost = Integer.parseInt(itemData[2]);
                        // 사용자 데이터 업데이트 (예: 데이터베이스 또는 메모리)
                        //GameUser user = GameUser.getInstance();
                        if (userName != null) {
                            //user.addItem(itemName, quantity);
                            t_display.append(userName + "님이 " + itemName2 + " " + quantity + "개를 구매했습니다.\n");
                            // 클라이언트에 응답 메시지 전송
                            ChatMsg responseMsg = new ChatMsg(
                                    "Server",
                                    ChatMsg.MODE_BUYITEM,
                                    itemName2 + "|" + quantity + "|" + totalCost
                            );
                            out.writeObject(responseMsg);
                        }
                        break;
                    case ChatMsg.MODE_SELLITEM:
                        itemData = msg.getMessage().split("\\|");
                        String itemName = itemData[0];
                        quantity = Integer.parseInt(itemData[1]);
                        totalCost = Integer.parseInt(itemData[2]);
                        // 사용자 데이터 업데이트 (예: 데이터베이스 또는 메모리)
                        if (userName != null) {
                            //user.addItem(itemName, quantity);
                            t_display.append(userName + "님이 " + itemName + " " + quantity + "개를 판매했습니다.\n");
                            // 클라이언트에 응답 메시지 전송
                            ChatMsg responseMsg = new ChatMsg(
                                    "Server",
                                    ChatMsg.MODE_SELLITEM,
                                    itemName + "|" + quantity + "|" + totalCost
                            );
                            out.writeObject(responseMsg);
                        }
                        break;

                    case ChatMsg.MODE_TX_GAME:
                        // 게임 상태 업데이트를 모든 클라이언트에게 브로드캐스트
                        broadcasting(msg);
                        break;

                    case ChatMsg.MODE_GAME_ACTION:
                    case ChatMsg.MODE_BUBBLE_POP:
                    case ChatMsg.MODE_GAME_SYNC:
                    case ChatMsg.MODE_GAME_OVER:
                        broadcasting(msg);
                        break;

                    case ChatMsg.MODE_GAME_SCORE:
                        // 게임 점수 업데이트를 모든 클라이언트에게 브로드캐스트
                        t_display.append("게임 점수 업데이트: " + userName + " - " + msg.getMessage() + "\n");
                        broadcasting(msg);
                        break;

                    default:
                        t_display.append("알 수 없는 메시지 모드: " + msg.getMode() + "\n");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            t_display.append("클라이언트 통신 오류 > " + e.getMessage() + "\n");
        } finally {
            // 클라이언트 연결 해제 처리
            synchronized (connectedUsers) {
                if (clientSocket != null && clientSocket.isConnected()) {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        t_display.append("소켓 종료 오류 > " + e.getMessage() + "\n");
                    }
                }
            }
        }
    }

    public class ClientHandler extends Thread {
        private Socket socket;
        private ServerMain server;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String userId;

        public ClientHandler(Socket socket, ServerMain server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                // GameUser 객체 수신
                GameUser gameUser = (GameUser) in.readObject();
                cmsg = (ChatMsg) in.readObject();
                String userName = gameUser.getId();

                while (true) {
                    ChatMsg msg = (ChatMsg) in.readObject();

                    switch (msg.getMode()) {
                        case ChatMsg.MODE_LOGIN:
                            userId = msg.getUserId();
                            server.printMessage(userId + "님이 로그인했습니다.");
                            server.broadcasting(new ChatMsg("Server", ChatMsg.MODE_LOGIN, userId + "님이 입장했습니다."));
                            break;

                        case ChatMsg.MODE_LOGOUT:
                            server.printMessage(userId + "님이 로그아웃했습니다.");
                            server.removeClient(this);
                            return;

                        case ChatMsg.MODE_TX_STRING:
                            server.printMessage(userId + ": " + msg.getMessage());
                            server.broadcasting(msg);
                            break;


                        default:
                            server.printMessage("알 수 없는 메시지 모드: " + msg.getMode());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                server.printMessage("클라이언트 통신 오류: " + e.getMessage());
            } finally {
                server.removeClient(this);
                closeConnection();
            }
        }

        public void sendMessage(ChatMsg msg) {
            try {
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                //server.printDisplay("메시지 전송 오류: " + e.getMessage());
                t_display.append("메시지 전송 오류: " + e.getMessage());
            }
        }

        public void closeConnection() {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                //server.printDisplay("연결 종료 오류: " + e.getMessage());
                t_display.append("연결 종료 오류: " + e.getMessage());
            }
        }
    }

    public void printMessage(String msg) {
        SwingUtilities.invokeLater(() -> t_display.append(msg + "\n"));
    }

    public static void main(String[] args) {
        int port = 12345;
        ServerMain server = new ServerMain(port);
        server.startServer();
    }
}
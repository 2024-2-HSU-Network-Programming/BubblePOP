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
                    case ChatMsg.MODE_TX_CREATEROOM:
                        String[] roomData = msg.getMessage().split("\\|");
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
                    case ChatMsg.MODE_LEAVE_ROOM:
                        String[] leaveRoomData = msg.getMessage().split("\\|");
                        int roomId = Integer.parseInt(leaveRoomData[0]);
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
                            GameRoom room = RoomManager.getInstance().getGameRoom(String.valueOf(enterRoomId));
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

                    case ChatMsg.MODE_TX_ROOMCHAT:
                        String[] roomChatData = msg.getMessage().split("\\|");
                        int roomChatId = Integer.parseInt(roomChatData[0]);
                        String chatMessage = msg.getMessage().split("\\|", 2)[1];

                        // 서버에서 해당 방의 모든 유저에게 한 번만 브로드캐스트
                        GameRoom room = RoomManager.getInstance().getGameRoom(String.valueOf(roomChatId));
                        if (room != null) {
                            // 방 전체에 대해 한 번만 브로드캐스트
                            ChatMsg roomChatMsg = new ChatMsg(userName, ChatMsg.MODE_TX_ROOMCHAT,
                                    roomChatId + "|" + chatMessage);
                            broadcasting(roomChatMsg);

                            // 서버 로그에 출력
                            printMessage("[방 " + roomChatId + "] " + chatMessage);
                        }

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
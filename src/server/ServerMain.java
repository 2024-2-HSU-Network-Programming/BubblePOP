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
import java.util.HashSet;
import java.util.Set;

public class ServerMain extends JFrame {
    private int port;
    private JTextArea t_display;
    private Set<String> connectedUsers; // 접속 중인 사용자 목록
    private ChatMsg cmsg;

    public ServerMain(int port) {
        super("BubblePOP Server");
        this.port = port;
        connectedUsers = new HashSet<>();

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
                Socket clientSocket = serverSocket.accept(); // 클라이언트의 연결 요청을 수락함
                t_display.append("클라이언트가 연결되었습니다.\n");
                //receiveMessages(clientSocket); // 연결되었으니 clientSocket에서 전달한 문자열을 받아야되겠지?

                // 클라이언트와 통신 처리
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch(IOException e) {
            System.err.println("서버 오류 > " + e.getMessage());
            t_display.append("서버 오류: " + e.getMessage() + "\n");

        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            // GameUser 객체 수신
            GameUser gameUser = (GameUser) in.readObject();
            cmsg = (ChatMsg) in.readObject();
            String userName = gameUser.getId();

            // 중복 사용자 검사
            synchronized (connectedUsers) {
                if (connectedUsers.contains(userName)) {
                    t_display.append("중복된 사용자: " + userName);
                    t_display.append("접속 거부: 이미 접속 중입니다.");
                    clientSocket.close(); // 연결 종료
                    return;
                } else {
                    connectedUsers.add(userName); // 사용자 추가
                    t_display.append("새 사용자 접속: " + userName);
                    ChatMsg welcomeMsg = new ChatMsg(userName, ChatMsg.MODE_LOGIN, userName + "님 환영합니다!");
                    out.writeObject(welcomeMsg);
                    out.flush();
                }
            }

            // 클라이언트와 계속 통신
            while (true) {
                String message = (String) in.readObject(); // 메시지 수신 (예제)
                t_display.append(userName + " 메시지: " + message);
                if (message.equalsIgnoreCase("exit" + "\n")) {
                    break; // 클라이언트가 종료 명령을 보냄
                }
                if(cmsg.getMode() == ChatMsg.MODE_LOGIN) {
                    int mode = cmsg.getMode();
                    t_display.append(Integer.toString(mode));
                    ChatMsg sendcmsg = new ChatMsg("Server", mode);
                    out.writeObject(sendcmsg);
                    out.flush();
                }

            }

            // 접속 종료 처리
            synchronized (connectedUsers) {
                connectedUsers.remove(userName);
                t_display.append("사용자 접속 종료: " + userName + "\n");
            }

        } catch (IOException | ClassNotFoundException e) {
            t_display.append("클라이언트 통신 오류 > " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        int port = 12345;
        ServerMain server = new ServerMain(port);
        server.startServer();
    }
}

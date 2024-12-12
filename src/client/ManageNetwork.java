package client;

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
                            //lobbyFrame = new LobbyFrame(this, cm.getUserId());
                            //lobbyFrame.addGlobalChatMessage(cm.getUserId() + "님이 로그인했습니다!");
                            break;
                        case ChatMsg.MODE_LOGOUT:
                            System.out.println("서버에서 연결 종료 메시지 수신: " + cm.getMessage());
                            closeConnection();
                            return; // 쓰레드 종료
                        default:
                            System.out.println("알 수 없는 모드: " + cm.getMode());
                    }
                }
            } else {
                System.out.println("받은 객체가 없음");
            }


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

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

    public ManageNetwork(ObjectInputStream in, ObjectOutputStream out, Socket socket) {
        this.in = in;
        this.out = out;
        this.socket = socket;
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
                            lobbyFrame = new LobbyFrame();
                            lobbyFrame.setVisible(true);
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


}

package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {
    private static List<GameRoom> roomList;
    private static AtomicInteger atomicInteger;
    static {
        roomList = new ArrayList<GameRoom>();
        atomicInteger = new AtomicInteger();
    }
    public RoomManager() {}

    public static GameRoom createRoom(String owner, String roomName, String password) {
        int roomId = atomicInteger.incrementAndGet();

        GameRoom room = new GameRoom(roomId, owner, roomName, password);

        roomList.add(room);
        System.out.println("Room Created : "+roomId);
        return room;
    }

    public static int whereInUser(String userName) {
        for(int i=0; i<roomList.size(); i++) {
            if(roomList.get(i).isUser(userName))
                return i+1; // �� ��ȣ�� 1���� ����.
        }
        return -1;
    }

    public static GameRoom getGameRoom(String roomIdStr) {
        int roomId = (Integer.parseInt(roomIdStr))-1; // �� ��ȣ�� 1���� ����, �ε����� 0���� ����
        return roomList.get(roomId);
    }

    public static int getRoomListSize() {
        return roomList.size();
    }
}

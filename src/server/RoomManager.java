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

        // 기본 방 추가
        createRoom("admin", "초보자 방", ""); // 방 생성자: owner, roomName, password
        createRoom("admin", "고수 방", "master");
        createRoom("admin", "같이 게임해요", "");

    }
    public RoomManager() {}

    public static GameRoom createRoom(String owner, String roomName, String password) {
        int roomId = atomicInteger.incrementAndGet();

        GameRoom room = new GameRoom(roomId, owner, roomName, password);

        roomList.add(room);
        System.out.println("Room Created : "+roomId);
        System.out.println("Room count: " + getRoomListSize());
        return room;
    }

    public static boolean addUserToRoom(int roomId, String userName) {
        for (GameRoom room : roomList) {
            if (room.getRoomId() == roomId) {
                System.out.println("Checking room: " + room.getRoomName());
                System.out.println("Current users: " + room.getUserListSize() + ", Full: " + room.isFullRoom());

                if (!room.isFullRoom()) {
                    room.enterUser(userName);
                    System.out.println("User added: " + userName + " to room " + room.getRoomName());
                    return true; // 사용자 추가 성공
                } else {
                    System.out.println("Room is full: " + room.getRoomName());
                    return false; // 방이 가득 참
                }
            }
        }
        System.out.println("Room not found: " + roomId);
        return false; // 방을 찾지 못함
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
    public static List<GameRoom> getRoomList() {
        return new ArrayList<>(roomList); // GameRoom 타입으로 반환
    }

    public static int getRoomListSize() {
        return roomList.size();
    }
}

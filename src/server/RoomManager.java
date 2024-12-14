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
        createRoom("admin", "초보자 방", "basic"); // 방 생성자: owner, roomName, password
        createRoom("admin", "고수 방", "master");
        createRoom("admin", "같이 게임해요", "with");

    }
    public RoomManager() {}

    public static GameRoom createRoom(String owner, String roomName, String password) {
        int roomId = atomicInteger.incrementAndGet();

        GameRoom room = new GameRoom(roomId, owner, roomName, password);

        roomList.add(room);

        // 디버깅용 로깅 추가
        System.out.println("RoomManager - 방 생성: ID=" + roomId +
                ", Owner=" + owner +
                ", Name=" + roomName);
        System.out.println("현재 총 방 개수: " + roomList.size());

        return room;
    }

    //방 떠날때
    public static void leaveRoom(int roomId, String userName) {
        for (GameRoom room : roomList) {
            if (room.getRoomId() == roomId) {
                room.removeUser(userName);

                // 방에 아무도 없으면 방 삭제
                if (room.getUserListSize() == 0) {
                    roomList.remove(room);
                    System.out.println("방 삭제: ID=" + roomId + ", 남은 방 개수: " + roomList.size());
                }
                break;
            }
        }
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

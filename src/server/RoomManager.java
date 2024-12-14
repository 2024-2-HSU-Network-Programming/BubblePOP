package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {
    private static List<GameRoom> roomList;
    private static AtomicInteger atomicInteger;

    static {
        roomList = new ArrayList<>();
        atomicInteger = new AtomicInteger();

        // 기본 방 추가
        createRoom("admin", "초보자 방", ""); // 방 생성자: owner, roomName, password
        createRoom("admin", "고수 방", "master");
        createRoom("admin", "같이 게임해요", "");
    }

    public RoomManager() {
        // 기본 생성자
    }

    // 방 생성 메서드
    public static GameRoom createRoom(String owner, String roomName, String password) {
        int roomId = atomicInteger.incrementAndGet();

        GameRoom room = new GameRoom(roomId, owner, roomName, password);
        roomList.add(room);

        // 디버깅용 출력
        System.out.println("Room Created : " + roomId);
        System.out.println("Room count: " + getRoomListSize());
        return room;
    }

    // 방에 사용자 추가
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

    // 방에서 사용자 제거
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

    // 특정 방 ID로 방 정보 가져오기
    public static GameRoom getGameRoom(String roomIdStr) {
        int roomId = Integer.parseInt(roomIdStr);
        for (GameRoom room : roomList) {
            if (room.getRoomId() == roomId) {
                return room;
            }
        }
        return null; // 방을 찾지 못한 경우
    }

    // 방 리스트 반환
    public static List<GameRoom> getRoomList() {
        return new ArrayList<>(roomList); // 방 리스트의 복사본 반환
    }

    // 현재 방 개수 반환
    public static int getRoomListSize() {
        return roomList.size();
    }
}

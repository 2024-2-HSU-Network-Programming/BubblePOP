package server;

import client.WaitingRoom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {
    //private static List<GameRoom> roomList;
    private static RoomManager instance = new RoomManager();
    private static Map<String, GameRoom> rooms = new HashMap<>();
    private static Map<String, ExchangeRoom> exchangeRooms = new HashMap<>();
    private static AtomicInteger atomicInteger;
    private static int roomIdCounter = 1;

//    static {
//        roomList = new ArrayList<>();
//        atomicInteger = new AtomicInteger();
//
//        // 기본 방 추가
//        createRoom("admin", "초보자 방", ""); // 방 생성자: owner, roomName, password
//        createRoom("admin", "고수 방", "master");
//        createRoom("admin", "같이 게임해요", "");
//    }


    public RoomManager() {
        // 기본 생성자
    }

//    // 방 생성 메서드
//    public static GameRoom createRoom(String owner, String roomName, String password) {
//        int roomId = atomicInteger.incrementAndGet();
//
//        GameRoom room = new GameRoom(roomId, owner, roomName, password);
//        roomList.add(room);
//
//        // 디버깅용 출력
//        System.out.println("Room Created : " + roomId);
//        System.out.println("Room count: " + getRoomListSize());
//        return room;
//    }
    public static RoomManager getInstance() {
    return instance;
}
    // 방 생성 메서드
    public synchronized GameRoom createRoom(String ownerName, String roomName, String password) {
        int roomId = roomIdCounter++;
        GameRoom newRoom = new GameRoom(roomId, ownerName, roomName, password);
        rooms.put(Integer.toString(roomId), newRoom);
        return newRoom;
    }
    // 교환방 생성 메서드
    public synchronized ExchangeRoom createExchangeRoom(String ownerName, String roomName, String password) {
        int roomId = roomIdCounter++;
        ExchangeRoom newRoom = new ExchangeRoom(roomId, ownerName, roomName, password);
        exchangeRooms.put(Integer.toString(roomId), newRoom);
        return newRoom;
    }


//    // 방에 사용자 추가
//    public static boolean addUserToRoom(int roomId, String userName) {
//        for (GameRoom room : roomList) {
//            if (room.getRoomId() == roomId) {
//                System.out.println("Checking room: " + room.getRoomName());
//                System.out.println("Current users: " + room.getUserListSize() + ", Full: " + room.isFullRoom());
//
//                if (!room.isFullRoom()) {
//                    room.enterUser(userName);
//                    System.out.println("User added: " + userName + " to room " + room.getRoomName());
//                    return true; // 사용자 추가 성공
//                } else {
//                    System.out.println("Room is full: " + room.getRoomName());
//                    return false; // 방이 가득 참
//                }
//            }
//        }
//        System.out.println("Room not found: " + roomId);
//        return false; // 방을 찾지 못함
//    }
    // 방에 사용자 추가
    public static boolean addUserToRoom(int roomId, String userName) {
        GameRoom room = rooms.get(Integer.toString(roomId));
        if (room != null) {
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
        System.out.println("Room not found: " + roomId);
        return false; // 방을 찾지 못함
    }
//    public static boolean addUserToExchangeRoom(int roomId, String userName) {
//        ExchangeRoom room = exchangeRooms.get(Integer.toString(roomId));
//        if (room != null) {
//            if (!room.isFullRoom()) {
//                room.enterUser(userName);
//                System.out.println("User added to exchange room: " + userName);
//                System.out.println("Current users: " + room.getUserList());
//                return true;
//            } else {
//                System.out.println("Exchange room is full.");
//                return false;
//            }
//        }
//        System.out.println("Exchange room not found: " + roomId);
//        return false;
//    }
public static boolean addUserToExchangeRoom(int roomId, String userName) {
    ExchangeRoom room = exchangeRooms.get(Integer.toString(roomId));
    if (room != null) {
        System.out.println("Checking ExchangeRoom: " + room.getRoomName());
        System.out.println("Current users: " + room.getUserListSize() + ", Full: " + room.isFullRoom());

        if (!room.isFullRoom()) {
            room.enterUser(userName);
            System.out.println("User added: " + userName + " to room " + room.getRoomName());
            return true; // 사용자 추가 성공
        } else {
            System.out.println("ExchangeRoom is full: " + room.getRoomName());
            return false; // 방이 가득 참
        }
    }
    System.out.println("ExchangeRoom not found: " + roomId);
    return false; // 방을 찾지 못함
}

    // 방에서 사용자 제거
    public static void leaveRoom(int roomId, String userName) {
        GameRoom room = rooms.get(Integer.toString(roomId));
        if (room != null) {
            room.removeUser(userName);

            // 방에 아무도 없으면 방 삭제
            if (room.getUserListSize() == 0) {
                rooms.remove(Integer.toString(roomId));
                System.out.println("방 삭제: ID=" + roomId + ", 남은 방 개수: " + rooms.size());
            }
        }
    }
    // 교환방에서 사용자 제거
    public static void removeUserFromExchangeRoom(int roomId, String userName) {
        ExchangeRoom room = exchangeRooms.get(Integer.toString(roomId));
        if (room != null) {
            room.removeUser(userName);
            if (room.getUserListSize() == 0) {
                exchangeRooms.remove(Integer.toString(roomId));
            }
        }
    }

//    // 특정 방 ID로 방 정보 가져오기
//    public static GameRoom getGameRoom(String roomIdStr) {
//        int roomId = Integer.parseInt(roomIdStr);
//        for (GameRoom room : roomList) {
//            if (room.getRoomId() == roomId) {
//                return room;
//            }
//        }
//        return null; // 방을 찾지 못한 경우
//    }
// 모든 방 반환 메서드
public synchronized List<GameRoom> getAllRooms() {
    System.out.println("getAllRooms 호출됨. 현재 방 개수: " + rooms.size());
    return new ArrayList<>(rooms.values());
}
    // 모든 교환방 반환
    public synchronized List<ExchangeRoom> getAllExchangeRooms() {
        System.out.println("getAllExchangeRooms 호출됨. 현재 방 개수: " + rooms.size());
        return new ArrayList<>(exchangeRooms.values());
    }
    // 특정 교환방 검색
    public static ExchangeRoom getExchangeRoom(String roomId) {
        return exchangeRooms.get(roomId);
    }

    // 방 검색 메서드
    public static GameRoom getGameRoom(String roomId) {
        return rooms.get(roomId);
    }


    //    // 방 리스트 반환
    //    public static List<GameRoom> getRoomList() {
    //        return new ArrayList<>(roomList); // 방 리스트의 복사본 반환
    //    }

    // 방 목록을 리스트로 반환
    public static List<GameRoom> getRoomList() {
        return new ArrayList<>(rooms.values());
    }


    // 방 목록 크기 반환
    public static int getRoomListSize() {
        return rooms.size();
    }
}

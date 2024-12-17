package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRoom {
    private int roomId; // Room Id
    private List<String> userList;
    private String roomOwner;
    private String roomName;
    private String roomPassword;
    private Boolean isFull;
    private Boolean isPlaying;

    private Map<String, String> selectedItems; // 유저별 선택한 아이템
    private Map<String, Boolean> readyStatus; // 유저별 준비 상태

    public ExchangeRoom(int roomId, String roomOwner, String roomName,String roomPassword) {
        this.roomId = roomId;
        this.roomOwner = roomOwner;
        this.roomName = roomName;
        this.isFull = false;
        this.isPlaying = false;
        this.roomPassword = roomPassword;

        userList = new ArrayList();
        this.selectedItems = new HashMap<>();
        this.readyStatus = new HashMap<>();

        userList.add(roomOwner);
        System.out.println("룸 생성 id: "+this.roomId+" owner: "+this.roomOwner+" name: "+this.roomName);
    }

    public Boolean isUser(String userName) {
        if(userList.contains(userName))
            return true;
        else
            return false;
    }

    // 방이 가득 찼는지 확인
    public Boolean isFullRoom() {
        return isFull;
    }

    public void enterUser(String user) {
        userList.add(user);
        if(userList.size() >= 2)
            isFull = true;
        else
            isFull = false;
    }

    // 아이템 선택 메소드
    public void setSelectedItem(String userId, String itemName) {
        selectedItems.put(userId, itemName);
    }

    // 준비 상태 설정 메소드
    public void setReady(String userId, boolean ready) {
        readyStatus.put(userId, ready);
    }

    // 교환 가능 상태 확인
    public boolean canExchange() {
        if (userList.size() != 2) return false;

        // 모든 유저가 아이템을 선택하고 준비 상태인지 확인
        for (String user : userList) {
            if (!selectedItems.containsKey(user) ||
                    !readyStatus.containsKey(user) ||
                    !readyStatus.get(user)) {
                return false;
            }
        }
        return true;
    }

    // 아이템 교환 실행
    public Map<String, String> executeExchange() {
        if (!canExchange()) return null;

        Map<String, String> exchangeResult = new HashMap<>();
        String user1 = userList.get(0);
        String user2 = userList.get(1);

        // 각 유저가 받을 아이템 매핑
        exchangeResult.put(user1, selectedItems.get(user2));
        exchangeResult.put(user2, selectedItems.get(user1));

        // 교환 후 초기화
        selectedItems.clear();
        readyStatus.clear();

        return exchangeResult;
    }

    public String getSelectedItem(String userId) {
        return selectedItems.get(userId);
    }

    public boolean isReady(String userId) {
        return readyStatus.getOrDefault(userId, false);
    }

    public boolean checkPassword(String password) {
        return this.roomPassword.equals(password);
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public int getUserListSize() {
        return userList.size();
    }


    public String getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(String roomOwner) {
        this.roomOwner = roomOwner;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    public Boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(Boolean isPlaying) {
        this.isPlaying = isPlaying;
    }


    public String getRoomPassword() { return roomPassword; }

    public void removeUser(String userName) {
        userList.remove(userName);
        // 방장이 나가면 다음 유저를 방장으로 지정 (옵션)
        if (roomOwner.equals(userName) && !userList.isEmpty()) {
            roomOwner = userList.get(0);
        }

        // 사용자 제거 후 방 인원 상태 업데이트
        isFull = userList.size() >= 4;

        System.out.println("사용자 나감: " + userName + ", 남은 인원: " + userList.size());
    }

}

package server;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private int roomId; // Room Id
    private List<String> userList;
    private String roomOwner;
    private String roomName;
    private String roomPassword;
    private Boolean isFull;
    private Boolean isPlaying;

    public GameRoom(int roomId, String roomOwner, String roomName,String roomPassword) {
        this.roomId = roomId;
        this.roomOwner = roomOwner;
        this.roomName = roomName;
        this.isFull = false;
        this.isPlaying = false;
        this.roomPassword = roomPassword;

        userList = new ArrayList();
        userList.add(roomOwner);
        System.out.println("룸 생성 id: "+this.roomId+" owner: "+this.roomOwner+" name: "+this.roomName);
    }

    public Boolean isUser(String userName) {
        if(userList.contains(userName))
            return true;
        else
            return false;
    }

    public Boolean isFullRoom() {
        if(isFull)
            return true;
        else
            return false;
    }

    public void enterUser(String user) {
        userList.add(user);
        if(userList.size() >= 4)
            isFull = true;
        else
            isFull = false;
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

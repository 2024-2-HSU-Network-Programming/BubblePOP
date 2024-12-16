package server;
import java.util.ArrayList;
import java.util.List;
public class ExchangeRoom {
    private int roomId;
    private String owner;
    private String roomName;
    private String password;
    private List<String> userList;
    private static final int MAX_USERS = 2; // 교환방은 최대 2명만 입장 가능
    public ExchangeRoom(int roomId, String owner, String roomName, String password) {
        this.roomId = roomId;
        this.owner = owner;
        this.roomName = roomName;
        this.password = password;
        this.userList = new ArrayList<>();
    }
    public int getRoomId() {
        return roomId;
    }
    public String getOwner() {
        return owner;
    }
    public String getRoomName() {
        return roomName;
    }
    public String getPassword() {
        return password;
    }
    public List<String> getUserList() {
        return new ArrayList<>(userList); // 사용자 리스트 복사본 반환
    }
    public int getUserListSize() {
        return userList.size();
    }
    public boolean isFullRoom() {
        return userList.size() >= MAX_USERS;
    }
    public boolean enterUser(String userName) {
        if (!isFullRoom()) {
            userList.add(userName);
            return true;
        }
        return false;
    }
    public void removeUser(String userName) {
        userList.remove(userName);
    }
}
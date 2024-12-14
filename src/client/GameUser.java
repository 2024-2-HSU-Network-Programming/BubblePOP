package client;

import java.io.Serializable;

public class GameUser  {
    private static final long serialVersionUID = 1L;
    private ManageNetwork net;
    private String id;
    private String password;

    private GameUser() {}

    public void init(String id, String password, ManageNetwork net) {
        this.id=id;
        this.password = password;
        this.net=net;
    }


//    public GameUser(String id, String password) {
//        this.id = id;
//        this.password = password;
//    }

    // holder에 의한 초기화
    private static class LazyHolder{
        public static final GameUser uniqueInstance = new GameUser();
    }
    public static GameUser getInstance() {
        return LazyHolder.uniqueInstance;
    }
    public ManageNetwork getNet() {
        return net;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "GameUser{" +
                "userName='" + id + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
package client;

import java.io.Serializable;

public class GameUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String password;

    public GameUser(String id, String password) {
        this.id = id;
        this.password = password;
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

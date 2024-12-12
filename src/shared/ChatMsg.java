package shared;

import java.io.Serializable;

public class ChatMsg implements Serializable {
    public final static int MODE_LOGIN = 100;
    public final static int MODE_LOGOUT = 200;
    public final static int MODE_TX_STRING = 0x10;
    public final static int MODE_TX_FILE = 300;
    public final static int MODE_TX_IMAGE = 301;
    public final static int MODE_TX_ITEMCHANGE =302;
    public final static int MODE_TX_ALLCHAT=303;
    public final static int MODE_TX_ROOMCHAT=304;
    public final static int MODE_TX_GAME=305;
    public final static int MODE_TX_CREATEROOM=400;


    private String userId;
    private int mode;
    private String message;

    public ChatMsg(String userId, int mode, String message) {
        this.userId = userId;
        this.mode = mode;
        this.message = message;
    }

    public ChatMsg(String userId, int code) {
        this(userId, code, null);
    }

    public String getUserId() { return userId; }
    public int getMode() { return mode; }
    public String getMessage() { return message; }

}

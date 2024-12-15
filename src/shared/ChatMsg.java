package shared;

import java.io.Serializable;

public class ChatMsg implements Serializable {
    public final static int MODE_LOGIN = 100;
    public final static int MODE_LOGOUT = 200;
    public final static int MODE_TX_STRING = 201;
    public final static int MODE_TX_FILE = 300;
    public final static int MODE_TX_IMAGE = 301;
    public final static int MODE_TX_ITEMCHANGE =302;
    public final static int MODE_TX_ALLCHAT=303;
    public final static int MODE_TX_ROOMCHAT=304;
   // public final static int MODE_TX_GAME=305;
    public final static int MODE_TX_CREATEROOM=400;
    public final static int MODE_LEAVE_ROOM=410;
    public static final int MODE_ENTER_ROOM = 451;

    public static final int MODE_GAME_START = 500;    // 게임 시작
    public static final int MODE_TX_GAME = 501;       // 게임 상태 전송
    public static final int MODE_GAME_ACTION = 502;   // 게임 액션 (발사, 이동 등)
    public static final int MODE_BUBBLE_POP = 503;    // 버블 터짐
    public static final int MODE_GAME_SYNC = 504;     // 게임 싱크
    public static final int MODE_GAME_OVER = 505;     // 게임 종료



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

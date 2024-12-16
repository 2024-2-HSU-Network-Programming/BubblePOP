package client;

import java.io.Serializable;

public class GameUser {
    private static final long serialVersionUID = 1L;
    private ManageNetwork net;
    private String id;
    private String password;

    private int coin = 3000;
    private int changeBubbleColor = 0;
    private int lineExplosion = 0;
    private int bomb = 0;

    private GameUser() {
    }

    public void init(String id, String password, ManageNetwork net) {
        this.id=id;
        this.password = password;
        this.net=net;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        if (coin >= 0) {
            this.coin = coin;
        } else {
            System.out.println("코인은 음수가 될 수 없습니다.");
        }
    }

    public void addCoin(int amount) {
        if (amount > 0) {
            this.coin += amount;
        } else {
            System.out.println("추가할 코인은 양수여야 합니다.");
        }
    }

    //게임내 아이템 사용
    public void useItem(String itemName) {
        switch (itemName) {
            case "구슬색 변경":
                changeBubbleColor--;
                break;
            case "라인 폭발":
                lineExplosion--;
                break;
            case "폭탄":
                bomb--;
                break;
        }
    }

    public void deductCoin(int amount) {
        if (amount > 0 && this.coin >= amount) {
            this.coin -= amount;
        } else if (amount <= 0) {
            System.out.println("차감할 코인은 양수여야 합니다.");
        } else {
            System.out.println("코인이 부족합니다.");
        }
    }

    // Getters and setters for items
    public int getChangeBubbleColor() {
        return changeBubbleColor;
    }

    public void setChangeBubbleColor(int quantity) {
        this.changeBubbleColor = quantity;
    }

    public int getLineExplosion() {
        return lineExplosion;
    }

    public void setLineExplosion(int quantity) {
        this.lineExplosion = quantity;
    }

    public int getBomb() {
        return bomb;
    }

    public void setBomb(int quantity) {
        this.bomb = quantity;
    }

    // Add items dynamically
    public void addItem(String itemTitle, int quantity, int buyPriceItem) {
        switch (itemTitle) {
            case "구슬색 변경":
                setChangeBubbleColor(getChangeBubbleColor() + quantity);
                deductCoin(buyPriceItem);
                break;
            case "라인 폭발":
                setLineExplosion(getLineExplosion() + quantity);
                deductCoin(buyPriceItem);
                break;
            case "폭탄":
                setBomb(getBomb() + quantity);
                deductCoin(buyPriceItem);
                break;
            default:
                System.out.println("알 수 없는 아이템: " + itemTitle);
                break;
        }
    }

    // Add items dynamically
    public void sellItem(String itemTitle, int quantity, int sellPricePerItem) {
        switch (itemTitle) {
            case "구슬색 변경":
                setChangeBubbleColor(getChangeBubbleColor() - quantity);
                addCoin(sellPricePerItem * quantity);
                break;
            case "라인 폭발":
                setLineExplosion(getLineExplosion() - quantity);
                addCoin(sellPricePerItem * quantity);
                break;
            case "폭탄":
                setBomb(getBomb() - quantity);
                addCoin(sellPricePerItem * quantity);
                break;
            default:
                System.out.println("알 수 없는 아이템: " + itemTitle);
                break;
        }
    }

//    public GameUser(String id, String password) {
//        this.id = id;
//        this.password = password;
//    }

    // holder에 의한 초기화
    private static class LazyHolder {
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
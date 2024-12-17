package client;

import shared.ChatMsg;
import javax.swing.*;
import java.awt.*;

public class ItemStoreFrame extends JFrame {
    private JTabbedPane itemTappedPane, myItemTappedPane;
    private Container c;
    private JPanel itemTapPanel, myItemTapPanel;
    private JPanel itemBoxPanel, myItemBoxPanel;

    private ImageIcon changeBubbleColorIcon = new ImageIcon(getClass().getResource("/client/assets/item/change-bubble.png"));
    private ImageIcon lineExplosionIcon = new ImageIcon(getClass().getResource("/client/assets/item/line-explosion.png"));
    private ImageIcon bombIcon = new ImageIcon(getClass().getResource("/client/assets/item/bomb.png"));
    private ManageNetwork network;
    private LobbyFrame lobbyFrame;
    GameUser user = GameUser.getInstance();

    public ItemStoreFrame() {
        setTitle("Lobby");
        setBounds(100,100, 960, 672);
        setResizable(false); // 크기 고정
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = getContentPane();
        c.setBackground(Color.black);

        buildGUI();
    }
    public ItemStoreFrame(ManageNetwork network, LobbyFrame lobbyFrame) {
        this.network = network;
        this.lobbyFrame = lobbyFrame;

        setTitle("Lobby");
        setBounds(100,100, 960, 672);
        setResizable(false); // 크기 고정
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = getContentPane();
        c.setBackground(Color.black);
        System.out.println("user: " + user.getId());
        buildGUI();
    }
    private void buildGUI() {
        c.setLayout(new BorderLayout());

        // 상단 제목 패널
        JPanel titlePanel = new JPanel(new BorderLayout()); // BorderLayout 사용
        titlePanel.setPreferredSize(new Dimension(960, 50));
        titlePanel.setBackground(Color.WHITE); // 배경 흰색
        JLabel titleLabel = new JLabel("아이템 상점", SwingConstants.CENTER); // 텍스트 중앙 정렬
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // JTabbedPane 생성
        itemTappedPane = new JTabbedPane();
        itemTapPanel = new JPanel();
        itemTapPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // 왼쪽에서 오른쪽으로 정렬
        itemTappedPane.addTab("아이템", itemTapPanel);
        itemTapPanel.add(ItemBoxPanel("구슬색 변경", changeBubbleColorIcon, 100, 80));
        itemTapPanel.add(ItemBoxPanel("라인 폭발", lineExplosionIcon, 200, 100));
        itemTapPanel.add(ItemBoxPanel("폭탄", bombIcon, 250, 120));

        myItemTappedPane = new JTabbedPane();
        myItemTapPanel = new JPanel();
        myItemTapPanel.setLayout(null);
        myItemTappedPane.addTab("내 아이템", myItemTapPanel);
        // Add items to "내 아이템" tab
        refreshMyItems();

        // JSplitPane으로 2:1 비율로 나누기
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(itemTappedPane);
        splitPane.setBottomComponent(myItemTappedPane);
        splitPane.setDividerLocation(600);

        // 프레임에 추가
        c.add(titlePanel, BorderLayout.NORTH);
        c.add(splitPane, BorderLayout.CENTER);


        setVisible(true); // 프레임 보이도록
    }

    // Create a panel for each item in "내 아이템"
    private JPanel createMyItemPanel(String itemName, ImageIcon itemIcon, int quantity) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setPreferredSize(new Dimension(200, 100)); // Panel 크기 설정
        // 아이템 이미지
        Image scaledImage = itemIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // 작은 크기로 축소
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        itemPanel.add(imageLabel, BorderLayout.WEST);
        // 아이템 정보
        JPanel infoPanel = new JPanel(new GridLayout(2, 1)); // 2행 1열 레이아웃
        JLabel nameLabel = new JLabel(itemName, SwingConstants.LEFT);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        JLabel quantityLabel = new JLabel("보유 수량: " + quantity, SwingConstants.LEFT);
        quantityLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        infoPanel.add(nameLabel);
        infoPanel.add(quantityLabel);
        itemPanel.add(infoPanel, BorderLayout.CENTER);
        return itemPanel;
    }
    private void refreshMyItems() {
        myItemTapPanel.removeAll(); // 기존 아이템 삭제
        int yPosition = 10; // 초기 y좌표
        int itemHeight = 100; // 각 아이템 패널의 높이
        int gap = 10; // 아이템 간의 간격
        // 아이템 정보 추가
        JPanel bubblePanel = createMyItemPanel("구슬색 변경", changeBubbleColorIcon, user.getChangeBubbleColor());
        bubblePanel.setBounds(10, yPosition, 400, itemHeight);
        myItemTapPanel.add(bubblePanel);
        yPosition += itemHeight + gap;
        JPanel linePanel = createMyItemPanel("라인 폭발", lineExplosionIcon, user.getLineExplosion());
        linePanel.setBounds(10, yPosition, 400, itemHeight);
        myItemTapPanel.add(linePanel);
        yPosition += itemHeight + gap;
        JPanel bombPanel = createMyItemPanel("폭탄", bombIcon, user.getBomb());
        bombPanel.setBounds(10, yPosition, 400, itemHeight);
        myItemTapPanel.add(bombPanel);
        yPosition += itemHeight + gap;
        // 코인 정보 패널 추가
        JPanel coinPanel = new JPanel();
        coinPanel.setLayout(new BorderLayout());
        coinPanel.setBounds(5, yPosition, 300, 50); // 크기와 위치 설정
        JLabel coinLabel = new JLabel("보유 코인: " + user.getCoin(), SwingConstants.CENTER);
        coinLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        coinPanel.add(coinLabel, BorderLayout.CENTER);
        myItemTapPanel.add(coinPanel);
        // 전체 패널 크기 업데이트
        myItemTapPanel.setPreferredSize(new Dimension(400, yPosition + 60)); // 크기 수정
        myItemTapPanel.revalidate();
        myItemTapPanel.repaint();
        // 디버깅 출력
        System.out.println("보유 코인: " + user.getCoin());
    }
    private JPanel ItemBoxPanel(String itemTitle, ImageIcon itemIcon, int buyCoin, int sellCoin) {
        itemBoxPanel = new JPanel();
        itemBoxPanel.setLayout(new BorderLayout());
        itemBoxPanel.setBackground(Color.white);
        itemBoxPanel.setPreferredSize(new Dimension(200, 250));

        // 상단 아이템 제목
        JLabel titleLabel = new JLabel(itemTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        itemBoxPanel.add(titleLabel, BorderLayout.NORTH);

        // 이미지 크기 조절
        Image scaledImage = itemIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // 150x150 크기로 조절
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // 이미지 라벨
        JLabel imageLabel = new JLabel(scaledIcon);
        itemBoxPanel.add(imageLabel, BorderLayout.CENTER);

        // 가격 정보와 구매/판매 버튼 포함 하단패널
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        // 가격 정보 패널
        JPanel pricePanel = new JPanel(new GridLayout(2, 1)); // 2행 1열
        pricePanel.setBackground(Color.white);
        JLabel buyPriceLabel = new JLabel(String.format("구매시 %d", buyCoin), SwingConstants.CENTER);
        buyPriceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        JLabel sellPriceLabel = new JLabel(String.format("판매시 %d", sellCoin), SwingConstants.CENTER);
        sellPriceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        pricePanel.add(buyPriceLabel);
        pricePanel.add(sellPriceLabel);
        bottomPanel.add(pricePanel, BorderLayout.NORTH);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1행 2열, 간격 10
        buttonPanel.setBackground(Color.white);
        JButton buyButton = new JButton("구매");
        JButton sellButton = new JButton("판매");
        buyButton.addActionListener(e -> {
            String[] options = {"1", "2", "3", "5", "10"};
            String quantityStr = (String) JOptionPane.showInputDialog(
                    null,
                    "구매할 개수를 선택하세요:",
                    "아이템 구매",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    "1"
            );
            if (quantityStr != null) {
                int quantity = Integer.parseInt(quantityStr);
                int totalCost = quantity * buyCoin;
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        String.format("총 %d개의 %s을 구매합니다. 비용은 %d입니다. 계속하시겠습니까?", quantity, itemTitle, totalCost),
                        "구매 확인",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    // 서버에 구매 요청 메시지 전송
                    ChatMsg buyMsg = new ChatMsg(
                            user.getId(),
                            ChatMsg.MODE_BUYITEM,
                            itemTitle + "|" + quantity + "|" + totalCost
                    );
                    user.getNet().sendMessage(buyMsg);
                    // UI 업데이트
//                    SwingUtilities.invokeLater(this::refreshMyItems); // 아이템 탭 새로고침
                    // 로컬 사용자 데이터 업데이트 (서버 응답 후 업데이트하는 것이 이상적)
//                    GameUser.getInstance().addItem(itemTitle, quantity);
                    SwingUtilities.invokeLater(() -> {
                        this.refreshMyItems();
                        if (lobbyFrame != null) {
                            lobbyFrame.updateCoinDisplay();//잔여 코인 업데이트
                            lobbyFrame.updateItemDisplay(); // 아이템 수량 업데이트
                        }
                    }); // 사용한 코인이 로비에도 반영되게
                    JOptionPane.showMessageDialog(null, "구매가 완료되었습니다!", "구매 성공", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        sellButton.addActionListener(e -> {
            // 사용자의 보유 아이템 수량 가져오기
            int userQuantity = 0;
            switch (itemTitle) {
                case "구슬색 변경":
                    userQuantity = user.getChangeBubbleColor();
                    break;
                case "라인 폭발":
                    userQuantity = user.getLineExplosion();
                    break;
                case "폭탄":
                    userQuantity = user.getBomb();
                    break;
            }
            // 보유 수량이 0인 경우 판매 불가 메시지 출력
            if (userQuantity <= 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "보유한 아이템이 없습니다. 판매할 수 없습니다.",
                        "판매 불가",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            // 보유 수량만큼의 옵션 생성
            String[] options = new String[userQuantity];
            for (int i = 0; i < userQuantity; i++) {
                options[i] = String.valueOf(i + 1);
            }
            // 판매 개수 선택
            String quantityStr = (String) JOptionPane.showInputDialog(
                    null,
                    "판매할 개수를 선택하세요:",
                    "아이템 판매",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    "1"
            );
            if (quantityStr != null) {
                int quantity = Integer.parseInt(quantityStr);
                int totalCost = quantity * sellCoin; // 판매 가격 계산
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        String.format("총 %d개의 %s을 판매합니다. 수익은 %d입니다. 계속하시겠습니까?", quantity, itemTitle, totalCost),
                        "판매 확인",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    // 서버에 구매 요청 메시지 전송
                    ChatMsg buyMsg = new ChatMsg(
                            GameUser.getInstance().getId(),
                            ChatMsg.MODE_SELLITEM,
                            itemTitle + "|" + quantity + "|" + totalCost
                    );
                    GameUser.getInstance().getNet().sendMessage(buyMsg);
                    // UI 업데이트
                   // SwingUtilities.invokeLater(this::refreshMyItems); // 아이템 탭 새로고침
                    // 로컬 사용자 데이터 업데이트 (서버 응답 후 업데이트하는 것이 이상적)
//                    GameUser.getInstance().addItem(itemTitle, quantity);
                    SwingUtilities.invokeLater(() -> {
                        this.refreshMyItems();
                        if (lobbyFrame != null) {
                            lobbyFrame.updateCoinDisplay();//잔여 코인 업데이트
                            lobbyFrame.updateItemDisplay(); // 아이템 수량 업데이트
                        }
                    });// 로비에 코인이 반영되게
                    JOptionPane.showMessageDialog(null, "판매가 완료되었습니다!", "판매 성공", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        buttonPanel.add(buyButton);
        buttonPanel.add(sellButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        itemBoxPanel.add(bottomPanel, BorderLayout.SOUTH);

        return itemBoxPanel;
    }

    public static void main(String[] args) {
        new ItemStoreFrame();
    }
}
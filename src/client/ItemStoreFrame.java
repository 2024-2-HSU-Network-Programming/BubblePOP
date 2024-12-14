package client;

import javax.swing.*;
import java.awt.*;

public class ItemStoreFrame extends JFrame {
    private JTabbedPane itemTappedPane, myItemTappedPane;
    private Container c;
    private JPanel itemTapPanel, myItemTapPanel;
    private JPanel itemBoxPanel, myItemBoxPanel;

    private ImageIcon changeBubbleColorIcon = new ImageIcon(getClass().getResource("/client/assets/bubble/bubble.png"));
    private ImageIcon lineExplosionIcon = new ImageIcon(getClass().getResource("/client/assets/item/line-explosion.png"));
    private ImageIcon bombIcon = new ImageIcon(getClass().getResource("/client/assets/item/bomb.png"));

    public ItemStoreFrame() {
        setTitle("Lobby");
        setBounds(100,100, 960, 672);
        setResizable(false); // 크기 고정
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = getContentPane();
        c.setBackground(Color.black);

        buildGUI();
    }
    private void buildGUI() {
        c.setLayout(new BorderLayout());

        // 상단 제목 패널
        JPanel titlePanel = new JPanel(new BorderLayout()); // BorderLayout 사용
        titlePanel.setPreferredSize(new Dimension(960, 50));
        titlePanel.setBackground(Color.WHITE); // 배경 흰색
        JLabel titleLabel = new JLabel("아이템 상점", SwingConstants.CENTER); // 텍스트 중앙 정렬
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
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
        myItemTappedPane.addTab("내 아이템", myItemTapPanel);

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

    private JPanel ItemBoxPanel(String itemTitle, ImageIcon itemIcon, int buyCoin, int sellCoin) {
        itemBoxPanel = new JPanel();
        itemBoxPanel.setLayout(new BorderLayout());
        itemBoxPanel.setBackground(Color.white);
        itemBoxPanel.setPreferredSize(new Dimension(200, 250));

        // 상단 아이템 제목
        JLabel titleLabel = new JLabel(itemTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
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
        buyPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel sellPriceLabel = new JLabel(String.format("판매시 %d", sellCoin), SwingConstants.CENTER);
        sellPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        pricePanel.add(buyPriceLabel);
        pricePanel.add(sellPriceLabel);
        bottomPanel.add(pricePanel, BorderLayout.NORTH);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1행 2열, 간격 10
        buttonPanel.setBackground(Color.white);
        JButton buyButton = new JButton("구매");
        JButton sellButton = new JButton("판매");
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

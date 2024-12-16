//package client;
//
//import shared.ChatMsg;
//import javax.swing.*;
//import java.awt.*;
//
//public class GameScreen extends JFrame {
//    private GamePanel leftPanel;  // 내 게임판
//    private GamePanel rightPanel; // 상대방 게임판
//    private String userId;
//    private ManageNetwork network;
//    private boolean isHost;
//    private String roomId;
//
//    public GameScreen(String userId, ManageNetwork network, boolean isHost, String roomId) {
//        this.userId = userId;
//        this.network = network;
//        this.isHost = isHost;
//        this.roomId = roomId;
//
//        setTitle("BubblePOP Game - " + userId);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(960, 672);
//        setResizable(false);
//
//        // 전체 레이아웃 설정
//        setLayout(new GridLayout(1, 2));
//
//        // 왼쪽 패널 (내 게임)
//        leftPanel = new GamePanel() {
//            @Override
//            protected void afterBubbleMove() {
//                // 게임 상태를 상대방에게 전송
//                sendGameState();
//            }
//        };
//        add(leftPanel);
//
//        // 오른쪽 패널 (상대방 게임)
//        rightPanel = new GamePanel();
//        rightPanel.setKeyListener(false); // 키 입력 비활성화
//        add(rightPanel);
//
//        // 키보드 이벤트는 왼쪽 패널에만 추가
//        addKeyListener(leftPanel);
//        setFocusable(true);
//        requestFocus();
//
//        // 게임 상태 동기화를 위한 타이머
//        Timer syncTimer = new Timer(100, e -> {
//            sendGameState();
//        });
//        syncTimer.start();
//    }
//
//    private void sendGameState() {
//        StringBuilder state = new StringBuilder();
//
//        // 보드 상태
//        for (int i = 0; i < leftPanel.board.length; i++) {
//            for (int j = 0; j < leftPanel.board[i].length; j++) {
//                state.append(leftPanel.board[i][j]).append(",");
//            }
//        }
//
//        // 현재 상태 정보
//        state.append("|").append(leftPanel.angle).append(",")
//                .append(leftPanel.currentBubbleType).append(",")
//                .append(leftPanel.nextBubbleType).append(",")
//                .append(leftPanel.isBubbleMoving).append(",")
//                .append(leftPanel.bubbleX).append(",")
//                .append(leftPanel.bubbleY);
//
//        ChatMsg stateMsg = new ChatMsg(userId, ChatMsg.MODE_TX_GAME,
//                roomId + "|" + state.toString());
//        network.sendMessage(stateMsg);
//    }
//
//    public void updateOpponentState(String gameState) {
//        String[] parts = gameState.split("\\|");
//        String boardState = parts[1];
//        String[] values = boardState.split(",");
//
//        // 보드 상태 업데이트
//        int index = 0;
//        for (int i = 0; i < rightPanel.board.length && index < values.length; i++) {
//            for (int j = 0; j < rightPanel.board[i].length && index < values.length; j++) {
//                rightPanel.board[i][j] = Integer.parseInt(values[index++]);
//            }
//        }
//
//        // 게임 상태 업데이트
//        if (parts.length > 2) {
//            String[] stateValues = parts[2].split(",");
//            if (stateValues.length >= 6) {
//                rightPanel.angle = Double.parseDouble(stateValues[0]);
//                rightPanel.currentBubbleType = Integer.parseInt(stateValues[1]);
//                rightPanel.nextBubbleType = Integer.parseInt(stateValues[2]);
//                rightPanel.isBubbleMoving = Boolean.parseBoolean(stateValues[3]);
//                rightPanel.bubbleX = Integer.parseInt(stateValues[4]);
//                rightPanel.bubbleY = Integer.parseInt(stateValues[5]);
//            }
//        }
//
//        rightPanel.repaint();
//    }
//
//    public static void startGame(String userId, ManageNetwork network, boolean isHost, String roomId) {
//        SwingUtilities.invokeLater(() -> {
//            GameScreen game = new GameScreen(userId, network, isHost, roomId);
//            game.setVisible(true);
//        });
//    }
//}
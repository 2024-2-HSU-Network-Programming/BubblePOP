package client;

import shared.ChatMsg;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class OriginalGameScreen extends JFrame {
    private GamePanel player1Panel;  // 자신의 게임 패널
    private GamePanel player2Panel;  // 상대방의 게임 패널
    private String userId;
    private ManageNetwork network;
    private boolean isHost;
    private JLabel player1Label;
    private JLabel player2Label;

    // 배경 이미지
    private ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/client/assets/game/two_player_background.png"));

    public OriginalGameScreen(String userId, ManageNetwork network, boolean isHost) {
        this.userId = userId;
        this.network = network;
        this.isHost = isHost;

        initializeFrame();
        createPanels();
        addLabels();

        // 전체 게임 UI를 담을 메인 패널
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // 게임 패널들을 메인 패널에 추가
        if (isHost) {
            // 호스트인 경우: 왼쪽이 자신, 오른쪽이 상대방
            player1Panel = new GamePanel(true);  // 자신의 패널 (조작 가능)
            player2Panel = new GamePanel(false); // 상대방 패널 (조작 불가능)
        } else {
            // 게스트인 경우: 왼쪽이 자신, 오른쪽이 상대방
            player1Panel = new GamePanel(true);  // 자신의 패널 (조작 가능)
            player2Panel = new GamePanel(false); // 상대방 패널 (조작 불가능)
        }

        player1Panel.setBounds(0, 0, 450, 650);
        player2Panel.setBounds(475, 0, 450, 650);
        player1Panel.setOpaque(false);
        player2Panel.setOpaque(false);

        mainPanel.add(player1Panel);
        mainPanel.add(player2Panel);
        mainPanel.add(player1Label);
        mainPanel.add(player2Label);

        startGameLoop();
    }

    private void initializeFrame() {
        setTitle("BubblePOP Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 672);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void createPanels() {
        // 자신의 게임 패널 (왼쪽)
        player1Panel = new GamePanel(true);  // true = 조작 가능
        player1Panel.setBounds(0, 0, 450, 650);
        player1Panel.setOpaque(false);

        // 상대방의 게임 패널 (오른쪽)
        player2Panel = new GamePanel(false);  // false = 조작 불가능v
        player2Panel.setBounds(475, 0, 450, 650);
        player2Panel.setOpaque(false);

        // 키 리스너는 player1Panel에만 추가
        if (isHost) {
            player1Panel.setFocusable(true);
            player1Panel.requestFocus();
        }
    }

    private void addLabels() {
        player1Label = new JLabel(isHost ? "Host: " + userId : "Player: " + userId);
        player1Label.setBounds(65, 35, 200, 30);
        player1Label.setForeground(Color.WHITE);

        player2Label = new JLabel("Waiting for opponent...");
        player2Label.setBounds(322, 35, 200, 30);
        player2Label.setForeground(Color.WHITE);
    }

    // 상대방 정보 업데이트
    public void updateOpponentInfo(String opponentId) {
        SwingUtilities.invokeLater(() -> {
            player2Label.setText("Opponent: " + opponentId);
        });
    }

    // 게임 상태 업데이트 수신
    public void updateOpponentState(String gameState) {
        try {
            String[] stateData = gameState.split("\\|");
            if (stateData.length >= 4) {
                int bubbleX = Integer.parseInt(stateData[0]);
                int bubbleY = Integer.parseInt(stateData[1]);
                double angle = Double.parseDouble(stateData[2]);
                String boardState = stateData[3];

                // 상대방의 게임 상태만 player2Panel에 업데이트
                SwingUtilities.invokeLater(() -> {
                    player2Panel.updateFromNetwork(bubbleX, bubbleY, angle, boardState);
                    repaint();
                });
            }
        } catch (Exception e) {
            System.err.println("Error updating opponent state: " + e.getMessage());
        }
    }

    // 게임 루프 시작
    private void startGameLoop() {
        Timer gameLoop = new Timer(50, e -> {
            // 자신의 게임 상태만 서버로 전송
            String gameState = String.format("%d|%d|%f|%s",
                    player1Panel.getBubbleX(),
                    player1Panel.getBubbleY(),
                    player1Panel.getAngle(),
                    player1Panel.getBoardState()
            );

            // 게임 상태 전송
            network.sendMessage(new ChatMsg(userId, ChatMsg.MODE_TX_GAME, gameState));

            // 자신의 패널만 갱신
            player1Panel.repaint();
        });
        gameLoop.start();
    }

    // 게임 시작을 위한 정적 메서드
    public static void startGame(String userId, ManageNetwork network, boolean isHost) {
        SwingUtilities.invokeLater(() -> {
            OriginalGameScreen game = new OriginalGameScreen(userId, network, isHost);
            game.setVisible(true);
        });
    }

    // GamePanel 클래스는 이전 구현을 그대로 사용하되, 생성자에서 조작 가능 여부를 받도록 수정
    private class GamePanel extends JPanel implements KeyListener {        // ... (기존 GamePanel 코드는 유지)
        //기존
        private static final int BOARD_TOP = 65;
        private static final int BUBBLE_SIZE = 48;
        private static final int BOARD_LEFT = 65;
        private BufferedImage arrowImage; // 화살표 이미지

        private BufferedImage cc1; // 발사대 상단 이미지

        private BufferedImage dd1, dd2, dd3, dd4, dd5, dd6, dd7, dd8, dd9, dd10, dd11, dd12; // 발사대 하단 이미지
        private BufferedImage gameBottom; // 게임 하단 이미지
        private BufferedImage player1, player2; // 게임 캐릭터 이미지


        private BufferedImage b1, b2, b3, b4, b5, b6, b7, bF; // 버블 이미지
        private BufferedImage bP1_1, bP1_2, bP1_3, bP1_4; // 버블 팝 이미지
        private BufferedImage bP2_1, bP2_2, bP2_3, bP2_4; // 버블 팝 이미지
        private BufferedImage bP3_1, bP3_2, bP3_3, bP3_4; // 버블 팝 이미지
        private BufferedImage bP4_1, bP4_2, bP4_3, bP4_4; // 버블 팝 이미지
        private BufferedImage bP5_1, bP5_2, bP5_3, bP5_4; // 버블 팝 이미지
        private BufferedImage bP6_1, bP6_2, bP6_3, bP6_4; // 버블 팝 이미지
        private BufferedImage bP7_1, bP7_2, bP7_3, bP7_4; // 버블 팝 이미지


        private double angle = 0; // 화살표의 현재 각도 (라디안 값)
        private final double maxAngle = Math.toRadians(60); // 최대 각도 (50도)
        private final double minAngle = Math.toRadians(-60); // 최소 각도 (-50도)
        private final double rotationSpeed = Math.toRadians(8); // 회전 속도

        private boolean isBubbleMoving = false; // 구슬이 발사 중인지 여부
        private int bubbleX, bubbleY; // 발사되는 구슬의 현재 위치
        private final int bubbleSpeed = 15; // 구슬의 이동 속도

        private double dx, dy; // 구슬의 이동 방향 멤버 변수로 ...
        private int currentBubbleType = 1; // 현재 발사할 구슬의 타입 (1~7)
        private int nextBubbleType = 2;    // 다음 발사할 구슬의 타입 (1~7)

        // 구슬 흔들기
        private int shotCount = 0;  // 발사 횟수를 추적
        private boolean isShaking = false;  // 흔들림 상태
        private long shakeStartTime;  // 흔들기 시작 시간
        private static final long SHAKE_DURATION = 1000;  // 흔들림 지속 시간 (2초)
        private int shakeOffset = 0;  // 흔들림 오프셋

        private List<BubblePop> popAnimations = new ArrayList<>();
        //버블이 터지는 시간
        private static final long POP_DURATION = 400; // 0.4초

        private int[][] board = { // 구슬 상태를 저장하는 배열
                {1, 2, 3, 4, 5, 6, 7, 1},   // 첫 번째 줄 (8개)
                {1, 2, 3, 4, 5, 6, 7, 0},   // 두 번째 줄 (7개 + 오른쪽 빈칸)
                {1, 2, 3, 4, 5, 6, 7, 1},   // 세 번째 줄 (8개)
                {1, 2, 3, 4, 5, 6, 7, 0},   // 네 번째 줄 (7개 + 오른쪽 빈칸)
                {0, 0, 0, 0, 0, 0, 0, 0},   // 다섯 번째 줄 (빈 공간)
                {0, 0, 0, 0, 0, 0, 0, 0},   // 여섯 번째 줄 (빈 공간)
                {0, 0, 0, 0, 0, 0, 0, 0},   // 일곱 번째 줄 (빈 공간)
                {0, 0, 0, 0, 0, 0, 0, 0},   // 여덟 번째 줄 (빈 공간)
                {0, 0, 0, 0, 0, 0, 0, 0},   // 아홉 번째 줄 (빈 공간)
                {0, 0, 0, 0, 0, 0, 0, 0},   // 열 번째 줄 (빈 공간)


        };

        private boolean isControllable;

        public GamePanel(boolean isControllable) {
            this.isControllable = isControllable;
            // ... (기존 초기화 코드)

            try {
                // 화살표 이미지 로드
                arrowImage = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/arrow.png"));
                cc1 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/cc1.png"));
                dd1 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd1.png"));
                dd2 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd2.png"));
                dd3 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd3.png"));
                dd4 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd4.png"));
                dd5 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd5.png"));
                dd6 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd6.png"));
                dd7 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd7.png"));
                dd8 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd8.png"));
                dd9 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd9.png"));
                dd10 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd10.png"));
                dd11 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd11.png"));
                dd12 = ImageIO.read(getClass().getClassLoader().getResource("assets/game/shooter/dd12.png"));
                b1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble1.png"));
                b2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble2.png"));
                b3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble3.png"));
                b4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble4.png"));
                b5 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble5.png"));
                b6 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble6.png"));
                b7 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubble7.png"));
                bF = ImageIO.read(getClass().getClassLoader().getResource("assets/bubble/bubbleFinish.png"));

                bP1_1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble1Pop1.png"));
                bP1_2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble1Pop2.png"));
                bP1_3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble1Pop3.png"));
                bP1_4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble1Pop4.png"));

                bP2_2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble2Pop2.png"));
                bP2_3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble2Pop3.png"));
                bP2_1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble2Pop1.png"));
                bP2_4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble2Pop4.png"));

                bP3_1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble3Pop1.png"));
                bP3_2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble3Pop2.png"));
                bP3_3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble3Pop3.png"));
                bP3_4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble3Pop4.png"));

                bP4_1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble4Pop1.png"));
                bP4_2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble4Pop2.png"));
                bP4_3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble4Pop3.png"));
                bP4_4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble4Pop4.png"));

                bP5_1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble5Pop1.png"));
                bP5_2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble5Pop2.png"));
                bP5_3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble5Pop3.png"));
                bP5_4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble5Pop4.png"));

                bP6_1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble6Pop1.png"));
                bP6_2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble6Pop2.png"));
                bP6_3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble6Pop3.png"));
                bP6_4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble6Pop4.png"));

                bP7_1 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble7Pop1.png"));
                bP7_2 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble7Pop2.png"));
                bP7_3 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble7Pop3.png"));
                bP7_4 = ImageIO.read(getClass().getClassLoader().getResource("assets/bubblePop/bubble7Pop4.png"));


                gameBottom = ImageIO.read(getClass().getClassLoader().getResource("assets/game/gamebottom.png"));
                player1 = ImageIO.read(getClass().getClassLoader().getResource("assets/player1.png"));
                player1 = ImageIO.read(getClass().getClassLoader().getResource("assets/player2.png"));

                // 발사대 구슬 초기 위치 설정
                bubbleX = 212; // 발사대 중심 X 좌표
                bubbleY = 525; // 발사대 중심 Y 좌표

                // 초기 구슬 타입 설정
                currentBubbleType = (int) (Math.random() * 7) + 1;
                nextBubbleType = (int) (Math.random() * 7) + 1;
                // GamePanel 생성자에 추가
                Timer timer = new Timer(16, e -> repaint());
                timer.start();
                // 키 리스너는 조작 가능한 패널에만 추가
                if (isControllable) {
                    addKeyListener(this);
                    setFocusable(true);
                    requestFocus();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /////
        // DFS를 이용한 구슬 매칭 및 제거 메서드
        private void removeBubbles(int row, int col, int targetType, Set<int[]> connectedBubbles) {
            // 보드 범위를 벗어나면 종료
            if (row < 0 || row >= board.length || col < 0 || col >= board[row].length
                    || board[row][col] != targetType) {
                return;
            }

            // 이미 방문한 위치 체크
            for (int[] pos : connectedBubbles) {
                if (pos[0] == row && pos[1] == col) {
                    return;
                }
            }

            // 현재 위치 추가
            connectedBubbles.add(new int[]{row, col});

            // 8방향 탐색 (상, 하, 좌, 우, 대각선 4방향)
            int[][] directions = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                removeBubbles(newRow, newCol, targetType, connectedBubbles);
            }
        }

        // 구슬 제거 로직
        private void processConnectedBubbles(int row, int col, int bubbleType) {
            Set<int[]> connectedBubbles = new HashSet<>();
            removeBubbles(row, col, bubbleType, connectedBubbles);

            // 3개 이상 연결된 경우 제거
            if (connectedBubbles.size() >= 3) {
                for (int[] pos : connectedBubbles) {
                    board[pos[0]][pos[1]] = 0; // 구슬 제거
                }
            }
        }

        private Set<Point> findFloatingBubbles() {
            Set<Point> anchored = new HashSet<>();  // 천장에 연결된 구슬들
            Set<Point> allBubbles = new HashSet<>();  // 모든 구슬들

            // 천장에 붙어있는 구슬부터 시작
            for (int col = 0; col < board[0].length; col++) {
                if (board[0][col] != 0) {
                    findAnchoredBubbles(0, col, anchored);
                }
            }

            // 모든 구슬의 위치를 수집
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {
                    if (board[row][col] != 0) {
                        allBubbles.add(new Point(row, col));
                    }
                }
            }

            // 떠있는 구슬 찾기 (전체 구슬 - 고정된 구슬)
            allBubbles.removeAll(anchored);
            return allBubbles;  // 떠있는 구슬들 반환
        }

        private void findAnchoredBubbles(int row, int col, Set<Point> anchored) {
            // 경계 체크 및 이미 방문했거나 빈 공간인 경우 종료
            if (row < 0 || row >= board.length || col < 0 || col >= board[row].length ||
                    board[row][col] == 0 || anchored.contains(new Point(row, col))) {
                return;
            }

            // 현재 구슬을 고정된 구슬로 표시
            anchored.add(new Point(row, col));

            // 주변 6방향 확인
            int[][] directions;
            if (row % 2 == 0) {
                directions = new int[][]{
                        {-1, 0}, {-1, -1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}
                };
            } else {
                directions = new int[][]{
                        {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}
                };
            }

            // 재귀적으로 연결된 구슬 확인
            for (int[] dir : directions) {
                findAnchoredBubbles(row + dir[0], col + dir[1], anchored);
            }
        }


        @Override
        public void keyPressed(KeyEvent e) {
            if (!isControllable) return;
            // ... (기존 keyPressed 구현)
            {

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    // 기존 왼쪽 회전 로직 유지
                    angle -= rotationSpeed;
                    if (angle < minAngle) {
                        angle = minAngle;
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    // 기존 오른쪽 회전 로직 유지
                    angle += rotationSpeed;
                    if (angle > maxAngle) {
                        angle = maxAngle;
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!isBubbleMoving) {
                        isBubbleMoving = true;
                        double startX = bubbleX;
                        double startY = bubbleY;

                        dx = Math.cos(angle - Math.PI / 2) * bubbleSpeed;
                        dy = Math.sin(angle - Math.PI / 2) * bubbleSpeed;

                        new Thread(() -> {
                            int boardStartX = 43;
                            int boardEndX = boardStartX + (board[0].length - 1) * 48;

                            while (isBubbleMoving) {
                                bubbleX += dx;
                                bubbleY += dy;


                                // 좌우 벽에 부딪히면 반사
                                if (bubbleX <= boardStartX) {
                                    bubbleX = boardStartX;
                                    dx = -dx;
                                } else if (bubbleX >= boardEndX) {
                                    bubbleX = boardEndX;
                                    dx = -dx;
                                }

                                if (bubbleY <= BOARD_TOP) { // 상단 경계에 도달하면
                                    attachBubble(bubbleX, bubbleY);
                                    resetBubble();
                                    break;
                                }

                                // 구슬이 게임 보드에 닿았을 때 로직
                                boolean bubbleStopped = false;
                                for (int row = 0; row < board.length; row++) {
                                    int startXx = (row % 2 == 0) ? 43 : 67;
                                    for (int col = 0; col < board[row].length; col++) {
                                        if (board[row][col] != 0) {  // 빈 공간이 아닐 때만 충돌 체크
                                            int bubbleScreenX = startXx + col * 48;
                                            int bubbleScreenY = 65 + row * 48;

                                            if (Math.abs(bubbleX - bubbleScreenX) < 30 &&
                                                    Math.abs(bubbleY - bubbleScreenY) < 30) {
                                                attachBubble((int) bubbleX, (int) bubbleY);
                                                resetBubble();
                                                bubbleStopped = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (bubbleStopped) break;
                                }

                                // 구슬이 정지했을 때
                                if (!isBubbleMoving) {
                                    // 발사대 초기 위치로 재설정
                                    bubbleX = 212;
                                    bubbleY = 525;

//                            // 다음 구슬 타입으로 변경 (1~7 순환)
//                            currentBubbleType = (currentBubbleType % 7) + 1;
                                    break;
                                }

                                repaint();
                                try {
                                    Thread.sleep(16);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }

        }

        private Point findNearestGridPosition(int x, int y) {
            int row = (y - BOARD_TOP + BUBBLE_SIZE) / BUBBLE_SIZE;
            int col;

            if (row % 2 == 0) {
                col = (x - BOARD_LEFT + BUBBLE_SIZE) / BUBBLE_SIZE;
            } else {
                col = (x - (BOARD_LEFT + BUBBLE_SIZE) + BUBBLE_SIZE) / BUBBLE_SIZE;
            }

            // 경계 체크
            if (row < 0) row = 0;
            if (row >= board.length) row = board.length - 1;
            if (col < 0) col = 0;
            if (col >= board[row].length) col = board[row].length - 1;

            return new Point(row, col);
        }

        private void attachBubble(int x, int y) {
            Point gridPos = findNearestGridPosition(x, y);
            int row = gridPos.x;
            int col = gridPos.y;

            // 이미 구슬이 있는 위치라면 아래쪽에 배치
            if (board[row][col] != 0) {
                Point below = findAttachPosition(row, col);
                if (below != null) {
                    row = below.x;
                    col = below.y;
                }
            }

            // 일단 현재 위치에 구슬 배치
            board[row][col] = currentBubbleType;

            // 10번째 줄(인덱스 9)에 구슬이 생기면 모든 구슬을 8로 변경
            if (row == 9) {
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        if (board[i][j] != 0) {
                            board[i][j] = 8;  // 8을 finish 상태를 나타내는 값으로 사용
                        }
                    }
                }
                repaint();
                return;  // 더 이상의 처리 중단
            }

            // 같은 색상의 연결된 구슬 찾기
            Set<Point> connected = new HashSet<>();
            findConnectedBubbles(row, col, currentBubbleType, connected);

            // 3개 이상 연결된 구슬이 있는 경우 처리
            if (connected.size() >= 3) {
                // 먼저 팝 애니메이션 추가
                for (Point p : connected) {
                    Point screenPos = new Point(
                            (p.y * BUBBLE_SIZE) + (p.x % 2 == 0 ? 43 : 67),
                            65 + (p.x * BUBBLE_SIZE)
                    );
                    popAnimations.add(new GamePanel.BubblePop(screenPos, board[p.x][p.y]));
                }

                // 연결된 구슬 제거
                for (Point p : connected) {
                    board[p.x][p.y] = 0;
                }

                // 떠있는 구슬 처리
                Set<Point> floating = findFloatingBubbles();
                for (Point p : floating) {
                    Point screenPos = new Point(
                            (p.y * BUBBLE_SIZE) + (p.x % 2 == 0 ? 43 : 67),
                            65 + (p.x * BUBBLE_SIZE)
                    );
                    popAnimations.add(new GamePanel.BubblePop(screenPos, board[p.x][p.y]));
                    board[p.x][p.y] = 0;
                }
            }
        }

        // 구슬을 붙일 수 있는 위치 찾기
        private Point findAttachPosition(int row, int col) {
            // 주변 6방향 체크
            int[][] directions;
            if (row % 2 == 0) {
                directions = new int[][]{
                        {1, -1}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}
                };
            } else {
                directions = new int[][]{
                        {1, 0}, {1, 1}, {0, -1}, {0, 1}, {-1, 0}, {-1, 1}
                };
            }

            Point closestPoint = null;
            double minDistance = Double.MAX_VALUE;

            // 주변의 모든 빈 공간을 체크하고 가장 가까운 위치 찾기
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < board.length &&
                        newCol >= 0 && newCol < board[newRow].length &&
                        board[newRow][newCol] == 0) {

                    // 실제 픽셀 좌표 계산
                    int startX = (row % 2 == 0) ? 43 : 67;
                    int targetX = startX + newCol * 48;
                    int targetY = 65 + newRow * 48;

                    // 현재 위치와의 거리 계산
                    double distance = Math.sqrt(
                            Math.pow(bubbleX - targetX, 2) +
                                    Math.pow(bubbleY - targetY, 2)
                    );

                    // 더 가까운 위치를 찾으면 업데이트
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestPoint = new Point(newRow, newCol);
                    }
                }
            }

            return closestPoint;
        }

        private Point findLowestEmptySpace(int x) {
            // x 좌표에 가장 가까운 열 찾기
            int col = (x - BOARD_LEFT) / BUBBLE_SIZE;
            if (col < 0) col = 0;
            if (col >= board[0].length) col = board[0].length - 1;

            // 아래에서부터 위로 올라가며 빈 공간 찾기
            for (int row = board.length - 1; row >= 0; row--) {
                if (board[row][col] == 0) {
                    return new Point(row, col);
                }
            }
            return null;
        }

        // resetBubble 메서드 수정
        private void resetBubble() {
            isBubbleMoving = false;
            bubbleX = 212;
            bubbleY = 525;
            currentBubbleType = nextBubbleType;
            nextBubbleType = (int) (Math.random() * 7) + 1;

            // 발사 횟수 증가 및 흔들기 체크
            shotCount++;
            if (shotCount % 3 == 0) {
                startShaking();
            }
            repaint();
        }

        // 흔들기 시작 메서드
        private void startShaking() {
            isShaking = true;
            shakeStartTime = System.currentTimeMillis();

            // 흔들기 애니메이션을 위한 타이머 시작
            new Thread(() -> {
                while (isShaking) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - shakeStartTime > SHAKE_DURATION) {
                        isShaking = false;
                        shakeOffset = 0;
                    } else {
                        // 흔들림효과
                        shakeOffset = (int) (Math.sin((currentTime - shakeStartTime) / 20.0) * 3);
                    }
                    repaint();

                    try {
                        Thread.sleep(16);  // 약 60fps
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        private void findConnectedBubbles(int row, int col, int type, Set<Point> connected) {
            // 경계 체크 및 현재 위치가 유효한지 확인
            if (row < 0 || row >= board.length || col < 0 || col >= board[row].length ||
                    board[row][col] != type || connected.contains(new Point(row, col))) {
                return;
            }

            // 현재 위치 추가
            connected.add(new Point(row, col));

            // 인접한 6방향 체크 (홀수/짝수 행에 따라 다름)
            int[][] directions;
            if (row % 2 == 0) {  // 짝수 행
                directions = new int[][]{
                        {-1, 0},  // 위
                        {-1, -1}, // 왼쪽 위
                        {0, -1},  // 왼쪽
                        {0, 1},   // 오른쪽
                        {1, -1},  // 왼쪽 아래
                        {1, 0}    // 아래
                };
            } else {  // 홀수 행
                directions = new int[][]{
                        {-1, 0},  // 위
                        {-1, 1},  // 오른쪽 위
                        {0, -1},  // 왼쪽
                        {0, 1},   // 오른쪽
                        {1, 0},   // 아래
                        {1, 1}    // 오른쪽 아래
                };
            }

            // 각 방향에 대해 재귀적으로 연결된 구슬 찾기
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                findConnectedBubbles(newRow, newCol, type, connected);
            }
        }

        /////
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 배경 이미지 로드
//            ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("assets/game/two_player_background.png"));
//            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);

            // 게임 하단 이미지 출력
            if (gameBottom != null) {
                int gameBottomX = 5; // 하단의 X 위치
                int gameBottomY = 516; // 하단의 Y 위치
                g.drawImage(gameBottom, gameBottomX, gameBottomY, null);
            }

            // 구슬 배열을 기반으로 그리기
            int startX, startY = 65; // 시작 Y 위치
            int spacing = 48; // 구슬 간 간격

            for (int row = 0; row < board.length; row++) {
                startX = (row % 2 == 0) ? 43 : 67;
                for (int col = 0; col < board[row].length; col++) {
                    int bubbleType = board[row][col];
                    if (bubbleType != 0) {
                        BufferedImage bubbleImage = getBubbleImage(bubbleType);
                        if (bubbleImage != null) {
                            // 흔들림이 활성화된 경우 오프셋 적용
                            int xOffset = isShaking ? shakeOffset : 0;
                            g.drawImage(bubbleImage,
                                    startX + col * spacing + xOffset,
                                    startY,
                                    null);
                        }
                    }
                }
                startY += 48;
            }

            // 발사대 하단 이미지 출력
            BufferedImage currentImage = getImageForAngle();
            if (currentImage != null) {
                int ddX = 152; // 발사대 하단의 X 위치
                int ddY = 557; // 발사대 하단의 Y 위치
                g.drawImage(currentImage, ddX, ddY, null);
            }

            // 발사대 상단 이미지 출력
            if (cc1 != null) {
                int cc1X = 191; // 발사대 상단의 X 위치
                int cc1Y = 516; // 발사대 상단의 Y 위치
                g.drawImage(cc1, cc1X, cc1Y, null);
            }

            // 화살표 이미지 출력
            if (arrowImage != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 부드럽게 렌더링

                int arrowX = 208; // 화살표의 X 위치
                int arrowY = 470; // 화살표의 Y 위치
                int arrowWidth = arrowImage.getWidth();
                int arrowHeight = arrowImage.getHeight();

                // 중심점을 기준으로 회전
                g2d.rotate(angle, arrowX + arrowWidth / 2.0, arrowY + arrowHeight / 2.0);
                g2d.drawImage(arrowImage, arrowX, arrowY, arrowWidth, arrowHeight, null);
                g2d.rotate(-angle, arrowX + arrowWidth / 2.0, arrowY + arrowHeight / 2.0); // 회전 복구
            }

            // 다음 구슬 미리보기 출력
            BufferedImage nextBubbleImage = getBubbleImage(nextBubbleType);
            if (nextBubbleImage != null) {
                g.drawImage(nextBubbleImage, 94, 579, null);  // 위치 조정
            }

            // 발사대 구슬 출력
            if (!isBubbleMoving) {
                BufferedImage bubbleImage = getBubbleImage(currentBubbleType);
                if (bubbleImage != null) {
                    g.drawImage(bubbleImage, bubbleX, bubbleY, null);
                }
            }


            // 발사 중인 구슬 출력
            if (isBubbleMoving) {
                BufferedImage bubbleImage = getBubbleImage(currentBubbleType);
                if (bubbleImage != null) {
                    g.drawImage(bubbleImage, bubbleX, bubbleY, null);
                }
            }

            // 팝 애니메이션 그리기
            Iterator<GamePanel.BubblePop> iterator = popAnimations.iterator();
            while (iterator.hasNext()) {
                GamePanel.BubblePop pop = iterator.next();
                long elapsed = System.currentTimeMillis() - pop.startTime;

                if (elapsed >= POP_DURATION) {
                    iterator.remove();
                    continue;
                }

                // 프레임 계산 (0.7초를 4단계로 나눔)
                pop.frame = (int) (elapsed / (POP_DURATION / 4)) + 1;
                if (pop.frame > 4) pop.frame = 4;

                BufferedImage popImage = getBubblePopImage(pop.type, pop.frame);
                if (popImage != null) {
                    g.drawImage(popImage, pop.position.x, pop.position.y, null);
                }
            }
        }

        // 버블 이미지를 반환하는 메서드 추가
        private BufferedImage getBubbleImage(int bubbleNumber) {
            return switch (bubbleNumber) {
                case 1 -> b1;
                case 2 -> b2;
                case 3 -> b3;
                case 4 -> b4;
                case 5 -> b5;
                case 6 -> b6;
                case 7 -> b7;
                case 8 -> bF;
                default -> null;
            };
        }

        private BufferedImage getImageForAngle() {
            // 각도를 -50도에서 50도로 변환하여 이미지 인덱스를 계산
            double degrees = Math.toDegrees(angle); // 라디안을 도(degree)로 변환
            int index = (int) ((degrees + 50) / 100 * 12); // -50~50 -> 0~12 범위로 매핑

            // 인덱스 범위를 확인
            if (index < 0) index = 0;
            if (index > 11) index = 11;

            // 인덱스에 따른 이미지 반환
            return switch (index) {
                case 0 -> dd1;
                case 1 -> dd2;
                case 2 -> dd3;
                case 3 -> dd4;
                case 4 -> dd5;
                case 5 -> dd6;
                case 6 -> dd7;
                case 7 -> dd8;
                case 8 -> dd9;
                case 9 -> dd10;
                case 10 -> dd11;
                case 11 -> dd12;
                default -> dd1; // 기본값
            };
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // 아무 작업도 하지 않음 (키를 뗐을 때 회전 멈춤)
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        public class BubblePop {
            protected Point position;
            protected int type;
            protected int frame;
            protected long startTime;

            public BubblePop(Point position, int type) {
                this.position = position;
                this.type = type;
                this.frame = 1;
                this.startTime = System.currentTimeMillis();
            }
        }

        private BufferedImage getBubblePopImage(int type, int frame) {
            return switch (type) {
                case 1 -> switch (frame) {
                    case 1 -> bP1_1;
                    case 2 -> bP1_2;
                    case 3 -> bP1_3;
                    case 4 -> bP1_4;
                    default -> null;
                };
                case 2 -> switch (frame) {
                    case 1 -> bP2_1;
                    case 2 -> bP2_2;
                    case 3 -> bP2_3;
                    case 4 -> bP2_4;
                    default -> null;
                };
                case 3 -> switch (frame) {
                    case 1 -> bP3_1;
                    case 2 -> bP3_2;
                    case 3 -> bP3_3;
                    case 4 -> bP3_4;
                    default -> null;
                };
                case 4 -> switch (frame) {
                    case 1 -> bP4_1;
                    case 2 -> bP4_2;
                    case 3 -> bP4_3;
                    case 4 -> bP4_4;
                    default -> null;
                };
                case 5 -> switch (frame) {
                    case 1 -> bP5_1;
                    case 2 -> bP5_2;
                    case 3 -> bP5_3;
                    case 4 -> bP5_4;
                    default -> null;
                };
                case 6 -> switch (frame) {
                    case 1 -> bP6_1;
                    case 2 -> bP6_2;
                    case 3 -> bP6_3;
                    case 4 -> bP6_4;
                    default -> null;
                };
                case 7 -> switch (frame) {
                    case 1 -> bP7_1;
                    case 2 -> bP7_2;
                    case 3 -> bP7_3;
                    case 4 -> bP7_4;
                    default -> null;
                };
                default -> null;
            };
        }
        public void updateFromNetwork(int bubbleX, int bubbleY, double angle, String boardState) {
            this.bubbleX = bubbleX;
            this.bubbleY = bubbleY;
            this.angle = angle;
            updateBoardState(boardState);
            repaint();
        }

        public int getBubbleX() { return bubbleX; }
        public int getBubbleY() { return bubbleY; }
        public double getAngle() { return angle; }

        public String getBoardState() {
            StringBuilder state = new StringBuilder();
            for (int[] row : board) {
                for (int cell : row) {
                    state.append(cell).append(",");
                }
            }
            return state.toString();
        }

        private void updateBoardState(String boardState) {
            String[] cells = boardState.split(",");
            int index = 0;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (index < cells.length) {
                        board[i][j] = Integer.parseInt(cells[index++]);
                    }
                }
            }
        }
    }
//    public static void main(String[] args) {
//
//        // 게임 화면 시작
//        OriginalGameScreen.startGame(userId, network, isHost);
//    }
}
package client;

import shared.ChatMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginFrame extends JFrame{
    // 서버 연결 필요 요소
    String serverAddress = "localhost";
    int serverPort = 12345;
    Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ManageNetwork network;

    private JPanel startPane;
    private JPanel loginPane;
    private JTextField txtUserName;
    private JPasswordField txtUserPassword;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;
    private JButton btnLogin;

    private Image background = new ImageIcon(getClass().getResource("/client/assets/background/login_bg.jpg")).getImage();
    private ImageIcon logo = new ImageIcon(getClass().getResource("/client/assets/logo/logo.png"));
    private ImageIcon logo_character = new ImageIcon(getClass().getResource("/client/assets/logo/logo_character.png"));

    public LoginFrame() {
        setTitle("2024-2 Network Programming-BubblePOP");
        setBounds(100,100, 960, 672);
        setResizable(false); // 크기 고정
        startPane = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 이미지 크기를 창 크기에 맞게 그리기
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);            }
        };
        startPane.setBackground(new Color(255, 0,0,0));

        JLabel lblLogoImg = new JLabel(logo);
        JLabel lblLogoCharacterImg = new JLabel(logo_character);
        lblLogoImg.setBounds(341, 184, logo.getIconWidth(), logo.getIconHeight());
        lblLogoCharacterImg.setBounds(383, 74, logo_character.getIconWidth(), logo_character.getIconHeight());


        startPane.add(lblLogoImg);
        startPane.add(lblLogoCharacterImg);

        startPane.setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(startPane);

        loginPanel();
        setVisible(true); // 프레임 보이도록

    }

    // 로그인 패널
    private void loginPanel() {
        loginPane = new JPanel();
        loginPane.setBounds(258, 376, 465, 200);
        loginPane.setLayout(null);
        loginPane.setBackground(new Color(255, 255, 255, 180));

        // 아이디 입력
        JLabel lblUserName = new JLabel("아이디: ");
        lblUserName.setBounds(30, 50, 100, 30);
        lblUserName.setFont(new Font("Arial", Font.PLAIN, 20));
        txtUserName = new JTextField();
        txtUserName.setBounds(130, 40, 180, 50);

        // 비밀번호 입력
        JLabel lblUserPassword = new JLabel("비밀번호: ");
        lblUserPassword.setBounds(30, 120, 100, 30);
        lblUserPassword.setFont(new Font("Arial", Font.PLAIN, 20));
        txtUserPassword = new JPasswordField();
        txtUserPassword.setBounds(130, 110, 180, 50);

        // 로그인 버튼
        btnLogin = new JButton("로그인");
        btnLogin.setBounds(325, 22, 130, 157);
        btnLogin.setOpaque(true); // 배경색 적용 가능하도록 설정
        btnLogin.setBorderPainted(false); // 버튼 테두리 제거
        btnLogin.setFont(new Font("Arial", Font.BOLD, 20));
        btnLogin.setBackground(Color.BLACK);
        btnLogin.setForeground(Color.white);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        loginPane.add(lblUserName);
        loginPane.add(txtUserName);
        loginPane.add(lblUserPassword);
        loginPane.add(txtUserPassword);
        loginPane.add(btnLogin);

        startPane.add(loginPane);
    }

    /* 클라이언트와 서버간의 통신 코드 시작 */
    public void connectToServer() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

//            // 총관리 네트워크 설정
//            network = new ManageNetwork(in, out, socket);
//            network.start();

            // 로비 화면 생성
    //        LobbyFrame lobbyFrame = new LobbyFrame(userName, new ManageNetwork(in, out, socket, null));
            ManageNetwork network = new ManageNetwork(in, out, socket);
            network.start();

            // GameUser 객체 생성 및 전송
            String userName = txtUserName.getText();
            String password = new String(txtUserPassword.getPassword());
            GameUser gameUser = GameUser.getInstance();
            gameUser.init(userName, password, network);

//            out.writeObject(gameUser);
//            out.flush();

            // ChatMsg 객체 전송
            ChatMsg loginMsg = new ChatMsg(gameUser.getId(), ChatMsg.MODE_LOGIN, "Login");
            out.writeObject(loginMsg);
            out.flush();

            setVisible(false);
            //new LobbyFrame();

        } catch(IOException e) {
            // 서버 연결시 다양한 오류가 발생할 수 있기 때문에 꼭 작성해줘야함(서버 연결 실패, 네트워크 오류 등)
            System.out.println("서버 오류 > " + e.getMessage());
            System.exit(-1); // 비정상 오류 상태는 음수 값
        }
    }
    public static void main(String[] args) {
        new LoginFrame();
    }
}

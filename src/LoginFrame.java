import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame{
    private JPanel startPane;
    private JPanel loginPane;
    private JTextField txtUserName;
    private JPasswordField txtUserPassword;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;
    private JButton btnLogin;

    private Image background = new ImageIcon(getClass().getResource("/assets/background/login_bg.jpg")).getImage();
    private ImageIcon logo = new ImageIcon(getClass().getResource("/assets/logo/logo.png"));
    private ImageIcon logo_character = new ImageIcon(getClass().getResource("/assets/logo/logo_character.png"));

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

        loginPane.add(lblUserName);
        loginPane.add(txtUserName);
        loginPane.add(lblUserPassword);
        loginPane.add(txtUserPassword);
        loginPane.add(btnLogin);

        startPane.add(loginPane);
    }
    public static void main(String[] args) {
        new LoginFrame();
    }
}

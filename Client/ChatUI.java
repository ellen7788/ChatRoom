import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatUI extends JFrame {
    String userName;
    JTextField input;
    JTextArea chats;
    Socket socket;
    JScrollPane chatsScroll;
    String FileName = "";
    String FilePath = "";

    ChatUI(String userName, Socket socket) throws IOException{
        this.userName = userName;
        this.socket = socket;
        //	��M���X���b�h����
        ReceiveMessage sendMessage = new ReceiveMessage(socket, this, userName);
        sendMessage.start();

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            //  ����{�^�����������Ƃ��̋���
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    if(!socket.isClosed()){
                        out.println("END");
                        System.out.println("[�ޏo���܂���]");
                        System.out.println("closing...");
                        try {
                            sendMessage.join();
                            socket.close();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    super.windowClosing(we);
                }
            });

            setTitle("Chat Room" + "[" + userName + "����]");
            setSize(600, 400);
            setResizable(false);
            setLayout(new FlowLayout());
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.setOpaque(false);

            input = new JTextField(40);
            chats = new JTextArea(15, 40);
            chats.setBackground(new Color(0.95f, 0.95f, 0.95f));
            chats.setEditable(false);
            chatsScroll = new JScrollPane(chats, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            //  ���b�Z�[�W���M
            JButton sendButton = new JButton("���M");
            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    if(!input.getText().equals("")) {
                        out.println("MESSAGE");
                        out.println(input.getText());
                        input.setText("");
                    }
                }
            });
            sendButton.setMnemonic(KeyEvent.VK_ENTER);

            //���M�{�^��
            JButton fileButton = new JButton("�t�@�C�����M");
            fileButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    //  �t�@�C���̑��M
                    String filePath = FilePath;
                    String fileName = FileName;
                    File file = new File(filePath);
                    if (!file.exists()) return;
                    out.println("FILE");

                    try {
                        byte[] buf = new byte[512];
                        BufferedInputStream  inStream  = new BufferedInputStream(new FileInputStream(file));
                        BufferedOutputStream outStream = new BufferedOutputStream(socket.getOutputStream());

                        //  �t�@�C���T�C�Y�𑗐M
                        int fileSize = (int)file.length();
                        out.println(fileSize);

                        //  �t�@�C�������M
                        out.println(fileName);

                        Thread.sleep(Client.sleepTime);
                        //  �摜�t�@�C�����M
                        int bufSize;
                        for (int i = 0; i < (fileSize-1)/512+1; i++) {
                            bufSize = inStream.read(buf);
                            outStream.write(buf, 0, bufSize);
                        }

                        outStream.flush();
                        inStream.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            //  �p�X�w��{�^��
            JButton pathButton = new JButton("�t�@�C����I��");
            JLabel label = new JLabel();
            pathButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    File dir = new File("c:");
                    JFileChooser fileChooser = new JFileChooser(dir);

                    int selected = fileChooser.showOpenDialog(panel);
                    if (selected == JFileChooser.APPROVE_OPTION){
                        File file = fileChooser.getSelectedFile();
                        label.setText(fileChooser.getName(file));
                        FileName = fileChooser.getName(file);
                        FilePath = file.getAbsolutePath();
                    }
                }
            });

            //  �ޏo�{�^��
            JButton exitButton = new JButton("�ޏo");
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    out.println("END");
                    Receive("[�ޏo���܂���]");
                    System.out.println("[�ޏo���܂���]");
                    System.out.println("closing...");
                    sendButton.setEnabled(false);
                    exitButton.setEnabled(false);
                    fileButton.setEnabled(false);
                    pathButton.setEnabled(false);
                    try {
                        sendMessage.join();
                        socket.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            Container contentPane = getContentPane();
            contentPane.setBackground(Color.CYAN);
            contentPane.setLayout(new FlowLayout());
            contentPane.add(panel, BorderLayout.CENTER);
            contentPane.add(input, BorderLayout.EAST);
            contentPane.add(sendButton, BorderLayout.EAST);
            contentPane.add(exitButton, BorderLayout.EAST);
            contentPane.add(fileButton, BorderLayout.EAST);
            contentPane.add(pathButton, BorderLayout.EAST);
            contentPane.add(label, BorderLayout.EAST);
            contentPane.add(chatsScroll, BorderLayout.WEST);

            //	���[�U�[���𑗐M
            out.println(userName);

            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Receive(String str){
        chats.append(str);
        if (chatsScroll.getVerticalScrollBar().getValue() + 16 * (15 + 2) >= chatsScroll.getVerticalScrollBar().getMaximum()) {
            chatsScroll.getVerticalScrollBar().setValue(chatsScroll.getVerticalScrollBar().getMaximum());
        }
    }

}

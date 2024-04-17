package Chat.ChatGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class clientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader serverReader;
    private BufferedWriter clientWriter;

    public clientGUI() {
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        try {
            // Replace "remote_server_ip" with the actual IP address or hostname of the server
            socket = new Socket("localhost", 12345);
            serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(new ServerListener()).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            try {
                clientWriter.write(message);
                clientWriter.newLine();
                clientWriter.flush();
                chatArea.append("You: " + message + "\n");
                inputField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMsg;
                while ((serverMsg = serverReader.readLine()) != null) {
                    chatArea.append(serverMsg + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new clientGUI();
            }
        });
    }
}



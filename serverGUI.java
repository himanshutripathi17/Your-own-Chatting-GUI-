

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class serverGUI extends JFrame {
    private JTextArea chatArea;
    private ServerSocket serverSocket;
    private BufferedWriter serverWriter;
    private JTextField inputField;
    private JButton sendButton;

    public serverGUI() {
        setTitle("Chat Server");
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

       inputField=new JTextField();
       sendButton=new JButton("Send");

       JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);
            
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
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
            serverSocket = new ServerSocket(12345);
            System.out.println("Server is waiting for connections...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            serverWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            new Thread(new ClientListener(clientSocket)).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        setVisible(true);
    }
// logic for allowing the client also to recieve message
    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            try {
                serverWriter.write(message);
                serverWriter.newLine();
                serverWriter.flush();
                chatArea.append("You: " + message + "\n");
                inputField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientListener implements Runnable {
        private Socket socket;
        private BufferedReader clientReader;

        public ClientListener(Socket socket) throws IOException {
        clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String clientMsg;
                while ((clientMsg = clientReader.readLine()) != null) {
                    chatArea.append("Client: " + clientMsg + "\n");
                    serverWriter.write("Server: " + clientMsg + "\n");
                    serverWriter.flush();
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
                new serverGUI();
            }
        });
    }
}



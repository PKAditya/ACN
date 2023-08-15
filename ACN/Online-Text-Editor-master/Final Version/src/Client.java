import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
class Client extends JFrame implements ActionListener, KeyListener {
    public static Socket socClient;
    public static ObjectInputStream ClientInput;
    public static ObjectOutputStream ClientOutput;
    public String SelectedText;
    public String ClientIDToShare;
    JTextArea t;
    JFrame f;
    public static void main(String[] args) {
        try {
            socClient = new Socket("10.113.7.39", 9999); // named argument
            System.out.println("Connected!");
            Client c1 = new Client();
            Scanner scn = new Scanner(System.in);
            ClientOutput = new ObjectOutputStream(socClient.getOutputStream());
            ClientInput = new ObjectInputStream(socClient.getInputStream());
            System.out.print("Write your ID : ");
            String id = scn.nextLine();
            ClientOutput.writeUTF(id);
            ClientOutput.flush();
            System.out.println("Write the name for your frame");
            String filename = scn.nextLine();
            c1.ClientGUI(filename);
            System.out.print("Now You Start your Real Connection");
            while (true) {
                String NewDataInTextArea = ClientInput.readUTF();
                c1.ChangeText(NewDataInTextArea);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        }
    }
    public void ClientGUI(String str) {
        f = new JFrame(str);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
        t = new JTextArea();
        t.setLineWrap(true);
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");
        JMenuItem mi10 = new JMenuItem("Share");
        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        mi10.addActionListener(this);
        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        m1.add(mi10);
        JMenu m2 = new JMenu("Edit");
        JMenuItem mi4 = new JMenuItem("cut");
        JMenuItem mi5 = new JMenuItem("copy");
        JMenuItem mi6 = new JMenuItem("paste");
        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this);
        m2.add(mi4);
        m2.add(mi5);
        m2.add(mi6);
        JMenuItem mc = new JMenuItem("close");
        mc.addActionListener(this);
        mb.add(m1);
        mb.add(m2);
        mb.add(mc);
        f.setJMenuBar(mb);
        f.add(t);
        f.setSize(500, 500);
        f.show();

        t.addKeyListener(this);
    }
    public void ChangeText(String str) {
        t.setText(str);
    }
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("cut")) {
            t.cut();
        }
        else if (s.equals("copy")) {
            t.copy();
        }
        else if (s.equals("paste")) {
            t.paste();
        }
        else if (s.equals("Save")) {
            JFileChooser j = new JFileChooser("f:");
            int r;
            r = j.showSaveDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File fi = new File(j.getSelectedFile().getAbsolutePath());
                try {
                    FileWriter wr = new FileWriter(fi, false);
                    BufferedWriter w = new BufferedWriter(wr);
                    w.write(t.getText());

                    w.flush();
                    w.close();
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }
            }
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }
        else if (s.equals("Open")) {
            JFileChooser j = new JFileChooser("f:");
            int r;
            r = j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File fi = new File(j.getSelectedFile().getAbsolutePath());
                try {
                    String s2;
                    FileReader fr = new FileReader(fi);
                    BufferedReader br = new BufferedReader(fr);
                    String sl;
                    sl = br.readLine();
                    while ((s2 = br.readLine()) != null) {
                        sl = sl + "\n" + s2;
                    }
                    t.setText(sl);
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }
            }
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }
        else if (s.equals("New")) {
            t.setText("");
        }
        else if (s.equals("close")) {
            f.setVisible(false);
            try {
                ClientInput.close();
                socClient.close();
                ClientOutput.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (s.equals("Share")) {
            try {
                ClientOutput.writeUTF("Share");
                ClientOutput.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ClientIDToShare = JOptionPane.showInputDialog("Enter the ID's of the Client to send this text..");
            try {
                ClientOutput.writeUTF(ClientIDToShare);
                ClientOutput.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
        SelectedText = t.getText();
        try {
        	
            ClientOutput.writeUTF(SelectedText);
            ClientOutput.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
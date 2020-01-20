package game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class MainMenu {
    public MainMenu() {

    }
    public void init() {
        JFrame frame = new JFrame("Warcaby");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 320);

        JPanel panel = new JPanel();
        frame.add(panel);
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        JLabel l_player1 = new JLabel("Gracz 1:");
        JComboBox player1 = new JComboBox<>(new String[] {"człowiek", "komputer"});
        JLabel l_pawn1 = new JLabel("Pionki:");
        JComboBox pawn1 = new JComboBox<>(new String[] {"standardowe", "low poly"});

        panel.add(l_player1);
        panel.add(player1);
        panel.add(l_pawn1);
        panel.add(pawn1);
        layout.putConstraint(SpringLayout.WEST, l_player1, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, l_player1, 20, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, player1, 5, SpringLayout.EAST, l_player1);
        layout.putConstraint(SpringLayout.SOUTH, player1, 5, SpringLayout.SOUTH, l_player1);
        layout.putConstraint(SpringLayout.WEST, l_pawn1, 15, SpringLayout.EAST, player1);
        layout.putConstraint(SpringLayout.SOUTH, l_pawn1, 0, SpringLayout.SOUTH, l_player1);
        layout.putConstraint(SpringLayout.WEST, pawn1, 5, SpringLayout.EAST, l_pawn1);
        layout.putConstraint(SpringLayout.SOUTH, pawn1, 0, SpringLayout.SOUTH, player1);

        JLabel l_player2 = new JLabel("Gracz 2:");
        JComboBox player2 = new JComboBox<>(new String[] {"człowiek", "komputer"});
        JLabel l_pawn2 = new JLabel("Pionki:");
        JComboBox pawn2 = new JComboBox<>(new String[] {"standardowe", "low poly"});

        panel.add(l_player2);
        panel.add(player2);
        panel.add(l_pawn2);
        panel.add(pawn2);
        layout.putConstraint(SpringLayout.EAST, l_player2, 0, SpringLayout.EAST, l_player1);
        layout.putConstraint(SpringLayout.NORTH, l_player2, 20, SpringLayout.SOUTH, l_player1);
        layout.putConstraint(SpringLayout.WEST, player2, 5, SpringLayout.EAST, l_player2);
        layout.putConstraint(SpringLayout.SOUTH, player2, 5, SpringLayout.SOUTH, l_player2);
        layout.putConstraint(SpringLayout.WEST, l_pawn2, 15, SpringLayout.EAST, player2);
        layout.putConstraint(SpringLayout.SOUTH, l_pawn2, 0, SpringLayout.SOUTH, l_player2);
        layout.putConstraint(SpringLayout.WEST, pawn2, 5, SpringLayout.EAST, l_pawn2);
        layout.putConstraint(SpringLayout.SOUTH, pawn2, 0, SpringLayout.SOUTH, player2);

        JLabel l_board = new JLabel("Rozmiar planszy:");
        JTextField tf_board = new JTextField("8", 3);
        JLabel l_rows = new JLabel("Liczba szeregów:");
        JTextField tf_rows = new JTextField("3", 3);

        panel.add(l_board);
        panel.add(tf_board);
        panel.add(l_rows);
        panel.add(tf_rows);

        layout.putConstraint(SpringLayout.WEST, l_board, 0, SpringLayout.WEST, l_player2);
        layout.putConstraint(SpringLayout.NORTH, l_board, 50, SpringLayout.SOUTH, l_player2);
        layout.putConstraint(SpringLayout.WEST, tf_board, 10, SpringLayout.EAST, l_board);
        layout.putConstraint(SpringLayout.NORTH, tf_board, 0, SpringLayout.NORTH, l_board);

        layout.putConstraint(SpringLayout.WEST, l_rows, 0, SpringLayout.WEST, l_board);
        layout.putConstraint(SpringLayout.NORTH, l_rows, 10, SpringLayout.SOUTH, l_board);
        layout.putConstraint(SpringLayout.WEST, tf_rows, 0, SpringLayout.WEST, tf_board);
        layout.putConstraint(SpringLayout.NORTH, tf_rows, 0, SpringLayout.NORTH, l_rows);

        JLabel l_time = new JLabel("Czas gry [min]:");
        JTextField tf_time = new JTextField("5", 3);

        panel.add(l_time);
        panel.add(tf_time);

        layout.putConstraint(SpringLayout.WEST, l_time, 0, SpringLayout.WEST, l_rows);
        layout.putConstraint(SpringLayout.NORTH, l_time, 10, SpringLayout.SOUTH, l_rows);
        layout.putConstraint(SpringLayout.WEST, tf_time, 0, SpringLayout.WEST, tf_board);
        layout.putConstraint(SpringLayout.NORTH, tf_time, 0, SpringLayout.NORTH, l_time);

        JButton b_start = new JButton("Start");
        panel.add(b_start);
        layout.putConstraint(SpringLayout.WEST, b_start, 0, SpringLayout.WEST, l_time);
        layout.putConstraint(SpringLayout.NORTH, b_start, 10, SpringLayout.SOUTH, l_time);

        JCheckBox cb_no_animations = new JCheckBox("Brak animacji");
        panel.add(cb_no_animations);

        layout.putConstraint(SpringLayout.WEST, cb_no_animations, 5, SpringLayout.EAST, b_start);
        layout.putConstraint(SpringLayout.NORTH, cb_no_animations, 0, SpringLayout.NORTH, b_start);

        b_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int wtype = pawn1.getSelectedIndex();
                int btype = pawn2.getSelectedIndex();
                boolean p1_human = player1.getSelectedIndex()==0;
                boolean p2_human = player2.getSelectedIndex()==0;
                int time = (int)(Float.parseFloat(tf_time.getText())*60);
                int bsize = Integer.parseInt(tf_board.getText());
                int n_rows = Integer.parseInt(tf_rows.getText());
                boolean no_animations = cb_no_animations.isSelected();
                new Thread(() -> new Game(bsize, n_rows, p1_human, p2_human, wtype, btype, time, no_animations).run()).start();
                //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

            }
        });

        frame.setVisible(true);
    }
    public static void main(String args[]) {
        new MainMenu().init();
    }
}

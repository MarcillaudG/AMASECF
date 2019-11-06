package fr.irit.smac.learningdata.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView.TableRow;

import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Matrix extends JFrame {

	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Object[][] donnees = {
							{"I1","Johnathan", "Sykes"},
							{"I1","Nicolas", "Van de Kampf"},
							{"I1","Damien", "Cuthbert"},
							{"I1","Corinne", "Valance"},
							{"I1","Emilie", "Schrödinger"},
							{"I1","Delphine", "Duke"},
							{"I1","Eric", "Trump"},
					};

					String[] entetes = {"","Prénom", "Nom"};
					Matrix frame = new Matrix(entetes,donnees);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Matrix(String[] head, Object[][] row) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 988, 676);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);

		table = new JTable(row,head);
		scrollPane.setViewportView(table);
	}

	public void updateTable(String[] head, Object[][] row) {
		// Header
		for(int i = 0; i < head.length;i++) {
			this.table.setValueAt(head[i], 0, i);
		}

		for(int i = 0; i < row.length;i++) {
			for(int j =0; j < row[i].length;j++) {
				this.table.setValueAt(row[i][j], i, j);
			}
		}
	}
}

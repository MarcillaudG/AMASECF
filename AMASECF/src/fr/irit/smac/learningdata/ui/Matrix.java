package fr.irit.smac.learningdata.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.TableView.TableRow;

import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSplitPane;

public class Matrix extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTable oracleMatrix;

	/**
	 * Create the frame.
	 */
	public Matrix(String[] head, Object[][] row,String[] headOracle, Object[][] rowOracle) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 988, 676);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 0, 0, 0));

		System.out.println("HEAD "+head[1]+"|");
		if(head[1] ==null) {
			for(int i =0; i < row.length;i++) {
				head[i] = "V";
				headOracle[i] = "V";
			}
		}
		table = new JTable(row,head);
		contentPane.add(table);
		oracleMatrix = new JTable(rowOracle,headOracle);
		for(int i = 0; i < oracleMatrix.getColumnModel().getColumnCount();i++) {
			oracleMatrix.getColumnModel().getColumn(i).setCellRenderer(new StatusColumnCellRenderer());
		}
		contentPane.add(oracleMatrix);
	}

	public void updateTable(String[] head, Object[][] row) {
		// Header
		/*for(int i = 0; i < head.length;i++) {
			this.table.setValueAt(head[i], 0, i);
		}*/

		for(int i = 0; i < row.length;i++) {
			for(int j =0; j < row[i].length;j++) {
				this.table.setValueAt(row[i][j], i, j);
			}
		}
	}

	public void updateOracleMatrix(String[] head, Object[][] row) {
		if(this.oracleMatrix == null) {
			this.oracleMatrix = new JTable(row, head);
		}
		else {

			/*for(int i = 0; i < head.length;i++) {
				this.oracleMatrix.setValueAt(head[i], 0, i);
			}*/

			for(int i = 0; i < row.length;i++) {
				for(int j =0; j < row[i].length;j++) {
					this.oracleMatrix.setValueAt(row[i][j], i, j);
				}
			}
		}
	}
	public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

			//Cells are by default rendered as a JLabel.
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			//Get the status for the current row.
			TableModel tableModel = (TableModel) table.getModel();
			if(value != null) {
				if (value.equals(1.0)) {
					l.setBackground(Color.GREEN);
				}
				else {
					l.setBackground(Color.WHITE);
				}
			}
			//Return the JLabel which renders the cell.
			return l;

		}
	}
}

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import fr.irit.smac.learningdata.Agents.Configuration;
import fr.irit.smac.learningdata.ui.Matrix.StatusColumnCellRenderer;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JTable;

public class Historic extends JFrame {

	private JPanel contentPane;
	private JTable table;
	
	private int nbLine;
	
	private DefaultTableModel model;

	

	/**
	 * Create the frame.
	 */
	public Historic() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 734, 672);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 1, 0, 0));
		this.nbLine = 0;
		table = new JTable(new DefaultTableModel());
		contentPane.add(table);
		this.model = (DefaultTableModel) table.getModel();
		TreeMap<Integer,Double> mapi = new TreeMap<Integer,Double>();
	}

	public void addCycle(int cycle, String value,TreeMap<Integer,Double> line, double feedback2) {
		int modCycle = cycle-2;
		List<Object> first = new ArrayList<Object>();
		if(this.model.getRowCount() == 0) {
			this.model.addRow(first.toArray());
			this.model.addColumn(""+modCycle);
		}
		this.model.addColumn(""+(modCycle+1));
		for(int k = 0; k < this.model.getColumnCount();k++) {
			this.table.getColumnModel().getColumn(k).setCellRenderer(new StatusColumnCellRenderer());
		}
		List<Object> row = new ArrayList<Object>();
		row.add(""+modCycle);
		int ind = 1;
		this.model.setValueAt("", 0,0 );
		for(Integer i : line.keySet()) {
			first.add(i);
			this.model.setValueAt(""+i, 0,i+1 );
			row.add(ind,""+line.get(i));
			ind++;
		}
		this.model.setValueAt(""+modCycle, 0,modCycle+1 );
		row.add(value);
		this.model.addRow(row.toArray());
		this.model.setValueAt(value, modCycle+1, modCycle+1);

	}
	
	public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

			//Cells are by default rendered as a JLabel.
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			//Get the status for the current row.
			TableModel tableModel = (TableModel) table.getModel();
			if(value != null) {
				if (((String)value).contains("G")) {
					l.setBackground(Color.GREEN);
				}
				else
				if (((String)value).contains("M")) {
					l.setBackground(Color.RED);
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

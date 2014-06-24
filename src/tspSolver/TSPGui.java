package tspSolver;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;


public class TSPGui extends JFrame {

	private JPanel contentPane;
	private JTextArea jtaLog;
	private JButton btnOpen;
	private JFileChooser fc;
	private BruteForceTSPSolver bfSolver = new BruteForceTSPSolver();
	private NearestNeighborTSPSolver gSolver = new NearestNeighborTSPSolver();
	//private DynamicProgrammingSolver dpSolver = new DynamicProgrammingSolver();
	private String selectedAlgo = "";
	private JComboBox jcbSolverSelect;
	
	private File tspFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TSPGui frame = new TSPGui();
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
	public TSPGui() {
		setTitle("Traveling Salesman Problem Solver - NOW WITH MULTIPLE ALGOS!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 354);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(2, 1, 0, 0));
		
		//Create a file chooser
        fc = new JFileChooser();
		
		jcbSolverSelect = new JComboBox();
		jcbSolverSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedAlgo = (String) jcbSolverSelect.getSelectedItem();
			}
		});
		jcbSolverSelect.setModel(new DefaultComboBoxModel(new String[] {"Please Select an Algorithm to Use", "Brute Force Algorithm", "Greedy Algorithm (Nearest Neighbor)", "Dynamic Programming Solver"}));
		panel.add(jcbSolverSelect);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		btnOpen = new JButton("Open TSPLIB file");
		panel_1.add(btnOpen);
		
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//open a tsplib file and display its contents in the log window
		        if ((e.getSource() == btnOpen) && (!(selectedAlgo.equals("")))) {
		            int returnVal = fc.showOpenDialog(TSPGui.this);
		 
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                tspFile = fc.getSelectedFile();
		                
		                //This is where we would open the file.
		                jtaLog.append("Opening: " + tspFile.getName() + "......\n\n");
		                
		                //parse new file into array and display contents here
		                String fileContents = "";
		                
						if (selectedAlgo.startsWith("Brute")) {
			                fileContents = bfSolver.parseNewFile(tspFile);
						}
						else if (selectedAlgo.startsWith("Greedy")) {
			                fileContents = gSolver.parseNewFile(tspFile);
						}
						//else if (selectedAlgo.startsWigh("Dynamic")) {
							//fileContents = dpSolver.parseNewFile(tspFile);
						//}
          
		                jtaLog.append(fileContents);
		            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
		                jtaLog.append("Open command cancelled by user.\n\n");
		            } 
		            jtaLog.setCaretPosition(jtaLog.getDocument().getLength());
				}
		        else {
					jtaLog.append("Please choose an algorithm from the drop down box first and then open.\n\n");
				}

			}
		});
		
		JButton btnCompute = new JButton("Compute Tour w/Smallest Cost");
		panel_1.add(btnCompute);
		btnCompute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//run algorithm on file last opened,display output in stats window, & save to file
				jtaLog.append("\nAttempting to compute tour with smallest cost...\n\n");
				if (tspFile != null){
					if (selectedAlgo.startsWith("Brute")) {
						jtaLog.append(bfSolver.solve());
						try {
							jtaLog.append(bfSolver.sendFinalDataForWrite(tspFile));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					else if (selectedAlgo.startsWith("Greedy")) {
						jtaLog.append(gSolver.solve());
						try {
							jtaLog.append(gSolver.sendFinalDataForWrite(tspFile));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					//else if (selectedAlgo.startsWith("Dynamic")) {
						//jtaLog.append(dpSolver.solve());
						//try {
							//jtaLog.append(dpSolver.sendFinalDataForWrite(tspFile));
						//} catch (IOException e1) {
							//e1.printStackTrace();
						//}
					//}
					else {
						jtaLog.append("Please choose an algorithm from the drop down box.");
					}
				}
				else {
					jtaLog.append("FILE DOES NOT EXIST! SELECT A NEW FILE!\n\n");
				}
			}
		});
		
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		jtaLog = new JTextArea();
		jtaLog.setWrapStyleWord(true);
		jtaLog.setLineWrap(true);
		jtaLog.setText("Welcome! Select an algorithm from the drop down box to use in computing the solution.\n"
				+ "Click the Open button to select a TSPLIB file.\n"
				+ "Click the Compute button to generate permutations and compute the tour with the smallest cost.\n"
				+ "Click the Clear button to clear the contents of this window.\n\n");
		scrollPane.setViewportView(jtaLog);
		
		JButton btnClearLog = new JButton("Clear Log");
		btnClearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jtaLog.setText("");
			}
		});
		contentPane.add(btnClearLog, BorderLayout.SOUTH);
		

        
	}

}

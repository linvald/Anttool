package dk.linvald.anttool;
import javax.swing.*;

import java.awt.*;

import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.security.*;

import org.apache.tools.ant.*;

import java.util.ArrayList;


public class Main extends JFrame {

  private AntModel _antModel = null;
  private String _buildFile;
  private Project _project;
  private ArrayList _targets;
  private Properties  _propertyFileProperties;
  private Hashtable _inlineProperties;

  private XmlHandler _xmlHandler;
  private Properties _properties;
  public boolean _verbose = false;
  private int[]_debugLevels= new int[]{1,2,3,4};

  JLabel jLabel1 = new JLabel();
  JTextField tBuildFile = new JTextField();
  JButton bBrowseBuild = new JButton();
  JLabel jLabel2 = new JLabel();
  JComboBox comboProperties = new JComboBox();
  JTextField tPropertyValue = new JTextField();
  JButton bSetProp = new JButton();
  JLabel jLabel5 = new JLabel();
  JComboBox comboTargets = new JComboBox();
  JButton bExecuteTargets = new JButton();
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea jTextArea1 = new JTextArea();
  File buildFile = null;
  PrintStream aPrintStream;


  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JComboBox comboPropertyFileProperties = new JComboBox();
  JTextField tPropertyFileProperty = new JTextField();
 // JButton bSetInlineProperty = new JButton();
  JComboBox comboDebugLevels = new JComboBox();
  JLabel jLabel6 = new JLabel();
  JButton bShowClassPath = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public Main() {
		  this.setUpStream();
	  try {
		jbInit();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  }
	  catch (Exception e) {
		e.printStackTrace();
	  }
	}


  public static void main(String[] args) {
	Main frame1 = new Main();
	frame1.pack();
	frame1.show();
  }

public void intializeDebugLevelDropDown(){
	this.comboDebugLevels.addItem("Regular");
	this.comboDebugLevels.addItem("Regular+");
	this.comboDebugLevels.addItem("Regular++");
	this.comboDebugLevels.addItem("Verbose");
	//this.comboDebugLevels.setSelectedIndex(0);
}



  public void setStatus(String s) {
  	if(s != null && s != "")
		this.jTextArea1.append(s+"\n");
  }

	public void setUpStream() {
	  aPrintStream =
		  new PrintStream(
		  new DirectedStream(
		  new ByteArrayOutputStream(), this.jTextArea1));
	  // RuntimePermission per = new RuntimePermission("setIO");
	  AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		  System.setOut(aPrintStream);
		  System.setErr(aPrintStream);
		  return null; // nothing to return
		}
	  });
	}

  public void addAntProperties(Hashtable table){
	this.comboProperties.removeAllItems();
	 for (Enumeration en = table.keys(); en.hasMoreElements(); ) {
	   String key = (String) en.nextElement();
	   this.comboProperties.addItem(key);
	 }
  }
public void addAntFileProperties(Hashtable table){
  this.comboProperties.removeAllItems();
   for (Enumeration en = table.keys(); en.hasMoreElements(); ) {
	 String key = (String) en.nextElement();
	 this.comboProperties.addItem(key);
   }
}
 public void addAntTargets(Hashtable table){
   this.comboTargets.removeAllItems();
	for (Enumeration en = table.keys(); en.hasMoreElements(); ) {
	  String key = (String) en.nextElement();
	  this.comboProperties.addItem(key);
	}
 }


  void bBrowseBuild_actionPerformed(ActionEvent e) {
	JFileChooser fc = new JFileChooser();
	int choice = fc.showDialog(this, "Select Ant build file");
	if (choice == JFileChooser.APPROVE_OPTION) {
	  File file = fc.getSelectedFile();
	  _antModel = new AntModel(file,this.jTextArea1);
	  this.tBuildFile.setText(this._antModel.get_buildFile().getAbsolutePath());
	  this.setStatus("Ant build file:" + file.toString());
	}

	this._targets = this._antModel.getTargetsList();
	this.populateDropDown(this.comboTargets, this._targets);

	this._propertyFileProperties = this._antModel.getAntFileProperties();
	if(this._propertyFileProperties.size()>0){
	  for (Enumeration en = _propertyFileProperties.keys(); en.hasMoreElements(); ) {
		String key = (String) en.nextElement();
		this.comboPropertyFileProperties.addItem(key);
	  }
	}
	else{
	  this.comboPropertyFileProperties.addItem("No propertyfile found");
	}


		this._inlineProperties =  this._antModel.getInlineProperties();
				if(this._inlineProperties.size()>0){
				  for (Enumeration en = _inlineProperties.keys(); en.hasMoreElements(); ) {
						String key = (String) en.nextElement();
						this.comboProperties.addItem(key);
				  }
				}
				else{
				  this.comboPropertyFileProperties.addItem("No propertyfile found");
				}



		intializeDebugLevelDropDown();

  }



  public void populateDropDown(JComboBox combo, ArrayList items){
	for(int i = 0 ; i<items.size(); i++){
	  combo.addItem(items.get(i));
	}
  }

  void bSetProp_actionPerformed(ActionEvent e) {
	_project.setProperty(this.comboProperties.getSelectedItem().toString(),
						 this.tPropertyValue.getText());
	this._antModel.writeNewPropertyValueToPropertyFile(this.comboPropertyFileProperties.getSelectedItem().toString(),this.tPropertyFileProperty.getText());
  }



  void bExecuteTargets_actionPerformed(ActionEvent e) {
		//this._antModel.executeTarget(this.comboTargets.getSelectedItem().toString());
		ExeThread t = new ExeThread();
		t.start();
  }
  class ExeThread extends Thread{
  	public void run(){
		_antModel.executeTarget(comboTargets.getSelectedItem().toString());
  	}
  }

  void tBuildFile_actionPerformed(ActionEvent e) {

  }

  void comboDebugLevels_actionPerformed(ActionEvent e) {
	this._antModel.setDebugLevel(this.comboDebugLevels.getSelectedIndex()+1);
  }

  void comboPropertyFileProperties_actionPerformed(ActionEvent e) {
	this.tPropertyFileProperty.setText(this._propertyFileProperties.get(this.comboPropertyFileProperties.getSelectedItem()).toString());
  }

  void comboProperties_actionPerformed(ActionEvent e) {
		this.tPropertyValue.setText(this._inlineProperties.get(this.comboProperties.getSelectedItem()).toString());
  }

  private void jbInit() throws Exception {
   jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));

   jLabel1.setText("Build file");

   this.setLocale(java.util.Locale.getDefault());
   this.setTitle("Ant gui");
   this.getContentPane().setLayout(gridBagLayout1);
   bBrowseBuild.setText("Browse");
   bBrowseBuild.addActionListener(new Frame1_bBrowseBuild_actionAdapter(this));
   jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));

   jLabel2.setText("Inline properties");
   bSetProp.setText("Set");
   bSetProp.addActionListener(new Frame1_bSetProp_actionAdapter(this));
   jLabel5.setFont(new java.awt.Font("Dialog", 1, 11));

   jLabel5.setText("Targets");

   bExecuteTargets.setText("Execute");
   bExecuteTargets.addActionListener(new Frame1_bExecuteTargets_actionAdapter(this));
   tBuildFile.setText("");
   tBuildFile.addActionListener(new Main_tBuildFile_actionAdapter(this));
   comboProperties.addActionListener(new Frame1_comboProperties_actionAdapter(this));
   jPanel1.setLayout(new BorderLayout());
   jTextArea1.setText("");

   jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
   this.jScrollPane1.setAutoscrolls(true);
   jLabel3.setToolTipText(
	   "You can change the properties of the property file!");
   jLabel3.setText("TIP");
   comboProperties.addActionListener(new ActionListener(this));
   jLabel4.setFont(new java.awt.Font("Dialog", 1, 11));
   jLabel4.setText("Property file properties");
   tPropertyFileProperty.setText("");
   //bSetInlineProperty.setText("Set");
   jLabel6.setFont(new java.awt.Font("Dialog", 1, 11));
   jLabel6.setText("Debug level");
   comboDebugLevels.addActionListener(new Main_comboDebugLevels_actionAdapter(this));
   comboPropertyFileProperties.addActionListener(new Main_comboPropertyFileProperties_actionAdapter(this));
   bShowClassPath.setText("Show project classpath");
	bShowClassPath.addActionListener(new Main_bShowClassPath_actionAdapter(this));
	this.getContentPane().add(jPanel1,  new GridBagConstraints(0, 5, 5, 1, 1.0, 1.0
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 2, 0, 9), 766, 481));
   jPanel1.add(jScrollPane1, null);
   this.getContentPane().add(bBrowseBuild,  new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 13, 0, 9), 31, -3));
   this.getContentPane().add(tBuildFile,  new GridBagConstraints(2, 0, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(6, 0, 0, 0), 496, 0));
   this.getContentPane().add(tPropertyValue,  new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 309, 0));
   jScrollPane1.getViewport().add(jTextArea1, null);
   this.getContentPane().add(comboPropertyFileProperties,  new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 0, 0, 0), 161, 0));
   this.getContentPane().add(tPropertyFileProperty,  new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 6, 0, 0), 303, 0));
   this.getContentPane().add(bSetProp,  new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 11, 0, 9), 55, -3));
//   this.getContentPane().add(bSetInlineProperty,  new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
//			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8, 12, 0, 9), 53, -5));
   this.getContentPane().add(jLabel6,  new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 3, 38), 47, 0));
	this.getContentPane().add(comboProperties,  new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 161, 0));
	this.getContentPane().add(bExecuteTargets,  new GridBagConstraints(3, 3, 2, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(11, 0, 0, 52), 315, -1));
	this.getContentPane().add(comboTargets,  new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(12, 24, 0, 17), 199, 1));
	this.getContentPane().add(jLabel3,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(11, 2, 0, 36), 19, 3));
	this.getContentPane().add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 2, 0, 0), 25, 0));
	this.getContentPane().add(jLabel2,  new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(9, 2, 0, 30), 27, 3));
	this.getContentPane().add(jLabel5,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15, 2, 0, 14), 15, 4));
	this.getContentPane().add(jLabel4,  new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(17, 2, 0, 0), 19, 2));
	this.getContentPane().add(comboDebugLevels,  new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 12, 3, 133), 95, -4));
	this.getContentPane().add(bShowClassPath,  new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8, 23, 0, 65), 29, -7));

   comboProperties.addActionListener(new java.awt.event.ActionListener() {
	 public void actionPerformed(ActionEvent arg0) {
	   tPropertyValue.setText("");

	 }
   });
 }

  void bShowClassPath_actionPerformed(ActionEvent e) {
	this.setStatus(this._antModel.getProjectClassPath());
  }

}//end Main

class Frame1_bBrowseBuild_actionAdapter
	implements java.awt.event.ActionListener {
  Main adaptee;

  Frame1_bBrowseBuild_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
	adaptee.bBrowseBuild_actionPerformed(e);
  }
}

class Frame1_bSetProp_actionAdapter
	implements java.awt.event.ActionListener {
  Main adaptee;

  Frame1_bSetProp_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
	adaptee.bSetProp_actionPerformed(e);
  }
}

class Frame1_bExecuteTargets_actionAdapter
	implements java.awt.event.ActionListener {
  Main adaptee;

  Frame1_bExecuteTargets_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
	adaptee.bExecuteTargets_actionPerformed(e);
  }
}

class Frame1_comboProperties_actionAdapter
	implements java.awt.event.ActionListener {
  Main adaptee;

  Frame1_comboProperties_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
	adaptee.comboProperties_actionPerformed(e);
  }
}

class DirectedStream
	extends FilterOutputStream {
  JTextArea aTextArea = null;
  public DirectedStream(OutputStream aStream, JTextArea aTextArea) {
	super(aStream);
	this.aTextArea = aTextArea;
  }

  public void write(byte b[]) throws IOException {
	String aString = new String(b);
	aTextArea.append(aString);
  }

  public void write(byte b[], int off, int len) throws IOException {
	String aString = new String(b, off, len);
	aTextArea.append(aString);
  }
}

class ActionListener
	implements java.awt.event.ActionListener {
  Main adaptee;

  ActionListener(Main adaptee) {
	this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
	adaptee.comboProperties_actionPerformed(e);
  }
}

class Main_tBuildFile_actionAdapter
	implements java.awt.event.ActionListener {
  Main adaptee;

  Main_tBuildFile_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
	adaptee.tBuildFile_actionPerformed(e);
  }
}

class Main_comboDebugLevels_actionAdapter implements java.awt.event.ActionListener {
  Main adaptee;

  Main_comboDebugLevels_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
	adaptee.comboDebugLevels_actionPerformed(e);
  }
}

class Main_comboPropertyFileProperties_actionAdapter implements java.awt.event.ActionListener {
  Main adaptee;

  Main_comboPropertyFileProperties_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
	adaptee.comboPropertyFileProperties_actionPerformed(e);
  }
}

class Main_bShowClassPath_actionAdapter implements java.awt.event.ActionListener {
  Main adaptee;

  Main_bShowClassPath_actionAdapter(Main adaptee) {
	this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
	adaptee.bShowClassPath_actionPerformed(e);
  }
}

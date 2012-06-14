package dk.linvald.anttool;
import org.apache.tools.ant.*;
import org.apache.tools.ant.input.PropertyFileInputHandler;


import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.swing.*;
import java.util.ArrayList;


public class AntModel {

  //this is where the model gets hard coupled with the gui


  private File _buildFile;
  private Project _project;
  private dk.linvald.anttool.XmlHandler _xmlHandler = null;
  private Hashtable _properties;
  public boolean _verbose = false;
  private ArrayList _antFilePropertiesList, _targetsList, _propertyFilePropertiesList;
  private Properties _antFileProperties, _targets, _propertyFileProperties;
  private String _pathToPropertyFile = "";
  private JTextArea _statusText=null;
  public int _debugLevel;
  private MyBuildEventListener antListener = null;


  public AntModel(File buildFile, JTextArea statusText) {
	this._statusText = statusText;
    this._debugLevel = DebugLevels.REGULAR;
    this.set_buildFile(buildFile);
  }

  public void loadProject() {
     _project = new Project();
	 antListener = new MyBuildEventListener( this);
     _project.setInputHandler(new PropertyFileInputHandler());
     _project.addBuildListener(this.antListener);
	
	_project.init();
	ProjectHelper.configureProject(_project, this.get_buildFile());
   }

   public String findAndLoadPropertyFile(){
     _pathToPropertyFile = this._xmlHandler.getAttributeInTag("property","file");
     File file = new File(_pathToPropertyFile);

     if(file.exists()){
		return _pathToPropertyFile;
     }
     else{
		String[] splitted = _pathToPropertyFile.split("/");
		String newString = "";
		for (int i = 0; i < splitted.length; i++) {
			newString+=File.separatorChar + splitted[i];
		}
		newString+= File.separatorChar;
     	String pre = this._project.getBaseDir().toString();
     	String all = pre + newString;
     	return all;
     }
     //prepend project dir
    // return _pathToPropertyFile;
   }

   /**
    * @return
    */
   public File get_buildFile() {
     return _buildFile;
   }
   public void setStatus(String status){
   
     this._statusText.append( status + "\n");
   }

   /**
    * @param file
    */
   public void set_buildFile(File file) {
     try {
       this._buildFile = file;
	   this.loadProject();
       this._xmlHandler = new XmlHandler(file.getAbsolutePath());
      
       this.loadAntPropertyFile();
     }
     catch (RuntimeException e) {
       e.printStackTrace();
     }
   }
   public String getDebugLevel(int level){
        switch(level){
          case 1:
            return "Regular";
           case 2:
             return "Regular plus";
             case 3 :
               return "Regular plus plus";
               case 4:
                 return "Strict verbose";
        }
        return null;
      }

   public void setDebugLevel(int level){
     this._debugLevel = level;
     this.setStatus("Sat debug level to " + getDebugLevel(level) );
   }
   
   public int getDebugLevel(){
     return this._debugLevel;
   }

   public void loadAntPropertyFile() {
     _antFileProperties = new Properties();
     try {
       _antFileProperties.load(new FileInputStream(this.findAndLoadPropertyFile()));
     }
     catch (FileNotFoundException e1) {
       e1.printStackTrace();
     }
     catch (IOException e1) {
       e1.printStackTrace();
     }
   }

   public Properties getAntFileProperties() {
     return this._antFileProperties;
   }

	public Hashtable getInlineProperties(){
		this._properties =  this._project.getProperties();
		return _properties;
	}

  public String getAntFilePropertyValue(String key) {
      for (Enumeration en = this.getAntFileProperties().keys(); en.hasMoreElements(); ) {
        String curKey = (String) en.nextElement();
        if (curKey.equals(key)) {
          return this.getAntFileProperties().getProperty(curKey);
        }
      }
    return null;
  }

  public String getProjectClassPath() {
    StringBuffer buffer = new StringBuffer();
    String path = "";
    Hashtable uProp = _project.getReferences();
    Enumeration keysU = uProp.keys();
    while (keysU.hasMoreElements()) {
      String key = (String) keysU.nextElement();
      if (key.equals("classpath")) {
        path = uProp.get(key).toString();
      }
    }
    String[] all = path.split(";");
    for (int i = 0; i < all.length; i++) {
      buffer.append(all[i] + "\n");
    }
    
    return buffer.toString();
  }

  public String getTasksAsString() {
    StringBuffer buffer = new StringBuffer();
    Hashtable tasks = this._project.getProperties();
    Enumeration enu = tasks.keys();
    while (enu.hasMoreElements()) {
      String element = (String) enu.nextElement();
      buffer.append(element + "- " + tasks.get(element) + "\n");
    }
    return buffer.toString();
  }

  public ArrayList getTasksAsList() {
    ArrayList filled = new ArrayList();
    Hashtable tasks = this._project.getProperties();
    Enumeration enu = tasks.keys();
    while (enu.hasMoreElements()) {
      String element = (String) enu.nextElement();
      filled.add(element);
    }
    return filled;
  }

  public ArrayList getTargetsList() {
  ArrayList filled = new ArrayList();
    this._targets = new Properties();
    Hashtable targs = _project.getTargets();
    Enumeration keys = targs.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      filled.add(key);
    }
    return filled;
  }



  public void executeTarget(String target){
      _project.executeTarget(target);
  }

  public void writeNewPropertyValueToPropertyFile(String propertyKey, String selectedItemChanged) {
    FileWriter writer = null;
    try {
      writer = new FileWriter(this._pathToPropertyFile);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    BufferedWriter buf = new BufferedWriter(writer);
    try {
      this._antFileProperties.load(new FileInputStream(this._pathToPropertyFile));
      for (Enumeration en = _properties.keys(); en.hasMoreElements(); ) {
        String key = (String) en.nextElement();
        if (!key.equals(propertyKey)) {
          buf.write(key + "=" + this.getAntFilePropertyValue(key) + "\n");
        }
      }
      buf.write(propertyKey + "=" + selectedItemChanged);
      buf.close();
    }
    catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  class DebugLevels{
	public final static int LOW = 0;
    public final static int REGULAR = 1;
    public final static int REGULARPLUS = 2;
    public final static int REGULARPLUSPLUS = 3;
    public final static int VERBOSE = 4;
  }
}//end Model
class MyBuildEventListener implements BuildListener {
   private AntModel status = null;
   private AntModel model;
   
   public MyBuildEventListener( AntModel main) {
	 this.status = main;
   }

   /* (non-Javadoc)
	* @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
	*/
   public void buildStarted(BuildEvent arg0) {
	 //main.setStatus(arg0.getMessage());
   }

   /* (non-Javadoc)
	* @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
	*/
   public void buildFinished(BuildEvent arg0) {
	 //main.setStatus(arg0.getMessage());
   }

   /* (non-Javadoc)
	* @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
	*/
   public void targetStarted(BuildEvent arg0) {
	 //main.setStatus(arg0.getMessage());
   }

   /* (non-Javadoc)
	* @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
	*/
   public void targetFinished(BuildEvent arg0) {
	 //main.setStatus(arg0.getMessage());
   }

   /* (non-Javadoc)
	* @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
	*/
   public void taskStarted(BuildEvent arg0) {
	 //main.setStatus(arg0.getMessage());
   }

   /* (non-Javadoc)
	* @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
	*/
   public void taskFinished(BuildEvent arg0) {
	 //	main.setStatus(arg0.getMessage());
   }

   /* (non-Javadoc)
	* @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
	*/
   public void messageLogged(BuildEvent arg0) {
   	//MessageThread thread = new MessageThread(arg0);
   	//thread.start();
   	int chosenLevel = status.getDebugLevel();
	int priority = arg0.getPriority();
	
		 switch (chosenLevel) {
		   case AntModel.DebugLevels.LOW:
		   	if(priority == AntModel.DebugLevels.LOW)
			  status.setStatus(arg0.getMessage());
			 break;
		   case AntModel.DebugLevels.REGULAR:
		   if(priority == AntModel.DebugLevels.REGULAR)
		   status.setStatus(arg0.getMessage());
			 break;
		   case AntModel.DebugLevels.REGULARPLUS:
		   if(priority == AntModel.DebugLevels.REGULARPLUS)
		   status.setStatus(arg0.getMessage());
			 break;
		   case AntModel.DebugLevels.REGULARPLUSPLUS:
		   if(priority == AntModel.DebugLevels.REGULARPLUSPLUS)
		   status.setStatus(arg0.getMessage());
			 break;
		   case AntModel.DebugLevels.VERBOSE:
		   if(priority == AntModel.DebugLevels.VERBOSE)
		   status.setStatus(arg0.getMessage());
			 break;
		   default:
		   status.setStatus(arg0.getMessage());
			 break;
		 }
   }
 }






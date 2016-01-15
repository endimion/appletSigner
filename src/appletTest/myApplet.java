package appletTest;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;



public class myApplet extends JApplet {

	private static final long serialVersionUID = -8027306850890442426L;
	JButton keyStoreButton, jsignButton, signButton, fileButton;
	final JLabel keyPath = new JLabel("τοποθεσία κλειδιού");
	final JLabel jsignLabel = new JLabel("τοποθεσία JPdfSign.jar");
	final JLabel passwordLabel = new JLabel("κωδικός κλειδιού");
	final JLabel fileForSignLabel = new JLabel("αρχείο προς υπογραφή");
	final JPasswordField passField = new JPasswordField(15);
	final JLabel messageLabel = new JLabel(); 
	
	Container cp = getContentPane();
	
	File keyStoreFile, f;
	String jsignPath, password;
	String doc_id;
	
	public void init(){
        //passField.setMaximumSize(new Dimension(300, 20));
        keyStoreFile = null;
        doc_id = getParameter("doc_id");
        //System.out.println(doc_id);
        messageLabel.setText("doc_id" + doc_id);
        
        
        fileButton = new JButton("Αναζήτηση");
        fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
            	int returnVal = fc.showSaveDialog( myApplet.this ); 
            	if ( returnVal == JFileChooser.APPROVE_OPTION )   
            	{	  
            		f = fc.getSelectedFile();
            		fileForSignLabel.setText(f.getAbsolutePath());
            	}
			}//end of actionPerformed
		});;//end of actionListener
        
        
        jsignButton = new JButton("Αναζήτηση");
        jsignButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
            	int returnVal = fc.showSaveDialog( myApplet.this ); 
            	if ( returnVal == JFileChooser.APPROVE_OPTION )   
            	{	  
            		File jsingFile = fc.getSelectedFile();
            		jsignLabel.setText(jsingFile.getParent());
            		jsignPath = jsingFile.getParent();
            	}
			}//end of actionPerformed
		});//end of addActionListener
        
        signButton = new JButton("Υπογραφή & Aποστολή");
        signButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        try {
		        			//File f = new File("/home/nikos/workspace/appletTest/src/appletTest/test.pdf");
		        				password = String.valueOf(passField.getPassword());
		        				String keyStore = keyStoreFile.getAbsolutePath();
		        				Uploader uploader = new Uploader(f, keyStore, password, jsignPath,doc_id);
		        				uploader.upload(new URL("http://localhost:8000/documents/upload/"));
		        				cp.removeAll();
		        				cp.add(new JLabel("Επιτυχής Αποστολή"));
		        				cp.revalidate();
		        				cp.repaint();
		        			
		        } catch (Exception ex) {
		        	messageLabel.setText("Παρακαλώ συμπληρώστε όλα τα πεδία και προσπαθήστε ξανά");
		        	ex.printStackTrace();
		        }//end 0f catch
			}//end of actionPreformed
		});//end of ActionListener
        
        
        keyStoreButton = new JButton("Κλειδί");
        keyStoreButton.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent ae )
            { 
            	JFileChooser fc = new JFileChooser();
            	//fc.setCurrentDirectory( new File( "" ) );
            	
            	int returnVal = fc.showSaveDialog( myApplet.this ); 
            	if ( returnVal == JFileChooser.APPROVE_OPTION )   
            	{	  
            		keyStoreFile = fc.getSelectedFile();
            		keyPath.setText(keyStoreFile.getAbsolutePath());
            	}
            }//end of actionPerformed
          });//end of addActionlistener
        
        
        
        JPanel keyPane = new JPanel(new FlowLayout());
        keyPane.add(keyPath);
        keyPane.add(keyStoreButton);
        
        JPanel jsignPane = new JPanel(new FlowLayout());
        jsignPane.add(jsignLabel);
        jsignPane.add(jsignButton);
        
        JPanel passwordPane  = new JPanel(new FlowLayout());
        passwordPane.add(passwordLabel);
        passwordPane.add(passField);
        
        JPanel signButtonPane = new JPanel(new FlowLayout());
        signButtonPane.add(new JLabel(""));
        signButtonPane.add(signButton);
        
        JPanel fileForSignPane = new JPanel(new FlowLayout());
        fileForSignPane.add(fileForSignLabel);
        fileForSignPane.add(fileButton);
        
        JPanel messagePane = new JPanel(new FlowLayout());
        messagePane.add(messageLabel);
        
        JPanel appletWrapper = new JPanel();
        BoxLayout bxl = new BoxLayout(appletWrapper, BoxLayout.Y_AXIS);
        appletWrapper.setLayout(bxl);
        appletWrapper.add(keyPane);
        appletWrapper.add(jsignPane);
        appletWrapper.add(passwordPane);
        appletWrapper.add(fileForSignPane);
        appletWrapper.add(messagePane);
        appletWrapper.add(signButtonPane);
        
        cp.add(appletWrapper);
	}//end of init
	

	
	
	public void paint(Graphics g){
		//g.drawString(doc_id, 100, 20);
	}//end of paint
	
}

package appletTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;



public class Uploader {
	
	 //private OutputStream outputStream;
	 private PrintWriter writer;
	 private String boundary;
	 private static final String LINE_FEED = "\r\n";
	 private OutputStream outputStream;


 	private final String filePath ; //=  f.getAbsolutePath();
 	private final String keyStore ;//= "/home/nikos/myCA/mycert.pfx";
 	private final String keyStorePass ;//= "panathinaikos";
 	private final String outputPath ; //=  f.getParent();//"/home/nikos/workspace/appletTest/src/appletTest/"; 
 	private final String jsignPath ; //= "/home/nikos/Downloads/jsignpdf-1.6.1";
	private final String doc_id;
	 
	 public Uploader(File f, String keyStore, String keyStorePass, String jsignPath, String doc_id){
		 this.filePath = f.getAbsolutePath();
		 this.keyStore = keyStore;
		 this.keyStorePass= keyStorePass;
		 this.outputPath = f.getParent();
		 this.jsignPath = jsignPath;
		 this.doc_id= doc_id;
	 }//end of constructor

	 
	 public void upload(URL url) throws Exception
    {
    	boundary = "===" + System.currentTimeMillis() + "===";
        
        //URL url = new URL(requestURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty("Test", "Bonjour");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),
                true);
    	
    	//now we have to sign the file
    	File signed_file_to_upload = signFile(filePath, keyStore,keyStorePass, 
    																													outputPath,jsignPath);
    	
    	addFilePart("the_file", signed_file_to_upload);
    	addFormField("doc_id", doc_id);
    	
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        
        // read & parse the response
        InputStream is = httpConn.getInputStream();
        StringBuilder response = new StringBuilder();
        byte[] respBuffer = new byte[4096];
        while (is.read(respBuffer) >= 0)
        {
            response.append(new String(respBuffer).trim());
        }
        is.close();
        System.out.println(response.toString());
    }//end of upload

  



    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
    }//end of addFilePart

    
    /**
     * Adds to the POST request a form filed with the given name and value
     * @param name, the name of the field
     * @param value, its value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }//end of addFormField

    
    
    
    /**
     *  This method digitally signs the given file (from the filePath) using
     *  an external program called JSignPdf 
     * @param filePath, the path to the file we want to sign
     * @param keyStore, the location of the users private key
     * @param keyStorePass, the password for opening the keystore
     * @param outputPath, where to store the signed document
     * @param jsignPath, the path for the directory in which the JSignPdf.jar resides
     * @return
     */
    public File signFile(String filePath, String keyStore, String keyStorePass, String outputPath, String jsignPath){
    	String[] CmdArgs = { "java", 
    			"-jar", "JSignPdf.jar", "-kst","PKCS12", "-ksf", keyStore,
    			 "-ksp", keyStorePass,"-V",
    			 filePath,
    			 "-d", outputPath, "-a"};
    	Process proc;
		try {
			proc = Runtime.getRuntime().exec(CmdArgs, null, new File(jsignPath));
			proc.waitFor();
			
			BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(proc.getInputStream()));

				BufferedReader stdError = new BufferedReader(new 
				     InputStreamReader(proc.getErrorStream()));

				// read the output from the command
				System.out.println("Here is the standard output of the command:\n");
				String s = null;
				while ((s = stdInput.readLine()) != null) {
				    System.out.println(s);
				}

				// read any errors from the attempted command
				System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
				    System.out.println(s);
				}
			    
				String signed_file_path = filePath.split("\\.")[0] + "_signed.pdf";
				System.out.println("Uploader.signFile:: finished Writting!!!" + " " + signed_file_path);
				return new File(signed_file_path);
		} catch (Exception e) { e.printStackTrace();}
    	
    	return null;
    }//end of signFile






}
	
	
	
	
	
	
	
	
	
	



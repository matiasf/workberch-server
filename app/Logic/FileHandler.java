package Logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import play.Logger;

public class FileHandler {

	
	public static String ReadProperty(String strKey){
    	Properties prop = new Properties();
    	InputStream input = null;
        String propertyValues = "";
    	try {
     
    		input = new FileInputStream("./config.properties");
     
    		prop.load(input);
    		propertyValues = prop.getProperty(strKey);
    		//Logger.error(propertyValues);
    	} catch (IOException io) {
    		Logger.error(io.toString());
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				Logger.error(e.toString());
    				
    			}
    		}
     
    	}
    	return propertyValues;
    }
	
	public static String GetStringFromDocument(Document dom) throws TransformerException{
		//set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		
		//create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(dom);
		trans.transform(source, result);
		String xmlString = sw.toString();
		return xmlString;
	}
	
	public static void delete(File f) throws IOException {
	  if (f.isDirectory()) {
	    for (File c : f.listFiles())
	      delete(c);
	  }
	  if (!f.delete())
	    throw new FileNotFoundException("Failed to delete file: " + f);
	}
	
	public static boolean CreateDirectory(String directoryName,boolean deleteFolder) throws IOException{
		File theDir = new File(directoryName);
		boolean result = false;
		if (theDir.exists() && deleteFolder){
			delete(theDir);
		}
		if (!theDir.exists()) {			
	        theDir.mkdir();
	        result = true;
		}else{
			result = false;
		}
		return result;
	}
	
	public static boolean ExistesDirectory(String directoryName){
		File theDir = new File(directoryName);
		return theDir.exists();
	}
	
	public static boolean IsEmptyFolder(String directoryName){
		File theDir = new File(directoryName);
		return ! (theDir.isDirectory() && theDir.list() != null && theDir.list().length > 0);
	}
		
	public static boolean ExistesFile(String files,String directoryName, String filename){
		File theDir = new File(files+directoryName+"/"+filename);
		return theDir.exists();
	}
	
	public static boolean CreateFile(String directoryName, String fileName, String content) throws IOException{
		
		File file = new File(directoryName+"/"+fileName);
	    FileWriter fw = new FileWriter(file.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write(content);
	    bw.close();
		return true;
	}
	

    public static String GetWorkfowOutputFiles(String directoryName){
    	String responseBegin = "<t2sr:directoryContents xmlns:xlink=\"http://www.w3.org/1999/xlink"+
        "xmlns:t2s=\"http://ns.taverna.org.uk/2010/xml/server/"+
        "xmlns:t2sr=\"http://ns.taverna.org.uk/2010/xml/server/rest/\">";
    
    	String responseEnd = "</t2sr:directoryContents>";
    
    	String responseMiddle = "";//"<t2s:file xlink:href=\"<RUN_URI>/wd/out/FOO.OUT\">out/FOO.OUT</t2s:file>";
    	File folder = new File(directoryName);
    	File[] listOfFiles = folder.listFiles();

    	for (File file : listOfFiles) {
    	    if (file.isFile()) {
    	    	//Logger.debug(file.getName());
    	    	responseMiddle += "<t2s:file xlink:href=\"<RUN_URI>/wd/out/"+file.getName()+"\">out/"+file.getName()+"</t2s:file>";
    	    }
    	}
    	 
    	return responseBegin + responseMiddle + responseEnd;
    }
    
    public static String GetWorkfowOutputFile(String directoryName, String idPart ) throws IOException{    	    
    	String responseMiddle = "";
    	BufferedReader br = null;
		String sCurrentLine;
 
		br = new BufferedReader(new FileReader(directoryName+"/"+idPart));
 
		while ((sCurrentLine = br.readLine()) != null) {
			responseMiddle += sCurrentLine;
		}
		if (br != null)br.close();   	 
    	return responseMiddle;
    }
}

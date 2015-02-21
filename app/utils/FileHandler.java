package utils;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;

import play.Logger;

import com.google.common.base.Throwables;

import redis.clients.jedis.Jedis;

public class FileHandler {

	public static String ReadProperty(final String strKey) {
		final Properties prop = new Properties();
		InputStream input = null;
		String propertyValues = StringUtils.EMPTY;
		try {
			input = new FileInputStream("./config.properties");

			prop.load(input);
			propertyValues = prop.getProperty(strKey);
		} catch (final IOException io) {
			Throwables.propagate(io);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (final IOException e) {
					Logger.error(e.toString());

				}
			}
		}
		return propertyValues;
	}

	public static String GetStringFromDocument(final Document dom) throws TransformerException {
		final TransformerFactory transfac = TransformerFactory.newInstance();
		final Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		final StringWriter sw = new StringWriter();
		final StreamResult result = new StreamResult(sw);
		final DOMSource source = new DOMSource(dom);
		trans.transform(source, result);
		final String xmlString = sw.toString();
		return xmlString;
	}

	public static void delete(final File f) throws IOException {
		if (f.isDirectory()) {
			for (final File c : f.listFiles()) {
				delete(c);
			}
		}
		if (!f.delete()) {
			throw new FileNotFoundException("Failed to delete file: " + f);
		}
	}

	public static boolean CreateDirectory(final String directoryName, final boolean deleteFolder) throws IOException {
		final File theDir = new File(directoryName);
		boolean result = false;
		if (theDir.exists() && deleteFolder) {
			delete(theDir);
		}
		if (!theDir.exists()) {
			theDir.mkdir();
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public static boolean ExistesDirectory(final String directoryName) {
		final File theDir = new File(directoryName);
		return theDir.exists();
	}

	public static boolean IsEmptyFolder(final String directoryName) {
		final File theDir = new File(directoryName);
		return !(theDir.isDirectory() && theDir.list() != null && theDir.list().length > 0);
	}

	public static boolean ExistesFile(final String files, final String directoryName, final String filename) {
		final File theDir = new File(files + directoryName + "/" + filename);
		return theDir.exists();
	}

	public static boolean CreateFile(final String directoryName, final String fileName, final String content) throws IOException {

		final File file = new File(directoryName + "/" + fileName);
		final FileWriter fw = new FileWriter(file.getAbsoluteFile());
		final BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		return true;
	}

	public static String GetWorkfowOutputFiles(final String directoryName) {
		final String responseBegin = "<t2sr:directoryContents xmlns:xlink=\"http://www.w3.org/1999/xlink\""
				+ " xmlns:t2s=\"http://ns.taverna.org.uk/2010/xml/server/\"" + " xmlns:t2sr=\"http://ns.taverna.org.uk/2010/xml/server/rest/\">";

		final String responseEnd = "</t2sr:directoryContents>";

		String responseMiddle = StringUtils.EMPTY;
		final File folder = new File(directoryName);
		final File[] listOfFiles = folder.listFiles();

		for (final File file : listOfFiles) {
			if (file.isFile()) {				
				//responseMiddle += "<t2s:file xlink:href=\"<RUN_URI>/wd/out/" + file.getName() + "\">out/" + file.getName() + "</t2s:file>";
				responseMiddle += "<t2s:file xlink:href=\"/wd/out/" + file.getName() + "\">" + file.getName() + "</t2s:file>";
			}
		}

		return responseBegin + responseMiddle + responseEnd;
	}

	public static String GetWorkfowOutputFile(final String directoryName, final String idPart) throws IOException {
		String responseMiddle = StringUtils.EMPTY;
		BufferedReader br = null;
		String sCurrentLine;

		br = new BufferedReader(new FileReader(directoryName + "/" + idPart));

		while ((sCurrentLine = br.readLine()) != null) {
			responseMiddle += sCurrentLine;
		}
		if (br != null) {
			br.close();
		}
		return responseMiddle;
	}
	
	public static String GetWorkfowIds(final String directoryName, Jedis jedis) {
		final String responseBegin = "<t2sr:runList>";

		final String responseEnd = "</t2sr:runList>";

		String responseMiddle = StringUtils.EMPTY;
		final File folder = new File(directoryName);
		final File[] listOfFiles = folder.listFiles();

		for (final File file : listOfFiles) {
			if (file.isDirectory() && NumberUtils.toLong(jedis.get(file.getName() + "_outputs"), -1L) != -1L) {				
				responseMiddle += "<t2sr:run xlink:href=\"/runs/" + file.getName() + "\"/>";
			}
		}

		return responseBegin + responseMiddle + responseEnd;
	}
}

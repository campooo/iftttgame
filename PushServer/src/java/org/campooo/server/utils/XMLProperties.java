/*
 * a xml parser
 *
 * @author ckb
 * 
 * @date 2015年11月10日 上午12:12:15
 */
package org.campooo.server.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLProperties {

	private static final Logger Log = Logger.getLogger(XMLProperties.class);

	private Map<String, String> propertyCache = new HashMap<String, String>();

	private Document document;

	private File file;

	private static final String ROOT_ELEMENT = "server";

	public XMLProperties(File file) throws Exception {
		if (!file.exists()) {
			File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
			if (tempFile.exists()) {
				tempFile.renameTo(file);
			} else {
				throw new FileNotFoundException("XMLProperties file not exists " + file.getName());
			}
		}
		if (!file.canRead()) {
			System.err.println("file must be readable!");
		}
		if (!file.canWrite()) {
			System.err.println("file must be writable!");
		}
		this.file = file;
		FileReader reader = new FileReader(file);
		buildDocument(reader);
	}

	public XMLProperties(String fileName) throws Exception {
		this(new File(fileName));
	}

	public XMLProperties(InputStream inStream) throws Exception {
		InputStreamReader reader = new InputStreamReader(inStream, "utf-8");
		buildDocument(reader);
	}

	public synchronized String getProperty(String name) {

		String value = propertyCache.get(name);
		if (value != null) {
			return value;
		}

		String[] propName = parseName(name);
		Element element = document.getRootElement();
		for (String aPropName : propName) {
			element = element.element(aPropName);
			if (element == null) {
				return null;
			}
		}
		value = element.getTextTrim();
		if ("".equals(value)) {
			return null;
		}
		propertyCache.put(name, value);
		return value;
	}

	/**
	 * <pre>
	 * &lt;foo&gt;
	 *     &lt;bar&gt;
	 *         &lt;prop&gt;some value&lt;/prop&gt;
	 *         &lt;prop&gt;other value&lt;/prop&gt;
	 *         &lt;prop&gt;last value&lt;/prop&gt;
	 *     &lt;/bar&gt;
	 * &lt;/foo&gt;
	 * </pre>
	 * 
	 * getProperties("foo.bar.prop") will return
	 * 
	 * {"some value", "other value", "last value"}
	 * 
	 * @param name
	 * @return
	 */
	public String[] getProperties(String name) {
		String[] propName = parseName(name);

		Element element = document.getRootElement();
		int i = 0;
		for (; i < propName.length - 1; i++) {
			element = element.element(propName[i]);
			if (element == null) {
				return new String[] {};
			}
		}
		Iterator<?> it = element.elementIterator(propName[i]);
		List<String> props = new ArrayList<String>();
		String value;
		while (it.hasNext()) {
			value = ((Element) it.next()).getTextTrim();
			if ("".equals(value)) {
				continue;
			}
			props.add(value);
		}
		return props.toArray(new String[props.size()]);
	}

	public Iterator<?> getChildProperties(String name) {
		String[] propName = parseName(name);

		Element element = document.getRootElement();
		int i = 0;
		for (; i < propName.length; i++) {
			element = element.element(propName[i]);
			if (element == null) {
				return Collections.EMPTY_LIST.iterator();
			}
		}
		Iterator<?> it = element.elementIterator(propName[i]);
		ArrayList<String> props = new ArrayList<String>();
		while (it.hasNext()) {
			props.add(((Element) it.next()).getText());
		}
		return props.iterator();
	}

	public String getAttribute(String name, String attribute) {
		if (name == null || attribute == null) {
			return null;
		}
		String[] propName = parseName(name);

		Element element = document.getRootElement();
		for (String child : propName) {
			element = element.element(child);
			if (element == null) {
				return null;
			}
		}
		if (element != null) {
			return element.attributeValue(attribute);
		}
		return null;
	}

	public String[] getChildrenProperties(String parent) {
		String[] propName = parseName(parent);
		Element element = document.getRootElement();
		for (String aPropName : propName) {
			element = element.element(aPropName);
			if (element == null) {
				return new String[] {};
			}
		}
		List<?> children = element.elements();
		int childCount = children.size();
		String[] childrenNames = new String[childCount];
		for (int i = 0; i < childCount; i++) {
			childrenNames[i] = ((Element) children.get(i)).getName();
		}
		return childrenNames;
	}

	private String[] parseName(String name) {
		List<String> names = new ArrayList<String>(5);
		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		while (tokenizer.hasMoreTokens()) {
			names.add(tokenizer.nextToken());
		}
		if (!names.isEmpty()) {
			if (ROOT_ELEMENT.equals(names.get(0))) {
				names.remove(0);
			}
		}
		return names.toArray(new String[names.size()]);
	}

	/**
	 * @param reader
	 */
	private void buildDocument(Reader reader) {
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("utf-8");
		try {
			document = saxReader.read(reader);
			if (document == null) {
				createDocument();
			}
		} catch (DocumentException e) {
			createDocument();
		}
	}

	private void createDocument() {
		document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		document.addElement("server");
		flushToFile();
	}

	public synchronized void setProperty(String name, String value) {
		if (name == null) {
			return;
		}
		if (value == null) {
			value = "";
		}
		propertyCache.put(name, value);
		saveProperty(name, value);
	}

	private void saveProperty(String name, String value) {
		String[] propName = parseName(name);
		Element element = document.getRootElement();
		for (String aPropName : propName) {
			if (element.element(aPropName) == null) {
				element.addElement(aPropName);
			}
			element = element.element(aPropName);
		}
		element.setText(value);
		flushToFile();
	}

	private void flushToFile() {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(fos, format);
			writer.write(document);
			writer.close();
		} catch (IOException ioe) {
			Log.error("save property error !");
		}
	}
}

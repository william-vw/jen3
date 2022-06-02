package org.apache.jen3.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtils {

	public static File getResourceFromCaller(String relativePath)
			throws IOException, URISyntaxException {

		return Paths.get("src/main/resources/" + relativePath).toFile();
	}

	public static InputStream getStreamFromCaller(String relativePath)
			throws IOException, URISyntaxException {

		return new FileInputStream(getResourceFromCaller(relativePath));
	}

	public static File getResource(Class<?> cls, String relativePath)
			throws IOException, URISyntaxException {

		return getResourcePath(cls, relativePath).toFile();
	}

	public static Path getResourcePath(Class<?> cls, String relativePath)
			throws IOException, URISyntaxException {

		if (!relativePath.startsWith("/"))
			relativePath = "/" + relativePath;

		return Paths.get(cls.getResource(relativePath).toURI());
	}

	public static InputStream getResourceInputStream(Class<?> cls, String relativePath)
			throws IOException, URISyntaxException {

		if (!relativePath.startsWith("/"))
			relativePath = "/" + relativePath;

		return cls.getResourceAsStream(relativePath);
	}

	public static String head(String filePath, int nrLines) throws IOException {
		StringBuffer str = new StringBuffer();

		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = null;

//		while (skip-- > 0)
//			br.readLine();

		while (nrLines-- > 0 && (line = br.readLine()) != null)
			str.append(line).append("\n");

		br.close();

		return str.toString();
	}

	public static String anyLineWith(String filePath, String word) throws IOException {
		StringBuffer str = new StringBuffer();

		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.contains(word))
				str.append(line).append("\n");
		}

		br.close();

		return str.toString();
	}

	public static InputStream getFileStream(String filePath) throws IOException {
		return new FileInputStream(filePath);
	}

	public static String readFile(String filePath) throws IOException {
		InputStream fin = getFileStream(filePath);

		return read(fin);
	}

	public static Object readObject(String filePath) throws Exception {
		InputStream in = new FileInputStream(filePath);

		ObjectInputStream ois = new ObjectInputStream(in);
		Object ret = ois.readObject();

		in.close();

		return ret;
	}

	public static InputStream getURLStream(String url) throws IOException {
		return new URL(url).openStream();
	}

	public static String readUrl(String url) throws IOException {
		InputStream uin = getURLStream(url);

		return read(uin);
	}

	public static String read(InputStream in) throws IOException {
		byte[] b = new byte[1024];
		int len = 0;
		StringBuffer sb = new StringBuffer();
		while ((len = in.read(b)) != -1) {
			sb.append(new String(b, 0, len));
		}
		String str = sb.toString();

		b = null;
		in.close();
		sb.delete(0, sb.length());
		sb = null;

		return str;
	}

	public static void writeFile(String filePath, String contents) throws IOException {
		writeFile(filePath, contents, true);
	}

	public static void writeFile(String filePath, String contents, boolean append)
			throws IOException {

		File f = new File(filePath);
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();

		FileOutputStream out = new FileOutputStream(filePath, append);

		out.write(contents.getBytes());

		out.flush();
		out.close();
	}

	public static void writeFile(String filePath, Object obj) throws IOException {
		FileOutputStream out = new FileOutputStream(filePath, false);

		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(obj);

		out.flush();
		out.close();
	}

	public static void deleteFile(String filePath) {
		new File(filePath).delete();
	}

	public static void deleteContents(String folderPath) throws IOException {
		deleteContents(new File(folderPath));
	}

	public static void deleteFolder(File folder) throws IOException {
		deleteContents(folder);

		folder.delete();
	}

	public static void deleteContents(File folder) throws IOException {
		if (!folder.exists())
			return;

		File[] files = folder.listFiles();

		for (File file : files) {
			if (file.isDirectory())
				deleteContents(file);

			file.delete();
		}
	}

	public static String readFromFile(File file) throws IOException {
		BufferedReader bReader = new BufferedReader(new FileReader(file));

		StringBuffer contents = new StringBuffer();
		String line = null;

		while ((line = bReader.readLine()) != null)
			contents.append(line).append("\n");

		bReader.close();

		return contents.toString();
	}

	public static int readInt(InputStream in) throws IOException {
		byte[] bytes = new byte[4];
		in.read(bytes);

		return byteArrayToInt(bytes);
	}

	public static String readStr(InputStream in) throws IOException {
		int length = readInt(in);

		if (length > 0) {
			byte[] bytes = new byte[length];
			int bytesRead = 0;
			while ((bytesRead += in.read(bytes, bytesRead, length - bytesRead)) < length) {
			}
			return new String(bytes);
		}

		else
			return null;
	}

	public static String readStr(InputStream in, boolean lengthGiven) throws IOException {
		if (!lengthGiven) {

			StringBuffer sb = new StringBuffer();
			byte[] bytes = new byte[4096];
			int bytesRead = in.read(bytes);
			while (bytesRead != -1) {
				sb.append(new String(bytes, 0, bytesRead));
				bytesRead = in.read(bytes);
			}

			return sb.toString();
		} else
			return readStr(in);
	}

	public static float readFloat(InputStream in) throws IOException {
		return Float.valueOf(readStr(in)).floatValue();
	}

	public static double readDouble(InputStream in) throws IOException {
		return Double.valueOf(readStr(in)).doubleValue();
	}

	public static boolean readBool(InputStream in) throws IOException {
		return (in.read() == 1);
	}

	public static void writeToFile(String contents, File file) throws IOException {
		writeToFile(contents, file, false);
	}

	public static void writeToFile(String contents, File file, boolean append) throws IOException {
		if (!file.exists())
			file.createNewFile();

		FileWriter writer = new FileWriter(file, append);

		writer.write(contents);
		writer.close();
	}

	public static void writeToFile(InputStream in, String filePath) throws IOException {
		writeToFile(in, new File(filePath));
	}

	public static void writeToFile(InputStream in, File destFile) throws IOException {
		in = new BufferedInputStream(in);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));

		int BUF_SIZE = 8 * 1024;

		byte[] buf = new byte[BUF_SIZE];
		int len;

		while ((len = in.read(buf, 0, BUF_SIZE)) > 0)
			out.write(buf, 0, len);

		in.close();
		out.close();
	}

	public static void write(OutputStream out, int i) throws IOException {
		out.write(intToByteArray(i));
	}

	public static void write(OutputStream out, String str) throws IOException {
		if (str == null) {
			write(out, 0);
		} else {
			byte[] bytes = str.getBytes();

			write(out, bytes.length);
			out.write(bytes);
		}
	}

	public static void write(OutputStream out, String str, boolean giveLength) throws IOException {
		if (!giveLength) {
			if (str != null)
				out.write(str.getBytes());
		} else {
			write(out, str);
		}
	}

	public static void writeTo(BufferedReader reader, File file, boolean append)
			throws IOException {
		Writer writer = new FileWriter(file, append);

		String line = null;
		while ((line = reader.readLine()) != null)
			writer.write(line + "\n");

		writer.close();
		reader.close();
	}

	public static void write(OutputStream out, float f) throws IOException {
		write(out, String.valueOf(f));
	}

	public static void write(OutputStream out, double d) throws IOException {
		write(out, String.valueOf(d));
	}

	private static byte[] intToByteArray(int nr) {
		byte[] bytes = new byte[4];
		for (int i = 0; i <= 3; i++) {
			int p = (nr >>> (i * 8));
			bytes[i] = (byte) p;
		}

		return bytes;
	}

	private static int byteArrayToInt(byte[] bytes) {
		int nr = 0;
		int rightOp = 0x000000FF;
		for (int i = 0; i <= 3; i++) {
			rightOp = rightOp << (i * 8);
			int p = (bytes[i] << (i * 8)) & rightOp;
			nr += p;
		}

		return nr;
	}

	public static String readFromStream(InputStream in) throws IOException {
		// FileInputStream fin =
		// (FileInputStream) AndroidQuery.context.
		// getResources().openRawResource();

		byte[] b = new byte[1024];
		int len = 0;
		StringBuffer sb = new StringBuffer();
		while ((len = in.read(b)) != -1) {
			sb.append(new String(b, 0, len));
		}
		String fileStr = sb.toString();

		b = null;
		in.close();
		// sb.delete(0, sb.length());
		sb = null;

		return fileStr;
	}

	public static String readString(InputStream in) throws IOException {
		BufferedReader bRead = new BufferedReader(new InputStreamReader(in));
		StringBuffer buffer = new StringBuffer();

		String line = null;
		while ((line = bRead.readLine()) != null)
			buffer.append(line + "\n");

		return buffer.toString();
	}

	public static String readString(Reader reader) throws IOException {
		BufferedReader bRead = new BufferedReader(reader);
		StringBuffer buffer = new StringBuffer();

		String line = null;
		while ((line = bRead.readLine()) != null)
			buffer.append(line + "\n");

		return buffer.toString();
	}
}

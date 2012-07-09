package ciknowmgr.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.dom4j.DocumentException;

public class HtmlUtil {
	public static void main(String[] args) throws DocumentException, IOException{
		String filename = args[0];
		String oldMetadata = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";
		String newMetadata = 
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
				"\t\t<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"NO-CACHE\">\n" + 
				"\t\t<META HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">\n" +
				"\t\t<META HTTP-EQUIV=\"EXPIRES\" CONTENT=\"Mon, 22 Jul 2002 11:12:01 GMT\">\n";
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = reader.readLine();
		while (line != null){
			sb.append(line).append("\n");
			line = reader.readLine();
		}
		reader.close();
		String fileData = sb.toString().replace(oldMetadata, newMetadata);
		PrintWriter writer = new PrintWriter(filename);
		writer.append(fileData);
		writer.close();
	}
		
}

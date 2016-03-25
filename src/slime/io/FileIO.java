package slime.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileIO {

	public static void WriteData(String pathToFile, Object[] dataToPrint) throws IOException
	{
		PrintWriter printWriter = new PrintWriter(new FileWriter(pathToFile));
		for(Object data : dataToPrint)
		{
			printWriter.println(data.toString());
		}
		printWriter.flush();
		printWriter.close();
		
	}
	public static Object[] ReadData(String pathToFile) throws FileNotFoundException, IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile));
		List<String> dataArray = new ArrayList<String>();
		while(bufferedReader.ready())
		{
			dataArray.add(bufferedReader.readLine());
		}
		bufferedReader.close();
		return dataArray.toArray();
	}

}

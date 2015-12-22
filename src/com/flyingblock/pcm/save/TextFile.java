package com.flyingblock.pcm.save;

import java.io.*;
import java.util.ArrayList;

public class TextFile
{	
	private ArrayList<String> text = new ArrayList<>();
	private File file;
	
	public TextFile(String path)
	{
		this(new File(path));
	}
	
	public TextFile(File file)
	{
		this.file = file;
		if(file.exists())
			read();
	}
	
	public void setText(ArrayList<String> text)
	{
		this.text = text;
	}
	
	public ArrayList<String> getText()
	{
		return text;
	}

	public void save()
	{
		save(file.getAbsolutePath());
	}
	
	public void save(String path)
	{
		File file = new File(path);	
		//if(file.exists())
			//file.delete();
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			String write = "";
			for(String line : text)
				write = write.concat(line).concat("\n");
			writer.write(write);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void read()
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null)
				text.add(line);
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

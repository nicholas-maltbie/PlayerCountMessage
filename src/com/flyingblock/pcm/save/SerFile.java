package com.flyingblock.pcm.save;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//ragtime is fun to listen to :)

public class SerFile 
{
	private String path;
	protected File file;
	protected Object obj;
	
        public SerFile(File file)
        {
            this(file.getAbsolutePath());
        }
        
	public SerFile(String path)
	{
		new File(path.substring(0, path.lastIndexOf(File.separatorChar))).mkdirs();
		this.path = path;
		file = new File(path);
	}
	
	public void checkSave(Object o)
	{
		if(!file.exists())
			save(o);
	}
	
	public Object getData()
	{
		return obj;
	}
	
	public void save(Object o)
	{
		try
	    {
	        FileOutputStream fileOut = new FileOutputStream(path);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(o);
	        out.close();
	        fileOut.close();
	        //System.out.print("saved file at " + new File(path).getAbsolutePath());
	     }
		catch(IOException i)
	     {
	    	 i.printStackTrace();
	     }
	}
	
	public void read()
	{
		if(file == null)
			return;
		try(
		      InputStream file = new FileInputStream(path);
		      InputStream buffer = new BufferedInputStream(file);
		      ObjectInputStream input = new ObjectInputStream (buffer);)
		{
		      obj = (Object) input.readObject();
		}
		catch(IOException i)
		{
			i.printStackTrace();
			return;
		}
		catch(ClassNotFoundException c)
		{
			c.printStackTrace();
			return;
		}
	}

}

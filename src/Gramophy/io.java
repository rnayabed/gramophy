package Gramophy;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

public class io {
    public static String readFileRaw(String fileName)
    {
        String toBeReturned = null;
        try
        {
            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            toBeReturned = bf.readLine();
            bf.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return toBeReturned;
    }

    public static String[] readFileArranged(String fileName, String s)
    {
        return readFileRaw(fileName).split(s);
    }

    public static void writeToFile(String content,String fileName)
    {
        try
        {
            BufferedWriter bf = new BufferedWriter(new FileWriter(fileName));
            bf.write(content);
            bf.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void writeToFileRaw(byte[] toWrite, String fileName)
    {
        try
        {
            FileOutputStream fs = new FileOutputStream(fileName);
            fs.write(toWrite);
            fs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static File[] getFilesInFolder(String folderPath)
    {
        File[] raw = new File(folderPath).listFiles();
        return raw;
    }

    public static String returnHTTPRawResponse(String urlPassed) throws Exception
    {
        InputStreamReader r = new InputStreamReader(new URL(urlPassed).openStream());

        StringBuilder tbr = new StringBuilder();

        while(true)
        {
            int c = r.read();
            if(c==-1) break;
            else
            {
                tbr.append((char)c);
            }
        }

        return new String(tbr);
    }

    public static void log(String txt)
    {
        System.out.println(txt);
    }
}

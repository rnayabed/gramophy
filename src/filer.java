import java.nio.file.*;
import java.io.*;

public class filer {
    public static String readFileAsString(String fileName) throws Exception
    {
        String data = "";
        try
        {
            console.pln("Reading Data from the file...");
            data = new String(Files.readAllBytes(Paths.get(fileName)));        
            console.pln(data);
        }
        catch(Exception e)
        {
            console.pln("");
            console.pln("Internal Error Occured!");
            console.pln("");
            e.printStackTrace();
            console.pln("");
            console.pln("VoteIT will now quit");
            //exitError x = new exitError();
            //x.setProblem(e.getStackTrace());
            //x.setVisible(true);
        }

        return data;
    }
    
    public static boolean writeToFile(String fileName, String toBeWritten) throws Exception
    {
        boolean isSuccessful = true;
        try
        {
            console.pln("Initializing FileOutputStream...");
            FileOutputStream o = new FileOutputStream(fileName);
            console.pln("Initializing OutputStreamWriter...");
            OutputStreamWriter p = new OutputStreamWriter(o,"UTF-8");
            console.pln("Initializing BufferedWriter...");
            BufferedWriter q = new BufferedWriter(p);
            console.pln("Writing Data...");
            q.write(toBeWritten);
            console.pln("Closing BufferedWriter...");
            q.close();
            console.pln("Successfully Written!");
            isSuccessful = true;
        }
        catch(Exception e)
        {
            isSuccessful = false;
            console.pln("Unable to init Stream...");
            console.pln("Error Occurred!");
        }
        
        return isSuccessful;
    }
    
    public boolean doesFileExists(String fileNamePassed)
    {
        boolean toBeReturned;
        try
        {
            String tmpData = new String(Files.readAllBytes(Paths.get(fileNamePassed)));
            toBeReturned = true;
        }
        catch(Exception e)
        {
            toBeReturned = false;
        }
        
        return toBeReturned;
    }
}



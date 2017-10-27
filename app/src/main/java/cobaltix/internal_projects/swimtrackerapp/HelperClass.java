package cobaltix.internal_projects.swimtrackerapp;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class HelperClass
{
    private static final String DB_PATH = "cobaltix.internal_projects.swimtrackerapp/databases/";

    private HelperClass(){}

    public static void writeToSD() throws IOException {
        File sd = Environment.getExternalStorageDirectory();


        if (sd.canWrite()) {
            String currentDBPath = "Events.db";
            String backupDBPath = "backupname.db";
            File currentDB = new File(DB_PATH, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

}

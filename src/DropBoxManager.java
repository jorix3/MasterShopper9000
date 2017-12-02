import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.*;

/**
 * DropBoxManager
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.12.02
 * @since       1.8
 */
public class DropBoxManager {
    private final String ACCES_TOKEN = "ATeMJijlPUQAAAAAAAAIW72Kz5qt8Kdb3ldV6YDLwBvSSf6c6Umf6MuLhsbezGNB";
    private DbxClientV2 client;

    /**
     * Instantiates a new Drop box manager.
     */
    public DropBoxManager() {
        DbxRequestConfig config;
        config = DbxRequestConfig.newBuilder("MasterShopper9000/1.0").build();
        client = new DbxClientV2(config, ACCES_TOKEN);


    }

    /**
     * Saves file to dropbox.
     *
     * @param  file  the file to save
     */
    public void save(File file) {
        try (InputStream in = new FileInputStream(file)) {
            String fileName = file.getName();
            client.files().uploadBuilder("/" + fileName).uploadAndFinish(in);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void load(String path) {

    }
}

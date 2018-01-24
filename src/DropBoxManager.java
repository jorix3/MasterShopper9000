import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.*;

/**
 * DropBoxManager controls DropBox connection
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.12.02
 * @since       1.8
 */
public class DropBoxManager {
    private final String ACCESS_TOKEN = "ATeMJijlPUQAAAAAAAAIW72Kz5qt8Kdb"
                                    + "3ldV6YDLwBvSSf6c6Umf6MuLhsbezGNB";
    private DbxClientV2 client;

    /**
     * Instantiates a new DropBox manager with default properties.
     */
    public DropBoxManager() {
        DbxRequestConfig config = DbxRequestConfig
                                    .newBuilder("MasterShopper9000/1.0")
                                    .build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    /**
     * Instantiates a new DropBox manager with custom properties.
     *
     * @param  accessToken  identifies DropBox account to connect to
     */
    public DropBoxManager(String accessToken) {
        DbxRequestConfig config = DbxRequestConfig
                                    .newBuilder("MasterShopper9000/1.0")
                                    .build();
        client = new DbxClientV2(config, accessToken);
    }

    /**
     * Saves file to DropBox.
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

//    public void load(String path) {
//
//    }
}

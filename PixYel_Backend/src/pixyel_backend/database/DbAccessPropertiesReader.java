package pixyel_backend.database;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Yannick
 *
 */
public class DbAccessPropertiesReader {

    /**
     * @return All properties form the db_access.properties
     * @throws Exception
     */
    public static Properties getProperties() throws Exception {

        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + "/db_access.properties")) {
            prop.load(input);
        }

        return prop;
    }

}

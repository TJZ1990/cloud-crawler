package cloudwalk.master.server.comm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by 1333907 on 2/23/16.
 * Read File into String.
 */
public final class StringFileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringFileReader.class);

    public static String read(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            LOGGER.error("File does not exist");
        }
        BufferedReader input = new BufferedReader(new FileReader(file));
        StringBuilder index = new StringBuilder();
        String string;
        while ((string = input.readLine()) != null) {
            index.append(string);
            index.append("\n");
        }
        return index.toString();
    }
}

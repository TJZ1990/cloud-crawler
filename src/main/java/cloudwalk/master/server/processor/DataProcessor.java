package cloudwalk.master.server.processor;

import cloudwalk.master.server.entity.DataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by apple on 2/18/16.
 * A util class to process data.
 */
public final class DataProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

    public static DataEntity process(String data) {
        DataEntity dataEntity = new DataEntity();

        String[] strings = data.split("&");
        for (String string : strings) {
            String[] twoStrings = string.split("=");
            if (twoStrings.length != 2) {
                continue;
            }
            String key = twoStrings[0];
            String value = twoStrings[1];
            if ("uri".equals(key)) {
                dataEntity.setUri(value);
            }
            if ("description".equals(key)) {
                dataEntity.setDescription(value);
            }
        }

        LOGGER.info("Generate an entity whose uri is " + dataEntity.getUri() + " and description is " + dataEntity.getDescription());
        return dataEntity;
    }
}

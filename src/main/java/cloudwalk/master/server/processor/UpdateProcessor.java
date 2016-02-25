package cloudwalk.master.server.processor;

import cloudwalk.master.server.SlaveManager;
import cloudwalk.master.server.entity.SlaveEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 1333907 on 2/23/16.
 * Update slaves.
 */
public class UpdateProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProcessor.class);

    public static void process(String data) {
        SlaveEntity entity = new SlaveEntity();
        long number = 0L;

        String[] strings = data.split("&");
        for (String string : strings) {
            String[] twoStrings = string.split("=");
            if (twoStrings.length != 2) {
                continue;
            }
            String key = twoStrings[0];
            String value = twoStrings[1];
            if ("hostname".equals(key)) {
                entity.setHostname(value);
            }
            if ("ip".equals(key)) {
                entity.setIp(value);
            }
            if ("port".equals(key)) {
                int port = Integer.parseInt(value);
                entity.setPort(port);
            }
            if ("name".equals(key)) {
                entity.setName(value);
            }
            if ("number".equals(key)) {
                number = Long.parseLong(value);
            }
        }

        SlaveManager.updateSlave(entity, number);
    }
}

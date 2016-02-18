package cloudwalk.master.server.processor;

import cloudwalk.master.server.entity.DataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by apple on 2/18/16.
 * A util class to process entity.
 */
public final class EntityProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityProcessor.class);

    public static void process(DataEntity dataEntity) {
        LOGGER.info("Process an entity whose uri is " + dataEntity.getUri() + " and description is " + dataEntity.getDescription());
    }
}

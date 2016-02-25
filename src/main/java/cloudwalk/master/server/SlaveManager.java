package cloudwalk.master.server;

import cloudwalk.master.server.entity.SlaveEntity;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1333907 on 2/18/16.
 * Master manages slaves.
 */

public final class SlaveManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveManager.class);
    // If a slave is not registered before, this field will determine whether its updated number will be received.
    private static final boolean ALWAYS_INSERT_INTO_SLAVE_TABLE = true;

    private static HashMap<SlaveEntity, Long> slaveTable = new HashMap<>();

    /**
     * Is a singleton class with private constructor.
     */
    private SlaveManager() {
    }

    public static void registerSlave(SlaveEntity slaveEntity) {
        if (slaveTable.containsKey(slaveEntity)) {
            LOGGER.warn("Slave with " + slaveEntity.getSlaveName() + " has already registered");
        } else {
            slaveTable.put(slaveEntity, 0L);
        }
    }

    public static void updateSlave(SlaveEntity slaveEntity, long number) {
        if (ALWAYS_INSERT_INTO_SLAVE_TABLE || slaveTable.containsKey(slaveEntity)) {
            slaveTable.put(slaveEntity, number);
        }
    }

    public static JSONObject getSlaves() {
        JSONObject slaves = new JSONObject();
        for (Map.Entry<SlaveEntity, Long> entry : slaveTable.entrySet()) {
            SlaveEntity entity = entry.getKey();
            long number = entry.getValue();

            JSONObject slave = JSONObject.fromObject(entity);
            LOGGER.info(slave.toString());
            slave.put("number", number);
            slaves.put(entity.getSlaveName(), slave);
        }
        return slaves;
    }

    public static JSONObject getNumber() {
        JSONObject number = new JSONObject();
        for (Map.Entry<SlaveEntity, Long> entry : slaveTable.entrySet()) {
            number.put(entry.getKey().getSlaveName(), entry.getValue());
        }
        return number;
    }
}

package cloudwalk.master.server;
import cloudwalk.master.server.entity.SlaveInfoEntity;
import cloudwalk.master.server.entity.SlaveNameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1333907 on 2/18/16.
 * Master manages slaves.
 */

public final class SlaveManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveManager.class);

    private static HashMap<SlaveNameEntity, SlaveInfoEntity> slaveTable = new HashMap<>();

    /**
     * Is a singleton class with private constructor.
     */
    private SlaveManager() {
        
    }

    public static void registerSlave(SlaveNameEntity slaveNameEntity) {
        if (slaveTable.containsKey(slaveNameEntity)) {
            LOGGER.warn("Slave with " + slaveNameEntity.getSlaveName() + " has already registered");
        } 
        else {
            slaveTable.put(slaveNameEntity, new SlaveInfoEntity());
        }
    }

    public static List<String> getSlaveNameList() {
        List<String> nameList = new ArrayList<>();
        for (SlaveNameEntity entity : slaveTable.keySet()) {
            nameList.add(entity.getSlaveName());
        }

        return nameList;
    }
}

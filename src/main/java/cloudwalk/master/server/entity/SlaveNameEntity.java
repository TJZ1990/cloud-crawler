package cloudwalk.master.server.entity;

/**
 * Created by 1333907 on 2/18/16.
 * Entity for slaves.
 */
public class SlaveNameEntity {
    private String ip = null;
    private int port = -1;
    private String name = null;

    public SlaveNameEntity() {
        this(null, -1, null);
    }

    public SlaveNameEntity(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public SlaveNameEntity setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public SlaveNameEntity setPort(int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }

    public SlaveNameEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getSlaveName() {
        return ip + ":" + port + ":" + name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SlaveNameEntity)) {
            return false;
        }
        SlaveNameEntity slaveNameEntity = (SlaveNameEntity) o;
        if (ip == null) {
            if (slaveNameEntity.getIp() != null) {
                return false;
            }
        }
        if (name == null) {
            if (slaveNameEntity.getName() != null) {
                return false;
            }
        }
        return ip.equals(slaveNameEntity.getIp()) && port == slaveNameEntity.getPort() && name.equals(slaveNameEntity.getName());
    }
}

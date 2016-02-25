package cloudwalk.master.server.entity;

/**
 * Created by 1333907 on 2/18/16.
 * Entity for slaves.
 */

public class SlaveEntity {
    private String hostname = null;
    private String ip = null;
    private int port = -1;
    private String name = null;

    public SlaveEntity() {
        this(null, null, -1, null);
    }

    public SlaveEntity(String hostname, String ip, int port, String name) {
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public SlaveEntity setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    public SlaveEntity setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public SlaveEntity setPort(int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }

    public SlaveEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getSlaveName() {
        return hostname + ":" + ip + ":" + port + ":" + name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SlaveEntity)) {
            return false;
        }
        SlaveEntity slaveEntity = (SlaveEntity) o;
        if (hostname == null) {
            if (slaveEntity.getHostname() != null) {
                return false;
            }
        }
        if (ip == null) {
            if (slaveEntity.getIp() != null) {
                return false;
            }
        }
        if (name == null) {
            if (slaveEntity.getName() != null) {
                return false;
            }
        }
        return ip.equals(slaveEntity.getIp()) && port == slaveEntity.getPort() && name.equals(slaveEntity.getName());
    }
}

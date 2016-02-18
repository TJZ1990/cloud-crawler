package cloudwalk.master.server.entity;

/**
 * Created by apple on 2/18/16.
 * Data needed by master
 */
public class DataEntity {
    private String uri = null;
    private String description = null;

    public DataEntity() {
        this(null);
    }

    public DataEntity(String uri) {
        this(uri, null);
    }

    public DataEntity(String uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public DataEntity setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public DataEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }
}

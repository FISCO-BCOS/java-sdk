package org.fisco.bcos.sdk.contract.precompiled.bfs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileInfo {
    private String name;
    private String type;
    private String extra;
    private String gid;
    private String uid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;
        FileInfo fileInfo = (FileInfo) o;
        return Objects.equals(name, fileInfo.name)
                && Objects.equals(type, fileInfo.type)
                && Objects.equals(extra, fileInfo.extra)
                && Objects.equals(gid, fileInfo.gid)
                && Objects.equals(uid, fileInfo.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, extra, gid, uid);
    }

    @Override
    public String toString() {
        return "FileInfo{"
                + "name='"
                + name
                + '\''
                + ", type='"
                + type
                + '\''
                + ", extra='"
                + extra
                + '\''
                + ", gid='"
                + gid
                + '\''
                + ", uid='"
                + uid
                + '\''
                + '}';
    }
}

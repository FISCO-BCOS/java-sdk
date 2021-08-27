package org.fisco.bcos.sdk.contract.precompiled.bfs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileInfo {
    private String name;
    private String type;
    private List<FileInfo> subdirectories;

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

    public List<FileInfo> getSubdirectories() {
        return subdirectories;
    }

    public void setSubdirectories(List<FileInfo> subdirectories) {
        this.subdirectories = subdirectories;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, subdirectories);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileInfo) {
            FileInfo fileInfo = (FileInfo) obj;
            return Objects.equals(name, fileInfo.name)
                    && Objects.equals(type, fileInfo.type)
                    && Objects.equals(subdirectories, fileInfo.subdirectories);
        } else return false;
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
                + ", subDir="
                + subdirectories
                + '}';
    }
}

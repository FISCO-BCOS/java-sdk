package org.fisco.bcos.sdk.v3.contract.precompiled.bfs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BFSInfo {
    private String fileName;
    private String fileType;
    private String address;
    private String abi;

    public BFSInfo(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    public static BFSInfo fromPrecompiledBfs(BFSPrecompiled.BfsInfo bfsInfo) {
        if (Objects.isNull(bfsInfo)
                || Objects.isNull(bfsInfo.fileName)
                || bfsInfo.fileName.isEmpty()) {
            return null;
        }
        BFSInfo info = new BFSInfo(bfsInfo.getFileName(), bfsInfo.getFileType());
        if (bfsInfo.ext.size() == 2 && "link".equals(bfsInfo.getFileType())) {
            info.setAddress(bfsInfo.ext.get(0));
            info.setAbi(bfsInfo.ext.get(1));
        }
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BFSInfo)) return false;
        BFSInfo bfsInfo = (BFSInfo) o;
        return getFileName().equals(bfsInfo.getFileName())
                && getFileType().equals(bfsInfo.getFileType())
                && Objects.equals(getAddress(), bfsInfo.getAddress())
                && Objects.equals(getAbi(), bfsInfo.getAbi());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFileName(), getFileType(), getAddress(), getAbi());
    }

    @Override
    public String toString() {
        return "BFSInfo{"
                + "fileName='"
                + fileName
                + '\''
                + ", fileType='"
                + fileType
                + '\''
                + ", address='"
                + address
                + '\''
                + ", abi='"
                + abi
                + '\''
                + '}';
    }
}

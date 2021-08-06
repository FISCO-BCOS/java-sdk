// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 1.7.2.
// **********************************************************************

package org.fisco.bcos.sdk.client.protocol.model;

import com.qq.tars.protocol.tars.TarsInputStream;
import com.qq.tars.protocol.tars.TarsOutputStream;
import com.qq.tars.protocol.tars.annotation.TarsStruct;
import com.qq.tars.protocol.tars.annotation.TarsStructProperty;
import com.qq.tars.protocol.util.TarsUtil;

@TarsStruct
public class ParentInfo {

    @TarsStructProperty(order = 1, isRequire = true)
    public long blockNumber = 0L;

    @TarsStructProperty(order = 2, isRequire = true)
    public byte[] blockHash = null;

    public long getBlockNumber() {
        return this.blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public byte[] getBlockHash() {
        return this.blockHash;
    }

    public void setBlockHash(byte[] blockHash) {
        this.blockHash = blockHash;
    }

    public ParentInfo() {}

    public ParentInfo(long blockNumber, byte[] blockHash) {
        this.blockNumber = blockNumber;
        this.blockHash = blockHash;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + TarsUtil.hashCode(this.blockNumber);
        result = prime * result + TarsUtil.hashCode(this.blockHash);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ParentInfo)) {
            return false;
        }
        ParentInfo other = (ParentInfo) obj;
        return (TarsUtil.equals(this.blockNumber, other.blockNumber)
                && TarsUtil.equals(this.blockHash, other.blockHash));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ParentInfo(");
        sb.append("blockNumber:");
        sb.append(this.blockNumber);
        sb.append(", ");
        sb.append("blockHash:");
        if (this.blockHash == null) {
            sb.append("null");
        } else {
            sb.append(this.blockHash);
        }
        sb.append(")");
        return sb.toString();
    }

    public void writeTo(TarsOutputStream _os) {
        _os.write(this.blockNumber, 1);
        _os.write(this.blockHash, 2);
    }

    static byte[] cache_blockHash;

    static {
        cache_blockHash = new byte[1];
        byte var_5 = (byte) 0;
        cache_blockHash[0] = var_5;
    }

    public void readFrom(TarsInputStream _is) {
        this.blockNumber = _is.read(this.blockNumber, 1, true);
        this.blockHash = (byte[]) _is.read(cache_blockHash, 2, true);
    }
}

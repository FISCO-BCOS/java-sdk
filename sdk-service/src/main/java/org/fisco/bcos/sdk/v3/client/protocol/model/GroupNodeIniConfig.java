package org.fisco.bcos.sdk.v3.client.protocol.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupNodeIniConfig {
    private static final Logger logger = LoggerFactory.getLogger(GroupNodeIniConfig.class);

    public static GroupNodeIniConfig newIniConfig(GroupNodeIniInfo groupIniConfig) {

        try {
            // chain section
            GroupNodeIniConfig.Chain chain = new GroupNodeIniConfig.Chain();
            chain.setChainID(groupIniConfig.getChainID());
            chain.setGroupID(groupIniConfig.getGroupID());
            chain.setSmCrypto(groupIniConfig.getSmCryptoType());

            // executor
            GroupNodeIniConfig.Executor executor = new GroupNodeIniConfig.Executor();
            executor.setWasm(groupIniConfig.getWasm());
            executor.setAuthCheck(groupIniConfig.getAuthCheck());

            GroupNodeIniConfig groupNodeIniConfig = new GroupNodeIniConfig();
            groupNodeIniConfig.setChain(chain);
            groupNodeIniConfig.setExecutor(executor);
            return groupNodeIniConfig;
        } catch (Exception e) {
            logger.error("Failed to resolve the node configuration, e:", e);
            throw new RuntimeException(
                    "Failed to resolve the node ini config, error: " + e.getCause());
        }
    }

    private Chain chain;
    private Executor executor;

    public Chain getChain() {
        return chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public static class Executor {
        private boolean isWasm;
        private boolean isAuthCheck;

        public boolean isWasm() {
            return isWasm;
        }

        public void setWasm(boolean wasm) {
            isWasm = wasm;
        }

        public boolean isAuthCheck() {
            return isAuthCheck;
        }

        public void setAuthCheck(boolean authCheck) {
            isAuthCheck = authCheck;
        }

        @Override
        public String toString() {
            return "Executor{" + "isWasm=" + isWasm + ", isAuthCheck=" + isAuthCheck + '}';
        }
    }

    public static class Chain {
        private boolean smCrypto;
        private String groupID;
        private String chainID;

        public boolean isSmCrypto() {
            return smCrypto;
        }

        public void setSmCrypto(boolean smCrypto) {
            this.smCrypto = smCrypto;
        }

        public String getGroupID() {
            return groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }

        public String getChainID() {
            return chainID;
        }

        public void setChainID(String chainID) {
            this.chainID = chainID;
        }

        @Override
        public String toString() {
            return "Chain{"
                    + "smCrypto="
                    + smCrypto
                    + ", groupID='"
                    + groupID
                    + '\''
                    + ", chainID='"
                    + chainID
                    + '\''
                    + '}';
        }
    }
}

package org.fisco.bcos.sdk.client.protocol.model;

import java.io.StringReader;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupNodeIniConfig {
    private static final Logger logger = LoggerFactory.getLogger(GroupNodeIniConfig.class);

    public static GroupNodeIniConfig newIniConfig(String iniBuffer) {

        try {
            INIConfiguration iniConfiguration = new INIConfiguration();
            StringReader stringReader = new StringReader(iniBuffer);
            iniConfiguration.read(stringReader);

            // chain section
            SubnodeConfiguration chainSection = iniConfiguration.getSection("chain");
            boolean sm_crypto = chainSection.getBoolean("sm_crypto");
            String group_id = chainSection.getString("group_id");
            String chain_id = chainSection.getString("chain_id");

            GroupNodeIniConfig.Chain chain = new GroupNodeIniConfig.Chain();
            chain.setChainID(chain_id);
            chain.setGroupID(group_id);
            chain.setSmCrypto(sm_crypto);

            // executor
            SubnodeConfiguration executorSection = iniConfiguration.getSection("executor");
            boolean is_wasm = executorSection.getBoolean("is_wasm");
            GroupNodeIniConfig.Executor executor = new GroupNodeIniConfig.Executor();
            executor.setWasm(is_wasm);

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

    /*
    group node inif config sample:

    [chain]
        sm_crypto = false
        group_id = group0
        chain_id = chain0

    [service]
        rpc = chain0.agencyBRpcService
        gateway = chain0.agencyBGatewayService
        node_name = node10

    [security]
        private_key_path = conf/node.pem

    [consensus]
        min_seal_time = 500

    [executor]
        is_wasm = false

     [storage]
        data_path = data

    [txpool]
        limit = 15000
        notify_worker_num = 2
        verify_worker_num = 2

    [log]
        enable = true
        log_path = ./log
        stat_flush_interval = 60
        level = DEBUG
        max_log_file_size = 200
    */

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

        public boolean isWasm() {
            return isWasm;
        }

        public void setWasm(boolean wasm) {
            isWasm = wasm;
        }

        @Override
        public String toString() {
            return "Executor{" + "isWasm=" + isWasm + '}';
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

package org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig;

import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;

public class SystemConfigFeature {
    public enum Features {
        BUGFIX_REVERT("bugfix_revert", EnumNodeVersion.BCOS_3_2_3.getVersion()),
        BUGFIX_STATESTORAGE_HASH(
                "bugfix_statestorage_hash", EnumNodeVersion.BCOS_3_2_4.getVersion()),
        BUGFIX_EVM_CREATE2_DELEGATECALL_STATICCALL_CODECOPY(
                "bugfix_evm_create2_delegatecall_staticcall_codecopy",
                EnumNodeVersion.BCOS_3_2_4.getVersion()),
        BUGFIX_EVENT_LOG_ORDER("bugfix_event_log_order", EnumNodeVersion.BCOS_3_2_7.getVersion()),
        BUGFIX_CALL_NOADDR_RETURN(
                "bugfix_call_noaddr_return", EnumNodeVersion.BCOS_3_2_7.getVersion()),
        BUGFIX_PRECOMPILED_CODEHASH(
                "bugfix_precompiled_codehash", EnumNodeVersion.BCOS_3_2_7.getVersion()),
        BUGFIX_DMC_REVERT("bugfix_dmc_revert", EnumNodeVersion.BCOS_3_2_7.getVersion()),
        FEATURE_DMC2SERIAL("feature_dmc2serial", EnumNodeVersion.BCOS_3_2_4.getVersion()),
        FEATURE_SHARDING("feature_sharding", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_RPBFT("feature_rpbft", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_RPBFT_EPOCH_BLOCK_NUM(
                "feature_rpbft_epoch_block_num", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_RPBFT_EPOCH_SEALER_NUM(
                "feature_rpbft_epoch_sealer_num", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_PAILLIER("feature_paillier", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_BALANCE("feature_balance", EnumNodeVersion.BCOS_3_6_0.getVersion()),
        FEATURE_BALANCE_PRECOMPILED(
                "feature_balance_precompiled", EnumNodeVersion.BCOS_3_6_0.getVersion()),
        FEATURE_BALANCE_POLICY1("feature_balance_policy1", EnumNodeVersion.BCOS_3_6_0.getVersion());

        private final String featureName;
        private final int enableVersion;

        Features(String name, int enableVersion) {
            this.featureName = name;
            this.enableVersion = enableVersion;
        }

        @Override
        public String toString() {
            return featureName;
        }

        public int enableVersion() {
            return enableVersion;
        }
    }

    public static Features fromString(String name) {
        switch (name) {
            case "bugfix_revert":
                return Features.BUGFIX_REVERT;
            case "bugfix_statestorage_hash":
                return Features.BUGFIX_STATESTORAGE_HASH;
            case "bugfix_evm_create2_delegatecall_staticcall_codecopy":
                return Features.BUGFIX_EVM_CREATE2_DELEGATECALL_STATICCALL_CODECOPY;
            case "bugfix_event_log_order":
                return Features.BUGFIX_EVENT_LOG_ORDER;
            case "bugfix_call_noaddr_return":
                return Features.BUGFIX_CALL_NOADDR_RETURN;
            case "bugfix_precompiled_codehash":
                return Features.BUGFIX_PRECOMPILED_CODEHASH;
            case "bugfix_dmc_revert":
                return Features.BUGFIX_DMC_REVERT;
            case "feature_dmc2serial":
                return Features.FEATURE_DMC2SERIAL;
            case "feature_sharding":
                return Features.FEATURE_SHARDING;
            case "feature_rpbft":
                return Features.FEATURE_RPBFT;
            case "feature_rpbft_epoch_block_num":
                return Features.FEATURE_RPBFT_EPOCH_BLOCK_NUM;
            case "feature_rpbft_epoch_sealer_num":
                return Features.FEATURE_RPBFT_EPOCH_SEALER_NUM;
            case "feature_paillier":
                return Features.FEATURE_PAILLIER;
            case "feature_balance":
                return Features.FEATURE_BALANCE;
            case "feature_balance_precompiled":
                return Features.FEATURE_BALANCE_PRECOMPILED;
            case "feature_balance_policy1":
                return Features.FEATURE_BALANCE_POLICY1;
            default:
                return null;
        }
    }
}

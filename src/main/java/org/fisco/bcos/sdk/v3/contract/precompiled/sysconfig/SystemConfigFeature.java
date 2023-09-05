package org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig;

import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;

public class SystemConfigFeature {
    public enum Features {
        BUGFIX_REVERT("bugfix_revert", EnumNodeVersion.BCOS_3_2_3.getVersion()),
        FEATURE_SHARDING("feature_sharding", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_RPBFT("feature_rpbft", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_RPBFT_EPOCH_BLOCK_NUM(
                "feature_rpbft_epoch_block_num", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_RPBFT_EPOCH_SEALER_NUM(
                "feature_rpbft_epoch_sealer_num", EnumNodeVersion.BCOS_3_5_0.getVersion()),
        FEATURE_PAILLIER("feature_paillier", EnumNodeVersion.BCOS_3_5_0.getVersion());

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
            default:
                return null;
        }
    }
}

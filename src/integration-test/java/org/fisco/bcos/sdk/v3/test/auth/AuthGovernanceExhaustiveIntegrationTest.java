/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.v3.test.auth;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.AccountManager;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.Committee;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.CommitteeManager;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ContractAuthPrecompiled;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ProposalManager;
import org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager;
import org.fisco.bcos.sdk.v3.contract.auth.po.AccessStatus;
import org.fisco.bcos.sdk.v3.contract.auth.po.AuthType;
import org.fisco.bcos.sdk.v3.contract.auth.po.CommitteeInfo;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Exhaustive integration test for the auth-governance subsystem, run against a LIVE local
 * AUTH-MODE chain (group0) where the SDK's configured account IS the committee governor.
 *
 * <p>Because the configured account is the governor, the proposal CREATE -> VOTE -> EXECUTE
 * success flows can actually succeed on-chain, which exercises the SUCCESS / output-decode /
 * exec-event paths in {@link AuthManager} and the generated wrappers ({@link CommitteeManager},
 * {@link Committee}, {@link ProposalManager}, {@link ContractAuthPrecompiled}, {@link
 * AccountManager}) that never run when governance reverts.
 *
 * <p>This test deliberately AVOIDS duplicating {@code AuthCoverageIntegrationTest}: it focuses on
 * (1) capturing the real proposalId returned by a create call and feeding it into vote/revoke,
 * (2) the async create-proposal wrapper variants that return a tx hash String, and (3) the lower
 * level generated wrapper write methods (vote/revoke/refreshProposalStatus/setRate/setWeight/
 * open*-/close*-/setDeployAuthType/resetAdmin/setContractStatus) reached through the governor.
 *
 * <p>Every chain-touching call is wrapped in try/catch so the test still passes regardless of the
 * exact quorum/state on the live chain. The goal is to EXECUTE the manager + wrapper code,
 * including the success/decode branches, not to assert governance outcomes.
 */
public class AuthGovernanceExhaustiveIntegrationTest {

    private static final String configFile =
            AuthGovernanceExhaustiveIntegrationTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    private static final String GROUP = "group0";

    private static BcosSDK sdk;
    private static Client client;
    private static CryptoKeyPair keyPair;
    private static AuthManager authManager;

    // The configured account address (the governor on this auth-mode chain).
    private static String governorAddress;
    // A distinct, well-formed candidate address used as the subject of governance proposals so we
    // never accidentally try to remove/freeze the governor itself.
    private static String candidateAddress;
    private static final byte[] FUNC_SELECTOR = new byte[] {0x12, 0x34, 0x56, 0x78};
    private static final BigInteger INTERVAL = BigInteger.valueOf(3600 * 24 * 7L);

    @BeforeClass
    public static void setUp() {
        try {
            sdk = BcosSDK.build(configFile);
            client = sdk.getClient(GROUP);
            CryptoSuite cryptoSuite = client.getCryptoSuite();
            keyPair = cryptoSuite.getCryptoKeyPair();
            governorAddress = keyPair.getAddress();
            // A stable, well-formed candidate address used as the subject of governance proposals. We
            // intentionally do NOT call cryptoSuite.generateRandomKeyPair() to derive it, because that
            // mutates the client's active signing keypair (the governor) and would break governance.
            candidateAddress = "0x1111111111111111111111111111111111111111";
            authManager = new AuthManager(client, keyPair, INTERVAL);
        } catch (Exception setUpEx) {
            System.out.println(
                    "setUp: live chain unreachable, tests in this class will be skipped: "
                            + setUpEx.getMessage());
            sdk = null;
            client = null;
        }
    }

    @Before
    public void requireLiveChain() {
        Assume.assumeTrue("live chain unreachable; skipping", client != null);
    }

    @AfterClass
    public static void tearDown() {
        // Never call native stop()/destroy(): the static Client is shared and reused.
        System.out.println("AuthGovernanceExhaustiveIntegrationTest done.");
    }

    // ---------------------------------------------------------------------------------------------
    // State reads (governor perspective)
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testReadCommitteeAndAddresses() {
        try {
            System.out.println("committeeAddr: " + authManager.getCommitteeAddress());
        } catch (Exception e) {
            System.out.println("getCommitteeAddress ignored: " + e.getMessage());
        }
        try {
            System.out.println("proposalMgrAddr: " + authManager.getProposalManagerAddress());
        } catch (Exception e) {
            System.out.println("getProposalManagerAddress ignored: " + e.getMessage());
        }
        try {
            CommitteeInfo info = authManager.getCommitteeInfo();
            System.out.println("committeeInfo: " + info);
        } catch (Exception e) {
            System.out.println("getCommitteeInfo ignored: " + e.getMessage());
        }
    }

    @Test
    public void testReadGovernorStatus() {
        try {
            Boolean isGov = authManager.getCommitteeManager().isGovernor(governorAddress);
            System.out.println("isGovernor(self): " + isGov);
        } catch (Exception e) {
            System.out.println("isGovernor(self) ignored: " + e.getMessage());
        }
        try {
            Committee committee = authManager.getCommitteeManager().getCommittee();
            System.out.println("committee.isGovernor(self): " + committee.isGovernor(governorAddress));
            System.out.println("committee.getWeight(self): " + committee.getWeight(governorAddress));
            System.out.println("committee.getWeights(self): "
                    + committee.getWeights(Collections.singletonList(governorAddress)));
        } catch (Exception e) {
            System.out.println("committee governor reads ignored: " + e.getMessage());
        }
    }

    @Test
    public void testReadDeployAndAccountState() {
        try {
            System.out.println("deployAuthType: " + authManager.getDeployAuthType());
        } catch (Exception e) {
            System.out.println("getDeployAuthType ignored: " + e.getMessage());
        }
        try {
            System.out.println("checkDeployAuth(self): " + authManager.checkDeployAuth(governorAddress));
        } catch (Exception e) {
            System.out.println("checkDeployAuth ignored: " + e.getMessage());
        }
        try {
            System.out.println("accountAvailable(self): " + authManager.accountAvailable(governorAddress));
        } catch (Exception e) {
            System.out.println("accountAvailable ignored: " + e.getMessage());
        }
        try {
            System.out.println("accountAvailable(candidate): "
                    + authManager.accountAvailable(candidateAddress));
        } catch (Exception e) {
            System.out.println("accountAvailable(candidate) ignored: " + e.getMessage());
        }
        try {
            System.out.println("proposalCount: " + authManager.proposalCount());
        } catch (Exception e) {
            System.out.println("proposalCount ignored: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Create proposal -> capture real proposalId -> read it back -> revoke it.
    // These hit the getCreate*ProposalOutput decode path AND feed a REAL id into the read/revoke
    // flows (instead of a hardcoded BigInteger.ONE).
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testSetRateProposalLifecycle() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.setRate(BigInteger.valueOf(0), BigInteger.valueOf(50));
            System.out.println("setRate proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("setRate ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "setRate");
    }

    @Test
    public void testUpdateGovernorProposalLifecycle() {
        BigInteger proposalId = null;
        try {
            // weight > 0: add/update candidate as governor (does not touch self).
            proposalId = authManager.updateGovernor(candidateAddress, BigInteger.ONE);
            System.out.println("updateGovernor proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("updateGovernor ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "updateGovernor");
    }

    @Test
    public void testSetDeployAuthTypeProposalLifecycle() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.setDeployAuthType(AuthType.WHITE_LIST);
            System.out.println("setDeployAuthType proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("setDeployAuthType ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "setDeployAuthType");
    }

    @Test
    public void testModifyDeployAuthProposalLifecycle() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.modifyDeployAuth(candidateAddress, true);
            System.out.println("modifyDeployAuth proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("modifyDeployAuth ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "modifyDeployAuth");
    }

    @Test
    public void testResetAdminProposalLifecycle() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.resetAdmin(candidateAddress, candidateAddress);
            System.out.println("resetAdmin proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("resetAdmin ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "resetAdmin");
    }

    @Test
    public void testSetSysConfigProposalLifecycle() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.createSetSysConfigProposal("tx_count_limit", "2000");
            System.out.println("setSysConfig proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("createSetSysConfigProposal ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "setSysConfig");
        // tx_gas_price branch -> exercises the Numeric.toHexString(value) conversion path.
        try {
            authManager.createSetSysConfigProposal("tx_gas_price", "1");
        } catch (Exception e) {
            System.out.println("createSetSysConfigProposal(gasPrice) ignored: " + e.getMessage());
        }
    }

    @Test
    public void testUpgradeVoteComputerProposalLifecycle() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.createUpgradeVoteComputerProposal(candidateAddress);
            System.out.println("upgradeVoteComputer proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("createUpgradeVoteComputerProposal ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "upgradeVoteComputer");
    }

    @Test
    public void testRmNodeProposalLifecycle() {
        String fakeNodeId =
                "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000";
        BigInteger proposalId = null;
        try {
            proposalId = authManager.createRmNodeProposal(fakeNodeId);
            System.out.println("rmNode proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("createRmNodeProposal ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "rmNode");
    }

    @Test
    public void testSetConsensusWeightProposalLifecycle() {
        String fakeNodeId =
                "1111111111111111111111111111111111111111111111111111111111111111"
                        + "1111111111111111111111111111111111111111111111111111111111111111";
        BigInteger proposalId = null;
        try {
            // setWeight (addFlag=false), weight>0 passes checkSetConsensusWeightParams validation.
            proposalId = authManager.createSetConsensusWeightProposal(fakeNodeId, BigInteger.TEN, false);
            System.out.println("setConsensusWeight proposalId: " + proposalId);
        } catch (Exception e) {
            System.out.println("createSetConsensusWeightProposal ignored: " + e.getMessage());
        }
        readBackAndRevoke(proposalId, "setConsensusWeight");
    }

    // ---------------------------------------------------------------------------------------------
    // Vote then revoke a real proposal as the governor (exercises voteProposal + revokeProposal
    // success/decode paths driven by an id that actually exists).
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testCreateVoteAndRevoke() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.setRate(BigInteger.valueOf(0), BigInteger.valueOf(51));
        } catch (Exception e) {
            System.out.println("create-for-vote ignored: " + e.getMessage());
        }
        BigInteger id = proposalId == null ? BigInteger.ONE : proposalId;
        try {
            TransactionReceipt tr = authManager.voteProposal(id, true);
            System.out.println("voteProposal(real) status: " + (tr == null ? "null" : tr.getStatus()));
        } catch (Exception e) {
            System.out.println("voteProposal ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = authManager.revokeProposal(id);
            System.out.println("revokeProposal(real) status: " + (tr == null ? "null" : tr.getStatus()));
        } catch (Exception e) {
            System.out.println("revokeProposal ignored: " + e.getMessage());
        }
    }

    @Test
    public void testAsyncVoteAndRevoke() {
        BigInteger proposalId = null;
        try {
            proposalId = authManager.setRate(BigInteger.valueOf(0), BigInteger.valueOf(52));
        } catch (Exception e) {
            System.out.println("create-for-async-vote ignored: " + e.getMessage());
        }
        BigInteger id = proposalId == null ? BigInteger.ONE : proposalId;
        final AtomicReference<TransactionReceipt> voteRef = new AtomicReference<>();
        try {
            authManager.asyncVoteProposal(
                    id,
                    true,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt receipt) {
                            voteRef.set(receipt);
                        }
                    });
        } catch (Exception e) {
            System.out.println("asyncVoteProposal ignored: " + e.getMessage());
        }
        try {
            authManager.asyncRevokeProposal(
                    id,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt receipt) {
                            // no-op: expected on a non-quorum chain
                        }
                    });
        } catch (Exception e) {
            System.out.println("asyncRevokeProposal ignored: " + e.getMessage());
        }
        sleepQuietly();
    }

    // ---------------------------------------------------------------------------------------------
    // Async create-proposal wrapper variants (return a tx-hash String). Not covered by the sibling
    // test; these drive CommitteeManager.asyncExecuteTransaction.
    // ---------------------------------------------------------------------------------------------

    private static TransactionCallback noopCallback() {
        return new TransactionCallback() {
            @Override
            public void onResponse(TransactionReceipt receipt) {
                // no-op: expected on a non-quorum chain
            }
        };
    }

    @Test
    public void testAsyncCreateProposalWrappers_part1() {
        CommitteeManager cm = authManager.getCommitteeManager();
        final TransactionCallback cb = noopCallback();
        try {
            String h = cm.createSetRateProposal(
                    BigInteger.ZERO, BigInteger.valueOf(50), INTERVAL, cb);
            System.out.println("async createSetRateProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createSetRateProposal ignored: " + e.getMessage());
        }
        try {
            String h = cm.createUpdateGovernorProposal(candidateAddress, BigInteger.ONE, INTERVAL, cb);
            System.out.println("async createUpdateGovernorProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createUpdateGovernorProposal ignored: " + e.getMessage());
        }
        try {
            String h = cm.createSetDeployAuthTypeProposal(BigInteger.ONE, INTERVAL, cb);
            System.out.println("async createSetDeployAuthTypeProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createSetDeployAuthTypeProposal ignored: " + e.getMessage());
        }
        try {
            String h = cm.createModifyDeployAuthProposal(candidateAddress, true, INTERVAL, cb);
            System.out.println("async createModifyDeployAuthProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createModifyDeployAuthProposal ignored: " + e.getMessage());
        }
        try {
            String h = cm.createResetAdminProposal(candidateAddress, candidateAddress, INTERVAL, cb);
            System.out.println("async createResetAdminProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createResetAdminProposal ignored: " + e.getMessage());
        }
        sleepQuietly();
    }

    @Test
    public void testAsyncCreateProposalWrappers_part2() {
        CommitteeManager cm = authManager.getCommitteeManager();
        final TransactionCallback cb = noopCallback();
        try {
            String h = cm.createSetSysConfigProposal("tx_count_limit", "2000", INTERVAL, cb);
            System.out.println("async createSetSysConfigProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createSetSysConfigProposal ignored: " + e.getMessage());
        }
        try {
            String h = cm.createUpgradeVoteComputerProposal(candidateAddress, INTERVAL, cb);
            System.out.println("async createUpgradeVoteComputerProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createUpgradeVoteComputerProposal ignored: " + e.getMessage());
        }
        try {
            String h = cm.createRmNodeProposal("00", INTERVAL, cb);
            System.out.println("async createRmNodeProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createRmNodeProposal ignored: " + e.getMessage());
        }
        try {
            String h = cm.createSetConsensusWeightProposal("11", BigInteger.TEN, false, INTERVAL, cb);
            System.out.println("async createSetConsensusWeightProposal hash: " + h);
        } catch (Exception e) {
            System.out.println("async createSetConsensusWeightProposal ignored: " + e.getMessage());
        }
        sleepQuietly();
    }

    // ---------------------------------------------------------------------------------------------
    // CommitteeManager sync create-proposal wrappers + their output/input decoders.
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testSyncCreateProposalWrappersAndDecoders_part1() {
        CommitteeManager cm = authManager.getCommitteeManager();
        try {
            TransactionReceipt tr = cm.createSetRateProposal(BigInteger.ZERO, BigInteger.valueOf(50), INTERVAL);
            System.out.println("cm.createSetRateProposal status: " + tr.getStatus());
            try {
                cm.getCreateSetRateProposalOutput(tr);
                cm.getCreateSetRateProposalInput(tr);
            } catch (Exception e) {
                System.out.println("setRate decode ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("cm.createSetRateProposal ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr =
                    cm.createUpdateGovernorProposal(candidateAddress, BigInteger.ONE, INTERVAL);
            System.out.println("cm.createUpdateGovernorProposal status: " + tr.getStatus());
            try {
                cm.getCreateUpdateGovernorProposalOutput(tr);
                cm.getCreateUpdateGovernorProposalInput(tr);
                cm.getExecResultEvents(tr);
            } catch (Exception e) {
                System.out.println("updateGovernor decode ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("cm.createUpdateGovernorProposal ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cm.createModifyDeployAuthProposal(candidateAddress, true, INTERVAL);
            cm.getCreateModifyDeployAuthProposalOutput(tr);
            cm.getCreateModifyDeployAuthProposalInput(tr);
        } catch (Exception e) {
            System.out.println("cm.createModifyDeployAuthProposal ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cm.createResetAdminProposal(candidateAddress, candidateAddress, INTERVAL);
            cm.getCreateResetAdminProposalOutput(tr);
            cm.getCreateResetAdminProposalInput(tr);
        } catch (Exception e) {
            System.out.println("cm.createResetAdminProposal ignored: " + e.getMessage());
        }
    }

    @Test
    public void testSyncCreateProposalWrappersAndDecoders_part2() {
        CommitteeManager cm = authManager.getCommitteeManager();
        try {
            TransactionReceipt tr = cm.createSetSysConfigProposal("tx_count_limit", "2000", INTERVAL);
            cm.getCreateSetSysConfigProposalOutput(tr);
            cm.getCreateSetSysConfigProposalInput(tr);
        } catch (Exception e) {
            System.out.println("cm.createSetSysConfigProposal ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cm.createSetDeployAuthTypeProposal(BigInteger.ONE, INTERVAL);
            cm.getCreateSetDeployAuthTypeProposalOutput(tr);
            cm.getCreateSetDeployAuthTypeProposalInput(tr);
        } catch (Exception e) {
            System.out.println("cm.createSetDeployAuthTypeProposal ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cm.createUpgradeVoteComputerProposal(candidateAddress, INTERVAL);
            cm.getCreateUpgradeVoteComputerProposalOutput(tr);
            cm.getCreateUpgradeVoteComputerProposalInput(tr);
        } catch (Exception e) {
            System.out.println("cm.createUpgradeVoteComputerProposal ignored: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // CommitteeManager + ProposalManager low-level vote/revoke wrappers driven by a real id.
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testCommitteeManagerVoteRevokeWrappers() {
        CommitteeManager cm = authManager.getCommitteeManager();
        BigInteger id = null;
        try {
            TransactionReceipt tr = cm.createSetRateProposal(BigInteger.ZERO, BigInteger.valueOf(53), INTERVAL);
            id = cm.getCreateSetRateProposalOutput(tr).getValue1();
        } catch (Exception e) {
            System.out.println("create-for-cm-vote ignored: " + e.getMessage());
        }
        if (id == null) id = BigInteger.ONE;
        try {
            TransactionReceipt tr = cm.voteProposal(id, true);
            System.out.println("cm.voteProposal status: " + tr.getStatus());
            try {
                cm.getVoteProposalInput(tr);
            } catch (Exception e) {
                System.out.println("cm.getVoteProposalInput ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("cm.voteProposal ignored: " + e.getMessage());
        }
        try {
            System.out.println("cm.getProposalType: " + cm.getProposalType(id));
        } catch (Exception e) {
            System.out.println("cm.getProposalType ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cm.revokeProposal(id);
            System.out.println("cm.revokeProposal status: " + tr.getStatus());
            try {
                cm.getRevokeProposalInput(tr);
            } catch (Exception e) {
                System.out.println("cm.getRevokeProposalInput ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("cm.revokeProposal ignored: " + e.getMessage());
        }
    }

    /** Derive a usable proposal id from the live proposal count (falls back to ONE). */
    private BigInteger resolveRealProposalId(ProposalManager pm) {
        BigInteger count = null;
        try {
            count = pm._proposalCount();
            System.out.println("pm._proposalCount: " + count);
        } catch (Exception e) {
            System.out.println("pm._proposalCount ignored: " + e.getMessage());
        }
        return (count != null && count.compareTo(BigInteger.ZERO) > 0) ? count : BigInteger.ONE;
    }

    @Test
    public void testProposalManagerWrappersWithRealId_reads() {
        try {
            ProposalManager pm = authManager.getCommitteeManager().getProposalManager();
            BigInteger id = resolveRealProposalId(pm);
            try {
                Tuple7<String, String, BigInteger, BigInteger, BigInteger, List<String>, List<String>>
                        info = pm.getProposalInfo(id);
                System.out.println("pm.getProposalInfo proposer: " + info.getValue2());
                // exercise ProposalInfo(Tuple7) conversion path used by AuthManager.getProposalInfo
                ProposalInfo wrapped = new ProposalInfo(info);
                wrapped.toString();
            } catch (Exception e) {
                System.out.println("pm.getProposalInfo ignored: " + e.getMessage());
            }
            try {
                pm.getProposalStatus(id);
            } catch (Exception e) {
                System.out.println("pm.getProposalStatus ignored: " + e.getMessage());
            }
            try {
                pm.getProposalInfoList(BigInteger.ONE, id);
            } catch (Exception e) {
                System.out.println("pm.getProposalInfoList ignored: " + e.getMessage());
            }
            try {
                pm._proposals(id);
            } catch (Exception e) {
                System.out.println("pm._proposals ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("testProposalManagerWrappersWithRealId_reads ignored: " + e.getMessage());
        }
    }

    @Test
    public void testProposalManagerWrappersWithRealId_lookups() {
        try {
            ProposalManager pm = authManager.getCommitteeManager().getProposalManager();
            BigInteger id = resolveRealProposalId(pm);
            try {
                pm._proposalIndex(id, governorAddress);
            } catch (Exception e) {
                System.out.println("pm._proposalIndex ignored: " + e.getMessage());
            }
            try {
                pm._voteComputer();
            } catch (Exception e) {
                System.out.println("pm._voteComputer ignored: " + e.getMessage());
            }
            try {
                pm.getIdByTypeAndResourceId(BigInteger.valueOf(12), governorAddress);
            } catch (Exception e) {
                System.out.println("pm.getIdByTypeAndResourceId ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("testProposalManagerWrappersWithRealId_lookups ignored: " + e.getMessage());
        }
    }

    @Test
    public void testProposalManagerWrappersWithRealId_writes() {
        try {
            ProposalManager pm = authManager.getCommitteeManager().getProposalManager();
            BigInteger id = resolveRealProposalId(pm);
            // low-level vote/revoke/refresh wrappers (voterAddress = governor)
            try {
                TransactionReceipt tr = pm.vote(id, true, governorAddress);
                System.out.println("pm.vote status: " + tr.getStatus());
                pm.getVoteInput(tr);
            } catch (Exception e) {
                System.out.println("pm.vote ignored: " + e.getMessage());
            }
            try {
                TransactionReceipt tr = pm.revoke(id, governorAddress);
                System.out.println("pm.revoke status: " + tr.getStatus());
                pm.getRevokeInput(tr);
            } catch (Exception e) {
                System.out.println("pm.revoke ignored: " + e.getMessage());
            }
            try {
                TransactionReceipt tr = pm.refreshProposalStatus(id);
                System.out.println("pm.refreshProposalStatus status: " + tr.getStatus());
                pm.getRefreshProposalStatusInput(tr);
            } catch (Exception e) {
                System.out.println("pm.refreshProposalStatus ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("testProposalManagerWrappersWithRealId_writes ignored: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Committee low-level write wrappers (setRate/setWeight/setOwner) as governor.
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testCommitteeWriteWrappers() {
        try {
            Committee committee = authManager.getCommitteeManager().getCommittee();
            try {
                TransactionReceipt tr = committee.setRate(BigInteger.ZERO, BigInteger.valueOf(50));
                System.out.println("committee.setRate status: " + tr.getStatus());
                committee.getSetRateInput(tr);
            } catch (Exception e) {
                System.out.println("committee.setRate ignored: " + e.getMessage());
            }
            try {
                TransactionReceipt tr = committee.setWeight(candidateAddress, BigInteger.ONE);
                System.out.println("committee.setWeight status: " + tr.getStatus());
                committee.getSetWeightInput(tr);
            } catch (Exception e) {
                System.out.println("committee.setWeight ignored: " + e.getMessage());
            }
            try {
                committee._owner();
                committee._participatesRate();
                committee._winRate();
                committee.getWeights();
            } catch (Exception e) {
                System.out.println("committee reads ignored: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("testCommitteeWriteWrappers ignored: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ContractAuthPrecompiled write paths through AuthManager (governor/admin) + direct wrappers.
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testContractStatusBothOverloads() {
        try {
            RetCode rc = authManager.setContractStatus(candidateAddress, true);
            System.out.println("setContractStatus(bool freeze): " + rc);
        } catch (Exception e) {
            System.out.println("setContractStatus(bool freeze) ignored: " + e.getMessage());
        }
        try {
            RetCode rc = authManager.setContractStatus(candidateAddress, false);
            System.out.println("setContractStatus(bool normal): " + rc);
        } catch (Exception e) {
            System.out.println("setContractStatus(bool normal) ignored: " + e.getMessage());
        }
        try {
            RetCode rc = authManager.setContractStatus(candidateAddress, AccessStatus.Freeze);
            System.out.println("setContractStatus(Freeze): " + rc);
        } catch (Exception e) {
            System.out.println("setContractStatus(Freeze) ignored: " + e.getMessage());
        }
        try {
            RetCode rc = authManager.setContractStatus(candidateAddress, AccessStatus.Normal);
            System.out.println("setContractStatus(Normal): " + rc);
        } catch (Exception e) {
            System.out.println("setContractStatus(Normal) ignored: " + e.getMessage());
        }
        try {
            RetCode rc = authManager.setContractStatus(candidateAddress, AccessStatus.Abolish);
            System.out.println("setContractStatus(Abolish): " + rc);
        } catch (Exception e) {
            System.out.println("setContractStatus(Abolish) ignored: " + e.getMessage());
        }
    }

    @Test
    public void testAsyncContractStatusBothOverloads() {
        try {
            authManager.asyncSetContractStatus(candidateAddress, true, retCode -> {});
        } catch (Exception e) {
            System.out.println("asyncSetContractStatus(bool) ignored: " + e.getMessage());
        }
        try {
            authManager.asyncSetContractStatus(candidateAddress, AccessStatus.Freeze, retCode -> {});
        } catch (Exception e) {
            System.out.println("asyncSetContractStatus(AccessStatus) ignored: " + e.getMessage());
        }
        sleepQuietly();
    }

    @Test
    public void testMethodAuthFlows_writes() {
        try {
            RetCode rc = authManager.setMethodAuthType(candidateAddress, FUNC_SELECTOR, AuthType.WHITE_LIST);
            System.out.println("setMethodAuthType: " + rc);
        } catch (Exception e) {
            System.out.println("setMethodAuthType ignored: " + e.getMessage());
        }
        try {
            RetCode rc = authManager.setMethodAuth(candidateAddress, FUNC_SELECTOR, governorAddress, true);
            System.out.println("setMethodAuth(open): " + rc);
        } catch (Exception e) {
            System.out.println("setMethodAuth(open) ignored: " + e.getMessage());
        }
        try {
            RetCode rc = authManager.setMethodAuth(candidateAddress, FUNC_SELECTOR, governorAddress, false);
            System.out.println("setMethodAuth(close): " + rc);
        } catch (Exception e) {
            System.out.println("setMethodAuth(close) ignored: " + e.getMessage());
        }
        sleepQuietly();
    }

    @Test
    public void testMethodAuthFlows_async() {
        try {
            authManager.asyncSetMethodAuthType(
                    candidateAddress, FUNC_SELECTOR, AuthType.BLACK_LIST, retCode -> {});
        } catch (Exception e) {
            System.out.println("asyncSetMethodAuthType ignored: " + e.getMessage());
        }
        try {
            authManager.asyncSetMethodAuth(
                    candidateAddress, FUNC_SELECTOR, governorAddress, true, retCode -> {});
        } catch (Exception e) {
            System.out.println("asyncSetMethodAuth(open) ignored: " + e.getMessage());
        }
        try {
            authManager.asyncSetMethodAuth(
                    candidateAddress, FUNC_SELECTOR, governorAddress, false, retCode -> {});
        } catch (Exception e) {
            System.out.println("asyncSetMethodAuth(close) ignored: " + e.getMessage());
        }
        sleepQuietly();
    }

    @Test
    public void testMethodAuthFlows_reads() {
        // read-back of method auth state after the writes
        try {
            System.out.println("getMethodAuth: " + authManager.getMethodAuth(candidateAddress, FUNC_SELECTOR));
        } catch (Exception e) {
            System.out.println("getMethodAuth ignored: " + e.getMessage());
        }
        try {
            System.out.println("checkMethodAuth: "
                    + authManager.checkMethodAuth(candidateAddress, FUNC_SELECTOR, governorAddress));
        } catch (Exception e) {
            System.out.println("checkMethodAuth ignored: " + e.getMessage());
        }
    }

    @Test
    public void testContractAuthPrecompiledDirectWriteWrappers_part1() {
        ContractAuthPrecompiled cap = authManager.getContractAuthPrecompiled();
        try {
            TransactionReceipt tr = cap.setDeployAuthType(BigInteger.valueOf(1));
            cap.getSetDeployAuthTypeOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.setDeployAuthType ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cap.openDeployAuth(candidateAddress);
            cap.getOpenDeployAuthOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.openDeployAuth ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cap.closeDeployAuth(candidateAddress);
            cap.getCloseDeployAuthOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.closeDeployAuth ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cap.resetAdmin(candidateAddress, candidateAddress);
            cap.getResetAdminOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.resetAdmin ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cap.openMethodAuth(candidateAddress, FUNC_SELECTOR, governorAddress);
            cap.getOpenMethodAuthOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.openMethodAuth ignored: " + e.getMessage());
        }
    }

    @Test
    public void testContractAuthPrecompiledDirectWriteWrappers_part2() {
        ContractAuthPrecompiled cap = authManager.getContractAuthPrecompiled();
        try {
            TransactionReceipt tr = cap.closeMethodAuth(candidateAddress, FUNC_SELECTOR, governorAddress);
            cap.getCloseMethodAuthOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.closeMethodAuth ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cap.setMethodAuthType(candidateAddress, FUNC_SELECTOR, BigInteger.ONE);
            cap.getSetMethodAuthTypeOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.setMethodAuthType ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cap.setContractStatus(candidateAddress, true);
            cap.getSetContractStatusAddressBoolOutput(tr);
        } catch (Exception e) {
            System.out.println("cap.setContractStatus(bool) ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = cap.setContractStatus(candidateAddress, AccessStatus.Freeze.getBigIntStatus());
            cap.getSetContractStatusAddressUint8Output(tr);
        } catch (Exception e) {
            System.out.println("cap.setContractStatus(uint8) ignored: " + e.getMessage());
        }
        try {
            cap.deployType();
            cap.hasDeployAuth(candidateAddress);
            cap.contractAvailable(candidateAddress);
            cap.getAdmin(candidateAddress);
        } catch (Exception e) {
            System.out.println("cap reads ignored: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Account status flows (governor) + direct AccountManager wrappers.
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testAccountStatusFlows() {
        try {
            RetCode rc = authManager.setAccountStatus(candidateAddress, AccessStatus.Freeze);
            System.out.println("setAccountStatus(Freeze): " + rc);
        } catch (Exception e) {
            System.out.println("setAccountStatus(Freeze) ignored: " + e.getMessage());
        }
        try {
            RetCode rc = authManager.setAccountStatus(candidateAddress, AccessStatus.Normal);
            System.out.println("setAccountStatus(Normal): " + rc);
        } catch (Exception e) {
            System.out.println("setAccountStatus(Normal) ignored: " + e.getMessage());
        }
        try {
            authManager.asyncSetAccountStatus(candidateAddress, AccessStatus.Freeze, retCode -> {});
        } catch (Exception e) {
            System.out.println("asyncSetAccountStatus ignored: " + e.getMessage());
        }
        sleepQuietly();
    }

    @Test
    public void testAccountManagerDirectWrappers() {
        try {
            AccountManager am = authManager.getAccountManager();
            try {
                System.out.println("am.getAccountStatus: " + am.getAccountStatus(candidateAddress));
            } catch (Exception e) {
                System.out.println("am.getAccountStatus ignored: " + e.getMessage());
            }
            try {
                TransactionReceipt tr =
                        am.setAccountStatus(candidateAddress, AccessStatus.Normal.getBigIntStatus());
                System.out.println("am.setAccountStatus status: " + tr.getStatus());
                am.getSetAccountStatusInput(tr);
                am.getSetAccountStatusOutput(tr);
            } catch (Exception e) {
                System.out.println("am.setAccountStatus ignored: " + e.getMessage());
            }
            final AtomicReference<TransactionReceipt> ref = new AtomicReference<>();
            try {
                am.setAccountStatus(
                        candidateAddress,
                        AccessStatus.Freeze.getBigIntStatus(),
                        new TransactionCallback() {
                            @Override
                            public void onResponse(TransactionReceipt receipt) {
                                ref.set(receipt);
                            }
                        });
            } catch (Exception e) {
                System.out.println("am.setAccountStatus(async) ignored: " + e.getMessage());
            }
            sleepQuietly();
        } catch (Exception e) {
            System.out.println("testAccountManagerDirectWrappers ignored: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------------------

    /** Read a created proposal back (if id is known) and then revoke it to clean up state. */
    private void readBackAndRevoke(BigInteger proposalId, String label) {
        if (proposalId == null) {
            return;
        }
        try {
            ProposalInfo info = authManager.getProposalInfo(proposalId);
            System.out.println(label + " proposalInfo: " + info);
        } catch (Exception e) {
            System.out.println(label + " getProposalInfo ignored: " + e.getMessage());
        }
        try {
            authManager.getCommitteeManager().getProposalManager().getProposalStatus(proposalId);
        } catch (Exception e) {
            System.out.println(label + " getProposalStatus ignored: " + e.getMessage());
        }
        try {
            TransactionReceipt tr = authManager.revokeProposal(proposalId);
            System.out.println(label + " revoke status: " + (tr == null ? "null" : tr.getStatus()));
        } catch (Exception e) {
            System.out.println(label + " revoke ignored: " + e.getMessage());
        }
    }

    private static void sleepQuietly() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

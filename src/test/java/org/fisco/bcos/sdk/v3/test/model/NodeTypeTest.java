package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.NodeType;
import org.junit.Assert;
import org.junit.Test;

public class NodeTypeTest {

    @Test
    public void testEnumValues() {
        NodeType[] values = NodeType.values();
        Assert.assertNotNull(values);
        Assert.assertEquals(4, values.length);
    }

    @Test
    public void testConsensusSealer() {
        NodeType type = NodeType.CONSENSUS_SEALER;
        Assert.assertNotNull(type);
        Assert.assertEquals("CONSENSUS_SEALER", type.name());
    }

    @Test
    public void testConsensusObserver() {
        NodeType type = NodeType.CONSENSUS_OBSERVER;
        Assert.assertNotNull(type);
        Assert.assertEquals("CONSENSUS_OBSERVER", type.name());
    }

    @Test
    public void testConsensusCandidateSealer() {
        NodeType type = NodeType.CONSENSUS_CANDIDATE_SEALER;
        Assert.assertNotNull(type);
        Assert.assertEquals("CONSENSUS_CANDIDATE_SEALER", type.name());
    }

    @Test
    public void testUnknown() {
        NodeType type = NodeType.UNKNOWN;
        Assert.assertNotNull(type);
        Assert.assertEquals("UNKNOWN", type.name());
    }

    @Test
    public void testValueOf() {
        NodeType type = NodeType.valueOf("CONSENSUS_SEALER");
        Assert.assertEquals(NodeType.CONSENSUS_SEALER, type);

        type = NodeType.valueOf("CONSENSUS_OBSERVER");
        Assert.assertEquals(NodeType.CONSENSUS_OBSERVER, type);

        type = NodeType.valueOf("CONSENSUS_CANDIDATE_SEALER");
        Assert.assertEquals(NodeType.CONSENSUS_CANDIDATE_SEALER, type);

        type = NodeType.valueOf("UNKNOWN");
        Assert.assertEquals(NodeType.UNKNOWN, type);
    }

    @Test
    public void testEnumComparison() {
        Assert.assertSame(NodeType.CONSENSUS_SEALER, NodeType.valueOf("CONSENSUS_SEALER"));
        Assert.assertNotSame(NodeType.CONSENSUS_SEALER, NodeType.CONSENSUS_OBSERVER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidValueOf() {
        NodeType.valueOf("INVALID_TYPE");
    }
}

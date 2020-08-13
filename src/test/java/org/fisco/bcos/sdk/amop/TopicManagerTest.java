/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.amop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.amop.topic.TopicManager;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.crypto.keystore.PEMManager;
import org.junit.Assert;
import org.junit.Test;

public class TopicManagerTest {
    private AmopCallback cb1 = new TestAmopCallback();
    private AmopCallback cb2 = new TestAmopCallback();
    private AmopCallback cb3 = new TestAmopCallback();

    @Test
    public void testAddTopic() {
        TopicManager topicManager = new TopicManager();
        topicManager.addTopic("test", null);
        Assert.assertEquals("test", topicManager.getFullTopicString("test"));

        String keyFile =
                TopicManagerTest.class
                        .getClassLoader()
                        .getResource(
                                "keystore/ecdsa/0x0fc3c4bb89bd90299db4c62be0174c4966286c00.pem")
                        .getPath();
        KeyManager km = new PEMManager(keyFile);
        topicManager.addPrivateTopicSubscribe("priv", km, null);
        Assert.assertEquals(
                "#!$TopicNeedVerify_priv",
                topicManager.getFullTopicString("priv").substring(17, 40));

        List<KeyManager> list = new ArrayList<>();
        list.add(km);
        topicManager.addPrivateTopicSend("priv2", list);
        Assert.assertEquals(
                "#!$PushChannel_#!$TopicNeedVerify_priv2",
                topicManager.getFullTopicString("priv2"));
        Assert.assertNotNull(topicManager.getPublicKeysByTopic("#!$TopicNeedVerify_priv2"));

        Set<String> topics = topicManager.getTopicNames();
        Assert.assertTrue(topics.contains("test"));
        Assert.assertEquals(3, topics.size());
        Assert.assertTrue(topics.contains("priv"));
        Assert.assertTrue(topics.contains("priv2"));
    }

    @Test
    public void testCallback() {
        TopicManager topicManager = getTestTopicManager();
        // test set default callback
        Assert.assertNull(topicManager.getCallback("test"));
        topicManager.setCallback(cb1);
        Assert.assertNotNull(topicManager.getCallback("test"));
        Assert.assertEquals(cb1, topicManager.getCallback("test"));

        // test set spacial callback
        topicManager.addTopic("test", cb2);
        Assert.assertEquals(cb2, topicManager.getCallback("test"));

        // test set private topic callback
        topicManager.addPrivateTopicCallback("priv", cb3);
        Assert.assertEquals(cb3, topicManager.getCallback("#!$TopicNeedVerify_priv"));
    }

    @Test
    public void testRemoveTopic() {
        TopicManager topicManager = getTestTopicManager();
        topicManager.setCallback(cb1);
        Set<String> topics = topicManager.getTopicNames();
        Assert.assertTrue(topics.contains("priv2"));
        Assert.assertEquals(cb3, topicManager.getCallback("#!$TopicNeedVerify_priv2"));
        Assert.assertNotNull(topicManager.getPublicKeysByTopic("#!$TopicNeedVerify_priv2"));
        Assert.assertEquals(3, topics.size());
        Assert.assertNull(topicManager.getPrivateKeyByTopic("#!$TopicNeedVerify_priv2"));

        topicManager.removeTopic("priv2");
        topics = topicManager.getTopicNames();
        Assert.assertFalse(topics.contains("priv2"));
        Assert.assertEquals(cb1, topicManager.getCallback("#!$TopicNeedVerify_priv2"));
        Assert.assertNull(topicManager.getPublicKeysByTopic("#!$TopicNeedVerify_priv2"));
        Assert.assertEquals(2, topics.size());
        Assert.assertNull(topicManager.getPrivateKeyByTopic("#!$TopicNeedVerify_priv2"));
    }

    @Test
    public void testBlockNotify() {
        TopicManager tm = getTestTopicManager();
        List<String> group = new ArrayList<>();
        group.add("1");
        group.add("2");
        tm.addBlockNotify("127.0.0.1:3033", group);

        Set<String> topics = tm.getSubByPeer("127.0.0.1:3033");
        Assert.assertEquals(7, topics.size());
        Assert.assertTrue(topics.contains("_block_notify_1"));
        Assert.assertTrue(topics.contains("_block_notify_2"));

        topics = tm.getSubByPeer("127.0.0.1:8000");
        Assert.assertEquals(5, topics.size());
        Assert.assertFalse(topics.contains("_block_notify_1"));
        Assert.assertFalse(topics.contains("_block_notify_2"));
    }

    @Test
    public void testUpdateUUID() {
        TopicManager tm = getTestTopicManager();
        Set<String> topics = tm.getSubByPeer("127.0.0.1:3033");
        for (String topic : topics) {
            System.out.println(topic);
        }
        tm.updatePrivateTopicUUID();
        System.out.println("*****");
        topics = tm.getSubByPeer("127.0.0.1:3033");
        for (String topic : topics) {
            System.out.println(topic);
        }
    }

    private TopicManager getTestTopicManager() {
        TopicManager topicManager = new TopicManager();
        topicManager.addTopic("test", null);
        String keyFile =
                TopicManagerTest.class
                        .getClassLoader()
                        .getResource(
                                "keystore/ecdsa/0x0fc3c4bb89bd90299db4c62be0174c4966286c00.pem")
                        .getPath();
        KeyManager km = new PEMManager(keyFile);
        topicManager.addPrivateTopicSubscribe("priv", km, null);
        List<KeyManager> list = new ArrayList<>();
        list.add(km);
        topicManager.addPrivateTopicSend("priv2", list);
        topicManager.addPrivateTopicCallback("priv2", cb3);
        return topicManager;
    }

    public class TestAmopCallback extends AmopCallback {

        @Override
        public void onSubscribedTopicMsg(AmopMsgIn msg) {
            // do nothing
        }
    }
}

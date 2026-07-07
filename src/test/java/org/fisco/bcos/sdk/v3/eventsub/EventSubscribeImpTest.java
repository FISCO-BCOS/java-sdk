package org.fisco.bcos.sdk.v3.eventsub;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import org.fisco.bcos.sdk.jni.event.EventSubJniObj;
import org.fisco.bcos.sdk.v3.client.Client;
import org.junit.Test;
import org.objenesis.ObjenesisStd;

public class EventSubscribeImpTest {

    /**
     * Allocates an {@link EventSubscribeImp} without running its constructor (which would trigger a
     * JNI call to {@code EventSubJniObj.build}), then injects the supplied fields via reflection.
     */
    private static EventSubscribeImp allocateEventSubscribeImp(
            Client client, EventSubJniObj eventSubJniObj, boolean ownsClient) throws Exception {
        EventSubscribeImp instance = new ObjenesisStd().newInstance(EventSubscribeImp.class);
        setField(instance, "ownerClient", client);
        setBooleanField(instance, "ownsClient", ownsClient);
        setField(instance, "eventSubJniObj", eventSubJniObj);
        setField(instance, "groupId", "group0");
        return instance;
    }

    @Test
    public void testStartActivatesEventChannelForBothCases() throws Exception {
        Client client = mock(Client.class);

        // Borrowed client: start() should still delegate to ownerClient.start()
        EventSubscribeImp borrowed =
                allocateEventSubscribeImp(client, mock(EventSubJniObj.class), false);
        borrowed.start();
        verify(client, times(1)).start();

        // Owned client: start() should also delegate to ownerClient.start()
        EventSubscribeImp owned =
                allocateEventSubscribeImp(client, mock(EventSubJniObj.class), true);
        owned.start();
        verify(client, times(2)).start();
    }

    @Test
    public void testStopOnSharedClientOnlyUnsubscribesOnce() throws Exception {
        Client client = mock(Client.class);
        EventSubJniObj eventSubJniObj = mock(EventSubJniObj.class);
        when(eventSubJniObj.getAllSubscribedEvents())
                .thenReturn(new HashSet<>(Arrays.asList("event-a", "event-b")));

        EventSubscribeImp eventSubscribe =
                allocateEventSubscribeImp(client, eventSubJniObj, false);

        eventSubscribe.stop();
        eventSubscribe.stop();

        verify(eventSubJniObj, times(1)).getAllSubscribedEvents();
        verify(eventSubJniObj, times(1)).unsubscribeEvent("event-a");
        verify(eventSubJniObj, times(1)).unsubscribeEvent("event-b");
        verify(eventSubJniObj, never()).stop();
        verify(client, never()).stop();
    }

    @Test
    public void testDestroyOnOwnedClientDelegatesLifecycleOnce() throws Exception {
        Client client = mock(Client.class);
        EventSubJniObj eventSubJniObj = mock(EventSubJniObj.class);
        when(eventSubJniObj.getAllSubscribedEvents())
                .thenReturn(new HashSet<>(Arrays.asList("event-a")));

        EventSubscribeImp eventSubscribe =
                allocateEventSubscribeImp(client, eventSubJniObj, true);

        eventSubscribe.destroy();
        eventSubscribe.destroy();

        verify(eventSubJniObj, times(1)).unsubscribeEvent("event-a");
        verify(eventSubJniObj, never()).stop();
        verify(client, times(1)).stop();
        verify(client, times(1)).destroy();
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void setBooleanField(Object target, String fieldName, boolean value)
            throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(target, value);
    }
}

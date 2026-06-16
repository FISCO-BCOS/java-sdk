package org.fisco.bcos.sdk.v3.eventsub;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.fisco.bcos.sdk.jni.event.EventSubJniObj;
import org.fisco.bcos.sdk.v3.client.Client;
import org.junit.Test;

public class EventSubscribeImpTest {

    @Test
    public void testStopOnSharedClientOnlyUnsubscribesOnce() throws Exception {
        Client client = mock(Client.class);
        when(client.getGroup()).thenReturn("group0");
        when(client.getNativePointer()).thenReturn(0L);

        EventSubscribeImp eventSubscribe = new EventSubscribeImp(client, null);
        EventSubJniObj eventSubJniObj = mock(EventSubJniObj.class);
        when(eventSubJniObj.getAllSubscribedEvents())
                .thenReturn(new HashSet<>(Arrays.asList("event-a", "event-b")));
        setField(eventSubscribe, "eventSubJniObj", eventSubJniObj);

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
        when(client.getGroup()).thenReturn("group0");
        when(client.getNativePointer()).thenReturn(0L);

        EventSubscribeImp eventSubscribe = new EventSubscribeImp(client, null, true);
        EventSubJniObj eventSubJniObj = mock(EventSubJniObj.class);
        when(eventSubJniObj.getAllSubscribedEvents())
                .thenReturn(new HashSet<>(Arrays.asList("event-a")));
        setField(eventSubscribe, "eventSubJniObj", eventSubJniObj);

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
}

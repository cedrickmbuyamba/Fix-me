package org.snitchers.fix_me.utilities;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentMap;

public class DecoratedHandler {
    protected ConcurrentMap<SocketChannel, Integer> brokers;

    public DecoratedHandler(ConcurrentMap<SocketChannel, Integer> brokers) {
        this.brokers = brokers;
    }
}

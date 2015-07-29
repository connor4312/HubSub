package io.peet.hubsub.server;


import java.net.InetSocketAddress;

/**
 * The Open class can be sent to the Server to start it listening for
 * incoming events.
 */
public class Open {
    private InetSocketAddress addr;

    /**
     * Specify the port to open the connection on.
     * @param addr the address to listen on
     */
    public Open(InetSocketAddress addr) {
        this.addr = addr;
    }

    /**
     * Returns the socket address we intend to open on.
     */
    public InetSocketAddress getAddr() {
        return addr;
    }
}

package de.is24.util.karmatestrunner;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class PortFinder {


    // the ports above 49151 are dynamic and/or private
    private static final int MAX_PORT_NUMBER = 49151;

    /**
     * Finds a free port between minPortNumber and MAX_PORT_NUMBER
     *
     * @return a free port
     * @throw RuntimeException if a port could not be found
     */
    public static int findFreePort(int minPortNumber) {
        for (int i = minPortNumber; i <= MAX_PORT_NUMBER; i++) {
            if (available(i)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find an available port between " +
                minPortNumber + " and " + MAX_PORT_NUMBER);
    }

    /**
     * Returns true if the specified port is available on this host.
     *
     * @param port the port to check
     * @return true if the port is available, false otherwise
     */
    private static boolean available(final int port) {
        ServerSocket serverSocket = null;
        DatagramSocket dataSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            dataSocket = new DatagramSocket(port);
            dataSocket.setReuseAddress(true);
            return true;
        } catch (final IOException e) {
            return false;
        } finally {
            if (dataSocket != null) {
                dataSocket.close();
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (final IOException e) {
                    // can never happen
                }
            }
        }
    }
}

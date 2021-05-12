package com.network.test;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32Util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetInfo {
    private static final String NEWLINE = System.lineSeparator();

    /*
    Sourced from:
     https://github.com/mattsheppard/gethostname4j/blob/master/src/main/java/com/kstruct/gethostname4j/Hostname.java
     */

    private interface UnixCLibrary extends Library {
        UnixCLibrary INSTANCE = (UnixCLibrary) Native.load("c", UnixCLibrary.class);
        public int gethostname(byte[] hostname, int bufferSize);
    }


    public static void main(String[] args) throws java.net.SocketException, UnknownHostException {
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        appendLine("[Java NetworkInterface Info]", sb);

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            Enumeration<InetAddress> addrs = ni.getInetAddresses();

            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                appendLine("========================================================================================", sb);
                appendLine( ni.getDisplayName(), sb);
                appendLine( "InetAddress.getLocalHost() => " + InetAddress.getLocalHost().toString(), sb);
                appendLine( "InetAddress.getLocalHost().getHostName() => " + InetAddress.getLocalHost().getHostName(), sb);
                appendLine("      Host address: " + addr.getHostAddress(), sb);
                appendLine("          Hostname: " + addr.getHostName() , sb);
                appendLine("Canonical Hostname: " + addr.getCanonicalHostName(), sb);
                appendLine(" isWildcardAddress: " + addr.isAnyLocalAddress(), sb);
                appendLine(" isLoopbackAddress: " + addr.isLoopbackAddress(), sb);
                appendLine(" isLinkLocalAddress: " + addr.isLinkLocalAddress(), sb);
                appendLine("JNA-based Hostname: " + getHostname(), sb);
                appendLine("========================================================================================", sb);
            }
        }
        System.out.println(sb.toString());
    }

    private static void appendLine(Object o, StringBuilder b) {
        b.append(o.toString());
        b.append(NEWLINE);
    }

    /*
    Sourced from:
    https://github.com/mattsheppard/gethostname4j/blob/master/src/main/java/com/kstruct/gethostname4j/Hostname.java
     */
    public static String getHostname() {
        if (Platform.isWindows()) {
            return Kernel32Util.getComputerName();
        } else {
            byte[] hostnameBuffer = new byte[4097];
            // http://pubs.opengroup.org/onlinepubs/9699919799/basedefs/limits.h.html suggests
            // the actual limit would be 255.

            int result = UnixCLibrary.INSTANCE.gethostname(hostnameBuffer, hostnameBuffer.length);
            if (result != 0) {
                throw new RuntimeException("gethostname call failed");
            }

            return Native.toString(hostnameBuffer);
        }
    }
}

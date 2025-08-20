package io.github.plotnik.obsidian_web_editor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Component
public class StartupBanner implements CommandLineRunner {

    @Value("${server.port:8080}")
    private String serverPort;

    @Override
    public void run(String... args) throws Exception {
        String localIpAddress = getLocalIpAddress();
        
        System.out.println();
        System.out.println("            ::   Local URL: http://localhost:" + serverPort + " ::");
        if (localIpAddress != null) {
            System.out.println("            :: Network URL: http://" + localIpAddress + ":" + serverPort + " ::");
        }
        System.out.println();
    }

    private String getLocalIpAddress() {
        String localIpAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // Skip loopback addresses and IPv6 addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        localIpAddress = inetAddress.getHostAddress();
                        break;
                    }
                }
                if (localIpAddress != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            // Log error if needed, but don't break the application
        }
        return localIpAddress;
    }
}

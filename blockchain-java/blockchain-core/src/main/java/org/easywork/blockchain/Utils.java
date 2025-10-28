package org.easywork.blockchain;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern IP_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?\\.){3})(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    public static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateBlockchainAddress(PublicKey publicKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(publicKey.getEncoded());
            // 取后20字节作为地址（类似以太坊）
            byte[] addressBytes = new byte[20];
            System.arraycopy(hash, hash.length - 20, addressBytes, 0, 20);
            StringBuilder sb = new StringBuilder();
            for (byte b : addressBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    public List<String> findNeighbors(String myHost, int myPort, int startIp, int endIp, int startPort, int endPort) {
        String myAddress = String.format("%s:%s", myHost, myPort);
        Matcher matcher = IP_PATTERN.matcher(myAddress);
        if (!matcher.matches()) {
            return List.of();
        }
        String prefixHost = matcher.group(1);
        int lastIp;
        try {
            lastIp = Integer.parseInt(matcher.group(matcher.groupCount()));
        } catch (NumberFormatException e) {
            return List.of();
        }

        List<String> neighbors = new ArrayList<>();

        // 遍历端口范围和 IP 范围
        for (int port = startPort; port <= endPort; port++) {
            for (int ip = startIp; ip <= endIp; ip++) {
                // 计算猜测的 IP 最后一段（注意：Java 中 byte 是有符号的，这里按无符号处理）
                int guessIpLast = lastIp + (ip & 0xFF);
                String guessHost = prefixHost + guessIpLast;
                String guessTarget = String.format("%s:%s", guessHost, port);

                // 排除自身地址且验证主机是否可达
                if (!guessTarget.equals(myAddress) && isFoundHost(guessHost, port)) {
                    neighbors.add(guessTarget);
                }
            }
        }
        return neighbors;
    }

    private boolean isFoundHost(String guessHost, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(guessHost, port), 500);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
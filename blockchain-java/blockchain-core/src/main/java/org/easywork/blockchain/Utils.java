package org.easywork.blockchain;

import cn.hutool.core.net.NetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern IP_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?\\.){3})(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    //private static final Pattern IP_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.)(\\d+)");

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

    public static List<String> findNeighbors(String myHost, int myPort, int startIp, int endIp, int startPort, int endPort) {
        String myAddress = String.format("%s:%s", myHost, myPort);
        Matcher matcher = IP_PATTERN.matcher(myHost);
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

    private static boolean isFoundHost(String guessHost, int port) {
//        return NetUtil.isOpen(new InetSocketAddress(guessHost, port), 5000);
//        try (Socket socket = new Socket()) {
//            socket.connect(new InetSocketAddress(guessHost, port), 5000);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
        // 构建 nc 命令：-z 只检测端口，-w 超时时间（秒）
        String command = String.format("nc -z -w %d %s %d", 1, guessHost, port);
        Process process = null;
        try {
            // 执行命令
            process = Runtime.getRuntime().exec(command);
            // 等待命令执行完成，超时时间略大于 nc 的超时
            boolean finished = process.waitFor(500, TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroy(); // 超时未完成，强制终止
                return false;
            }
            // 命令退出码为 0 表示成功
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) {
                process.destroy(); // 确保进程终止
            }
        }
    }
}
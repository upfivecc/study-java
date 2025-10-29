package org.easywork.blockchain.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern IP_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?\\.){3})(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    //private static final Pattern IP_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.)(\\d+)");


    public static String sign(PrivateKey privateKey, String data) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String data, String signatureStr, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            byte[] sigBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(sigBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


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


    public static PublicKey generatePublicKey(String publicKeyStr) {
        try {
            byte[] publicKeyBase64 = Base64.getDecoder().decode(publicKeyStr);
            // 1. 将Base64字符串解码为字节数组（对应publicKey.getEncoded()的原始字节）
            //byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

            // 2. 使用X509EncodedKeySpec规范（适用于大多数公钥格式，如RSA/EC的公钥）
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBase64);

            // 3. 通过密钥工厂生成PublicKey对象
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey generatePrivateKey(String privateKeyStr) {
        try {
            byte[] privateKeyBase64 = Base64.getDecoder().decode(privateKeyStr);
            // 1. 将Base64字符串解码为字节数组（对应publicKey.getEncoded()的原始字节）
            //byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

            // 2. 使用X509EncodedKeySpec规范（适用于大多数公钥格式，如RSA/EC的公钥）
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBase64);

            // 3. 通过密钥工厂生成PublicKey对象
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
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
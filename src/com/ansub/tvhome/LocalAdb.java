package com.ansub.tvhome;

import android.content.Context;
import android.util.AtomicFile;
import android.util.Base64;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.*;

/** Private loopback-only legacy ADB identity. Automatic connections never request new trust. */
final class LocalAdb implements Closeable {
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;
    private int remote;
    private byte[] pending = new byte[0];
    private int offset;
    LocalAdb(Context context, boolean authorize) throws Exception {
        PrivateKey privateKey = key(context);
        RSAPrivateKey rsa = (RSAPrivateKey) privateKey;
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsa.getModulus(), java.math.BigInteger.valueOf(65537)));
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("127.0.0.1", 5555), 2000); socket.setSoTimeout(authorize ? 45000 : 3000);
            input = socket.getInputStream(); output = socket.getOutputStream();
            AdbWire.send(output, AdbWire.CNXN, 0x01000000, 4096, bytes("host::\0"));
            boolean signed = false, published = false;
            for (int i = 0; i < 4; i++) {
                AdbWire.Packet packet = AdbWire.receive(input);
                if (packet.command == AdbWire.CNXN) return;
                if (packet.command != AdbWire.AUTH || packet.arg0 != 1 || packet.data.length != 20) throw new IOException("Unexpected ADB handshake");
                if (!signed) {
                    Signature signature = Signature.getInstance("NONEwithRSA"); signature.initSign(privateKey);
                    signature.update(new byte[] {0x30,0x21,0x30,0x09,0x06,0x05,0x2b,0x0e,0x03,0x02,0x1a,0x05,0x00,0x04,0x14});
                    signature.update(packet.data); AdbWire.send(output, AdbWire.AUTH, 2, 0, signature.sign()); signed = true;
                } else if (authorize && !published) {
                    String value = Base64.encodeToString(AdbWire.publicKey((java.security.interfaces.RSAPublicKey) publicKey), Base64.NO_WRAP) + " overlook@tv\0";
                    AdbWire.send(output, AdbWire.AUTH, 3, 0, bytes(value)); published = true;
                } else throw new SecurityException("TV debugging authorization required");
            }
            throw new IOException("ADB handshake incomplete");
        } catch (Exception e) { socket.close(); throw e; }
    }
    private static synchronized PrivateKey key(Context context) throws Exception {
        AtomicFile file = new AtomicFile(new File(context.getFilesDir(), "helper-adb-key.pk8"));
        byte[] encoded;
        if (file.getBaseFile().exists()) {
            try (InputStream input = file.openRead()) { encoded = limited(input, 8192); }
        } else {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4));
            encoded = generator.generateKeyPair().getPrivate().getEncoded();
            FileOutputStream output = null;
            try { output = file.startWrite(); output.write(encoded); file.finishWrite(output); }
            catch (Exception e) { if (output != null) file.failWrite(output); throw e; }
            file.getBaseFile().setReadable(false, false); file.getBaseFile().setReadable(true, true);
            file.getBaseFile().setWritable(false, false); file.getBaseFile().setWritable(true, true);
        }
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }
    static byte[] limited(InputStream input, int limit) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream(); byte[] buffer = new byte[4096]; int read;
        while ((read = input.read(buffer)) != -1) { if (result.size() + read > limit) throw new IOException("Input too large"); result.write(buffer, 0, read); }
        return result.toByteArray();
    }
    void open(String service) throws Exception {
        AdbWire.send(output, AdbWire.OPEN, 1, 0, bytes(service + "\0"));
        AdbWire.Packet packet = AdbWire.receive(input);
        if (packet.command != AdbWire.OKAY || packet.arg1 != 1) throw new IOException("ADB service rejected");
        remote = packet.arg0;
    }
    void write(byte[] data) throws Exception {
        for (int start = 0; start < data.length; start += 4096) {
            byte[] chunk = java.util.Arrays.copyOfRange(data, start, Math.min(start + 4096, data.length));
            AdbWire.send(output, AdbWire.WRTE, 1, remote, chunk);
            AdbWire.Packet reply = AdbWire.receive(input);
            if (reply.command != AdbWire.OKAY || reply.arg0 != remote || reply.arg1 != 1) throw new IOException("ADB write rejected");
        }
    }
    private byte[] read(int length) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        while (result.size() < length) {
            if (offset == pending.length) {
                AdbWire.Packet packet = AdbWire.receive(input);
                if (packet.command != AdbWire.WRTE || packet.arg0 != remote || packet.arg1 != 1) throw new IOException("ADB stream ended");
                AdbWire.send(output, AdbWire.OKAY, 1, remote, new byte[0]); pending = packet.data; offset = 0;
            }
            int count = Math.min(length - result.size(), pending.length - offset);
            result.write(pending, offset, count); offset += count;
        }
        return result.toByteArray();
    }
    void upload(byte[] jar) throws Exception {
        open("sync:");
        sync("SEND", bytes("/data/local/tmp/overlook-recents-helper.next.jar,384"));
        for (int i = 0; i < jar.length; i += 32768) sync("DATA", java.util.Arrays.copyOfRange(jar, i, Math.min(jar.length, i + 32768)));
        write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putInt(AdbWire.word("DONE")).putInt((int)(System.currentTimeMillis() / 1000)).array());
        ByteBuffer response = ByteBuffer.wrap(read(8)).order(ByteOrder.LITTLE_ENDIAN);
        if (response.getInt() != AdbWire.word("OKAY") || response.getInt() != 0) throw new IOException("Helper transfer failed");
    }
    private void sync(String command, byte[] data) throws Exception {
        write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putInt(AdbWire.word(command)).putInt(data.length).array()); write(data);
    }
    void startHelper() throws Exception {
        open("shell,raw:chmod 400 /data/local/tmp/overlook-recents-helper.next.jar && mv -f /data/local/tmp/overlook-recents-helper.next.jar /data/local/tmp/overlook-recents-helper.jar && (umask 077; CLASSPATH=/data/local/tmp/overlook-recents-helper.jar nohup app_process /system/bin com.ansub.tvhome.RecentsHelper serve 0 >/data/local/tmp/overlook-recents-helper.log 2>&1 </dev/null & sleep 1)");
        int total = 0;
        for (int i = 0; i < 32; i++) {
            AdbWire.Packet packet = AdbWire.receive(input);
            if (packet.arg0 != remote || packet.arg1 != 1) throw new IOException("Invalid shell channel");
            if (packet.command == AdbWire.CLSE) { AdbWire.send(output, AdbWire.CLSE, 1, remote, new byte[0]); return; }
            if (packet.command != AdbWire.WRTE || (total += packet.data.length) > 65536) throw new IOException("Invalid shell output");
            AdbWire.send(output, AdbWire.OKAY, 1, remote, new byte[0]);
        }
        throw new IOException("Helper command incomplete");
    }
    private static byte[] bytes(String text) { return text.getBytes(StandardCharsets.UTF_8); }
    public void close() throws IOException { socket.close(); }
}

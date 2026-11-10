package com.ansub.tvhome;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

/** Legacy ADB framing and Android RSA public-key encoding; no Android dependencies. */
final class AdbWire {
    static int word(String value) { return value.charAt(0) | value.charAt(1) << 8 | value.charAt(2) << 16 | value.charAt(3) << 24; }
    static final int CNXN = word("CNXN"), AUTH = word("AUTH"), OPEN = word("OPEN"), OKAY = word("OKAY"), WRTE = word("WRTE"), CLSE = word("CLSE");
    static final class Packet {
        int command, arg0, arg1; byte[] data;
        Packet(int command, int arg0, int arg1, byte[] data) { this.command = command; this.arg0 = arg0; this.arg1 = arg1; this.data = data; }
    }
    static byte[] read(InputStream input, int length) throws IOException {
        byte[] bytes = new byte[length]; int offset = 0;
        while (offset < length) { int n = input.read(bytes, offset, length - offset); if (n < 0) throw new IOException("ADB closed"); offset += n; }
        return bytes;
    }
    static Packet receive(InputStream input) throws IOException {
        ByteBuffer header = ByteBuffer.wrap(read(input, 24)).order(ByteOrder.LITTLE_ENDIAN);
        int command = header.getInt(), a = header.getInt(), b = header.getInt(), length = header.getInt(), sum = header.getInt(), magic = header.getInt();
        if (length < 0 || length > 1024 * 1024 || magic != (command ^ -1)) throw new IOException("Invalid ADB frame");
        byte[] bytes = read(input, length); int checksum = 0; for (byte value : bytes) checksum += value & 255;
        if (checksum != sum) throw new IOException("Invalid ADB checksum");
        return new Packet(command, a, b, bytes);
    }
    static void send(OutputStream output, int command, int a, int b, byte[] data) throws IOException {
        int sum = 0; for (byte value : data) sum += value & 255;
        output.write(ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN).putInt(command).putInt(a).putInt(b)
                .putInt(data.length).putInt(sum).putInt(command ^ -1).array());
        output.write(data); output.flush();
    }
    static byte[] publicKey(RSAPublicKey key) {
        BigInteger n = key.getModulus(), base = BigInteger.ONE.shiftLeft(32);
        ByteBuffer buffer = ByteBuffer.allocate(524).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(64).putInt(n.modInverse(base).negate().mod(base).intValue());
        little(buffer, n, 256); little(buffer, BigInteger.ONE.shiftLeft(4096).mod(n), 256);
        buffer.putInt(key.getPublicExponent().intValue()); return buffer.array();
    }
    private static void little(ByteBuffer buffer, BigInteger number, int length) {
        byte[] value = number.toByteArray();
        for (int i = 0; i < length; i++) buffer.put(i < value.length ? value[value.length - 1 - i] : (byte) 0);
    }
}

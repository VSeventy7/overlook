package com.ansub.tvhome;

import java.io.*;
import java.math.BigInteger;
import java.nio.*;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

public final class PlaybackChecks {
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    public static void main(String[] args) throws Exception {
        PlaybackRules.Gate gate = new PlaybackRules.Gate();
        for (int i = 0; i < 12; i++) check(!gate.sample("app", "movie", i * 5000, i * 5000, true), "short playback rejected");
        check(gate.sample("app", "movie", 60000, 60000, true), "sustained playback qualifies");
        check(!gate.sample("app", "other", 65000, 90000, true), "title change resets");
        check(!gate.sample("app", "other", 70000, 900000, true), "seek cannot qualify");
        check(!gate.sample("app", "other", 75000, 905000, false), "pause resets");
        gate.clear();
        for (int i = 0; i < 30; i++) check(!gate.sample("app", "movie", i * 5000, 70000, true), "stale progress rejected");
        gate.clear();
        gate.sample("app", "movie", 0, 60000, true);
        check(!gate.sample("app", "movie", 60000, 120000, true), "sampling gap resets");
        check(PlaybackRules.generic("Netflix", "Netflix"), "generic app title rejected");
        check(!PlaybackRules.generic("Arrival", "Netflix"), "real title accepted");
        check(PlaybackRules.normalize("WALL·E").equals(PlaybackRules.normalize("Wall-E")), "punctuation matching");
        check(PlaybackRules.expired(1, 1 + PlaybackRules.RETENTION_MS), "30-day boundary expires");
        check(!PlaybackRules.expired(1, PlaybackRules.RETENTION_MS), "younger records retained");
        String dump = "package=com.netflix.ninja\n active=true\n state=PlaybackState {state=3, position=120000, speed=1.0}\n metadata: size=2, description=Movie, With Comma, null, null\n";
        java.util.List<PlaybackDump.Session> sessions = PlaybackDump.parse(dump);
        check(sessions.size() == 1 && sessions.get(0).title.equals("Movie, With Comma") && sessions.get(0).playing, "dump preserves commas and playback state");
        check(PlaybackDump.parse(dump.replace(", null, null", ", ambiguous subtitle, null")).isEmpty(), "ambiguous title rejected");
        check(PlaybackDump.parse(dump.replace("active=true", "active=false")).isEmpty(), "inactive session rejected");
        byte[] payload = {0, 127, (byte)255, 10}; ByteArrayOutputStream output = new ByteArrayOutputStream();
        AdbWire.send(output, AdbWire.WRTE, 4, 9, payload);
        AdbWire.Packet packet = AdbWire.receive(new ByteArrayInputStream(output.toByteArray()));
        check(packet.command == AdbWire.WRTE && packet.arg0 == 4 && packet.arg1 == 9 && Arrays.equals(packet.data, payload), "ADB packet roundtrip");
        byte[] corrupt = output.toByteArray(); corrupt[24] ^= 1;
        try { AdbWire.receive(new ByteArrayInputStream(corrupt)); throw new AssertionError("checksum accepted"); } catch (IOException expected) { }
        byte[] huge = output.toByteArray(); ByteBuffer.wrap(huge).order(ByteOrder.LITTLE_ENDIAN).putInt(12, Integer.MAX_VALUE);
        try { AdbWire.receive(new ByteArrayInputStream(huge)); throw new AssertionError("huge frame accepted"); } catch (IOException expected) { }
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(2048);
        RSAPublicKey key = (RSAPublicKey) generator.generateKeyPair().getPublic();
        ByteBuffer encoded = ByteBuffer.wrap(AdbWire.publicKey(key)).order(ByteOrder.LITTLE_ENDIAN);
        check(encoded.getInt() == 64, "ADB key words"); long inverse = encoded.getInt() & 0xffffffffL;
        byte[] modulusLE = new byte[256]; encoded.get(modulusLE);
        check(fromLittle(modulusLE).equals(key.getModulus()), "ADB modulus encoding");
        check(key.getModulus().multiply(BigInteger.valueOf(inverse)).add(BigInteger.ONE).mod(BigInteger.ONE.shiftLeft(32)).signum() == 0, "ADB Montgomery inverse");
        byte[] rrLE = new byte[256]; encoded.get(rrLE);
        check(fromLittle(rrLE).equals(BigInteger.ONE.shiftLeft(4096).mod(key.getModulus())), "ADB Montgomery RR");
        check(encoded.getInt() == 65537, "ADB exponent");
        System.out.println("Playback qualification, retention and ADB wire/key checks passed");
    }
    private static BigInteger fromLittle(byte[] bytes) {
        byte[] big = new byte[bytes.length]; for (int i = 0; i < bytes.length; i++) big[i] = bytes[bytes.length - i - 1];
        return new BigInteger(1, big);
    }
}

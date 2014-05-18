package ch.ethz.jcd.main.utils;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidKeyException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class VCrypterTest {
    private static final String key = "test key";
    private VCrypter crypter;

    @Before
    public void setUp() {
        crypter = new VCrypter();
        crypter.setKey(key);
    }

    @Test
    public void test() throws Exception {
        byte[] cleartext = new byte[VUtil.BLOCK_SIZE];
        for (int i = 0; i < cleartext.length; i++) {
            cleartext[i] = (byte) i;
        }
        assertArrayEquals(cleartext, crypter.decrypt(crypter.encrypt(cleartext)));

        cleartext = new byte[VUtil.BLOCK_SIZE / 3];
        for (int i = 0; i < cleartext.length; i++) {
            cleartext[i] = (byte) i;
        }
        assertArrayEquals(cleartext, crypter.decrypt(crypter.encrypt(cleartext)));

        VCrypter noKeyCrypter = new VCrypter();
        try {
            noKeyCrypter.encrypt(new byte[]{0, 1});
            fail("Exception was expected with invalid encrypter");
        } catch (InvalidKeyException e) {
        }

        try {
            noKeyCrypter.decrypt(new byte[]{0, 1});
            fail("Exception was expected with invalid encrypter");
        } catch (InvalidKeyException e) {
        }
    }
}
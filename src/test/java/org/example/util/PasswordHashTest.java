package org.example.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHashTest {

    @Test
    void hashPassword_shouldReturnSameHash_whenSameInput() {
        String password = "123456";

        String hash1 = PasswordHash.hashPassword(password);
        String hash2 = PasswordHash.hashPassword(password);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(hash1, hash2);
    }

    @Test
    void hashPassword_shouldReturnDifferentHash_whenDifferentInput() {
        String hash1 = PasswordHash.hashPassword("123456");
        String hash2 = PasswordHash.hashPassword("abcdef");

        assertNotEquals(hash1, hash2);
    }

    @Test
    void hashPassword_shouldNotReturnPlainText() {
        String password = "123456";
        String hash = PasswordHash.hashPassword(password);

        assertNotEquals(password, hash);
    }

    @Test
    void hashPassword_shouldReturn64HexCharacters() {
        String hash = PasswordHash.hashPassword("123456");

        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    void checkPassword_shouldReturnTrue_whenPasswordMatchesHash() {
        String password = "matkhau123";
        String hash = PasswordHash.hashPassword(password);

        boolean result = PasswordHash.checkPassword(password, hash);

        assertTrue(result);
    }

    @Test
    void checkPassword_shouldReturnFalse_whenPasswordDoesNotMatchHash() {
        String password = "matkhau123";
        String wrongPassword = "saimatkhau";
        String hash = PasswordHash.hashPassword(password);

        boolean result = PasswordHash.checkPassword(wrongPassword, hash);

        assertFalse(result);
    }

    @Test
    void checkPassword_shouldReturnFalse_whenHashBelongsToAnotherPassword() {
        String hash1 = PasswordHash.hashPassword("abc123");
        String hash2 = PasswordHash.hashPassword("xyz789");

        assertFalse(PasswordHash.checkPassword("abc123", hash2));
        assertFalse(PasswordHash.checkPassword("xyz789", hash1));
    }
}
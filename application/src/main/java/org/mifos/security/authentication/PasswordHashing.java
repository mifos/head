/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.security.authentication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.mifos.framework.exceptions.SystemException;

/**
 * This class encapsulate all the logic related to password hashing
 */
public class PasswordHashing {
    MessageDigest messageDigest = null;

    /**
     * This function will return the hashed password out of the passed string
     * password
     * 
     * @param password
     *            password passed by the user
     * @param randomBytes
     *            random bytes
     */
    public byte[] getHashedPassword(String password, byte[] randomBytes) {
        byte[] hashedPassword = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.update(randomBytes);
            messageDigest.update(password.getBytes("UTF-8"));
            hashedPassword = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException(e);
        } catch (UnsupportedEncodingException e) {
            throw new SystemException(e);

        }
        return hashedPassword;

    }

    /**
     * This function verifies a given password
     * 
     */
    public boolean verifyPassword(String password, byte[] encPassword) {
        byte[] randomBytes = new byte[12];
        byte[] decPassword = null;
        System.arraycopy(encPassword, 0, randomBytes, 0, randomBytes.length);

        byte[] decTempPassword = getHashedPassword(password, randomBytes);
        decPassword = new byte[randomBytes.length + decTempPassword.length];

        System.arraycopy(randomBytes, 0, decPassword, 0, randomBytes.length);
        System.arraycopy(decTempPassword, 0, decPassword, randomBytes.length, decTempPassword.length);

        return compare(encPassword, decPassword);
    }

    /**
     * This function create the hashed password
     * 
     */
    public byte[] createEncryptedPassword(String password) {
        byte[] randomBytes = generateRandomBytes();
        byte[] encPassword = null;

        byte[] tempEncPassword = getHashedPassword(password, randomBytes);

        encPassword = new byte[randomBytes.length + tempEncPassword.length];
        System.arraycopy(randomBytes, 0, encPassword, 0, randomBytes.length);
        System.arraycopy(tempEncPassword, 0, encPassword, randomBytes.length, tempEncPassword.length);
        return encPassword;

    }

    /**
     * Hepler function which compare two hashed password
     * 
     * @param encPassword
     * @param decPassword
     * @return boolean compare result
     */
    public boolean compare(byte[] encPassword, byte[] decPassword) {
        if (Arrays.equals(encPassword, decPassword))
            return true;
        else
            return false;
    }

    /**
     * This function generate and returns the random no of bytes
     * 
     * @return randomBytes
     */
    public byte[] generateRandomBytes() {
        byte[] randomBytes = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        return randomBytes;
    }

}
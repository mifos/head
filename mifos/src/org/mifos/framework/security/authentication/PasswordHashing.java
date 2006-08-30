package org.mifos.framework.security.authentication;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.mifos.framework.exceptions.EncryptionException;
import org.mifos.framework.exceptions.SystemException;



/**
 * This class encapsulate all the logic related to password hashing 
 */
public class PasswordHashing 
{
	MessageDigest messageDigest = null;
	/**
	 * This function will return the  hashed password out of the passed string password
	 * @param password password passed by the user 
	 * @param randomBytes random bytes 
	 * @return
	 * @throws EncryptionException
	 * @throws SystemException
	 */
	public byte[] getHashedPassword(String password,byte[] randomBytes) throws EncryptionException,SystemException
	{
		byte[] hashedPassword=null;
		try
		{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");


		messageDigest.update(randomBytes);
		messageDigest.update(password.getBytes("UTF-8"));
		 hashedPassword = messageDigest.digest();
		}
		catch ( NoSuchAlgorithmException e)
		{
			throw new SystemException(e);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new SystemException(e);
			
		}
		return hashedPassword;

	}

	/**
	 * This function varifies a given password 
	 * @param password
	 * @param encPassword
	 * @return
	 * @throws EncryptionException
	 * @throws SystemException
	 */
	public boolean verifyPassword(String password,byte[] encPassword) throws EncryptionException,SystemException
	{
		 byte[] randomBytes =  new byte[12];
		 byte[] decPassword = null;
		 System.arraycopy(encPassword,0,randomBytes,0,randomBytes.length);

		 byte[] decTempPassword =  getHashedPassword(password , randomBytes);
		 decPassword = new byte[randomBytes.length+decTempPassword.length];


		System.arraycopy(randomBytes,0,decPassword,0,randomBytes.length);
		System.arraycopy(decTempPassword,0,decPassword,randomBytes.length,decTempPassword.length);



		 return compare(encPassword,decPassword );
	}

	/**
	 * This function create the hashed password
	 * @param password
	 * @return
	 * @throws EncryptionException
	 * @throws SystemException
	 */
	public byte[] createEncryptedPassword(String password) throws EncryptionException,SystemException
	{
		byte[] randomBytes = generateRandomBytes();
		byte[] encPassword = null;

		byte[] tempEncPassword =  getHashedPassword(password , randomBytes);


		encPassword = new byte[randomBytes.length+tempEncPassword.length];
		System.arraycopy(randomBytes,0,encPassword,0,randomBytes.length);
		System.arraycopy(tempEncPassword,0,encPassword,randomBytes.length,tempEncPassword.length);
		return encPassword;

	}


	/**
	 * Hepler function which compare two hashed password
	 * @param encPassword
	 * @param decPassword
	 * @return
	 */
	public boolean compare(byte[]encPassword,byte[]decPassword)
	{
		if(Arrays.equals(encPassword,decPassword))
			return true;
		else
			return false;
	}

	/**
	 * This function generate and returns the random no of bytes 
	 * @return
	 */
	public byte[] generateRandomBytes() 
	{
		byte[] randomBytes = new byte[12];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(randomBytes);

		return randomBytes;
	}

}
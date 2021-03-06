package cs455.scaling.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCalculator {
  public static String SHA1FromBytes(byte[] data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA1");
      byte[] hash = digest.digest(data);
      BigInteger hashInt = new BigInteger(1, hash);
      return hashInt.toString(16);
    } catch (NoSuchAlgorithmException nsae) {
      nsae.printStackTrace();
    }
    return null;
  }
}

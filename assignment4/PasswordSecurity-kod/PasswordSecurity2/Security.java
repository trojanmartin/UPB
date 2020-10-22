//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha1: Vytvorit funkciu na bezpecne generovanie saltu.              //
// Uloha2: Vytvorit funkciu na hashovanie.                              //
// Je vhodne vytvorit aj dalsie pomocne funkcie napr. na porovnavanie   //
// hesla ulozeneho v databaze so zadanym heslom.                        //
//////////////////////////////////////////////////////////////////////////
package passwordsecurity2;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Security {
    
    protected static String hash(String password, byte[] salt) throws Exception{  
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            md.update(salt);

            byte byteData[] = md.digest(password.getBytes());

            md.reset();

            return toHex(byteData);
        }
        catch(NoSuchAlgorithmException ex){
            return null;
        }
    }

    protected static boolean isMatch(String hashedPaswword, String salt, String password){
        try {
            return hashedPaswword.equals(hash(password, fromHex(salt)));
        }
        catch (Exception e) {
            //TODO: handle exception
            return false;
        }
       
    }
    
    protected static byte[] getSalt() {
        try
        {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");

            byte[] salt = new byte[16];
    
            secureRandom.nextBytes(salt);
    
            return salt;
        }
        catch(Exception ex){
            return null;
        }
    }
    
    public static byte[] fromHex(String hex){
        byte[] binary = new byte[hex.length()/2];

        for(int i = 0; i< binary.length; i++){
            binary[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }

        return binary;
    }

    public static String toHex(byte[] array){
        BigInteger bi = new BigInteger(1,array);

        String hex = bi.toString(16);

        int paddingLength = (array.length *2 ) - hex.length();

        if(paddingLength > 0){
            return String.format("%0" + paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
}


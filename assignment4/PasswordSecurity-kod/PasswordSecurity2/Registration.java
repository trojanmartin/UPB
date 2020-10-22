//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha1: Do suboru s heslami ulozit aj sal.                           //
// Uloha2: Pouzit vytvorenu funkciu na hashovanie a ulozit heslo        //
//         v zahashovanom tvare.                                        //
//////////////////////////////////////////////////////////////////////////
package passwordsecurity2;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import passwordsecurity2.Database.MyResult;


public class Registration {
    protected static MyResult registracia(String meno, String heslo) throws NoSuchAlgorithmException, Exception{
        if (Database.exist("hesla.txt", meno)){
            System.out.println("Meno je uz zabrate.");
            return new MyResult(false, "Meno je uz zabrate.");
        }
        else {

            //velke,male,cislo min 8 znakov
            if(!heslo.matches("(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}"))
                return new MyResult(false,"Heslo nesplna minimalne poziadavky");

            byte[] salt = Security.getSalt();
            String password = Security.hash(heslo,salt);

            Database.add("hesla.txt", meno + ":"+ password + ":" + Security.toHex(salt));
        }
        return new MyResult(true, "");
    }
    
}

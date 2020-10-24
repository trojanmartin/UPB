//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha1: Do suboru s heslami ulozit aj sal.                           //
// Uloha2: Pouzit vytvorenu funkciu na hashovanie a ulozit heslo        //
//         v zahashovanom tvare.                                        //
//////////////////////////////////////////////////////////////////////////
package PasswordSecurity2;
import java.security.NoSuchAlgorithmException;
import PasswordSecurity2.Database.MyResult;


public class Registration {
    protected static MyResult registracia(String meno, String heslo) throws NoSuchAlgorithmException, Exception{
        if (Database.find(meno).getFirst()){
            System.out.println("Meno je uz zabrate.");
            return new MyResult(false, "Meno je uz zabrate.");
        }
        else {

            //velke,male,cislo min 8 znakov
            if(!heslo.matches("(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}"))
                return new MyResult(false,"Heslo nesplna minimalne poziadavky");

            byte[] salt = Security.getSalt();
            String password = Security.hash(heslo,salt);

            Database.add(meno,password,Security.toHex(salt));
        }
        return new MyResult(true, "");
    }
    
}

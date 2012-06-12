import java.security.MessageDigest
import sun.misc.BASE64Encoder
import sun.misc.CharacterEncoder


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sxg282
 */
class PasswordCodec {

    	static encode = { password  ->
		MessageDigest md = MessageDigest.getInstance('SHA')
		md.update(nonce.getBytes('UTF-8'))
                md.update(created.getBytes('UTF-8'))
                md.update(password.getBytes('UTF-8'))
		return (new BASE64Encoder()).encode(md.digest())
	}
	
}


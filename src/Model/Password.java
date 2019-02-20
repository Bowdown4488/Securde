/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author luisenricolopez
 */
public class Password {

    private final byte[] hash;
    private final byte[] salt;

    public Password(byte[] hash, byte[] salt) {
        this.hash = hash;
        this.salt = salt;
    }

    public byte[] getHash() {
        return hash;
    }
    
    public byte[] getSalt() {
        return salt;
    }

}

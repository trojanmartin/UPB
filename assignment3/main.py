from Crypto.PublicKey import RSA
from Crypto.Random import get_random_bytes
from Crypto.Cipher import AES, PKCS1_OAEP

import sys
import getopt


def decrypt(filename):
    private_key = RSA.import_key(open("private.pem").read())

    with open(filename, "rb") as file:
        enc_session_key = file.read(private_key.size_in_bytes())
        nonce = file.read(16)
        tag = file.read(16)
        ciphertext = file.read(-1)

    # Decrypt the session key with the private RSA key
    cipher_rsa = PKCS1_OAEP.new(private_key)
    session_key = cipher_rsa.decrypt(enc_session_key)

    # Decrypt the data with the AES session key
    cipher_aes = AES.new(session_key, AES.MODE_EAX, nonce)
    data = cipher_aes.decrypt_and_verify(ciphertext, tag)
    with open("decrypted_" + filename, "wb") as dec:
        dec.write(data)


def encrypt(filename):
    with open(filename) as datafile:
        data = datafile.read().encode()

    file_out = open(filename + "_encrypted.bin", "wb")
    recipient_key = RSA.import_key(open("public.pem").read())
    session_key = get_random_bytes(16)

    # Encrypt the session key with the public RSA key
    cipher_rsa = PKCS1_OAEP.new(recipient_key)
    enc_session_key = cipher_rsa.encrypt(session_key)

    # Encrypt the data with the AES session key
    cipher_aes = AES.new(session_key, AES.MODE_EAX)
    ciphertext, tag = cipher_aes.encrypt_and_digest(data)

    file_out.write(enc_session_key)
    file_out.write(cipher_aes.nonce)
    file_out.write(tag)
    file_out.write(ciphertext)
    file_out.close()

def generateKeys():
    key = RSA.generate(2048)
    private_key = key.export_key()
    file_out = open("private.pem", "wb")
    file_out.write(private_key)
    file_out.close()

    public_key = key.publickey().export_key()
    file_out = open("public.pem", "wb")
    file_out.write(public_key)
    file_out.close()

def main(argv):
    opts, args = getopt.getopt(argv, "gdef")

    for opt, arg in opts:
        if "-d" in opt:
            operation = "decrypt"
        if "-g" in opt:
            generateKeys()
            return 
        elif "-e" in opt:
            operation = "encrypt"
        elif "-f" in opt:
            datafilename = arg

    if operation == "encrypt":
        encrypt(datafilename)
        return

    elif operation == "decrypt":
        decrypt(datafilename)


if __name__ == "__main__":
    main(sys.argv[1:])

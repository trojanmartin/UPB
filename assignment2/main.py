from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from timeit import default_timer as timer

import sys
import getopt


def decrypt(key, filename):
    with open(filename, "rb") as file:
        nonce = file.read(16)
        tag = file.read(16)
        ciphertext = file.read(-1)

    cipher = AES.new(key, AES.MODE_EAX, nonce)
    data = cipher.decrypt_and_verify(ciphertext, tag)

    with open(filename + ".decrypted", "wb") as datafile:
        datafile.write(data)


def encrypt(filename):

    with open(filename, "rb") as datafile:
        data = datafile.read()

    key = get_random_bytes(16)

    with open(filename + ".key", "wb") as keyfile:
        keyfile.write(key)

    start = timer()

    cipher = AES.new(key, AES.MODE_EAX)
    ciphertext, tag = cipher.encrypt_and_digest(data)
    file_out = open(filename + ".encrypted", "wb")
    [file_out.write(x) for x in (cipher.nonce, tag, ciphertext)]
    file_out.close()

    end = timer()

    time = end - start
    print("Encryption took " + str(time) + "seconds")

def main(argv):
    opts, args = getopt.getopt(argv, "def:k:")

    for opt, arg in opts:
        if "-d" in opt:
            operation = "decrypt"
        elif "-e" in opt:
            operation = "encrypt"
        elif "-f" in opt:
            datafilename = arg
        elif "-k" in opt:
            keyfilename = arg

    if operation == "encrypt":
        encrypt(datafilename)
        return

    elif operation == "decrypt":
        with open(keyfilename, "rb") as keyfile:
            key = keyfile.read()
        decrypt(key, datafilename)


if __name__ == "__main__":
    main(sys.argv[1:])

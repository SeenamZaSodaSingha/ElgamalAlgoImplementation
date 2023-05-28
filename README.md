# Elgamal Algorithm implementation in Java

### This is part of the final project for the course of Cryptography Algorithms at the KMITL.

### Contributor
- Nattakrit Klindokkeaw
- Thammanit Chensintananan
- Siriwat Kantasit

## Instruction

You can run the program by using the following command.

```bash
java app.java inputMode encrypt/decryptMode keyMode blockSize key input output
```
- input mode

    `-t` for text input

    `-f` for file input
- encrypt/decrypt mode

    `-e` for encryption

    `-d` for decryption

- key mode

    `-k` for key input

    `-gen` for key generation key

- Block size
    
    Input block size in bit for encryption mode (e.g., 32, 64)

- key

    if key mode is `-k` then key is the key for encryption/decryption. key can be plaintext or path to the key file.

        keyInPlainText or /path_to/keyfile

- input
    
    if input mode is `-t` then input is the plaintext for encryption/decryption. input can be plaintext or path to the input file.
    if input mode is `-f` then input is the path to the input file.
    
        plain text or /path_to/inputfile

- output

    path to the output file.
    
        /path_to/output_file
## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
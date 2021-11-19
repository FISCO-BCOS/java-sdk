package org.fisco.bcos.sdk.codegen;

import java.io.File;



public class codeGenTest {
    public static File binFile = new File("sdk-codegen/src/main/java/org/fisco/bcos/sdk/codegen/HelloWorld.bin");
    public static File smFile = new File("sdk-codegen/src/main/java/org/fisco/bcos/sdk/codegen/HelloWorld_gm.bin");
    public static File abiFile = new File("sdk-codegen/src/main/java/org/fisco/bcos/sdk/codegen/HelloWorld.abi");
    public static File tarDir = new File("sdk-codegen/src/main/java");
    public static ContractGenerator generator = new ContractGenerator(binFile, smFile, abiFile, tarDir, "org.fisco.bcos.sdk.codegen.test");
    public static void javaCodeGenTest() {
        try {
            generator.generateJavaFiles();
        } catch (Exception e) {
            CodeGenUtils.exitError(e);
        }
    }

    public static void main(String[] args) {
        javaCodeGenTest();
    }
    
}

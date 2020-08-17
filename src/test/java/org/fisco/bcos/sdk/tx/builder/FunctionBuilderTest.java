package org.fisco.bcos.sdk.tx.builder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.StaticArray;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderService;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class FunctionBuilderTest {

    private FunctionBuilderService functionBuilder = new FunctionBuilderService();

    @Test
    public void testEmpty() {
        try {
            functionBuilder.buildFunctionByAbi("Complex", "emptyArgs", null);
        } catch (TransactionBaseException e) {
            Assert.assertTrue(e.getRetCode().getCode() == ResultCodeEnum.PARSE_ERROR.getCode());
        }
    }

    @Test
    public void testSet() throws TransactionBaseException {
        String abiStr =
                "[{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
        List<Object> args = new ArrayList<>();
        args.add("hello");
        Function function = functionBuilder.buildFunctionByAbi(abiStr, "set", args).getFunction();
        Type solArg = function.getInputParameters().get(0);
        Assert.assertTrue(solArg.getTypeAsString().equals("string"));
        Assert.assertTrue(solArg.getValue().equals("hello"));
    }

    @Test
    public void testUint() throws TransactionBaseException {
        String abiStr =
                "[{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        List args = new ArrayList();
        args.add("1");
        Function function = functionBuilder.buildFunctionByAbi(abiStr, "set", args).getFunction();
        Type solArg = function.getInputParameters().get(0);
        Assert.assertTrue(solArg.getTypeAsString().equals("uint256"));
        Assert.assertTrue(solArg.getValue().equals(BigInteger.ONE));

        args = new ArrayList();
        args.add(1);
        function = functionBuilder.buildFunctionByAbi(abiStr, "set", args).getFunction();
        solArg = function.getInputParameters().get(0);
        Assert.assertTrue(solArg.getTypeAsString().equals("uint256"));
        Assert.assertTrue(solArg.getValue().equals(BigInteger.ONE));

        args = new ArrayList();
        args.add(BigInteger.ONE);
        function = functionBuilder.buildFunctionByAbi(abiStr, "set", args).getFunction();
        solArg = function.getInputParameters().get(0);
        Assert.assertTrue(solArg.getTypeAsString().equals("uint256"));
        Assert.assertTrue(solArg.getValue().equals(BigInteger.ONE));
    }

    @Test
    public void testUintStaticArray() throws TransactionBaseException {
        String abiStr =
                "[{\"constant\":false,\"inputs\":[{\"name\":\"dynamicArr\",\"type\":\"uint256[]\"}],\"name\":\"setD\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"staticArr\",\"type\":\"uint256[2]\"}],\"name\":\"setS\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        // List X Integer
        List args = new ArrayList();
        args.add(Arrays.asList(1, 2));
        Function function = functionBuilder.buildFunctionByAbi(abiStr, "setS", args).getFunction();
        StaticArray arr = (StaticArray) function.getInputParameters().get(0);
        List arrValues = arr.getValue();
        Uint256 a1 = (Uint256) arrValues.get(0);
        Uint256 a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
        // List X String
        args = new ArrayList();
        args.add(Arrays.asList("1", "2"));
        function = functionBuilder.buildFunctionByAbi(abiStr, "setS", args).getFunction();
        arr = (StaticArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
        // List X BigInteger
        args = new ArrayList();
        args.add(Arrays.asList(BigInteger.ONE, BigInteger.valueOf(2)));
        function = functionBuilder.buildFunctionByAbi(abiStr, "setS", args).getFunction();
        arr = (StaticArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
        // Array X Integer
        args = new ArrayList();
        args.add(new int[] {1, 2});
        function = functionBuilder.buildFunctionByAbi(abiStr, "setS", args).getFunction();
        arr = (StaticArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());

        // Array X String
        args = new ArrayList();
        args.add(new String[] {"1", "2"});
        function = functionBuilder.buildFunctionByAbi(abiStr, "setS", args).getFunction();
        arr = (StaticArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());

        // Array X BigInteger
        args = new ArrayList();
        args.add(new BigInteger[] {BigInteger.ONE, BigInteger.valueOf(2)});
        function = functionBuilder.buildFunctionByAbi(abiStr, "setS", args).getFunction();
        arr = (StaticArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
    }

    @Test
    public void testUintDynamicArray() throws TransactionBaseException {
        String abiStr =
                "[{\"constant\":false,\"inputs\":[{\"name\":\"dynamicArr\",\"type\":\"uint256[]\"}],\"name\":\"setD\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"staticArr\",\"type\":\"uint256[2]\"}],\"name\":\"setS\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        // List X Integer
        List args = new ArrayList();
        args.add(Arrays.asList(1, 2));
        Function function = functionBuilder.buildFunctionByAbi(abiStr, "setD", args).getFunction();
        DynamicArray arr = (DynamicArray) function.getInputParameters().get(0);
        List arrValues = arr.getValue();
        Uint256 a1 = (Uint256) arrValues.get(0);
        Uint256 a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
        // List X String
        args = new ArrayList();
        args.add(Arrays.asList("1", "2"));
        function = functionBuilder.buildFunctionByAbi(abiStr, "setD", args).getFunction();
        arr = (DynamicArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
        // List X BigInteger
        args = new ArrayList();
        args.add(Arrays.asList(BigInteger.ONE, BigInteger.valueOf(2)));
        function = functionBuilder.buildFunctionByAbi(abiStr, "setD", args).getFunction();
        arr = (DynamicArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
        // Array X Integer
        args = new ArrayList();
        args.add(new int[] {1, 2});
        function = functionBuilder.buildFunctionByAbi(abiStr, "setD", args).getFunction();
        arr = (DynamicArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());

        // Array X String
        args = new ArrayList();
        args.add(new String[] {"1", "2"});
        function = functionBuilder.buildFunctionByAbi(abiStr, "setD", args).getFunction();
        arr = (DynamicArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());

        // Array X BigInteger
        args = new ArrayList();
        args.add(new BigInteger[] {BigInteger.ONE, BigInteger.valueOf(2)});
        function = functionBuilder.buildFunctionByAbi(abiStr, "setD", args).getFunction();
        arr = (DynamicArray) function.getInputParameters().get(0);
        arrValues = arr.getValue();
        a1 = (Uint256) arrValues.get(0);
        a2 = (Uint256) arrValues.get(1);
        Assert.assertEquals(BigInteger.ONE, a1.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), a2.getValue());
    }
}

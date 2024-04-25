package org.fisco.bcos.sdk.abi.datatypes.generated;

import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.StaticArray;
import org.fisco.bcos.sdk.abi.datatypes.Type;

/**
 * Auto generated code.
 *
 * <p><strong>Do not modifiy!</strong>
 *
 * <p>Please use AbiTypesGenerator in the <a
 * href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class StaticArray8<T extends Type> extends StaticArray<T> {
    @Deprecated
    public StaticArray8(List<T> values) {
        super(8, values);
    }

    @Deprecated
    @SafeVarargs
    public StaticArray8(T... values) {
        super(8, values);
    }

    public StaticArray8(Class<T> type, List<T> values) {
        super(type, 8, values);
    }

    @SafeVarargs
    public StaticArray8(Class<T> type, T... values) {
        super(type, 8, values);
    }
}

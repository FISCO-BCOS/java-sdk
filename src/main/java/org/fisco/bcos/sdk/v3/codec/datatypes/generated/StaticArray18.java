package org.fisco.bcos.sdk.v3.codec.datatypes.generated;

import java.util.List;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;

/**
 * Auto generated code.
 *
 * <p><strong>Do not modifiy!</strong>
 *
 * <p>Please use AbiTypesGenerator in the <a
 * href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class StaticArray18<T extends Type> extends StaticArray<T> {
    @Deprecated
    public StaticArray18(List<T> values) {
        super(18, values);
    }

    @Deprecated
    @SafeVarargs
    public StaticArray18(T... values) {
        super(18, values);
    }

    public StaticArray18(Class<T> type, List<T> values) {
        super(type, 18, values);
    }

    @SafeVarargs
    public StaticArray18(Class<T> type, T... values) {
        super(type, 18, values);
    }
}

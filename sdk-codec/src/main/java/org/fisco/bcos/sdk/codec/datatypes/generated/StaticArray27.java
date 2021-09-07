package org.fisco.bcos.sdk.codec.datatypes.generated;

import java.util.List;
import org.fisco.bcos.sdk.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.codec.datatypes.Type;

/**
 * Auto generated code.
 *
 * <p><strong>Do not modifiy!</strong>
 *
 * <p>Please use AbiTypesGenerator in the <a
 * href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class StaticArray27<T extends Type> extends StaticArray<T> {
    @Deprecated
    public StaticArray27(List<T> values) {
        super(27, values);
    }

    @Deprecated
    @SafeVarargs
    public StaticArray27(T... values) {
        super(27, values);
    }

    public StaticArray27(Class<T> type, List<T> values) {
        super(type, 27, values);
    }

    @SafeVarargs
    public StaticArray27(Class<T> type, T... values) {
        super(type, 27, values);
    }
}

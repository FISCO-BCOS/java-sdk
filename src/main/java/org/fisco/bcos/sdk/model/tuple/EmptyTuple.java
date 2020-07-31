package org.fisco.bcos.sdk.model.tuple;

/** Empty Tuple type. */
public class EmptyTuple implements Tuple {

    @Override
    public int getSize() {
        return 0;
    }
}

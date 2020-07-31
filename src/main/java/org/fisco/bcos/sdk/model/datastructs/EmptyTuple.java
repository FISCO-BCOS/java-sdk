package org.fisco.bcos.sdk.model.datastructs;

/** Empty Tuple type. */
public class EmptyTuple implements Tuple {

    @Override
    public int getSize() {
        return 0;
    }
}

package org.fisco.bcos.sdk.v3.model.callback;

import java.util.List;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.model.Response;

public abstract class CallCallback implements RespCallback<List<Type>> {
    @Override
    public abstract void onResponse(List<Type> types);

    @Override
    public abstract void onError(Response errorResponse);
}

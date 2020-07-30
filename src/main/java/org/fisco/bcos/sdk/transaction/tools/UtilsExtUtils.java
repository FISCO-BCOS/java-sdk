package org.fisco.bcos.sdk.transaction.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.abi.TypeMappingException;

public class UtilsExtUtils {

    public static List typeMap(List input, Class destType) throws TypeMappingException {

        List result = new ArrayList(input.size());

        if (!input.isEmpty()) {
            try {
                Constructor constructor = destType.getDeclaredConstructor(input.get(0).getClass());
                for (Object value : input) {
                    result.add(constructor.newInstance(value));
                }
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | InvocationTargetException
                    | InstantiationException e) {
                throw new TypeMappingException(e);
            }
        }
        return result;
    }
}

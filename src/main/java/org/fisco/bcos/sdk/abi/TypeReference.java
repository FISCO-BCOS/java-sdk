/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package org.fisco.bcos.sdk.abi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.fisco.bcos.sdk.abi.datatypes.Array;

/**
 * Type wrapper to get around limitations of Java's type erasure. This is so that we can pass around
 * Typed {@link Array} types.
 *
 * <p>See <a href="http://gafter.blogspot.com.au/2006/12/super-type-tokens.html">this blog post</a>
 * for further details.
 *
 * <p>It may make sense to switch to using Java's reflection <a
 * href="https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Type.html">Type</a> to avoid
 * working around this fundamental generics limitation.
 */
public abstract class TypeReference<T extends org.fisco.bcos.sdk.abi.datatypes.Type>
        implements Comparable<TypeReference<T>> {

    private final Type type;
    private final boolean indexed;

    protected TypeReference() {
        this(false);
    }

    protected TypeReference(boolean indexed) {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        this.indexed = indexed;
    }

    public int compareTo(TypeReference<T> o) {
        // taken from the blog post comments - this results in an errror if the
        // type parameter is left out.
        return 0;
    }

    public Type getType() {
        return type;
    }

    public boolean isIndexed() {
        return indexed;
    }

    /**
     * Workaround to ensure type does not come back as T due to erasure, this enables you to create
     * a TypeReference via {@link Class Class&lt;T&gt;}.
     *
     * @return the parameterized Class type if applicable, otherwise a regular class
     * @throws ClassNotFoundException if the class type cannot be determined
     */
    @SuppressWarnings("unchecked")
    public Class<T> getClassType() throws ClassNotFoundException {
        return Utils.getClassType(getType());
    }

    public static <T extends org.fisco.bcos.sdk.abi.datatypes.Type> TypeReference<T> create(
            Class<T> cls) {
        return create(cls, false);
    }

    public static <T extends org.fisco.bcos.sdk.abi.datatypes.Type> TypeReference<T> create(
            Class<T> cls, boolean indexed) {
        return new TypeReference<T>(indexed) {
            @Override
            public Type getType() {
                return cls;
            }
        };
    }
}

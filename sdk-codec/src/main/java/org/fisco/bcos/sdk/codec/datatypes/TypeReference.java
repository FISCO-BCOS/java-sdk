package org.fisco.bcos.sdk.codec.datatypes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public abstract class TypeReference<T extends org.fisco.bcos.sdk.codec.datatypes.Type>
        implements Comparable<TypeReference<T>> {
    protected static Pattern ARRAY_SUFFIX = Pattern.compile("\\[(\\d*)]");
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

    protected TypeReference(Type type) {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = type;
        this.indexed = false;
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
        Type clsType = getType();

        if (getType() instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) clsType).getRawType();
        } else {
            return (Class<T>) Class.forName(clsType.getTypeName());
        }
    }

    public static <T extends org.fisco.bcos.sdk.codec.datatypes.Type> TypeReference<T> create(
            Class<T> cls) {
        return create(cls, false);
    }

    public static <T extends org.fisco.bcos.sdk.codec.datatypes.Type> TypeReference<T> create(
            Class<T> cls, boolean indexed) {
        return new TypeReference<T>(indexed) {
            @Override
            public Type getType() {
                return cls;
            }
        };
    }

    public static <T extends org.fisco.bcos.sdk.codec.datatypes.Type> TypeReference<T> create(
            Type type) {
        return new TypeReference<T>(type) {
            @Override
            public Type getType() {
                return super.getType();
            }
        };
    }

    public abstract static class StaticArrayTypeReference<
                    T extends org.fisco.bcos.sdk.codec.datatypes.Type>
            extends TypeReference<T> {

        private final int size;

        protected StaticArrayTypeReference(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    /**
     * This is a helper method that only works for atomic types (uint, bytes, etc). Array types must
     * be wrapped by a {@link java.lang.reflect.ParameterizedType}.
     */
    protected static Class<? extends org.fisco.bcos.sdk.codec.datatypes.Type> getAtomicTypeClass(
            String type) throws ClassNotFoundException {

        if (ARRAY_SUFFIX.matcher(type).find()) {
            throw new ClassNotFoundException(
                    "getAtomicTypeClass does not work with array types."
                            + " See makeTypeReference()");
        } else {
            return AbiTypes.getType(type);
        }
    }

    public static TypeReference makeTypeReference(String type, final boolean indexed)
            throws ClassNotFoundException {
        Matcher nextSquareBrackets = ARRAY_SUFFIX.matcher(type);
        if (!nextSquareBrackets.find()) {
            System.out.println("3: ");
            final Class<? extends org.fisco.bcos.sdk.codec.datatypes.Type> typeClass =
                    getAtomicTypeClass(type);
            return create(typeClass, indexed);
        }
        System.out.println("4: ");
        int lastReadStringPosition = nextSquareBrackets.start();

        final Class<? extends org.fisco.bcos.sdk.codec.datatypes.Type> baseClass =
                getAtomicTypeClass(type.substring(0, lastReadStringPosition));

        TypeReference arrayWrappedType = create(baseClass, indexed);
        final int len = type.length();

        // for each [\d*], wrap the previous TypeReference in an array
        while (lastReadStringPosition < len) {
            String arraySize = nextSquareBrackets.group(1);
            final TypeReference baseTr = arrayWrappedType;
            if (arraySize == null || arraySize.equals("")) {
                arrayWrappedType =
                        new TypeReference<DynamicArray>(indexed) {
                            @Override
                            public java.lang.reflect.Type getType() {
                                return new ParameterizedType() {
                                    @Override
                                    public java.lang.reflect.Type[] getActualTypeArguments() {
                                        return new java.lang.reflect.Type[] {baseTr.getType()};
                                    }

                                    @Override
                                    public java.lang.reflect.Type getRawType() {
                                        return DynamicArray.class;
                                    }

                                    @Override
                                    public java.lang.reflect.Type getOwnerType() {
                                        return Class.class;
                                    }
                                };
                            }
                        };
            } else {
                final Class arrayClass;
                int arraySizeInt = Integer.parseInt(arraySize);
                if (arraySizeInt <= StaticArray.MAX_SIZE_OF_STATIC_ARRAY) {
                    arrayClass =
                            Class.forName(
                                    "org.fisco.bcos.sdk.codec.datatypes.generated.StaticArray"
                                            + arraySize);
                } else {
                    arrayClass = StaticArray.class;
                }
                arrayWrappedType =
                        new TypeReference.StaticArrayTypeReference<StaticArray>(arraySizeInt) {
                            @Override
                            public boolean isIndexed() {
                                return indexed;
                            }

                            @Override
                            public java.lang.reflect.Type getType() {
                                return new ParameterizedType() {
                                    @Override
                                    public java.lang.reflect.Type[] getActualTypeArguments() {
                                        return new java.lang.reflect.Type[] {baseTr.getType()};
                                    }

                                    @Override
                                    public java.lang.reflect.Type getRawType() {
                                        return arrayClass;
                                    }

                                    @Override
                                    public java.lang.reflect.Type getOwnerType() {
                                        return Class.class;
                                    }
                                };
                            }
                        };
            }
            lastReadStringPosition = nextSquareBrackets.end();
            nextSquareBrackets = ARRAY_SUFFIX.matcher(type);
            // cant find any more [] and string isn't fully parsed
            if (!nextSquareBrackets.find(lastReadStringPosition) && lastReadStringPosition != len) {
                throw new ClassNotFoundException("Unable to make TypeReference from " + type);
            }
        }
        return arrayWrappedType;
    }
}

package org.fisco.bcos.sdk.v3.codec.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;

/**
 * ABIDefinition wrapper
 *
 * <p>Link https://solidity.readthedocs.io/en/develop/abi-spec.html#json <br>
 * type: "function", "constructor", "receive" (the "receive Ether" function) or "fallback" (the
 * "default" function); <br>
 * name: the name of the function; <br>
 * inputs: an array of objects, each of which contains: <br>
 * name: the name of the parameter. <br>
 * type: the canonical type of the parameter (more below). <br>
 * components: used for tuple types (more below). <br>
 * outputs: an array of objects similar to inputs. <br>
 * stateMutability: a string with one of the following values: pure (specified to not read
 * blockchain state), view (specified to not modify the blockchain state), nonpayable (function does
 * not accept Ether - the default) and payable (function accepts Ether). <br>
 */
public class ABIDefinition {
    private String name;
    private String type;
    private boolean constant;
    private boolean payable;
    private boolean anonymous;
    private String stateMutability;
    private List<ConflictField> conflictFields = new ArrayList<>();

    private List<NamedType> inputs = new ArrayList<>();
    private List<NamedType> outputs = new ArrayList<>();
    private List<Long> selector = new ArrayList<>();
    public static List<String> CONSTANT_KEY = Arrays.asList("view");

    public ABIDefinition() {}

    public List<Long> getSelector() {
        return selector;
    }

    public void setSelector(List<Long> selector) {
        this.selector = selector;
    }

    public ABIDefinition(
            String name,
            String type,
            boolean constant,
            boolean payable,
            boolean anonymous,
            String stateMutability) {
        this.name = name;
        this.type = type;
        this.constant = constant;
        this.payable = payable;
        this.anonymous = anonymous;
        this.stateMutability = stateMutability;
    }

    public ABIDefinition(
            boolean constant,
            List<NamedType> inputs,
            String name,
            List<NamedType> outputs,
            String type,
            boolean payable) {
        this(constant, inputs, name, outputs, type, payable, null);
    }

    public ABIDefinition(
            boolean constant,
            List<NamedType> inputs,
            String name,
            List<NamedType> outputs,
            String type,
            boolean payable,
            String stateMutability) {
        this.constant = constant;
        this.inputs = inputs;
        this.name = name;
        this.outputs = outputs;
        this.type = type;
        this.payable = payable;
        this.stateMutability = stateMutability;
    }

    public static ABIDefinition createDefaultConstructorABIDefinition() {
        return new ABIDefinition(
                false, new ArrayList<>(), null, null, "constructor", false, "nonpayable");
    }

    public static ABIDefinition createABIDefinition(String methodSignature) {
        {
            int start = methodSignature.indexOf("(");
            int end = methodSignature.lastIndexOf(")");
            if (start == -1 || end == -1 || start >= end) {
                throw new IllegalArgumentException(
                        "Invalid method signature format, sig: " + methodSignature);
            }

            String name = methodSignature.substring(0, start).trim();
            String type = methodSignature.substring(start + 1, end).trim();
            List<ABIDefinition.NamedType> inputs = new ArrayList<ABIDefinition.NamedType>();

            if (!type.isEmpty()) {
                String[] types = type.split(",");
                for (String s : types) {
                    if (s.startsWith("tuple")) {
                        throw new IllegalArgumentException(
                                "Not support create ABIDefinition by method signature which contains tuple params, sig: "
                                        + methodSignature);
                    }
                    ABIDefinition.NamedType input = new ABIDefinition.NamedType("name", s.trim());
                    inputs.add(input);
                }
            }

            return new ABIDefinition(false, inputs, name, null, "function", false, "nonpayable");
        }
    }

    /**
     * string method signature
     *
     * @return the method signature string
     */
    public String getMethodSignatureAsString() {
        StringBuilder result = new StringBuilder();
        result.append(this.name);
        result.append("(");
        String params =
                this.getInputs().stream()
                        .map(NamedType::getTypeAsString)
                        .collect(Collectors.joining(","));
        result.append(params);
        result.append(")");
        return result.toString();
    }

    /**
     * calculate the method id
     *
     * @param cryptoSuite the crypto suite used for hash calculation
     * @return the method id
     */
    @Deprecated
    public byte[] getMethodId(CryptoSuite cryptoSuite) {
        // Note: it's ok to use the abi encoder for all ABIDefinition, because only use
        // buildMethodId
        org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder encoder =
                new org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder(cryptoSuite.getHashImpl());
        return encoder.buildMethodId(this.getMethodSignatureAsString());
    }

    public byte[] getMethodId(Hash hash) {
        // Note: it's ok to use the abi encoder for all ABIDefinition, because only use
        // buildMethodId
        org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder encoder =
                new org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder(hash);
        return encoder.buildMethodId(this.getMethodSignatureAsString());
    }

    public boolean isConstant() {
        return this.constant || CONSTANT_KEY.contains(this.getStateMutability());
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public List<NamedType> getInputs() {
        return this.inputs;
    }

    public void setInputs(List<NamedType> inputs) {
        this.inputs = inputs;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NamedType> getOutputs() {
        return this.outputs;
    }

    public boolean hasOutputs() {
        return !this.outputs.isEmpty();
    }

    public void setOutputs(List<NamedType> outputs) {
        this.outputs = outputs;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPayable() {
        return this.payable;
    }

    public void setPayable(boolean payable) {
        this.payable = payable;
    }

    public String getStateMutability() {
        return this.stateMutability;
    }

    public void setStateMutability(String stateMutability) {
        this.stateMutability = stateMutability;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ABIDefinition)) {
            return false;
        }

        ABIDefinition that = (ABIDefinition) o;

        if (this.isConstant() != that.isConstant()) {
            return false;
        }
        if (this.isPayable() != that.isPayable()) {
            return false;
        }
        if (this.getInputs() != null
                ? !this.getInputs().equals(that.getInputs())
                : that.getInputs() != null) {
            return false;
        }
        if (this.getName() != null
                ? !this.getName().equals(that.getName())
                : that.getName() != null) {
            return false;
        }
        if (this.getOutputs() != null
                ? !this.getOutputs().equals(that.getOutputs())
                : that.getOutputs() != null) {
            return false;
        }
        if (this.getStateMutability() != null
                ? !this.getStateMutability().equals(that.getStateMutability())
                : that.getStateMutability() != null) {
            return false;
        }
        return this.getType() != null
                ? this.getType().equals(that.getType())
                : that.getType() == null;
    }

    @Override
    public int hashCode() {
        int result = (this.isConstant() ? 1 : 0);
        result = 31 * result + (this.getInputs() != null ? this.getInputs().hashCode() : 0);
        result = 31 * result + (this.getName() != null ? this.getName().hashCode() : 0);
        result = 31 * result + (this.getOutputs() != null ? this.getOutputs().hashCode() : 0);
        result = 31 * result + (this.getType() != null ? this.getType().hashCode() : 0);
        result = 31 * result + (this.isPayable() ? 1 : 0);
        result =
                31 * result
                        + (this.getStateMutability() != null
                                ? this.getStateMutability().hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ABIDefinition{"
                + "name='"
                + name
                + '\''
                + ", type='"
                + type
                + '\''
                + ", constant="
                + constant
                + ", payable="
                + payable
                + ", anonymous="
                + anonymous
                + ", stateMutability='"
                + stateMutability
                + '\''
                + ", conflictFields="
                + conflictFields
                + ", inputs="
                + inputs
                + ", outputs="
                + outputs
                + ", selector="
                + selector
                + '}';
    }

    public List<ConflictField> getConflictFields() {
        return conflictFields;
    }

    public void setConflictFields(List<ConflictField> conflictFields) {
        this.conflictFields = conflictFields;
    }

    public static class Type {
        public String type;
        public String rawType;
        public List<Integer> dimensions = new ArrayList<Integer>();

        public Type(String name) {
            int index = name.indexOf('[');
            this.rawType = (-1 == index) ? name.trim() : name.substring(0, index);
            this.type = name;
            this.initialize();
        }

        private void initialize() {
            Pattern p = Pattern.compile("\\[[0-9]*\\]");
            Matcher m = p.matcher(this.type);
            while (m.find()) {
                String s = m.group();
                String dig = s.substring(s.indexOf('[') + 1, s.indexOf(']')).trim();
                if (dig.isEmpty()) {
                    this.dimensions.add(0);
                } else {
                    this.dimensions.add(Integer.valueOf(dig));
                }
            }
        }

        @Override
        public String toString() {
            return "Type{"
                    + "name='"
                    + this.type
                    + '\''
                    + ", baseName='"
                    + this.rawType
                    + '\''
                    + ", dimensions="
                    + this.dimensions
                    + '}';
        }

        public String getType() {
            return this.type;
        }

        public String getRawType() {
            return this.rawType;
        }

        public Type reduceDimensionAndGetType() {
            if (this.isList()) {
                String r = this.rawType;
                for (int i = 0; i < this.dimensions.size() - 1; i++) {
                    r += ("[" + (this.dimensions.get(i) != 0 ? this.dimensions.get(i) : "") + "]");
                }

                return new Type(r);
            }

            return new Type(this.rawType);
        }

        public boolean isList() {
            return !this.dimensions.isEmpty();
        }

        public boolean isDynamicList() {
            return this.isList() && (this.dimensions.get(this.dimensions.size() - 1) == 0);
        }

        public boolean isFixedList() {
            return this.isList() && (this.dimensions.get(this.dimensions.size() - 1) != 0);
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setRawType(String rawType) {
            this.rawType = rawType;
        }

        public List<Integer> getDimensions() {
            return this.dimensions;
        }

        public Integer getLastDimension() {
            if (!this.isList()) {
                return 0;
            }

            return this.dimensions.get(this.dimensions.size() - 1);
        }

        public void setDimensions(List<Integer> dimensions) {
            this.dimensions = dimensions;
        }
    }

    public static class ConflictField {
        private Integer kind;
        private String slot;
        private List<Integer> value;

        public ConflictField() {}

        public List<Integer> getValue() {
            return value;
        }

        public void setValue(List<Integer> value) {
            this.value = value;
        }

        public Integer getKind() {
            return kind;
        }

        public void setKind(Integer kind) {
            this.kind = kind;
        }

        public String getSlot() {
            return slot;
        }

        public void setSlot(String slot) {
            this.slot = slot;
        }

        @Override
        public String toString() {
            return "ConflictField{"
                    + "kind="
                    + kind
                    + ", slot='"
                    + slot
                    + '\''
                    + ", value="
                    + value
                    + '}';
        }
    }

    public static class NamedType {
        private static String DEFAULT_INTERNAL_TYPE = "";

        private String name;
        private String type;
        private String internalType = DEFAULT_INTERNAL_TYPE;
        private boolean indexed;
        private List<NamedType> components = new ArrayList<>();

        public NamedType() {}

        public NamedType(String name, String type) {
            this(name, type, false);
        }

        public NamedType(String name, String type, boolean indexed) {
            this.name = name;
            this.type = type;
            this.indexed = indexed;
        }

        public NamedType(
                String name,
                String type,
                String internalType,
                boolean indexed,
                List<NamedType> components) {
            this.name = name;
            this.type = type;
            this.internalType = internalType;
            this.indexed = indexed;
            this.components = components;
        }

        public Type newType() {
            return new Type(this.type);
        }

        private String getTupleRawTypeAsString() {
            StringBuilder result = new StringBuilder();
            String params =
                    this.getComponents().stream()
                            .map(NamedType::getTypeAsString)
                            .collect(Collectors.joining(","));
            result.append(params);
            return result.toString();
        }

        public String getTypeAsString() {
            // not tuple, return
            if (!this.type.startsWith("tuple")) {
                return this.type;
            }

            String tupleRawString = this.getTupleRawTypeAsString();
            return this.type.replaceAll("tuple", "(" + tupleRawString + ")");
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInternalType() {
            return this.internalType;
        }

        public void setInternalType(String internalType) {
            this.internalType = internalType;
        }

        public int structIdentifier() {
            String typeIdentifier =
                    (internalType == null || internalType.isEmpty()) ? type : internalType;
            if (typeIdentifier.endsWith("[]")) {
                typeIdentifier = typeIdentifier.substring(0, typeIdentifier.indexOf('['));
            }
            return (typeIdentifier
                            + components.stream()
                                    .map(namedType -> String.valueOf(namedType.structIdentifier()))
                                    .collect(Collectors.joining()))
                    .hashCode();
        }

        public int nestedness() {
            if (getComponents().size() == 0) {
                return 0;
            }
            return 1 + getComponents().stream().mapToInt(NamedType::nestedness).max().getAsInt();
        }

        public boolean isIndexed() {
            return this.indexed;
        }

        public void setIndexed(boolean indexed) {
            this.indexed = indexed;
        }

        public List<NamedType> getComponents() {
            return this.components;
        }

        public void setComponents(List<NamedType> components) {
            this.components = components;
        }

        public boolean isDynamic() {
            if (getType().equals("string")
                    || getType().equals("bytes")
                    || getType().contains("[]")) {
                return true;
            }
            if (components.stream().anyMatch(NamedType::isDynamic)) {
                return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            NamedType namedType = (NamedType) o;
            return this.indexed == namedType.indexed
                    && Objects.equals(this.name, namedType.name)
                    && Objects.equals(this.type, namedType.type)
                    && Objects.equals(this.components, namedType.components);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.type, this.indexed, this.components);
        }

        @Override
        public String toString() {
            return "NamedType{"
                    + "name='"
                    + this.name
                    + '\''
                    + ", type='"
                    + this.type
                    + '\''
                    + ", indexed="
                    + this.indexed
                    + ", components="
                    + this.components
                    + '}';
        }
    }
}

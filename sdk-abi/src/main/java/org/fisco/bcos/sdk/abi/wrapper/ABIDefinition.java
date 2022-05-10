package org.fisco.bcos.sdk.abi.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.EventEncoder;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.crypto.CryptoSuite;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ABIDefinition {
    public static final String CONSTRUCTOR_TYPE = "constructor";
    public static final String FUNCTION_TYPE = "function";
    public static final String EVENT_TYPE = "event";
    public static final String FALLBACK_TYPE = "fallback";
    public static final String RECEIVE_TYPE = "receive";

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("constant")
    private boolean constant;

    @JsonProperty("payable")
    private boolean payable;

    @JsonProperty("anonymous")
    private boolean anonymous;

    @JsonProperty("stateMutability")
    private String stateMutability;

    @JsonProperty("inputs")
    private List<NamedType> inputs = new ArrayList<>();;

    @JsonProperty("outputs")
    private List<NamedType> outputs = new ArrayList<>();;

    public static List<String> CONSTANT_KEY = Arrays.asList("view");

    public ABIDefinition() {}

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

    /**
     * string method signature
     *
     * @return the method signature string
     */
    public String getMethodSignatureAsString() {
        StringBuilder result = new StringBuilder();
        // Fix: the name field of the fallback is empty
        if (name != null) {
            result.append(name);
        }
        result.append("(");
        if (getInputs() != null) {
            String params =
                    getInputs()
                            .stream()
                            .map(abi -> abi.getTypeAsString())
                            .collect(Collectors.joining(","));
            result.append(params);
        }
        result.append(")");
        return result.toString();
    }

    /**
     * calculate the method id
     *
     * @param cryptoSuite the crypto suite used for hash calculation
     * @return the method id
     */
    public String getMethodId(CryptoSuite cryptoSuite) {
        // from FunctionEncoder get methodId signature hash substring
        FunctionEncoder encoder = new FunctionEncoder(cryptoSuite);
        return encoder.buildMethodId(getMethodSignatureAsString());
    }

    /**
     * calculate the event topic
     *
     * @param cryptoSuite the crypto suite used for hash calculation
     * @return the event topic
     */
    public String getEventTopic(CryptoSuite cryptoSuite) {
        // from EventEncoder get eventTopic signature hash not substring
        EventEncoder encoder = new EventEncoder(cryptoSuite);
        return encoder.buildEventSignature(getMethodSignatureAsString());
    }

    public boolean isConstant() {
        return constant || CONSTANT_KEY.contains(this.getStateMutability());
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public List<NamedType> getInputs() {
        return inputs;
    }

    public void setInputs(List<NamedType> inputs) {
        this.inputs = inputs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NamedType> getOutputs() {
        return outputs;
    }

    public boolean hasOutputs() {
        return !outputs.isEmpty();
    }

    public void setOutputs(List<NamedType> outputs) {
        this.outputs = outputs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPayable() {
        return payable;
    }

    public void setPayable(boolean payable) {
        this.payable = payable;
    }

    public String getStateMutability() {
        return stateMutability;
    }

    public void setStateMutability(String stateMutability) {
        this.stateMutability = stateMutability;
    }

    public boolean isAnonymous() {
        return anonymous;
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

        if (isConstant() != that.isConstant()) {
            return false;
        }
        if (isPayable() != that.isPayable()) {
            return false;
        }
        if (getInputs() != null
                ? !getInputs().equals(that.getInputs())
                : that.getInputs() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        if (getOutputs() != null
                ? !getOutputs().equals(that.getOutputs())
                : that.getOutputs() != null) {
            return false;
        }
        if (getStateMutability() != null
                ? !getStateMutability().equals(that.getStateMutability())
                : that.getStateMutability() != null) {
            return false;
        }
        return getType() != null ? getType().equals(that.getType()) : that.getType() == null;
    }

    @Override
    public int hashCode() {
        int result = (isConstant() ? 1 : 0);
        result = 31 * result + (getInputs() != null ? getInputs().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getOutputs() != null ? getOutputs().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (isPayable() ? 1 : 0);
        result = 31 * result + (getStateMutability() != null ? getStateMutability().hashCode() : 0);
        return result;
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
            Pattern p = Pattern.compile("\\[[0-9]{0,}\\]");
            Matcher m = p.matcher(type);
            while (m.find()) {
                String s = m.group();
                String dig = s.substring(s.indexOf('[') + 1, s.indexOf(']')).trim();
                if (dig.isEmpty()) {
                    dimensions.add(0);
                } else {
                    dimensions.add(Integer.valueOf(dig));
                }
            }
        }

        @Override
        public String toString() {
            return "Type{"
                    + "name='"
                    + type
                    + '\''
                    + ", baseName='"
                    + rawType
                    + '\''
                    + ", dimensions="
                    + dimensions
                    + '}';
        }

        public String getType() {
            return type;
        }

        public String getRawType() {
            return rawType;
        }

        public Type reduceDimensionAndGetType() {
            if (isList()) {
                String r = rawType;
                for (int i = 0; i < dimensions.size() - 1; i++) {
                    r += ("[" + (dimensions.get(i) != 0 ? dimensions.get(i) : "") + "]");
                }

                return new Type(r);
            }

            return new Type(rawType);
        }

        public boolean isList() {
            return !dimensions.isEmpty();
        }

        public boolean isDynamicList() {
            return isList() && (dimensions.get(dimensions.size() - 1) == 0);
        }

        public boolean isFixedList() {
            return isList() && (dimensions.get(dimensions.size() - 1) != 0);
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setRawType(String rawType) {
            this.rawType = rawType;
        }

        public List<Integer> getDimensions() {
            return dimensions;
        }

        public Integer getLastDimension() {
            if (!isList()) {
                return 0;
            }

            return dimensions.get(dimensions.size() - 1);
        }

        public void setDimensions(List<Integer> dimensions) {
            this.dimensions = dimensions;
        }
    }

    public static class NamedType {
        private static String DEFAULT_INTERNAL_TYPE = "";

        private String name;
        private String type;
        private String internalType = DEFAULT_INTERNAL_TYPE;
        private boolean indexed;
        private List<NamedType> components = new ArrayList<>();;

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
            return new Type(type);
        }

        private String getTupleRawTypeAsString() {
            StringBuilder result = new StringBuilder();
            String params =
                    getComponents()
                            .stream()
                            .map(abi -> abi.getTypeAsString())
                            .collect(Collectors.joining(","));
            result.append(params);
            return result.toString();
        }

        public String getTypeAsString() {
            // not tuple, return
            if (!type.startsWith("tuple")) {
                return type;
            }

            String tupleRawString = getTupleRawTypeAsString();
            String result = type.replaceAll("tuple", "(" + tupleRawString + ")");
            return result;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
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
            if ("tuple[]".equals(typeIdentifier)) {
                typeIdentifier = "tuple";
            }

            return (typeIdentifier
                            + components
                                    .stream()
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

        public boolean isIndexed() {
            return indexed;
        }

        public void setIndexed(boolean indexed) {
            this.indexed = indexed;
        }

        public List<NamedType> getComponents() {
            return components;
        }

        public void setComponents(List<NamedType> components) {
            this.components = components;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NamedType namedType = (NamedType) o;
            return indexed == namedType.indexed
                    && Objects.equals(name, namedType.name)
                    && Objects.equals(type, namedType.type)
                    && Objects.equals(components, namedType.components);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type, indexed, components);
        }

        @Override
        public String toString() {
            return "NamedType{"
                    + "name='"
                    + name
                    + '\''
                    + ", type='"
                    + type
                    + '\''
                    + ", indexed="
                    + indexed
                    + ", components="
                    + components
                    + '}';
        }
    }
}

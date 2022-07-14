package org.fisco.bcos.sdk.v3.codec;

import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;

import java.math.BigInteger;

public class TestFixture {
    public static class Bar extends StaticStruct {
        public BigInteger id;

        public BigInteger data;

        public Bar(BigInteger id, BigInteger data) {
            super(new Uint256(id), new Uint256(data));
            this.id = id;
            this.data = data;
        }

        public Bar(Uint256 id, Uint256 data) {
            super(id, data);
            this.id = id.getValue();
            this.data = data.getValue();
        }
    }

    public static class Foo extends DynamicStruct {
        public String id;

        public String name;

        public Foo(String id, String name) {
            super(new Utf8String(id), new Utf8String(name));
            this.id = id;
            this.name = name;
        }

        public Foo(Utf8String id, Utf8String name) {
            super(id, name);
            this.id = id.getValue();
            this.name = name.getValue();
        }
    }

    public static class Baz extends DynamicStruct {
        public String id;

        public BigInteger data;

        public Baz(String id, BigInteger data) {
            super(new Utf8String(id), new Uint256(data));
            this.id = id;
            this.data = data;
        }

        public Baz(Utf8String id, Uint256 data) {
            super(id, data);
            this.id = id.getValue();
            this.data = data.getValue();
        }
    }

    public static class Boz extends DynamicStruct {
        public BigInteger data;

        public String id;

        public Boz(BigInteger data, String id) {
            super(new Uint256(data), new Utf8String(id));
            this.data = data;
            this.id = id;
        }

        public Boz(Uint256 data, Utf8String id) {
            super(data, id);
            this.data = data.getValue();
            this.id = id.getValue();
        }
    }

    public static class Fuzz extends StaticStruct {
        public Bar bar;

        public BigInteger data;

        public Fuzz(Bar bar, BigInteger data) {
            super(bar, new Uint256(data));
            this.bar = bar;
            this.data = data;
        }

        public Fuzz(Bar bar, Uint256 data) {
            super(bar, data);
            this.bar = bar;
            this.data = data.getValue();
        }
    }

    public static class Nuu extends DynamicStruct {
        public Foo foo;

        public Nuu(Foo foo) {
            super(foo);
            this.foo = foo;
        }
    }

    public static class Nar extends DynamicStruct {
        public Nuu nuu;

        public Nar(Nuu nuu) {
            super(nuu);
            this.nuu = nuu;
        }
    }

    public static class Naz extends DynamicStruct {
        public Nar nar;

        public BigInteger data;

        public Naz(Nar nar, BigInteger data) {
            super(nar, new Uint256(data));
            this.nar = nar;
            this.data = data;
        }

        public Naz(Nar nar, Uint256 data) {
            super(nar, data);
            this.nar = nar;
            this.data = data.getValue();
        }
    }
}

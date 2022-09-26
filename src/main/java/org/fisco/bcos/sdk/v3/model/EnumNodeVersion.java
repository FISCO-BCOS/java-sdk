package org.fisco.bcos.sdk.v3.model;

public enum EnumNodeVersion {
    BCOS_3_0_0_RC1("3.0.0-rc1"),
    BCOS_3_0_0("3.0.0");
    private String version;

    EnumNodeVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // the object of node version
    public class Version {
        private int major;
        private int minor;
        private int patch;
        private String ext;

        @Override
        public String toString() {
            return "Version [major="
                    + major
                    + ", minor="
                    + minor
                    + ", patch="
                    + patch
                    + ", ext="
                    + ext
                    + "]";
        }

        public String toVersionString() {
            return this.getMajor() + "." + this.getMinor();
        }

        public int getMajor() {
            return major;
        }

        public void setMajor(int major) {
            this.major = major;
        }

        public int getMinor() {
            return minor;
        }

        public void setMinor(int minor) {
            this.minor = minor;
        }

        public int getPatch() {
            return patch;
        }

        public void setPatch(int patch) {
            this.patch = patch;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }
    }

    public static Version getClassVersion(String version) throws Exception {
        try {
            // node version str format : "a.b.c" or "a.b.c-rcx" or gm version
            if (version.endsWith("gm")) {
                version = version.substring(0, version.indexOf("gm"));
            }
            String[] s0 = version.trim().split("-");

            Version v = EnumNodeVersion.BCOS_3_0_0.new Version();
            if (s0.length > 1) {
                v.setExt(s0[1]);
            }

            //
            String[] s1 = s0[0].split("\\.");
            if (s1.length >= 3) {
                v.setMajor(Integer.parseInt(s1[0].trim()));
                v.setMinor(Integer.parseInt(s1[1].trim()));
                v.setPatch(Integer.parseInt(s1[2].trim()));
            } else { // invaid format
                throw new Exception(" invalid node version format, version: " + version);
            }
            return v;
        } catch (Exception e) {
            throw new Exception(" invalid node version format, version: " + version);
        }
    }
}

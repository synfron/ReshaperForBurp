package synfron.reshaper.burp.core.rules.thens.entities.transform;

public enum HashType {
    Sha1("SHA-1"),
    Sha256("SHA-256"),
    Sha512("SHA-512"),
    Sha256V3("SHA3-256"),
    Sha512V3("SHA3-512"),
    Md5("MD5");

    HashType(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}

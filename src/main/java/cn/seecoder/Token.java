package cn.seecoder;

public class Token {
	private Type type;
    private String value;//record value of specific token

    public enum Type {
        LPAREN, RPAREN, LAMBDA, DOT, LCID, EOF
    }

    Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    Token(Type type) {
        this.type = type;
    }
    

    Type getType() {
        return type;
    }

    String getValue() {
        return value;
    }
}

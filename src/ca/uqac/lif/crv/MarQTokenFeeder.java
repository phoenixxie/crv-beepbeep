package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.input.TokenFeeder;

public class MarQTokenFeeder extends TokenFeeder {
    public static class Token {
        public String func;
        public String[] args;
    }

    public MarQTokenFeeder() {
        this.m_separatorBegin = "";
        this.m_separatorEnd = System.getProperty("line.separator");
    }

    @Override
    protected Object createTokenFromInput(String token) {
        token = token.trim();
        if(!token.isEmpty() && !token.startsWith("#")) {
            String[] parts = token.split(",");

            Token t = new Token();
            t.func = parts[0];
            t.args = new String[parts.length - 1];
            for (int i = 0; i < parts.length - 1; ++i) {
                t.args[i] = parts[i + 1];
            }

            return t;
        } else {
            return new NoToken();
        }
    }

    @Override
    public Processor clone() {
        return new MarQTokenFeeder();
    }
}

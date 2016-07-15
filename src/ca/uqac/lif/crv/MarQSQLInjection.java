package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.input.CsvFeeder;
import ca.uqac.lif.cep.input.TokenFeeder;
import ca.uqac.lif.cep.io.StreamReader;
import ca.uqac.lif.cep.tuples.TupleFeeder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;

public class MarQSQLInjection {

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 1) {
            return;
        }

        String filepath = args[0];

        StreamReader sr = new StreamReader(new FileInputStream(filepath));
        MarQTokenFeeder tf = new MarQTokenFeeder();

        Connector.connect(sr, tf);

        Pullable p = tf.getPullableOutput(0);

        HashSet<String> cleanFuncs = new HashSet<>();
        HashSet<String> unknownFuncs = new HashSet<>();

        while (p.hasNext() == Pullable.NextStatus.YES) {
            Object obj = p.pull();
            if (obj instanceof TokenFeeder.NoToken) {
                continue;
            }

            MarQTokenFeeder.Token token = (MarQTokenFeeder.Token) obj;
            switch (token.func) {
                case "input":
                    cleanFuncs.remove(token.args[0]);
                    unknownFuncs.add(token.args[0]);
                    break;
                case "derive":
                    cleanFuncs.remove(token.args[1]);
                    unknownFuncs.remove(token.args[1]);
                    if (cleanFuncs.contains(token.args[0])) {
                        cleanFuncs.add(token.args[1]);
                    } else {
                        unknownFuncs.add(token.args[1]);
                    }
                    break;
                case "use":
                    if (unknownFuncs.contains(token.args[0])) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }
                    break;
                case "sanitise":
                    unknownFuncs.remove(token.args[0]);
                    cleanFuncs.add(token.args[0]);
                    break;
            }

        }

        System.out.println("STATUS: Satisfied");
    }
}

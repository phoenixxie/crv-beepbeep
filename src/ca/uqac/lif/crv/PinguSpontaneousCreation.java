package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;

import java.io.FileNotFoundException;
import java.util.HashSet;

public class PinguSpontaneousCreation {

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 1) {
            return;
        }

        String filepath = args[0];

        PinguXMLReader reader = new PinguXMLReader(filepath);
        PinguIDChecker checker = new PinguIDChecker();

        Connector.connect(reader, checker);

        Pullable p = checker.getPullableOutput(0);

        while (p.hasNext() == Pullable.NextStatus.YES) {
            if (!(boolean)p.pull()) {
                System.out.println("STATUS: Violated");
                System.exit(0);
            }
        }

        System.out.println("STATUS: Satisfied");
    }
}

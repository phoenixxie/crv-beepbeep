package ca.uqac.lif.crv;

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

        Pullable p = reader.getPullableOutput(0);

        HashSet<Integer> pinguids = null;

        PinguTrace trace = null;
        while (p.hasNext() == Pullable.NextStatus.YES) {
            trace = (PinguTrace) p.pull();

            if (pinguids == null) {
                pinguids = new HashSet<>();
                for (PinguTrace.Character c : trace.getCharacters()) {
                    pinguids.add(c.id);
                }
            } else {
                int count = 0;
                for (PinguTrace.Character c : trace.getCharacters()) {
                    if (!pinguids.contains(c.id)) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                    }
                    ++count;
                }
                if (count != pinguids.size()) {
                    System.out.println("STATUS: Violated");
                    System.exit(0);
                }
            }
        }

        System.out.println("STATUS: Satisfied");
    }
}

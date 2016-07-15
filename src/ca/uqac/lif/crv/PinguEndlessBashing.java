package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SingleProcessor;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

public class PinguEndlessBashing {

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 1) {
            return;
        }

        String filepath = args[0];

        PinguXMLReader reader = new PinguXMLReader(filepath);

        Pullable p = reader.getPullableOutput(0);

        HashSet<Integer> basherids = new HashSet<>();

        PinguTrace trace = null;
        while (p.hasNext() == Pullable.NextStatus.YES) {
            trace = (PinguTrace) p.pull();

            int count = basherids.size();
            for (PinguTrace.Character c : trace.getCharacters()) {
                if (c.status == PinguTrace.Status.BASHER) {
                    if (!basherids.contains(c.id)) {
                        basherids.add(c.id);
                    } else {
                        --count;
                    }
                } else if (basherids.contains(c.id)) {
                    --count;
                    if (c.status != PinguTrace.Status.WALKER) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    } else {
                        basherids.remove(c.id);
                    }
                }
            }

            if (count != 0) {
                System.out.println("STATUS: Violated");
                System.exit(0);
                return;
            }
        }

        if (!basherids.isEmpty()) {
            System.out.println("STATUS: Violated");
            System.exit(0);
            return;
        }

        System.out.println("STATUS: Satisfied");
    }
}

package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SingleProcessor;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class PinguP4  {

     public static void main(String[] args) throws FileNotFoundException {

        PinguXMLReader reader = new PinguXMLReader("basher-blocker-090.xml");

        Pullable p = reader.getPullableOutput(0);

        PinguTrace trace = null;
        while (p.hasNext() == Pullable.NextStatus.YES) {
            trace = (PinguTrace) p.pull();
        }

        if (trace == null) {
            return;
        }

        for (PinguTrace.Character character : trace.getCharacters()) {
            if (character.status == PinguTrace.Status.BASHER) {
                System.out.println("STATUS: Violated");
                System.exit(0);
            }
        }
        System.out.println("STATUS: Satisfied");
    }
}

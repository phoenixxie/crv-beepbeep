package ca.uqac.lif.crv;

import ca.uqac.lif.cep.*;
import ca.uqac.lif.cep.io.StreamReader;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.tuples.Select;
import ca.uqac.lif.cep.tuples.TupleFeeder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Queue;

public class PinguTraceDisplay {
    public static void main(String[] args) throws FileNotFoundException {

        PinguXMLReader reader = new PinguXMLReader("small.xml");

        Pullable p = reader.getPullableOutput(0);
        while (p.hasNext() == Pullable.NextStatus.YES) {
            PinguTrace trace = (PinguTrace) p.pull();
            System.out.printf("Time: %d, number: %d\r\n", trace.getTimestamp(), trace.getCharacters().size());
        }

        System.out.println("aaaa");
    }

}

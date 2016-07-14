package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.CumulativeFunction;
import ca.uqac.lif.cep.CumulativeProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.epl.QueueSink;
import ca.uqac.lif.cep.io.StreamReader;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.tuples.Select;
import ca.uqac.lif.cep.tuples.TupleFeeder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Queue;

public class Benchmark {


    public static void main(String[] args) throws FileNotFoundException {

        StreamReader sr = new StreamReader(new FileInputStream(new File("tuples1.csv")));
        TupleFeeder tf = new TupleFeeder();
        Select s = new Select(1, "a");
        CumulativeProcessor comb = new CumulativeProcessor(new CumulativeFunction(Addition.instance));

        Connector.connect(sr, tf, s, comb);

        Pullable p = comb.getPullableOutput(0);
        while (p.hasNext() == Pullable.NextStatus.YES) {
            float sum = (Float) p.pull();
            System.out.println(sum);
        }

        System.out.println("aaaa");
    }

}

package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SingleProcessor;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Queue;

public class PinguIDChecker extends SingleProcessor {
    private HashSet<Integer> pinguids = null;


    public PinguIDChecker() {
        super(1, 1);
    }

    @Override
    protected Queue<Object[]> compute(Object[] objects) {
        for (Object obj : objects) {
            PinguTrace trace = (PinguTrace) obj;

            if (pinguids == null) {
                pinguids = new HashSet<>();
                for (PinguTrace.Character c : trace.getCharacters()) {
                    pinguids.add(c.id);
                }
            } else {
                for (PinguTrace.Character c : trace.getCharacters()) {
                    if (!pinguids.contains(c.id)) {
                        return wrapObject(false);
                    }
                }
            }
        }
        return wrapObject(true);
    }

    @Override
    public Processor clone() {
        PinguIDChecker checker = new PinguIDChecker();
        checker.pinguids = (HashSet<Integer>)this.pinguids.clone();
        return checker;
    }
}

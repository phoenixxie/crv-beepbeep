package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.interpreter.Interpreter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PinguP6 {

    public static void main(String[] args) throws FileNotFoundException {

        PinguXMLReader reader = new PinguXMLReader("basher-blocker-090.xml");

        Pullable p = reader.getPullableOutput(0);

        ArrayList<PinguTrace.Vector2D> blockers = new ArrayList<>();
        HashSet<Integer> blockerids = new HashSet<>();
        HashMap<Integer, Boolean> needturnids = new HashMap<>();

        PinguTrace trace = null;
        while (p.hasNext() == Pullable.NextStatus.YES) {
            trace = (PinguTrace) p.pull();

            for (PinguTrace.Character c : trace.getCharacters()) {
                if (!needturnids.containsKey(c.id)) {
                    continue;
                }

                if (c.status != PinguTrace.Status.WALKER) {
                    needturnids.remove(c.id);
                    continue;
                }

                boolean right = needturnids.get(c.id);
                if ((c.velocity.x > 0) != right) {
                    needturnids.remove(c.id);
                }
            }

            if (!needturnids.isEmpty()) {
                System.out.println("STATUS: Violated");
                System.exit(0);
                return;
            }

            for (PinguTrace.Character c : trace.getCharacters()) {
                if (c.status == PinguTrace.Status.BLOCKER) {
                    if (!blockerids.contains(c.id)) {
                        blockerids.add(c.id);
                        blockers.add(c.position);
                    }
                }
            }

            for (PinguTrace.Character c : trace.getCharacters()) {
                if (c.status != PinguTrace.Status.WALKER) {
                    continue;
                }

                for (PinguTrace.Vector2D v : blockers) {
                    if (Math.abs(v.x - c.position.x) <= 6f && Math.abs(v.y - c.position.y) < 3f) {
                        needturnids.put(c.id, c.velocity.x > 0);
                    }
                }
            }
        }

        System.out.println("STATUS: Satisfied");
    }
}

package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.input.TokenFeeder;
import ca.uqac.lif.cep.interpreter.Interpreter;
import ca.uqac.lif.cep.io.StreamReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

public class MarQCandidateSelection {

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 1) {
            return;
        }

        String filepath = args[0];

        StreamReader sr = new StreamReader(new FileInputStream(filepath));
        MarQTokenFeeder tf = new MarQTokenFeeder();

        Connector.connect(sr, tf);

        Pullable p = tf.getPullableOutput(0);

        HashMap<String, HashMap<String, BitSet>> memberParties = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> candidatesParties = new HashMap<>();
        HashMap<String, Integer> partiesCount = new HashMap<>();

        while (p.hasNext() == Pullable.NextStatus.YES) {
            Object obj = p.pull();
            if (obj instanceof TokenFeeder.NoToken) {
                continue;
            }

            MarQTokenFeeder.Token token = (MarQTokenFeeder.Token) obj;
            switch (token.func) {
                case "member":
                    if (memberParties.containsKey(token.args[0])) {
                        HashMap<String, BitSet> partySet = memberParties.get(token.args[0]);
                        partySet.put(token.args[1], new BitSet());
                    } else {
                        HashMap<String, BitSet> partySet = new HashMap<>();
                        partySet.put(token.args[1], new BitSet());
                        memberParties.put(token.args[0], partySet);
                    }
                    break;
                case "candidate":
                    if (!candidatesParties.containsKey(token.args[0])) {
                        HashMap<String, Integer> partyNo = new HashMap<>();

                        Integer cnt = partiesCount.get(token.args[1]);
                        if (cnt == null) {
                            cnt = 1;
                        } else {
                            cnt = cnt + 1;
                        }
                        partiesCount.put(token.args[1], cnt);
                        partyNo.put(token.args[0], cnt);

                        candidatesParties.put(token.args[0], partyNo);
                    } else {
                        HashMap<String, Integer> partyNo = candidatesParties.get(token.args[0]);
                        if (!partyNo.containsKey(token.args[1])) {
                            Integer cnt = partiesCount.get(token.args[1]);
                            if (cnt == null) {
                                cnt = 1;
                            } else {
                                cnt = cnt + 1;
                            }
                            partiesCount.put(token.args[1], cnt);
                            partyNo.put(token.args[0], cnt);
                        }
                    }
                    break;
                case "rank":
//                    HashMap<String, BitSet> partySet = memberParties.get(token.args[0]);
//                    HashMap<String, Integer> partyNo = candidatesParties.get(token.args[1]);
//                    if (partySet == null || partyNo == null || no == null) {
//                        System.out.println("STATUS: Violated");
//                        System.exit(0);
//                        return;
//                    }
//
//                    Integer no = partyNo.get(token.args[1]);
//                    BitSet bs = partySet.get(party);
//                    if (bs == null) {
//                        System.out.println("AAA " + party + ", " + token.args[0] + ", " + token.args[1]);
//                        System.out.println("STATUS: Violated");
//                        System.exit(0);
//                        return;
//                    }
//
//                    bs.set(no);

                    break;
            }
        }

        for (HashMap.Entry<String, HashMap<String, BitSet>> entry : memberParties.entrySet()) {
            HashMap<String, BitSet> partySet = entry.getValue();
            for (HashMap.Entry<String, BitSet> entry2 : partySet.entrySet()) {
                Integer cnt = partiesCount.get(entry2.getKey());
                if (cnt == null) {
                    cnt = 0;
                }
                if (entry2.getValue().size() != cnt) {
                    System.out.println("STATUS: Violated");
                    System.exit(0);
                    return;
                }
            }
        }

        System.out.println("STATUS: Satisfied");
    }
}

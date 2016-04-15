/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.arq;

import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import java.util.Set;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public class TBM_Plausibility extends TBM_FunctionBase {

    @Override
    public NodeValue exec(NodeValue v1, NodeValue v2) {
        checkArgs(v1, v2);

        return NodeValue.makeDouble(pls(graph, v1.asNode(), v2.asNode()));
    }

    public static double pls(Graph graph, Node v1, Node v2) {

        double pls = 0;

        try (ExtendedIterator< Triple> FE = graph.find(v1, TBM.hasFocalElement.asNode(), null)) {//this potential's FEs
            while (FE.hasNext()) {
                Node next = FE.next().getObject();
                if (containsFocalElement(graph, next, v2)) {
                    double mass = (double) graph.find(next, TBM.hasMass.asNode(), null).next().getObject().getLiteralValue();
                    pls += mass;
                }
            }
        }
        /*
        //get all configurations from v2
        Set<Triple> confsV2 = graph.find(v2, TBM.hasConfiguration.asNode(), null).toSet();
        Set<Triple> fElementsV1 = graph.find(v1, TBM.hasFocalElement.asNode(), null).toSet();
        
        for (Triple confV2 : confsV2) {
            for (Triple fElementV1 : fElementsV1) {
                Set<Triple> confsV1 = graph.find(fElementV1.getObject(), TBM.hasConfiguration.asNode(), null).toSet();
                for (Triple confV1 : confsV1) {
                    if(isSubsetOf(graph, confV2.getObject(), confV1.getObject())){
                        double mass = (double)graph.find(fElementV1.getObject(), TBM.hasMass.asNode(), null).toList().get(0).getObject().getLiteralValue();
                        pls += mass;
                        break;
                    }                        
                }
            }
        }*/
        //get al focal elements v1
        //get all configurations from v1.fe
        //if v2.config is subset of v1.fe.conf
        //add mass
        return pls;

    }

    /*private static boolean isSubsetOf(Graph graph, Node config1, Node config2) {
        Set<Triple> elements1 = graph.find(config1, TBM.hasElement.asNode(), null).toSet();

        for (Triple element1 : elements1) {
            if (!graph.contains(config2, TBM.hasElement.asNode(), element1.getObject())) {
                return false;
            }
        }
        return true;
    }*/
}

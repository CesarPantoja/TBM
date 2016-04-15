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
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Cesar
 */
public class TBM_Belief extends TBM_FunctionBase {

    @Override
    public NodeValue exec(NodeValue v1, NodeValue v2) {

        checkArgs(v1, v2);

        return NodeValue.makeDouble(bel(graph, v1.asNode(), v2.asNode()));
    }

    public static double bel(Graph graph, Node potential, Node queryFocalElement) {

        double bel = 0;

        try (ExtendedIterator<Triple> focalElements = graph.find(potential, TBM.hasFocalElement.asNode(), null)) {

            while (focalElements.hasNext()) {
                Node focalElement = focalElements.next().getObject();
                if (equalConfigs(graph, focalElement, queryFocalElement)) {
                    double mass = (double) graph.find(focalElement, TBM.hasMass.asNode(), null).next().getObject().getLiteralValue();
                    bel += mass;
                }
            }
        }
        return bel;
    }
}

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

/**
 *
 * @author Cesar
 */
public class TBM_Belief extends TBM_FunctionBase{

    @Override
    public NodeValue exec(NodeValue v1, NodeValue v2) {
        
        checkArgs(v1, v2);
        
        return NodeValue.makeDouble(bel(graph, v1.asNode(), v2.asNode()));
    }    
    
    public static double bel(Graph graph, Node v1, Node v2){
        double bel = 0;
        
        Set<Triple> focalElements = graph.find(v1, TBM.hasFocalElement.asNode(), null).toSet();
        
        for(Triple t : focalElements){
            if (isAllConfigsSubsetOf(graph, v2, t.getObject())) {
                double mass = (double)graph.find(t.getObject(), TBM.hasMass.asNode(), null).toList().get(0).getObject().getLiteralValue();
                bel+= mass;
            }
        }
        
        return bel;
    }

    private static boolean isAllConfigsSubsetOf(Graph graph, Node focalElement, Node object) {
        Set<Triple> feConfigs = graph.find(focalElement, TBM.hasConfiguration.asNode(), null).toSet(); //triples with [fe hasConfiguration config1]
        Set<Triple> oConfigs = graph.find(object, TBM.hasConfiguration.asNode(), null).toSet(); //triples with configs2
        
        for (Triple feConfig : feConfigs) {
            for (Triple oConfig : oConfigs) {
                if(!graph.find(feConfig.getObject(), TBM.hasElement.asNode(), null).toSet().stream().allMatch( //[config1 hasElement resource]
                        triple -> graph.contains(oConfig.getObject(), TBM.hasElement.asNode(), triple.getObject()))) // [config2 hasElement resource]
                    return false;
            }
        }
        return true;
    }
}

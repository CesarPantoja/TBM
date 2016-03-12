/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.arq;

import org.apache.jena.sparql.expr.NodeValue;

/**
 *
 * @author Cesar
 */
public class TBM_Ignorance extends TBM_FunctionBase {

    @Override
    public NodeValue exec(NodeValue v1, NodeValue v2) {
        checkArgs(v1, v2);
        return NodeValue.makeDouble(TBM_Plausibility.pls(graph, v1.asNode(), v2.asNode())-TBM_Belief.bel(graph, v1.asNode(), v2.asNode()));
    }
    
}

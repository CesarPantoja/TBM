/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.arq;

import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Cesar
 */
public abstract class TBM_FunctionBase implements Function {

    String uri = null;
    ExprList arguments = null;
    protected Graph graph;

    protected void checkArgs(NodeValue v1, NodeValue v2) {
        if (!this.graph.contains(v1.asNode(), RDF.type.asNode(), TBM.Potential.asNode())) {
            throw new ARQInternalErrorException("Arg1 is not of type " + TBM.Potential.toString());
        }

        if (!this.graph.contains(v2.asNode(), RDF.type.asNode(), TBM.FocalElement.asNode())) {
            throw new ARQInternalErrorException("Arg2 is not of type " + TBM.FocalElement.toString());
        }
    }

    @Override
    public void build(String uri, ExprList args) {
        this.uri = uri;
        arguments = args;
    }

    @Override
    public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
        this.graph = env.getActiveGraph();

        if (args == null) // The contract on the function interface is that this should not happen.
        {
            throw new ARQInternalErrorException("FunctionBase: Null args list");
        }

        List<NodeValue> evalArgs = new ArrayList<>();
        for (Expr e : args) {
            NodeValue x = e.eval(binding, env);
            evalArgs.add(x);
        }

        NodeValue nv = exec(evalArgs);
        arguments = null;
        return nv;
    }

    public final NodeValue exec(List<NodeValue> args) {
        if (args == null) // The contract on the function interface is that this should not happen.
        {
            throw new ARQInternalErrorException(Lib.className(this) + ": Null args list");
        }

        if (args.size() != 2) {
            throw new ExprEvalException(Lib.className(this) + ": Wrong number of arguments: Wanted 2, got " + args.size());
        }

        NodeValue v1 = args.get(0);
        NodeValue v2 = args.get(1);

        return exec(v1, v2);
    }

    protected static boolean hasElement(Graph graph, Node config, Node element) {
        return graph.contains(config, TBM.hasElement.asNode(), element);
    }

    protected static boolean validVar(Graph graph, Node domain, Node variable) {
        try (ExtendedIterator<Triple> vars = graph.find(domain, TBM.hasVariable.asNode(), null)) {
            while (vars.hasNext()) {
                if (graph.contains(variable, RDF.type.asNode(), vars.next().getObject())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean equalConfig(Graph graph, Node config1, Node config2, Node domain) {
        try (ExtendedIterator<Triple> iter1 = graph.find(config1, TBM.hasElement.asNode(), null)) {
            while (iter1.hasNext()) {
                Node elem = iter1.next().getObject();
                if (validVar(graph, domain, elem) && !hasElement(graph, config2, elem)) {
                    return false;
                }
            }
        }
        /*try (ExtendedIterator<Triple> iter2 = graph.find(config2, TBM.hasElement.asNode(), null)) {
            while (iter2.hasNext()) {
                Node elem = iter2.next().getObject();
                if (validVar(graph, domain, elem) && !hasElement(graph, config1, elem)) {
                    return false;
                }
            }
        }*/
        return true;
    }

    protected static boolean containsConfig(Graph graph, Node focalElement, Node config, Node domain) {
        try (ExtendedIterator<Triple> iter = graph.find(focalElement, TBM.hasConfiguration.asNode(), null)) {
            while (iter.hasNext()) {
                if (equalConfig(graph, config, iter.next().getObject(), domain)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean containsFocalElement(Graph graph, Node focalElement1, Node focalElement2) {
        try (ExtendedIterator<Triple> configThat = graph.find(focalElement2, TBM.hasConfiguration.asNode(), null)) {
            while (configThat.hasNext()) {
                if (!containsConfig(graph, focalElement1, configThat.next().getObject(), graph.find(focalElement2, TBM.hasDomain.asNode(), null).next().getObject())) {
                    return false;
                }
            }
        }
        return true;
    }

    protected static boolean equalConfigs(Graph graph, Node focalElement1, Node focalElement2) {
        try (ExtendedIterator<Triple> configThat = graph.find(focalElement2, TBM.hasConfiguration.asNode(), null)) {
            while (configThat.hasNext()) {
                if (!containsConfig(graph, focalElement1, configThat.next().getObject(), graph.find(focalElement2, TBM.hasDomain.asNode(), null).next().getObject())) {
                    return false;
                }
            }
        }
        try (ExtendedIterator<Triple> configThis = graph.find(focalElement1, TBM.hasConfiguration.asNode(), null)) {
            while (configThis.hasNext()) {
                if (!containsConfig(graph, focalElement2, configThis.next().getObject(), graph.find(focalElement2, TBM.hasDomain.asNode(), null).next().getObject())) {
                    return false;
                }
            }
        }
        /*ExtendedIterator<Statement> configThis = this.listProperties(TBM.hasConfiguration)
        while (configThis.hasNext()) {
            if (!focalElement.contains(configThis.next().getObject().as(TBMConfiguration.class), focalElement.getDomain())) {
                return false;
            }
        }*/
        return true;
    }

    public abstract NodeValue exec(NodeValue v1, NodeValue v2);
}

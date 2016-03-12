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
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.vocabulary.RDF;
/**
 *
 * @author Cesar
 */
public abstract class TBM_FunctionBase implements Function{
   
    String uri = null ;
    ExprList arguments = null ;
    protected Graph graph ;
    
    protected void checkArgs(NodeValue v1, NodeValue v2){
        if(!this.graph.contains(v1.asNode(), RDF.type.asNode(), TBM.Potential.asNode()))
            throw new ARQInternalErrorException("Arg1 is not of type "+TBM.Potential.toString()) ;
        
        if(!this.graph.contains(v2.asNode(), RDF.type.asNode(), TBM.FocalElement.asNode()))
            throw new ARQInternalErrorException("Arg2 is not of type "+TBM.FocalElement.toString()) ;
    }
    
    @Override
    public void build(String uri, ExprList args) {
        this.uri = uri ;
        arguments = args ;        
    }

    @Override
    public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
        this.graph = env.getActiveGraph() ;
        
        
        if ( args == null )
            // The contract on the function interface is that this should not happen.
            throw new ARQInternalErrorException("FunctionBase: Null args list") ;
        
        List<NodeValue> evalArgs = new ArrayList<>() ;
        for ( Expr e : args )
        {
            NodeValue x = e.eval( binding, env );
            evalArgs.add( x );
        }
        
        NodeValue nv =  exec(evalArgs) ;
        arguments = null ;
        return nv ;
    }
    
    public final NodeValue exec(List<NodeValue> args)
    {
        if ( args == null )
            // The contract on the function interface is that this should not happen.
            throw new ARQInternalErrorException(Lib.className(this)+": Null args list") ;
        
        if ( args.size() != 2 )
            throw new ExprEvalException(Lib.className(this)+": Wrong number of arguments: Wanted 2, got "+args.size()) ;
        
        NodeValue v1 = args.get(0) ;
        NodeValue v2 = args.get(1) ;
        
        return exec(v1, v2) ;
    }
    
    public abstract NodeValue exec(NodeValue v1, NodeValue v2) ;
}

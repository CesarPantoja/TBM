/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model.impl;

import ac.uk.qmul.mmv.tbm.model.TBMModel;
import ac.uk.qmul.mmv.tbm.model.TBMVarDomain;
import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.EnhNode;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.Profile;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public class TBMVarDomainImpl extends ResourceImpl implements TBMVarDomain {

    public TBMVarDomainImpl(Node n, EnhGraph m) {
        super(n, m);
    }

    
        // Static variables
    //////////////////////////////////

    /**
     * A factory for generating OntClass facets from nodes in enhanced graphs.
     * Note: should not be invoked directly by user code: use
     * {@link org.apache.jena.rdf.model.RDFNode#as as()} instead.
     */
    @SuppressWarnings("hiding")
    public static Implementation factory = new Implementation() {
        @Override
        public EnhNode wrap( Node n, EnhGraph eg ) {
            if (canWrap( n, eg )) {
                return new TBMVarDomainImpl( n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n.toString() + " to TBMVarDomain: it does not have rdf:type TBM:VarDomain or equivalent");
            }
        }

        @Override
        public boolean canWrap( Node node, EnhGraph eg ) {
            // node will support being an OntClass facet if it has rdf:type owl:Class or equivalent
            Profile profile = (eg instanceof TBMModel) ? ((TBMModel) eg).getProfile() : null;
            return (profile != null)  &&  profile.isSupported( node, eg, TBMVarDomain.class );
        }
    };
    
    @Override
    public void addVariable(Resource variable) {
        this.addProperty(TBM.hasVariable, variable);
    }
    
    @Override
    public boolean hasVariable(Resource variable) {
        return this.hasProperty(TBM.hasVariable, variable);
    }
    
    @Override
    public ExtendedIterator<Resource> listVariables(){
        return this.listProperties(TBM.hasVariable).mapWith(p -> p.getObject().asResource());
    }
    
}

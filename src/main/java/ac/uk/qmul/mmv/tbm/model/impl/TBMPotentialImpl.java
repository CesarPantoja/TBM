/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model.impl;

import ac.uk.qmul.mmv.tbm.model.TBMFocalElement;
import ac.uk.qmul.mmv.tbm.model.TBMModel;
import ac.uk.qmul.mmv.tbm.model.TBMPotential;
import ac.uk.qmul.mmv.tbm.model.TBMVarDomain;
import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import java.util.function.Function;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.EnhNode;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.Profile;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public class TBMPotentialImpl extends ResourceImpl implements TBMPotential {

    public TBMPotentialImpl(Node n, EnhGraph eg) {
        super(n, eg);
    }

    @Override
    public void setDomain(TBMVarDomain domain) {
        this.addProperty(TBM.hasDomain, domain);
    }

    @Override
    public TBMVarDomain getDomain() {
        return this.getPropertyResourceValue(TBM.hasDomain).as(TBMVarDomain.class);
    }

    @Override
    public void addFocalElement(TBMFocalElement focalElement) {
        this.addProperty(TBM.hasFocalElement, focalElement);
    }

    @Override
    public ExtendedIterator<TBMFocalElement> listFocalElements() {
        
        return this.listProperties(TBM.hasFocalElement).mapWith(t -> t.getObject().as(TBMFocalElement.class));              
    }

    @Override
    public float bel(TBMFocalElement query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double pls(TBMFocalElement query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double dou(TBMFocalElement query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double com(TBMFocalElement query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double ign(TBMFocalElement query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                return new TBMPotentialImpl(n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n.toString() + " to TBMPotential: it does not have rdf:type TBM:Potential or equivalent");
            }
        }

        @Override
        public boolean canWrap( Node node, EnhGraph eg ) {
            // node will support being an OntClass facet if it has rdf:type owl:Class or equivalent
            Profile profile = (eg instanceof TBMModel) ? ((TBMModel) eg).getProfile() : null;
            return (profile != null)  &&  profile.isSupported( node, eg, TBMPotential.class );
        }
    };
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model.impl;

import ac.uk.qmul.mmv.tbm.model.TBMConfiguration;
import ac.uk.qmul.mmv.tbm.model.TBMModel;
import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.EnhNode;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.Profile;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public class TBMConfigurationImpl extends ResourceImpl implements TBMConfiguration {

    private TBMConfigurationImpl(Node n, EnhGraph eg) {
        super(n, eg);
    }
    
    @Override
    public boolean hasElement(Resource element){
        return this.hasProperty(TBM.hasElement, element);
    }

    @Override
    public void addElement(Resource element) {
        this.addProperty(TBM.hasElement, element);
    }

    @Override
    public void addElements(Resource... elements) {        
        for (Resource element : elements) {
            this.addElement(element);
        }
    }

    @Override
    public ExtendedIterator<Resource> listAllElements() {
        return this.listProperties(TBM.hasElement).mapWith(p -> p.getResource());
    }
    
    @Override
    public boolean isSubsetOf(TBMConfiguration config){
        return this.listAllElements().toSet().stream().allMatch((res) -> (config.hasElement(res)));
    }
    
    @Override
    public boolean remove(){
        //System.out.println("remooving");
        this.getModel().removeAll(this, null, null);
        this.getModel().removeAll(null, null, this);
        return true;
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
                return new TBMConfigurationImpl(n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n.toString() + " to TBMConfiguration: it does not have rdf:type TBM:Configuration or equivalent");
            }
        }

        @Override
        public boolean canWrap( Node node, EnhGraph eg ) {
            // node will support being an OntClass facet if it has rdf:type owl:Class or equivalent
            Profile profile = (eg instanceof TBMModel) ? ((TBMModel) eg).getProfile() : null;
            return (profile != null)  &&  profile.isSupported( node, eg, TBMConfiguration.class );
        }
    };
}

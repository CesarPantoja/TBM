/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model.impl;

import ac.uk.qmul.mmv.tbm.model.TBMConfiguration;
import ac.uk.qmul.mmv.tbm.model.TBMFocalElement;
import ac.uk.qmul.mmv.tbm.model.TBMModel;
import ac.uk.qmul.mmv.tbm.model.TBMVarDomain;
import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.EnhNode;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.Profile;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Cesar
 */
public class TBMFocalElementImpl extends ResourceImpl implements TBMFocalElement {

    @Override
    public TBMModel getModel(){
        return (TBMModel)super.getModel();
    }
    
    public TBMFocalElementImpl(Node n, EnhGraph eg) {
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
    public void addConfiguration(TBMConfiguration configuration) {
        this.addProperty(TBM.hasConfiguration, configuration);
    }

    @Override
    public void addConfiguration(Resource... elements) {        
        TBMConfiguration config = this.getModel().createConfiguration();
        this.addConfiguration(config);
        config.addElements(elements);
    }

    @Override
    public void addAllConfigurations() {

        TBMVarDomain domain = this.getDomain();
        
        ExtendedIterator<Resource> vars = domain.listVariables();
        
        List<List<Resource>> sets = new ArrayList<>();
        
        //System.out.println("#################################");
        
        while(  vars.hasNext() ) {
            Resource var = vars.next() ;
            //System.out.println("++++++++++++++++++++++");
            //System.out.println(var);
            //System.out.println("******");
            ResIterator values = this.getModel().listResourcesWithProperty(RDF.type, var);
            List<Resource> curr = new ArrayList<>();
            sets.add(curr);

            while (values.hasNext()) {
                //Resource v = values.next();
                //System.out.println(v);
                curr.add(values.next());
            }
        }

        List<List<Resource>> result = _cartesianProduct(0, sets);
        
        result.forEach((configuration) -> {
            this.addConfiguration(configuration.toArray(new Resource[configuration.size()]));
        });
    }

    private static List<List<Resource>> _cartesianProduct(int index, List<List<Resource>> sets) {
        List<List<Resource>> ret = new ArrayList<>();
        if (index == sets.size()) {
            ret.add(new ArrayList<>());
        } else {
            for (Resource res : sets.get(index)) {
                for (List<Resource> set : _cartesianProduct(index + 1, sets)) {
                    set.add(res);
                    ret.add(set);
                }
            }
        }
        return ret;
    }
    
    @Override
    public ExtendedIterator<TBMConfiguration> listAllConfigurations(){
        return this.listProperties(TBM.hasConfiguration).mapWith(c -> c.getObject().as(TBMConfiguration.class));
    }
    

    @Override
    public void setMass(double mass) {
        this.addProperty(TBM.hasMass, mass);
    }
    
    @Override
    public double getMass(){
        return this.getProperty(TBM.hasMass).getDouble();
    }
    
    @Override
    public void updateMass(double mass){
        this.getProperty(TBM.hasMass).changeLiteralObject(mass);
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
                return new TBMFocalElementImpl(n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n.toString() + " to TBMFocalElement: it does not have rdf:type TBM:FocalElement or equivalent");
            }
        }

        @Override
        public boolean canWrap( Node node, EnhGraph eg ) {
            // node will support being an OntClass facet if it has rdf:type owl:Class or equivalent
            Profile profile = (eg instanceof TBMModel) ? ((TBMModel) eg).getProfile() : null;
            return (profile != null)  &&  profile.isSupported( node, eg, TBMFocalElement.class );
        }
    };
}

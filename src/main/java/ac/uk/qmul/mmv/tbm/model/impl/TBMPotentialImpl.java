/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model.impl;

import ac.uk.qmul.mmv.tbm.model.TBMConfiguration;
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
import org.apache.jena.rdf.model.Statement;
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
    public float pls(TBMFocalElement query) {
        float pls = 0;

        try(ExtendedIterator<TBMFocalElement> FE = this.listFocalElements()){//this potential's FEs
        while (FE.hasNext()) {
            TBMFocalElement next = FE.next();
            if (next.contains(query)) {
                pls += next.getMass();
            }
        }
        }
        return pls;

        /*try (ExtendedIterator<Statement> configQuery = query.listProperties(TBM.hasConfiguration)) { //Query FE's Configs
            while (configQuery.hasNext()) {
                TBMConfiguration confThat = configQuery.next().getObject().as(TBMConfiguration.class);
                try (ExtendedIterator<Statement> FE = this.listProperties(TBM.hasFocalElement)) {//this potential's FEs
                    while(FE.hasNext()){
                        TBMFocalElement FEThis = FE.next().getObject().as(TBMFocalElement.class);                        
                        try(ExtendedIterator<Statement> configThis = FEThis.listProperties(TBM.hasConfiguration)){//this potential's FEs' configs
                            while(configThis.hasNext()){
                                
                                if(confThat.isSubsetOf(configThis.next().getObject().as(TBMConfiguration.class))){
                                    pls += FEThis.getMass();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }*/
        //for each FE in this potential
        //  if all of query FE's configs are in FE
        //      add bel mass

        /*for (TBMConfiguration configQuery : query.listAllConfigurations().toSet()) {
            for (TBMFocalElement FE : this.listFocalElements().toSet()) {
                for (TBMConfiguration confThis : FE.listAllConfigurations().toSet()) {
                    if (configQuery.isSubsetOf(confThis)) {
                        pls += FE.getMass();
                        break;
                    }
                }
            }
        }*/
        //return pls;
        //for each FE focalElement
        //if FE == query
        //add up mass
        //return mass
    }

    @Override
    public float bel(TBMFocalElement query) {

        float bel = 0;

        try(ExtendedIterator<TBMFocalElement> FE = this.listFocalElements()){//this potential's FEs
        while (FE.hasNext()) {
            TBMFocalElement next = FE.next();
            if (next.equalConfigs(query)) {
                bel += next.getMass();
            }
        }
        }
        return bel;

        /*float bel = 0;
        for (TBMFocalElement focalElement : this.listFocalElements().toSet()) {
            if (query.isAllConfigsSubsetOf(focalElement)) {
                bel += focalElement.getMass();
            }
        }
        return bel;*/
    }

    @Override
    public float dou(TBMFocalElement query) {
        return 1 - pls(query);
    }

    @Override
    public float com(TBMFocalElement query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float ign(TBMFocalElement query) {
        return pls(query) - bel(query);
    }

    @Override
    public boolean remove() {
        this.listFocalElements().toSet().forEach(X -> X.remove());
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
        public EnhNode wrap(Node n, EnhGraph eg) {
            if (canWrap(n, eg)) {
                return new TBMPotentialImpl(n, eg);
            } else {
                throw new ConversionException("Cannot convert node " + n.toString() + " to TBMPotential: it does not have rdf:type TBM:Potential or equivalent");
            }
        }

        @Override
        public boolean canWrap(Node node, EnhGraph eg) {
            // node will support being an OntClass facet if it has rdf:type owl:Class or equivalent
            Profile profile = (eg instanceof TBMModel) ? ((TBMModel) eg).getProfile() : null;
            return (profile != null) && profile.isSupported(node, eg, TBMPotential.class);
            //return TBMModellImpl.prof.isSupported(node, eg, TBMPotential.class);
        }
    };
}

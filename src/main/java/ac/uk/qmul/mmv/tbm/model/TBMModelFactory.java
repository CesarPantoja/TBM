/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model;

import ac.uk.qmul.mmv.tbm.model.impl.TBMConfigurationImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMFocalElementImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMModellImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMPotentialImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMVarDomainImpl;
import org.apache.jena.enhanced.BuiltinPersonalities;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author Cesar
 */
public class TBMModelFactory{
    
    static{
        BuiltinPersonalities.model
                .add(TBMVarDomain.class, TBMVarDomainImpl.factory)
                .add(TBMPotential.class, TBMPotentialImpl.factory)
                .add(TBMFocalElement.class, TBMFocalElementImpl.factory)
                .add(TBMConfiguration.class, TBMConfigurationImpl.factory);
    }

    public static TBMModel createTBMModel(Model base) {
        
        return new TBMModellImpl(base);
    }    
}

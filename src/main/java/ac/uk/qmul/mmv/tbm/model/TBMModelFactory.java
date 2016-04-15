/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model;

import ac.uk.qmul.mmv.tbm.arq.TBM_Belief;
import ac.uk.qmul.mmv.tbm.arq.TBM_Doubt;
import ac.uk.qmul.mmv.tbm.arq.TBM_Ignorance;
import ac.uk.qmul.mmv.tbm.arq.TBM_Plausibility;
import ac.uk.qmul.mmv.tbm.model.impl.TBMConfigurationImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMFocalElementImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMModellImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMPotentialImpl;
import ac.uk.qmul.mmv.tbm.model.impl.TBMVarDomainImpl;
import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import org.apache.jena.enhanced.BuiltinPersonalities;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.function.FunctionRegistry;

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
        FunctionRegistry.get().put(TBM.uri + "bel", TBM_Belief.class);
        FunctionRegistry.get().put(TBM.uri + "pls", TBM_Plausibility.class);
        FunctionRegistry.get().put(TBM.uri + "dou", TBM_Doubt.class);
        FunctionRegistry.get().put(TBM.uri + "ign", TBM_Ignorance.class);
    }

    public static TBMModel createTBMModel(Model base) {
        
        return new TBMModellImpl(base);
    }    
}

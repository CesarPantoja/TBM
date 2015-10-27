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
import ac.uk.qmul.mmv.tbm.vocabulary.TBMProfile;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.ontology.Profile;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Cesar
 */
public class TBMModellImpl extends ModelCom implements TBMModel {

    Profile prof = new TBMProfile();

    public TBMModellImpl(Model base) {
        super(base != null ? base.getGraph() : org.apache.jena.graph.Factory.createGraphMem());
        this.getGraph().getPrefixMapping().setNsPrefix("TBM", TBM.getURI());
    }

    @Override
    public TBMVarDomain createDomain() {
        return createAnonResource(TBM.VarDomain, TBMVarDomain.class);
    }

    @Override
    public TBMFocalElement createFocalElement() {
        return createAnonResource(TBM.FocalElement, TBMFocalElement.class);
    }

    @Override
    public TBMPotential createPotential() {
        return createAnonResource(TBM.Potential, TBMPotential.class);
    }

    @Override
    public TBMPotential combine(TBMPotential potential1, TBMPotential potential2) {
        //create the new domain with the vars of both potential's domain
        TBMPotential combPotential = this.createPotential();
        TBMVarDomain combDomain = this.createDomain();
        //Set with all the vars
        Set<Resource> vars = potential1.getDomain().listVariables()
                .andThen(potential2.getDomain().listVariables()).toSet();
        //add all vars to the domain
        vars.forEach(p -> combDomain.addVariable(p));
        combPotential.setDomain(combDomain);

        //extend all the FEs of the two potentials
        //-> extend FEs of potential1
        Set<TBMFocalElement> extendedFEP1 = new HashSet<>();
        potential1.listFocalElements().forEachRemaining(FE -> extendedFEP1.add(extend(FE, combDomain)));
        //-> extend FEs of potential2
        Set<TBMFocalElement> extendedFEP2 = new HashSet<>();
        potential2.listFocalElements().forEachRemaining(FE -> extendedFEP2.add(extend(FE, combDomain)));
     
        double conflict = 0;
        
        //foreach focal element fe1 in potential1
        for (TBMFocalElement FE1 : extendedFEP1) {
            for (TBMFocalElement FE2 : extendedFEP2) {                
                //Create new Focal Element
                TBMFocalElement result = this.createFocalElement();
                result.setDomain(combDomain);
                
                boolean changed = false;
                
                for (TBMConfiguration config1 : FE1.listAllConfigurations().toSet()) {                    
                    for (TBMConfiguration config2 : FE2.listAllConfigurations().toSet()) {
                        //System.out.println("checking: *************");
                        //config1.listAllElements().forEachRemaining(R -> System.out.print(R+" "));
                        //System.out.println();
                        //config2.listAllElements().forEachRemaining(R -> System.out.print(R+" "));
                        //System.out.println();
                        //System.out.println("next config ************************");
                        boolean equal = true;
                        for (Resource element : config1.listAllElements().toSet()) {
                            //System.out.println("Checking "+element.toString());
                            if (!config2.hasElement(element)) {
                                //System.out.println("xxxxxx not found");
                                equal = false;
                                break;
                            }
                        }
                        if (equal) {
                            //System.out.println("found ttttttttttttttttttttttttttttttttttttttttttttttttttttttttt");
                            result.addConfiguration(config2);
                            changed = true;
                            // Add to new Focal Element
                        }
                    }
                }
                if (!changed) {
                    conflict += (FE1.getMass() * FE2.getMass());
                    //System.out.println("*****not changed "+(FE1.getMass() * FE2.getMass()));
                }
                else{
                    
                    result.setMass(FE1.getMass()*FE2.getMass());
                    //System.out.println("Changed. new mass:"+result.getMass());
                    combPotential.addFocalElement(result);
                }
                // If the new Focal Element is not empty, compute mass and add to new potential                
                // else accumulate the conflict
                //System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            }
        }
        
        //If there was conflict, recompute the masses of resulting focal elements
        if (conflict > 0) {
            //System.out.println("conflict! "+conflict);
            for (TBMFocalElement combFocalElmnt : combPotential.listFocalElements().toSet()) {
                
                combFocalElmnt.updateMass((1/(1-conflict))*combFocalElmnt.getMass());
            }
        }
        
        
        
        
        
        
        //Remove TMP extended focal elements
        extendedFEP1.forEach((FE) -> 
        {
            FE.listAllConfigurations().forEachRemaining(conf -> conf.removeProperties());
            FE.removeProperties();
        });
        
        extendedFEP2.forEach((FE) -> 
        {
            //FE.listAllConfigurations().forEachRemaining(conf -> conf.removeProperties());
            FE.removeProperties();
        });
        
        //foreach focal element fe2 in potential2
        //foreach configuration c1 in fe1
        //foreach configuration c2 in fe2
        //if c1.equals(c2)
        //add configuration to combPotential
        return combPotential;
    }

    // Creates a new Focal Element that is the extension of focalElement over domain
    private TBMFocalElement extend(TBMFocalElement focalElement, TBMVarDomain domain) {

        // Get from domain the variables that are not in focalElement 
        Set<Resource> diff = domain.listVariables().filterDrop(r -> focalElement.getDomain().hasVariable(r)).toSet();

        TBMFocalElement result = this.createFocalElement();

        result.setMass(focalElement.getMass());
        result.setDomain(domain);
        
        if (diff.isEmpty()) {
            // Clone focal element
            for (TBMConfiguration configuration : focalElement.listAllConfigurations().toSet()) {
                 //Create empty conf
                TBMConfiguration newConfig = this.createConfiguration();
                //Add variables of config
                configuration.listAllElements().forEachRemaining(res -> newConfig.addElement(res));                
                //Add config to result
                result.addConfiguration(configuration);
            }           
            
        } else {

            Set<TBMConfiguration> origConfigurations = focalElement.listAllConfigurations().toSet();

            Set<TBMConfiguration> resultConfigurations = new HashSet<>(origConfigurations);

            // For each of the different variables
            for (Resource var : diff) {

                Set<TBMConfiguration> newConfigurations = new HashSet<>();

                // For each of the instances of the variable
                for (Resource instance : this.listSubjectsWithProperty(RDF.type, var).toSet()) {
                    for (TBMConfiguration resultConfiguration : resultConfigurations) {
                        //Create empty conf
                        TBMConfiguration currentConfig = this.createConfiguration();
                        //Add variables of current config
                        resultConfiguration.listAllElements().forEachRemaining(res -> currentConfig.addElement(res));
                        //Add new variable
                        currentConfig.addElement(instance);
                        //Add result to current congig
                        newConfigurations.add(currentConfig);
                    }
                }
                //replace old configs with new configs
                resultConfigurations = newConfigurations;
            }
            
            //add resulting configs to result FE
            resultConfigurations.forEach(conf -> result.addConfiguration(conf));
        
        }
        return result;
    }

    @Override
    public TBMConfiguration createConfiguration() {
        return this.createAnonResource(TBM.Configuration, TBMConfiguration.class);
    }

    @Override
    public Profile getProfile() {
        return prof;
    }

    private <T extends RDFNode> T createAnonResource(Resource type, Class<T> view) {
        return this.createResource(type).
                as(view);
    }
}

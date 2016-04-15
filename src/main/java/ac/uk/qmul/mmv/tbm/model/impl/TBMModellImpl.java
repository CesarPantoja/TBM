/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model.impl;

import ac.uk.qmul.mmv.tbm.model.TBMConfiguration;
import ac.uk.qmul.mmv.tbm.model.TBMFocalElement;
import ac.uk.qmul.mmv.tbm.model.TBMModel;
import ac.uk.qmul.mmv.tbm.model.TBMModelFactory;
import ac.uk.qmul.mmv.tbm.model.TBMPotential;
import ac.uk.qmul.mmv.tbm.model.TBMVarDomain;
import ac.uk.qmul.mmv.tbm.vocabulary.TBM;
import ac.uk.qmul.mmv.tbm.vocabulary.TBMProfile;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.Profile;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.shared.Lock;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import static org.apache.jena.vocabulary.RDF.List;

/**
 *
 * @author Cesar
 */
public class TBMModellImpl extends ModelCom implements TBMModel {

    private static final Logger LOG = Logger.getLogger(TBMModellImpl.class.getName());

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
    public TBMFocalElement createFocalElement(String URI) {
        return createNamedResource(URI, TBM.FocalElement, TBMFocalElement.class);
    }

    @Override
    public TBMPotential createPotential() {
        return createAnonResource(TBM.Potential, TBMPotential.class);
    }

    @Override
    public TBMPotential createPotential(String URI) {
        return createNamedResource(URI, TBM.Potential, TBMPotential.class);
    }

    @Override
    public TBMPotential combine(TBMPotential potential1, TBMPotential potential2) {
        return this.combine(null, potential1, potential2);
    }

    //@Override
    public TBMPotential combineOld(String URI, TBMPotential potential1, TBMPotential potential2) {

        //create the new domain with the vars of both potential's domain
        TBMPotential combPotential;

        if (URI == null || URI.isEmpty()) {
            combPotential = this.createPotential();
        } else {
            combPotential = this.createPotential(URI);
        }

        TBMVarDomain combDomain = this.createDomain();
        //Set with all the vars
        Set<Resource> vars = potential1.getDomain().listVariables()
                .andThen(potential2.getDomain().listVariables()).toSet();
        //add all vars to the domain
        vars.forEach(p -> combDomain.addVariable(p));
        combPotential.setDomain(combDomain);
        /*potential1.getDomain().listVariables()
                .andThen(potential2.getDomain().listVariables()).forEachRemaining(v -> {
            if (!combDomain.hasVariable(v)) {
                combDomain.addVariable(v);
            }
        });*/

        //combDomain.listVariables().forEachRemaining(X->System.out.println(" "+X));
        //extend all the FEs of the two potentials
        //-> extend FEs of potential1
        Set<TBMFocalElement> extendedFEP1 = new HashSet<>();
        /*ExtendedIterator<TBMFocalElement> FE = potential1.listFocalElements();
        while (FE.hasNext()) {
            extendedFEP1.add(extend(FE.next(), combDomain));
        }*/
        //LOG.log(Level.INFO, "before extend fe1");
        for (TBMFocalElement FE : potential1.listFocalElements().toSet()) {
            extendedFEP1.add(extend(FE, combDomain));
        }
        //LOG.log(Level.INFO, " before extend fe2");
        //potential1.listFocalElements().forEachRemaining(FE -> extendedFEP1.add(extend(FE, combDomain)));
        //-> extend FEs of potential2
        Set<TBMFocalElement> extendedFEP2 = new HashSet<>();
        /*FE = potential2.listFocalElements();
        while (FE.hasNext()) {
            extendedFEP2.add(extend(FE.next(), combDomain));
        }*/
        for (TBMFocalElement FE : potential2.listFocalElements().toSet()) {
            extendedFEP2.add(extend(FE, combDomain));
        }

        //potential2.listFocalElements().forEachRemaining(FE -> extendedFEP2.add(extend(FE, combDomain)));
        double conflict = 0;

        //foreach focal element 
        for (TBMFocalElement FE1 : extendedFEP1) {
            //LOG.log(Level.INFO, "FE1************");
            for (TBMFocalElement FE2 : extendedFEP2) {
                //LOG.log(Level.INFO, "\tFE2*************");
                //Create new Focal Element
                TBMFocalElement result = this.createFocalElement();
                result.setDomain(combDomain);

                ExecutorService executor = Executors.newCachedThreadPool();

                Boolean changed = false;
                this.enterCriticalSection(Lock.READ);
                Set<TBMConfiguration> config1It = FE1.listAllConfigurations().toSet();
                this.leaveCriticalSection();
                for (TBMConfiguration config1 : config1It) {
                    //LOG.log(Level.INFO, "\t\tconfig1");
                    this.enterCriticalSection(Lock.READ);
                    Set<TBMConfiguration> config2It = FE2.listAllConfigurations().toSet();
                    this.leaveCriticalSection();
                    for (TBMConfiguration config2 : config2It) {

                        /*Runnable worker = new CombineConfThread(this, result, config1, config2, changed);
                        executor.execute(worker);*/

 /*//LOG.log(Level.INFO, "\t\t\tconfig2");
                        boolean equal = true;
                        ExtendedIterator<Resource> elemIterator = config1.listAllElements();
                        while (elemIterator.hasNext()) {
                            if (!config2.hasElement(elemIterator.next())) {
                                equal = false;
                                break;
                            }
                        }
                        if (equal) {
                            TBMConfiguration newConf = this.createConfiguration();
                            for (Resource res : config2.listAllElements().toSet()) {
                                newConf.addElement(res);
                            }
                            //config2.listAllElements().forEachRemaining(X->newConf.addElement(X));
                            result.addConfiguration(newConf);
                            changed = true;
                            // Add to new Focal Element
                        }*/
                    }
                }

                executor.shutdown();
                while (!executor.isTerminated()) {
                }

                if (!changed) {
                    conflict += (FE1.getMass() * FE2.getMass());
                    result.remove();
                } else {

                    result.setMass(FE1.getMass() * FE2.getMass());
                    combPotential.addFocalElement(result);
                }
            }
        }

        //If there was conflict, recompute the masses of resulting focal elements
        if (conflict > 0) {
            //combPotential.listFocalElements().forEachRemaining(combFocalElmnt -> combFocalElmnt.updateMass((1 / (1 - conflict)) * combFocalElmnt.getMass()));
            Set<TBMFocalElement> combFocalElmntIter = combPotential.listFocalElements().toSet();
            for (TBMFocalElement combFocalElmnt : combFocalElmntIter) {
                //TBMFocalElement combFocalElmnt = combFocalElmntIter.next();
                combFocalElmnt.updateMass((1 / (1 - conflict)) * combFocalElmnt.getMass());
            }
        }

        //Remove TMP extended focal elements
        extendedFEP1.forEach((FEE) -> FEE.remove());

        extendedFEP2.forEach((FEE) -> FEE.remove());

        return combPotential;
    }

    @Override
    public TBMPotential combine(String URI, TBMPotential potential1, TBMPotential potential2) {

        TBMPotential combPotential;
        //create the new domain with the vars of both potential's domain
        TBMModel tmpModel = TBMModelFactory.createTBMModel(null);
        if (URI == null || URI.isEmpty()) {
            combPotential = tmpModel.createPotential();
        } else {
            combPotential = tmpModel.createPotential(URI);
        }
        TBMVarDomain combDomain = tmpModel.createDomain();
        //Set with all the vars
        Set<Resource> vars = potential1.getDomain().listVariables()
                .andThen(potential2.getDomain().listVariables()).toSet();
        //add all vars to the domain
        vars.forEach(p -> combDomain.addVariable(p));
        combPotential.setDomain(combDomain);
        
        vars.forEach(var -> tmpModel.add(this.listStatements(null, RDF.type, var)));
        
        //varIters.close();
        /*potential1.getDomain().listVariables()
            .andThen(potential2.getDomain().listVariables()).forEachRemaining(v -> {
            if (!combDomain.hasVariable(v)) {
            combDomain.addVariable(v);
            }
            });*/
        //combDomain.listVariables().forEachRemaining(X->System.out.println(" "+X));
        //extend all the FEs of the two potentials
        //-> extend FEs of potential1
        //LOG.log(Level.INFO, "before extend 1");
        Set<TBMFocalElement> extendedFEP1 = new HashSet<>();
        try (ExtendedIterator<TBMFocalElement> FE = potential1.listFocalElements()) {
            while (FE.hasNext()) {
                extendedFEP1.add(extend(FE.next(), combDomain));
            }
        }
        //LOG.log(Level.INFO, "before extend 2");
        /*for (TBMFocalElement FE : potential1.listFocalElements().toSet()) {
            extendedFEP1.add(extend(FE, combDomain));
            }*/
        //potential1.listFocalElements().forEachRemaining(FE -> extendedFEP1.add(extend(FE, combDomain)));
        //-> extend FEs of potential2
        Set<TBMFocalElement> extendedFEP2 = new HashSet<>();
        try (ExtendedIterator<TBMFocalElement> FE = potential2.listFocalElements()) {
            while (FE.hasNext()) {
                extendedFEP2.add(extend(FE.next(), combDomain));
            }
        }
        //LOG.log(Level.INFO, "after extend");
        /*for (TBMFocalElement FE : potential2.listFocalElements().toSet()) {
            extendedFEP2.add(extend(FE, combDomain));
            }*/
        //potential2.listFocalElements().forEachRemaining(FE -> extendedFEP2.add(extend(FE, combDomain)));
        Conflict conflict = new Conflict();
        //double conflict = 0;
        ExecutorService executorFE = Executors.newWorkStealingPool();
        //foreach focal element
        //LOG.log(Level.INFO, "before comb");
        for (TBMFocalElement FE1 : extendedFEP1) {
            for (TBMFocalElement FE2 : extendedFEP2) {

                Runnable worker = new CombineFocalElmThread(tmpModel, FE1, FE2, combDomain, conflict, combPotential);
                executorFE.execute(worker);
                //Create new Focal Element
                /*TBMFocalElement result = tmpModel.createFocalElement();
                result.setDomain(combDomain);

                boolean changed = false;
                try (ExtendedIterator<TBMConfiguration> config1It = FE1.listAllConfigurations()) {
                    while (config1It.hasNext()) {
                        TBMConfiguration config1 = config1It.next();
                        try (ExtendedIterator<TBMConfiguration> config2It = FE2.listAllConfigurations()) {
                            while (config2It.hasNext()) {
                                TBMConfiguration config2 = config2It.next();
                                boolean equal = true;

                                try (ExtendedIterator<Resource> elemIterator = config1.listAllElements()) {
                                    while (elemIterator.hasNext()) {
                                        if (!config2.hasElement(elemIterator.next())) {
                                            equal = false;
                                            break;
                                        }
                                    }
                                }
                                if (equal) {
                                    TBMConfiguration newConf = tmpModel.createConfiguration();

                                    config2.listAllElements().forEachRemaining(res -> newConf.addElement(res));
                                    /*for (Resource res : config2.listAllElements().toSet()) {
                                        newConf.addElement(res);
                                        }*
                                    //config2.listAllElements().forEachRemaining(X->newConf.addElement(X));
                                    result.addConfiguration(newConf);
                                    changed = true;
                                    // Add to new Focal Element
                                }
                            }
                        }
                    }
                }
                if (!changed) {
                    conflict += (FE1.getMass() * FE2.getMass());
                    result.remove();
                } else {

                    result.setMass(FE1.getMass() * FE2.getMass());
                    combPotential.addFocalElement(result);
                }*/
            }
        }
        executorFE.shutdown();
        //try {
        while (!executorFE.isTerminated()) {
        }
        //executorFE.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        /*} catch (InterruptedException ex) {
            Logger.getLogger(TBMModellImpl.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        //LOG.log(Level.INFO, "after comb");
        //If there was conflict, recompute the masses of resulting focal elements
        if (conflict.getConflict() > 0) {
            //combPotential.listFocalElements().forEachRemaining(combFocalElmnt -> combFocalElmnt.updateMass((1 / (1 - conflict)) * combFocalElmnt.getMass()));
            try (ExtendedIterator<TBMFocalElement> combFocalElmntIter = combPotential.listFocalElements()) {
                while (combFocalElmntIter.hasNext()) {
                    TBMFocalElement combFocalElmnt = combFocalElmntIter.next();
                    combFocalElmnt.updateMass((1 / (1 - conflict.getConflict())) * combFocalElmnt.getMass());
                }
            }
        }

        //for each focal element fe1 in combpotential
        // for each focal element fe2 in combpotential
        //if fe1 = fe2
        //remove fe2
        //set fe1.mass += fe2.mass
        /*List<TBMFocalElement> fes = combPotential.listFocalElements().toList();

        for (int i = 0; i < fes.size(); i++) {
            TBMFocalElement fe1 = fes.get(i);
            for (int j = 0; j < fes.size(); j++) {
                TBMFocalElement fe2 = fes.get(j);

                if (fe1 != fe2 && fe1.equalConfigs(fe2)) {
                    fe1.updateMass(fe1.getMass() + fe2.getMass());
                    fes.remove(j);
                    fe2.remove();
                    j--;
                } 
            }
        }*/
        this.add(tmpModel);
        //tmpModel.write(System.out);
        //this.in
        TBMPotential p = this.getNodeAs(combPotential.asNode(), TBMPotential.class);
        tmpModel.close();
        return p;

        //return combPotential;
    }

    // Creates a new Focal Element that is the extension of focalElement over domain
    private TBMFocalElement extend(TBMFocalElement focalElement, TBMVarDomain domain) {

        TBMModel m = TBMModelFactory.createTBMModel(null);

        TBMFocalElement result = m.createFocalElement();

        result.setMass(focalElement.getMass());
        result.setDomain(domain);
        // Get from domain the variables that are not in focalElement 
        try (ExtendedIterator<Resource> diff = domain.listVariables().filterDrop(r -> focalElement.getDomain().hasVariable(r))) {

            if (!diff.hasNext()) { //there are no differences, clone the focalElement and it's configurations
                // Clone focal element
                try (ExtendedIterator<TBMConfiguration> iterConfs = focalElement.listAllConfigurations()) {
                    while (iterConfs.hasNext()) {
                        //Create empty conf
                        TBMConfiguration newConfig = m.createConfiguration();
                        //Add variables of config
                        //configuration.listAllElements().forEachRemaining(res -> newConfig.addElement(res));   
                        iterConfs.next().listAllElements().forEachRemaining(elem -> newConfig.addElement(elem));
                        //newConfig.addElement(iterElements.next());

                        //Add config to result
                        result.addConfiguration(newConfig);
                    }
                }

            } else { //there are differences

                Set<TBMConfiguration> resultConfigurations = new HashSet<>();

                try (ExtendedIterator<TBMConfiguration> origConfigurations = focalElement.listAllConfigurations()) {
                    while (origConfigurations.hasNext()) {
                        resultConfigurations.add(origConfigurations.next());
                    }
                }

                // For each of the different variables
                while (diff.hasNext()) {

                    Resource var = diff.next();

                    Set<TBMConfiguration> newConfigurations = new HashSet<>();

                    // For each of the instances of the variable
                    try (ExtendedIterator<Resource> instance = this.listSubjectsWithProperty(RDF.type, var)) {
                        while (instance.hasNext()) {
                            Resource newVar = instance.next();
                            for (TBMConfiguration resultConfiguration : resultConfigurations) {
                                //Create empty conf
                                TBMConfiguration currentConfig = m.createConfiguration();
                                //Add variables of current config
                                resultConfiguration.listAllElements().forEachRemaining(c -> currentConfig.addElement(c));
                                /*try (ExtendedIterator<Resource> res = resultConfiguration.listAllElements()) {
                                    while (res.hasNext()) {
                                        currentConfig.addElement(res.next());
                                    }
                                }*/
                                //resultConfiguration.listAllElements().forEachRemaining(res -> currentConfig.addElement(res));
                                //Add new variable
                                currentConfig.addElement(newVar);
                                //Add result to current congig
                                newConfigurations.add(currentConfig);
                            }
                        }
                    }

                    //replace old configs with new configs
                    resultConfigurations = newConfigurations;
                }

                //add resulting configs to result FE
                resultConfigurations.forEach(conf -> result.addConfiguration(conf));

            }
        }
        return result;

    }

    // Creates a new Focal Element that is the extension of focalElement over domain
    private TBMFocalElement extendOld(TBMFocalElement focalElement, TBMVarDomain domain) {

        TBMFocalElement result = this.createFocalElement();

        result.setMass(focalElement.getMass());
        result.setDomain(domain);
        // Get from domain the variables that are not in focalElement 
        Set<Resource> diff = domain.listVariables().filterDrop(r -> focalElement.getDomain().hasVariable(r)).toSet();

        if (diff.isEmpty()) { //there are no differences, clone the focalElement and it's configurations
            // Clone focal element
            Set<TBMConfiguration> iterConfs = focalElement.listAllConfigurations().toSet();
            for (TBMConfiguration conf : iterConfs) {
                //Create empty conf
                TBMConfiguration newConfig = this.createConfiguration();
                //Add variables of config
                //configuration.listAllElements().forEachRemaining(res -> newConfig.addElement(res));   
                conf.listAllElements().toSet().forEach(elem -> newConfig.addElement(elem));
                //newConfig.addElement(iterElements.next());

                //Add config to result
                result.addConfiguration(newConfig);
            }
        } else { //there are differences

            Set<TBMConfiguration> resultConfigurations = new HashSet<>();

            ExtendedIterator<TBMConfiguration> origConfigurations = focalElement.listAllConfigurations();
            while (origConfigurations.hasNext()) {
                resultConfigurations.add(origConfigurations.next());
            }

            // For each of the different variables
            for (Resource var : diff) {

                Set<TBMConfiguration> newConfigurations = new HashSet<>();

                // For each of the instances of the variable
                Set<Resource> instance = this.listSubjectsWithProperty(RDF.type, var).toSet();
                for (Resource newVar : instance) {

                    for (TBMConfiguration resultConfiguration : resultConfigurations) {
                        //Create empty conf
                        TBMConfiguration currentConfig = this.createConfiguration();
                        //Add variables of current config
                        for (Resource res : resultConfiguration.listAllElements().toSet()) {
                            currentConfig.addElement(res);
                        }
                        //resultConfiguration.listAllElements().forEachRemaining(res -> currentConfig.addElement(res));
                        //Add new variable
                        currentConfig.addElement(newVar);
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

    private <T extends RDFNode> T createNamedResource(String URI, Resource type, Class<T> view) {
        return this.createResource(URI)
                .addProperty(RDF.type, type)
                .as(view);
    }
}

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
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.Lock;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public class CombineFocalElmThread implements Runnable {

    private TBMModel destModel;

    /**
     * Get the value of destModel
     *
     * @return the value of destModel
     */
    public TBMModel getDestModel() {
        return destModel;
    }

    /**
     * Set the value of destModel
     *
     * @param destModel new value of destModel
     */
    public void setDestModel(TBMModel destModel) {
        this.destModel = destModel;
    }

    private TBMFocalElement FE1;

    /**
     * Get the value of FE1
     *
     * @return the value of FE1
     */
    public TBMFocalElement getFE1() {
        return FE1;
    }

    /**
     * Set the value of FE1
     *
     * @param FE1 new value of FE1
     */
    public void setFE1(TBMFocalElement FE1) {
        this.FE1 = FE1;
    }

    private TBMFocalElement FE2;

    /**
     * Get the value of FE2
     *
     * @return the value of FE2
     */
    public TBMFocalElement getFE2() {
        return FE2;
    }

    /**
     * Set the value of FE2
     *
     * @param FE2 new value of FE2
     */
    public void setFE2(TBMFocalElement FE2) {
        this.FE2 = FE2;
    }

    private TBMVarDomain combDomain;

    /**
     * Get the value of combDomain
     *
     * @return the value of combDomain
     */
    public TBMVarDomain getCombDomain() {
        return combDomain;
    }

    /**
     * Set the value of combDomain
     *
     * @param combDomain new value of combDomain
     */
    public void setCombDomain(TBMVarDomain combDomain) {
        this.combDomain = combDomain;
    }

    private Conflict conflict;

    /**
     * Get the value of conflict
     *
     * @return the value of conflict
     */
    public Conflict getConflict() {
        return conflict;
    }

    /**
     * Set the value of conflict
     *
     * @param conflict new value of conflict
     */
    public void setConflict(Conflict conflict) {
        this.conflict = conflict;
    }

    private TBMPotential combPotential;

    /**
     * Get the value of combPotential
     *
     * @return the value of combPotential
     */
    public TBMPotential getCombPotential() {
        return combPotential;
    }

    /**
     * Set the value of combPotential
     *
     * @param combPotential new value of combPotential
     */
    public void setCombPotential(TBMPotential combPotential) {
        this.combPotential = combPotential;
    }

    public CombineFocalElmThread(TBMModel destModel, TBMFocalElement FE1, TBMFocalElement FE2, TBMVarDomain combDomain, Conflict conflict, TBMPotential combPotential) {

        this.destModel = destModel;
        this.FE1 = FE1;
        this.FE2 = FE2;
        this.combDomain = combDomain;
        this.conflict = conflict;
        this.combPotential = combPotential;
    }

    @Override
    public void run() {
        //destModel.enterCriticalSection(Lock.WRITE);

        //        TBMModel tmpModel = TBMModelFactory.createTBMModel(null);
        //try (TBMModel tmpModel = TBMModelFactory.createTBMModel(null)) {
        TBMModel tmpModel = TBMModelFactory.createTBMModel(null);
            //destModel.enterCriticalSection(Lock.WRITE);
            //Create new Focal Element
            TBMFocalElement result = tmpModel.createFocalElement();
            result.setDomain(combDomain);
            //destModel.leaveCriticalSection();
            ExecutorService executor = Executors.newWorkStealingPool();
            Changed changed = new Changed();
            
            //destModel.enterCriticalSection(Lock.READ);
            //Set<TBMConfiguration> config1It = FE1.listAllConfigurations().toSet();
            //destModel.leaveCriticalSection();
            //try (ExtendedIterator<TBMConfiguration> config1It = FE1.listAllConfigurations()) {
            ExtendedIterator<TBMConfiguration> config1It = FE1.listAllConfigurations();
                while (config1It.hasNext()) {
                //for(TBMConfiguration config1 : config1It){
                    TBMConfiguration config1 = config1It.next();
                    //FE2.getModel().enterCriticalSection(Lock.READ);
                    //Set<TBMConfiguration> config2It = FE2.listAllConfigurations().toSet();
                    //FE2.getModel().leaveCriticalSection();
                    //try (ExtendedIterator<TBMConfiguration> config2It = FE2.listAllConfigurations()) {
                    ExtendedIterator<TBMConfiguration> config2It = FE2.listAllConfigurations();   
                        while (config2It.hasNext()) {
                        //for(TBMConfiguration config2 : config2It) {
                            TBMConfiguration config2 = config2It.next();

                            Runnable worker = new CombineConfThread(tmpModel, result, config1, config2, changed);
                            executor.execute(worker);
                        }
                    //}
                }
            //}

            executor.shutdown();
            //try {
                //executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                while (!executor.isTerminated()) {}
            /*} catch (InterruptedException ex) {
                Logger.getLogger(CombineFocalElmThread.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            if (!changed.isChanged()) {
                //destModel.enterCriticalSection(Lock.READ);                
                conflict.increase(FE1.getMass() * FE2.getMass());
                //destModel.leaveCriticalSection();

                //destModel.enterCriticalSection(Lock.WRITE);
                //result.remove();
                //destModel.leaveCriticalSection();
            } else {
                //destModel.enterCriticalSection(Lock.READ);
                //result.getModel().enterCriticalSection(Lock.WRITE);
                result.setMass(FE1.getMass() * FE2.getMass());
                //result.getModel().leaveCriticalSection();
                //destModel.leaveCriticalSection();
                destModel.enterCriticalSection(Lock.WRITE);
                destModel.add(tmpModel);                
                combPotential.addFocalElement(result);
                destModel.leaveCriticalSection();
            }
        //}
    }

}

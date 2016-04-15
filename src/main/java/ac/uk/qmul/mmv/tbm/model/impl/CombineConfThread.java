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
import java.util.Set;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.Lock;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public class CombineConfThread implements Runnable {

    private final TBMModel model;

    /**
     * Get the value of model
     *
     * @return the value of model
     */
    public TBMModel getModel() {
        return model;
    }

    private TBMConfiguration config1;

    /**
     * Get the value of Configuration1
     *
     * @return the value of Configuration1
     */
    public TBMConfiguration getConfig1() {
        return config1;
    }

    /**
     * Set the value of Configuration1
     *
     * @param config1 new value of Configuration1
     */
    public void setConfig1(TBMConfiguration config1) {
        this.config1 = config1;
    }

    private TBMConfiguration config2;

    /**
     * Get the value of Configuration2
     *
     * @return the value of Configuration2
     */
    public TBMConfiguration getConfig2() {
        return config2;
    }

    /**
     * Set the value of Configuration2
     *
     * @param config2 new value of Configuration2
     */
    public void setConfig2(TBMConfiguration config2) {
        this.config2 = config2;
    }

    private Changed changed;

    /**
     * Get the value of changed
     *
     * @return the value of changed
     */
    public Changed isChanged() {
        return changed;
    }

    /**
     * Set the value of changed
     *
     * @param changed new value of changed
     */
    public void setChanged(Changed changed) {
        this.changed = changed;
    }

    private TBMFocalElement result;

    /**
     * Get the value of result
     *
     * @return the value of result
     */
    public TBMFocalElement getResult() {
        return result;
    }

    /**
     * Set the value of result
     *
     * @param result new value of result
     */
    public void setResult(TBMFocalElement result) {
        this.result = result;
    }

    public CombineConfThread(TBMModel model, TBMFocalElement result, TBMConfiguration Configuration1, TBMConfiguration Configuration2, Changed changed) {
        this.model = model;
        this.result = result;
        this.config1 = Configuration1;
        this.config2 = Configuration2;
        this.changed = changed;
    }

    @Override
    public void run() {

        boolean equal = true;
        //config1.getModel().enterCriticalSection(Lock.READ);
        //Set<Resource> elemIterator = config1.listAllElements().toSet();
        //config1.getModel().leaveCriticalSection();
        //try (ExtendedIterator<Resource> elemIterator = config1.listAllElements()) {
        ExtendedIterator<Resource> elemIterator = config1.listAllElements();   
            //for(Resource elem : elemIterator) {
            while (elemIterator.hasNext()) {
            //config2.getModel().enterCriticalSection(Lock.READ);
            //boolean hasElement = config2.hasElement(elem/*Iterator.next()*/);            //
            //config2.getModel().leaveCriticalSection();
                if (!config2.hasElement(elemIterator.next())) {
                    equal = false;
                    break;
                }
            }
        //}
        if (equal) {
            //TBMModel tmpModel = TBMModelFactory.createTBMModel(null);
            //try (TBMModel tmpModel = TBMModelFactory.createTBMModel(null)) {
            TBMModel tmpModel = TBMModelFactory.createTBMModel(null);
                //model.enterCriticalSection(Lock.WRITE);
                TBMConfiguration newConf = tmpModel.createConfiguration();
                //model.leaveCriticalSection();
                //config2.listAllElements().forEachRemaining(res -> newConf.addElement(res));
                //config2.getModel().enterCriticalSection(Lock.READ);
                //Set<Resource> res = config2.listAllElements().toSet();
                //config2.getModel().leaveCriticalSection();
                //try (ExtendedIterator<Resource> res = config2.listAllElements()) {
                ExtendedIterator<Resource> res = config2.listAllElements();
                    //for(Resource r : res){
                    while (res.hasNext()) {
                        
                        newConf.addElement(res.next());
                        
                    }
                //}
                /*for (Resource res : config2.listAllElements().toSet()) {
                newConf.addElement(res);
                }*/
                //config2.listAllElements().forEachRemaining(X->newConf.addElement(X));
                //model.leaveCriticalSection();
                // Add to new Focal Element
                model.enterCriticalSection(Lock.WRITE);
                result.addConfiguration(newConf);
                model.add(tmpModel);
                model.leaveCriticalSection();
                changed.setChanged();
            //}
        }
    }

    //@Override
    public void runOld() {
        //LOG.log(Level.INFO, "\t\t\tconfig2");

        boolean equal = true;
        Set<Resource> elemIterator = null;

        //model.enterCriticalSection(Lock.READ);
        elemIterator = config1.listAllElements().toSet();
        //model.leaveCriticalSection();

        for (Resource res : elemIterator) {
            //model.enterCriticalSection(Lock.READ);
            if (!config2.hasElement(res)) {
                equal = false;
                break;
            }
            //model.leaveCriticalSection();
        }
        if (equal) {

            //model.enterCriticalSection(Lock.READ);
            Set<Resource> c = config2.listAllElements().toSet();
            model.leaveCriticalSection();

            //model.enterCriticalSection(Lock.WRITE);
            TBMConfiguration newConf = model.createConfiguration();
            for (Resource res : c) {
                newConf.addElement(res);
            }
            //config2.listAllElements().forEachRemaining(X->newConf.addElement(X));
            result.addConfiguration(newConf);
            //model.leaveCriticalSection();

            //changed = true;
            // Add to new Focal Element
        }

    }

}

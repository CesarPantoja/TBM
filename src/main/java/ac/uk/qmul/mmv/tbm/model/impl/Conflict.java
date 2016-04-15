/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model.impl;

/**
 *
 * @author Cesar
 */
public class Conflict {

    private double conflict = 0;

    /**
     * Get the value of Conflict
     *
     * @return the value of Conflict
     */
    public double getConflict() {
        return conflict;
    }

    public synchronized void increase(double value){
        conflict+=value;
    }
}

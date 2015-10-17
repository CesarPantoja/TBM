/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public interface TBMPotential extends Resource{

    public void addFocalElement(TBMFocalElement focalElement);
    
    public ExtendedIterator<TBMFocalElement> listFocalElements();
    
    public void setDomain(TBMVarDomain domain);
    
    public TBMVarDomain getDomain();

    public float bel(TBMFocalElement query);

    public double pls(TBMFocalElement query);

    public double dou(TBMFocalElement query);

    public double com(TBMFocalElement query);

    public double ign(TBMFocalElement query);
    
}

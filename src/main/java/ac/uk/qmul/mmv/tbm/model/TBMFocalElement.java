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
public interface TBMFocalElement extends Resource{

    public void setDomain(TBMVarDomain domain);
    
    public TBMVarDomain getDomain();

    public void addConfiguration(Resource... elements);
    
    public void addConfiguration(TBMConfiguration configuration);

    public void addAllConfigurations();
    
    public ExtendedIterator<TBMConfiguration> listAllConfigurations();

    public void setMass(double mass);
    
    public double getMass();

    public void updateMass(double mass);
    
}

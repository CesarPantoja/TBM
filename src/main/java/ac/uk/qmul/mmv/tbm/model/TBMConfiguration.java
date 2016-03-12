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
public interface TBMConfiguration extends Resource {

    public boolean hasElement(Resource element);
    
    public void addElement(Resource element);
    
    public void addElements(Resource... elements);
    
    public ExtendedIterator<Resource> listAllElements();
    
    public boolean isSubsetOf(TBMConfiguration config);
    
    public boolean remove();
    
}

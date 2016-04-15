/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Cesar
 */
public interface TBMVarDomain extends Resource {

    public void addVariable(Resource variable);
    
    public boolean hasVariable(Resource variable);
    
    public ExtendedIterator<Resource> listVariables(); 
    
    public boolean validVar(Resource variable);
}

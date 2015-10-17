/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.model;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Profile;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author Cesar
 */
public interface TBMModel extends Model{
    
    public Profile getProfile();

    public TBMVarDomain createDomain();

    public TBMFocalElement createFocalElement();

    public TBMPotential createPotential();

    public TBMPotential combine(TBMPotential potential1, TBMPotential potential2);

}

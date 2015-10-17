/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Cesar
 */
public class TBM {
    public static final String uri="http://mmv.eecs.qmul.ac.uk/TBM.owl#";
    
    public static String getURI()
        { return uri; }

    protected static final Resource resource( String local )
        { return ResourceFactory.createResource( uri + local ); }

    protected static final Property property( String local )
        { return ResourceFactory.createProperty( uri, local ); }
    
    public static final Resource VarDomain = resource("VarDomain");    
    public static final Resource FocalElement = resource("FocalElement");
    public static final Resource Configuration = resource("Configuration");
    public static final Resource Potential = resource("Potential");
    
    public static final Property hasVariable = property( "hasVariable" );
    public static final Property hasDomain = property( "hasDomain" );
    public static final Property hasConfiguration = property( "hasConfiguration" );
    public static final Property hasMass = property( "hasMass" );
    public static final Property hasElement = property( "hasElement" );
    public static final Property hasFocalElement = property( "hasFocalElement" );
                
}

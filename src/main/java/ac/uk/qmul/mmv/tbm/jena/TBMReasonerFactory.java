/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ac.uk.qmul.mmv.tbm.jena;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerFactory;
import org.apache.jena.reasoner.ReasonerRegistry;


/**
 *
 * @author Cesar
 */
public class TBMReasonerFactory implements ReasonerFactory {

    private static final String URI = "http://tbm.mmv.eecs.qmul.ac.uk/";

    private static TBMReasonerFactory theInstance;

    public static OntModelSpec THE_SPEC;

    static {
        theInstance = new TBMReasonerFactory();

        THE_SPEC = new OntModelSpec(ModelFactory.createMemModelMaker(), null, theInstance, URI);

        ReasonerRegistry.theRegistry().register(TBMReasonerFactory.theInstance());
    }

    public static TBMReasonerFactory theInstance() {
        return theInstance;
    }

    private Model reasonerCapabilities;

    private TBMReasonerFactory() {
    }

    @Override
    public Reasoner create(Resource configuration) {
        return new TBMReasoner();
    }

    public Model getCapabilities() {
        /*if (reasonerCapabilities == null) {
            reasonerCapabilities = ModelFactory.createDefaultModel();
            Resource base = reasonerCapabilities.createResource(URI);
            base.addProperty(ReasonerVocabulary.nameP, "TBM Reasoner")
                    .addProperty(ReasonerVocabulary.descriptionP, "Transferable Belief Model reasoner")
                    //////////////////////////ToDo: change this/////////////////////////////////
                    .addProperty(ReasonerVocabulary.supportsP, RDFS.subClassOf)
                    .addProperty(ReasonerVocabulary.supportsP, RDFS.subPropertyOf)
                    .addProperty(ReasonerVocabulary.supportsP, RDFS.member)
                    .addProperty(ReasonerVocabulary.supportsP, RDFS.range)
                    .addProperty(ReasonerVocabulary.supportsP, RDFS.domain)
                    .addProperty(ReasonerVocabulary.supportsP, ReasonerVocabulary.individualAsThingP)
                    .addProperty(ReasonerVocabulary.supportsP, ReasonerVocabulary.directSubClassOf)
                    .addProperty(ReasonerVocabulary.supportsP, ReasonerVocabulary.directSubPropertyOf)
                    .addProperty(ReasonerVocabulary.supportsP, ReasonerVocabulary.directRDFType);
        }

        return reasonerCapabilities;*/
        return null;
    }

    
    public String getURI() {
        return URI;
    }

}

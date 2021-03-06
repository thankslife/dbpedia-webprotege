package edu.stanford.bmir.protege.web.server.hierarchy;

import com.google.inject.Provider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 02/06/15
 */
public class ClassHierarchyRootProvider implements Provider<OWLClass> {

    private final OWLDataFactory dataFactory;

    @Inject
    public ClassHierarchyRootProvider(OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    @Override
    public OWLClass get() {
        return dataFactory.getOWLThing();
    }
}

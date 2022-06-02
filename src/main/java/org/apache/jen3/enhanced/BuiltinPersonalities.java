/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.enhanced;
import java.io.PrintWriter ;
import java.util.Map ;

import org.apache.jen3.ontology.AllDifferent;
import org.apache.jen3.ontology.AllValuesFromRestriction;
import org.apache.jen3.ontology.AnnotationProperty;
import org.apache.jen3.ontology.CardinalityQRestriction;
import org.apache.jen3.ontology.CardinalityRestriction;
import org.apache.jen3.ontology.ComplementClass;
import org.apache.jen3.ontology.DataRange;
import org.apache.jen3.ontology.DatatypeProperty;
import org.apache.jen3.ontology.EnumeratedClass;
import org.apache.jen3.ontology.FunctionalProperty;
import org.apache.jen3.ontology.HasValueRestriction;
import org.apache.jen3.ontology.Individual;
import org.apache.jen3.ontology.IntersectionClass;
import org.apache.jen3.ontology.InverseFunctionalProperty;
import org.apache.jen3.ontology.MaxCardinalityQRestriction;
import org.apache.jen3.ontology.MaxCardinalityRestriction;
import org.apache.jen3.ontology.MinCardinalityQRestriction;
import org.apache.jen3.ontology.MinCardinalityRestriction;
import org.apache.jen3.ontology.ObjectProperty;
import org.apache.jen3.ontology.OntClass;
import org.apache.jen3.ontology.OntProperty;
import org.apache.jen3.ontology.OntResource;
import org.apache.jen3.ontology.Ontology;
import org.apache.jen3.ontology.QualifiedRestriction;
import org.apache.jen3.ontology.Restriction;
import org.apache.jen3.ontology.SomeValuesFromRestriction;
import org.apache.jen3.ontology.SymmetricProperty;
import org.apache.jen3.ontology.TransitiveProperty;
import org.apache.jen3.ontology.UnionClass;
import org.apache.jen3.ontology.impl.AllDifferentImpl;
import org.apache.jen3.ontology.impl.AllValuesFromRestrictionImpl;
import org.apache.jen3.ontology.impl.AnnotationPropertyImpl;
import org.apache.jen3.ontology.impl.CardinalityQRestrictionImpl;
import org.apache.jen3.ontology.impl.CardinalityRestrictionImpl;
import org.apache.jen3.ontology.impl.ComplementClassImpl;
import org.apache.jen3.ontology.impl.DataRangeImpl;
import org.apache.jen3.ontology.impl.DatatypePropertyImpl;
import org.apache.jen3.ontology.impl.EnumeratedClassImpl;
import org.apache.jen3.ontology.impl.FunctionalPropertyImpl;
import org.apache.jen3.ontology.impl.HasValueRestrictionImpl;
import org.apache.jen3.ontology.impl.IndividualImpl;
import org.apache.jen3.ontology.impl.IntersectionClassImpl;
import org.apache.jen3.ontology.impl.InverseFunctionalPropertyImpl;
import org.apache.jen3.ontology.impl.MaxCardinalityQRestrictionImpl;
import org.apache.jen3.ontology.impl.MaxCardinalityRestrictionImpl;
import org.apache.jen3.ontology.impl.MinCardinalityQRestrictionImpl;
import org.apache.jen3.ontology.impl.MinCardinalityRestrictionImpl;
import org.apache.jen3.ontology.impl.ObjectPropertyImpl;
import org.apache.jen3.ontology.impl.OntClassImpl;
import org.apache.jen3.ontology.impl.OntPropertyImpl;
import org.apache.jen3.ontology.impl.OntResourceImpl;
import org.apache.jen3.ontology.impl.OntologyImpl;
import org.apache.jen3.ontology.impl.QualifiedRestrictionImpl;
import org.apache.jen3.ontology.impl.RestrictionImpl;
import org.apache.jen3.ontology.impl.SomeValuesFromRestrictionImpl;
import org.apache.jen3.ontology.impl.SymmetricPropertyImpl;
import org.apache.jen3.ontology.impl.TransitivePropertyImpl;
import org.apache.jen3.ontology.impl.UnionClassImpl;
import org.apache.jen3.rdf.model.Alt;
import org.apache.jen3.rdf.model.Bag;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.Collection;
import org.apache.jen3.rdf.model.Container;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.Property;
import org.apache.jen3.rdf.model.QuantifiedVariable;
import org.apache.jen3.rdf.model.Quantifier;
import org.apache.jen3.rdf.model.QuickVariable;
import org.apache.jen3.rdf.model.RDFList;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.ReifiedStatement;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Seq;
import org.apache.jen3.rdf.model.impl.AltImpl;
import org.apache.jen3.rdf.model.impl.BagImpl;
import org.apache.jen3.rdf.model.impl.CitedFormulaImpl;
import org.apache.jen3.rdf.model.impl.CollectionImpl;
import org.apache.jen3.rdf.model.impl.LiteralImpl;
import org.apache.jen3.rdf.model.impl.PropertyImpl;
import org.apache.jen3.rdf.model.impl.QuantifiedVariableImpl;
import org.apache.jen3.rdf.model.impl.QuantifierImpl;
import org.apache.jen3.rdf.model.impl.QuickVariableImpl;
import org.apache.jen3.rdf.model.impl.RDFListImpl;
import org.apache.jen3.rdf.model.impl.ReifiedStatementImpl;
import org.apache.jen3.rdf.model.impl.ResourceImpl;
import org.apache.jen3.rdf.model.impl.SeqImpl;

/**
    The personalities that are provided for the existing Jena classes. It is likely that this
    should be factored.
*/
public class BuiltinPersonalities {

    static final private Personality<RDFObject> graph = new Personality<>();

    static final public Personality<RDFObject> model = graph.copy()
        .add( Resource.class, ResourceImpl.factory )
        .add( Property.class, PropertyImpl.factory )
        .add( Literal.class,LiteralImpl.factory )
        .add( Container.class, ResourceImpl.factory )
        .add( Alt.class, AltImpl.factory )
        .add( Bag.class, BagImpl.factory )
        .add( Seq.class, SeqImpl.factory )
        .add( ReifiedStatement.class, ReifiedStatementImpl.reifiedStatementFactory )
        .add( RDFList.class, RDFListImpl.factory )

        // ontology additions
        .add( OntResource.class, OntResourceImpl.factory )
        .add( Ontology.class, OntologyImpl.factory )
        .add( OntClass.class, OntClassImpl.factory )
        .add( EnumeratedClass.class, EnumeratedClassImpl.factory )
        .add( IntersectionClass.class, IntersectionClassImpl.factory )
        .add( UnionClass.class, UnionClassImpl.factory )
        .add( ComplementClass.class, ComplementClassImpl.factory )
        .add( DataRange.class, DataRangeImpl.factory )

        .add( Restriction.class, RestrictionImpl.factory )
        .add( HasValueRestriction.class, HasValueRestrictionImpl.factory )
        .add( AllValuesFromRestriction.class, AllValuesFromRestrictionImpl.factory )
        .add( SomeValuesFromRestriction.class, SomeValuesFromRestrictionImpl.factory )
        .add( CardinalityRestriction.class, CardinalityRestrictionImpl.factory )
        .add( MinCardinalityRestriction.class, MinCardinalityRestrictionImpl.factory )
        .add( MaxCardinalityRestriction.class, MaxCardinalityRestrictionImpl.factory )
        .add( QualifiedRestriction.class, QualifiedRestrictionImpl.factory )
        .add( MinCardinalityQRestriction.class, MinCardinalityQRestrictionImpl.factory )
        .add( MaxCardinalityQRestriction.class, MaxCardinalityQRestrictionImpl.factory )
        .add( CardinalityQRestriction.class, CardinalityQRestrictionImpl.factory )

        .add( OntProperty.class, OntPropertyImpl.factory )
        .add( ObjectProperty.class, ObjectPropertyImpl.factory )
        .add( DatatypeProperty.class, DatatypePropertyImpl.factory )
        .add( TransitiveProperty.class, TransitivePropertyImpl.factory )
        .add( SymmetricProperty.class, SymmetricPropertyImpl.factory )
        .add( FunctionalProperty.class, FunctionalPropertyImpl.factory )
        .add( InverseFunctionalProperty.class, InverseFunctionalPropertyImpl.factory )
        .add( AllDifferent.class, AllDifferentImpl.factory )
        .add( Individual.class, IndividualImpl.factory )
        .add( AnnotationProperty.class, AnnotationPropertyImpl.factory )

        // N3 additions
        .add( CitedFormula.class, CitedFormulaImpl.factory )
        .add( QuickVariable.class, QuickVariableImpl.factory )
        .add( QuantifiedVariable.class, QuantifiedVariableImpl.factory )
        .add( Collection.class, CollectionImpl.factory )
        .add( Quantifier.class, QuantifierImpl.factory )
        
        // Last and least ?
        .add( RDFObject.class, ResourceImpl.rdfNodeFactory )
        ;


    /**
     * For debugging purposes, list the standard personalities on the given
     * output writer.
     *
     * @param writer A printwriter to list the personalities mapping to
     */
    static public void listPersonalities( PrintWriter writer ) {
        for ( Map.Entry<Class<? extends RDFObject>, Implementation> e : model.getMap().entrySet() )
        {
            writer.println( "personality key " + e.getKey().getName() + " -> value " + e.getValue() );
        }
        writer.flush();
    }
}

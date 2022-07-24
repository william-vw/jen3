Jena2+N3 README
============

Welcome to Apache Jena+N3,  a Java framework for writing Semantic Web applications in N3.

Documentation for the original framework can be found on the web at
http://jena.apache.org/

Create an issue on the GitHub repo for bugs and feedback: ..


- Programmatically creating N3 graphs

.. 

-- quantified variables
(creating qvars)

consider the following N3 graph: ..




(avoiding ambiguity between IRIs and qvars)
creating a qvar programmatically: ..
results in the following N3 code: ..

if you'd create an IRI with the same name later on: ..
you'd get the following N3 code: ..

Problem in this N3 code is that, as far as any system parsing it, the IRI is in fact an occurrence of the quantified variable. 
Jena+N3 does its best to avoid these situations by issuing a warning (or an exception, depending on the config) whenever that is detected. 
However, this detection scheme is based on the internal IRI cache kept by Jena (which has 1000 limit) so this will easily break for large N3 graphs.
It's rather meant to put beginning developers on the right path and warn them of the danger of name clashes.


(scoping of qvars and cited formulas)
the scope of quantified variables, and cited formulas, is an important concept in N3
e.g., .. (rule example here illustrating differences from scopes)

in practice, these scopes:
	tell Jena+N3 how to interpret and execute rules; 
	also, how to print N3 graphs: quantifiers of qvars within a cited formula will always be listed at the top
 
hence, a qvar or cited formula must be created using the create..() methods of its containing graph (e.g., original model, or a cited formula) 
	this way, Jena+N3 can easily keep track of these scopes - it makes it a lot easier than expecting the coder to do that manually
	the graph where it was created will be the parent-scope of the "scoped object" (i.e., cited formula or qvar)

e.g., the following N3 graph: ..

will give rise to the following scope "hierarchy": ..
(check these out yourself using scope#printHierarchy)

e.g., you can use a qvar from a higher scope in a lower-scoped cited formula:

in general, you can use a qvar in a graph if it is scoped within that graph, or one of the parent scopes of that graph
in other cases, such as using a qvar within two different (unrelated) cited formulas, Jena+N3 will throw an exception since only a single scope is allowed per object


(immutable cited formulas and collections)
the identity of cited-formulas and collections is wholly based on their contents
once these have been "closed", or added to an N3 model, they can no longer be modified
	if a modification attempt is made, an exception will be thrown
	
however, one can easily create a fresh copy (copy() methods) of a cited formula or collection  

(in Apache Jena, triples are indexed on the hashcodes of their S/P/O ; 
updating a cited formula or collection after they have been indexed means they will still be indexed on their prior hashcode, 
but equality will be determined based on their new content ; as a result, we'd no longer be able to find the updated version)
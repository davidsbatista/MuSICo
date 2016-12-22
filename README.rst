Minwise Hashing for Relationship Extraction from Text
=====================================================

The MinHash-based Semantic Relationship Classifier (MuSICo) is an  on-line approach for extracting of semantic relationships, based on the idea of nearest neighbor classification.  

Instead of learning a statistical model, it finds the most similar relationship instances in a database and uses these similarities to make the decision of whether the sentence holds a certain relationship type. The relationship expressed in the sentence is classified according to the relationship type of the most similar relationship instances in a database.

The computation is done by leveraging min-hash and locality sensitive hashing for efficiently measuring the similarity between instances.



References
==========
David S. Batista, Rui Silva, Bruno Martins, Mário J. Silva , `A Minwise Hashing Method for Addressing Relationship Extraction from Text <http://davidsbatista.github.io/publications/minwise-wise_13.pdf>`_ in Web Information Systems Engineering (WISE), 2013

David Soares Batista, David Forte, Rui Silva, Bruno Martins, Mário Silva , `Exploring DBpedia and Wikipedia for Portuguese Semantic Relationship Extraction <http://davidsbatista.github.io/publications/minwise-linguamtica-13.pdf>`_ in  Linguamática, 5(1), 2013.

David S. Batista, Ph.D. Thesis, Large-Scale Semantic Relationship Extraction for Information Discovery (Chapter 4), Instituto Superior Técnico, University of Lisbon, 2016

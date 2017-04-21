Minwise Hashing for Relationship Extraction from Text
=====================================================

The MinHash-based Semantic Relationship Classifier (MuSICo) is an  on-line approach for extracting of semantic relationships, based on the idea of nearest neighbor classification.  

Instead of learning a statistical model, it finds the most similar relationship instances in a database and uses these similarities to make the decision of whether the sentence holds a certain relationship type. The sentence is classified according to the relationship type of the most similar relationship instances in a database.

The computation is done by leveraging min-hash and locality sensitive hashing for efficiently measuring the similarity between instances.


Usage:
=====

First just `ant` to compile the source code, which should generate `MuSICo.jar` based on `build.xml`
    
    
    ant
    
    
All the external libs needed by MuSICo are in the `libs/` directory. Then you can call MuSICo.jar with the following parameters, e.g.:

    java -cp libs/*:MuSICo.jar bin.Main semeval true 400 50 5

**parameters**

    MuSICo.jar bin.Main dataset true|false #min-hash-sigs #bands #kNN [train_file] [test_file]
    
    dataset           semeval wiki aimed wikipt
    true|false        generate shingles ? if false need to pass train_file/test_file
    #min-hash-sigs    number of hash signatures
    #bands            size of the LSH bands
    #kNN              number of closest neighbors to consider


References
==========
David S. Batista, Rui Silva, Bruno Martins, Mário J. Silva, [A Minwise Hashing Method for Addressing Relationship Extraction from Text](http://davidsbatista.net/assets/documents/publications/minwise-wise_13.pdf) in Web Information Systems Engineering (WISE), 2013

David Soares Batista, David Forte, Rui Silva, Bruno Martins, Mário Silva, [Exploring DBpedia and Wikipedia for Portuguese Semantic Relationship Extraction](http://davidsbatista.net/assets/documents/publications/minwise-linguamtica-13.pdf) in  Linguamática, 5(1), 2013.

David S. Batista, Ph.D. Thesis, [Large-Scale Semantic Relationship Extraction for Information Discovery (Chapter 4)](http://davidsbatista.net/assets/documents/publications/dsbatista-phd-thesis-2016.pdf), Instituto Superior Técnico, University of Lisbon, 2016

/*
 * (c) Copyright 2000, 2001, 2002, 2003, Hewlett-Packard Development Company, LP
 * [See end of file]
 */

/* CVS $Id: DC_11.java,v 1.3 2003-08-27 13:08:11 andy_seaborne Exp $ */
package com.hp.hpl.jena.vocabulary;
 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from file:vocabularies/dublin-core_11.xml 
 * @author Auto-generated by schemagen on 13 May 2003 08:51 
 */
public class DC_11 {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabalary as a string ({@value})</p> */
    public static final String NS = "http://purl.org/dc/elements/1.1/";
    
    /** <p>The namespace of the vocabalary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabalary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>A name given to the resource.</p> */
    public static final Property title = m_model.createProperty( "http://purl.org/dc/elements/1.1/title" );
    
    /** <p>An entity primarily responsible for making the content of the resource.</p> */
    public static final Property creator = m_model.createProperty( "http://purl.org/dc/elements/1.1/creator" );
    
    /** <p>The topic of the content of the resource.</p> */
    public static final Property subject = m_model.createProperty( "http://purl.org/dc/elements/1.1/subject" );
    
    /** <p>An account of the content of the resource.</p> */
    public static final Property description = m_model.createProperty( "http://purl.org/dc/elements/1.1/description" );
    
    /** <p>An entity responsible for making the resource available</p> */
    public static final Property publisher = m_model.createProperty( "http://purl.org/dc/elements/1.1/publisher" );
    
    /** <p>An entity responsible for making contributions to the content of the resource.</p> */
    public static final Property contributor = m_model.createProperty( "http://purl.org/dc/elements/1.1/contributor" );
    
    /** <p>A date associated with an event in the life cycle of the resource.</p> */
    public static final Property date = m_model.createProperty( "http://purl.org/dc/elements/1.1/date" );
    
    /** <p>The nature or genre of the content of the resource.</p> */
    public static final Property type = m_model.createProperty( "http://purl.org/dc/elements/1.1/type" );
    
    /** <p>The physical or digital manifestation of the resource.</p> */
    public static final Property format = m_model.createProperty( "http://purl.org/dc/elements/1.1/format" );
    
    /** <p>An unambiguous reference to the resource within a given context.</p> */
    public static final Property identifier = m_model.createProperty( "http://purl.org/dc/elements/1.1/identifier" );
    
    /** <p>A reference to a resource from which the present resource is derived.</p> */
    public static final Property source = m_model.createProperty( "http://purl.org/dc/elements/1.1/source" );
    
    /** <p>A language of the intellectual content of the resource.</p> */
    public static final Property language = m_model.createProperty( "http://purl.org/dc/elements/1.1/language" );
    
    /** <p>A reference to a related resource.</p> */
    public static final Property relation = m_model.createProperty( "http://purl.org/dc/elements/1.1/relation" );
    
    /** <p>The extent or scope of the content of the resource.</p> */
    public static final Property coverage = m_model.createProperty( "http://purl.org/dc/elements/1.1/coverage" );
    
    /** <p>Information about rights held in and over the resource.</p> */
    public static final Property rights = m_model.createProperty( "http://purl.org/dc/elements/1.1/rights" );
    
}


/*
 *  (c) Copyright 2003  Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

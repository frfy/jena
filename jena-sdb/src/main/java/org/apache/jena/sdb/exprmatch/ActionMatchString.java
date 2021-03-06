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

package org.apache.jena.sdb.exprmatch;

import org.apache.jena.sparql.core.Var ;
import org.apache.jena.sparql.expr.Expr ;

public class ActionMatchString extends ActionMatchBind
{
    @Override
    public boolean match(Var var, Expr expr, MapResult resultMap)
    {
        if ( ! expr.isConstant() )
            // throw new NoMatch("ActionMatchString: Not a variable: "+expr) ;
            return false ;
        
        if ( ! expr.getConstant().isString() )
            return false ;
        
        String x = expr.getConstant().getString() ;
        // Managed to get the string - pass on up.
        return super.match(var, expr, resultMap) ;
    }
}

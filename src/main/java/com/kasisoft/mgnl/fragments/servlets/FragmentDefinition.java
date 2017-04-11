package com.kasisoft.mgnl.fragments.servlets;

import org.apache.http.*;

import javax.jcr.*;

import java.util.function.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

/**
 * Each fragment consists of the following parameters:
 * 
 * <ul>
 *   <li><code>name</code>     : The segment below the fragment path.</li>
 *   <li><code>template</code> : The template URI.</li>
 *   <li><code>setup</code>    : A method filling the model map.</li> 
 * </ul>
 * 
 * The model map will definitely include the attributes <code>segmentName</code> and <code>subPath</code>.
 * The <code>segmentName</code> is the name associated with this fragment. The <code>subPath</code> is the
 * path below the <code>segmentName</code> (might be <code>null</code>).
 * 
 * For instance:
 * 
 * /fragments/menu          -> segmentName = 'menu', subPath = ''
 * /fragments/menu/341      -> segmentName = 'menu', subPath = '342'
 * /fragments/menu/main/abc -> segmentName = 'menu', subPath = 'main/abc'
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FragmentDefinition {

  Function<Map<String, Object>, Node>   nodeIdentifier;
  Consumer<Map<String, Object>>         modelInitializer;
  String                                contentType;
  String                                i18nBasename;
  String                                name;
  String                                template;
  Boolean                               getOrPost;
  int                                   errorCode         = HttpStatus.SC_INTERNAL_SERVER_ERROR;
  Class<?>                              modelClass;
  
  public boolean isValid() {
    return (modelInitializer != null)
        && (nodeIdentifier   != null)
        && (name             != null)
        && (template         != null);
  }
  
} /* ENDCLASS */

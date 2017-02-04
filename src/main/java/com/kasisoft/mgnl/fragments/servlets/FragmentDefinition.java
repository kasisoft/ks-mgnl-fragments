package com.kasisoft.mgnl.fragments.servlets;

import com.kasisoft.libs.common.constants.*;

import com.kasisoft.libs.common.text.*;

import org.apache.commons.lang3.builder.*;
import org.apache.http.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

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
public class FragmentDefinition {

  private Consumer<Map<String, Object>>         modelInitializer  = $ -> {};
  private Function<Map<String, Object>, Node>   nodeIdentifier    = $ -> null;
  private int                                   errorCode         = HttpStatus.SC_INTERNAL_SERVER_ERROR;
  private String                                i18nBasename;
  private String                                name;
  private String                                template;
  private String                                contentType       = MimeType.Html.getMimeType();
  private Boolean                               getOrPost;
  private Class<?>                              modelClass;
  
  public boolean isValid() {
    return (modelInitializer != null)
        && (nodeIdentifier   != null)
        && (name             != null)
        && (template         != null);
  }
  
  public void setModelClass( Class<?> newModelClass ) {
    modelClass = newModelClass;
  }
  
  public Class<?> getModelClass() {
    return modelClass;
  }
  
  public void setErrorCode( int newErrorCode ) {
    errorCode = newErrorCode;
  }
  
  public int getErrorCode() {
    return errorCode;
  }
  
  public void setContentType( @Nullable String newContentType ) {
    contentType = StringFunctions.cleanup( newContentType );
    if( contentType == null ) {
      contentType = MimeType.Html.getMimeType();
    }
  }
  
  @Nonnull
  public String getContentType() {
    return contentType;
  }
  
  public void setNodeIdentifier( @Nullable Function<Map<String, Object>, Node> newNodeIdentifier ) {
    nodeIdentifier  = newNodeIdentifier != null ? newNodeIdentifier : $ -> null;
  }
  
  @Nonnull
  public Function<Map<String, Object>, Node> getNodeIdentifier() {
    return nodeIdentifier;
  }
  
  public void setModelInitializer( @Nullable Consumer<Map<String, Object>> newModelInitializer ) {
    modelInitializer = newModelInitializer != null ? newModelInitializer : $ -> {};
  }
  
  @Nonnull
  public Consumer<Map<String, Object>> getModelInitializer() {
      return modelInitializer;
  }
  
  public void setTemplate( @Nonnull String newTemplate ) {
    template = StringFunctions.cleanup( newTemplate );
  }
  
  @Nonnull
  public String getTemplate() {
      return template;
  }

  public void setName( @Nonnull String newName ) {
    name = StringFunctions.cleanup( newName );
  }
  
  @Nonnull
  public String getName() {
      return name;
  }
  
  public void setI18nBasename( @Nonnull String newI18nBasename ) {
    i18nBasename = StringFunctions.cleanup( newI18nBasename );
  }
  
  @Nullable
  public String getI18nBasename() {
    return i18nBasename;
  }
  
  public void setGetOrPost( Boolean newGetOrPost ) {
    getOrPost = newGetOrPost;
  }
  
  public Boolean getGetOrPost() {
    return getOrPost;
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString( this );
  }

} /* ENDCLASS */

package com.kasisoft.mgnl.fragments.servlets;

import static com.kasisoft.mgnl.fragments.internal.Messages.*;

import info.magnolia.module.site.*;
import info.magnolia.module.site.functions.*;

import info.magnolia.context.*;

import info.magnolia.jcr.util.*;

import com.kasisoft.libs.common.constants.*;

import com.kasisoft.libs.common.model.*;
import com.kasisoft.mgnl.util.*;

import org.apache.http.*;

import javax.servlet.http.*;

import javax.servlet.*;

import javax.annotation.*;
import javax.inject.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

import java.io.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

import freemarker.template.*;
import info.magnolia.cms.filters.*;
import info.magnolia.freemarker.*;
import info.magnolia.objectfactory.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FragmentServlet extends HttpServlet implements SelfMappingServlet {

  @Inject
  transient FreemarkerHelper        fmHelper;
  
  @Inject
  transient SiteFunctions           siteFunctions;

  Map<String, FragmentDefinition>   fragments;

  @Getter @Setter
  String                            i18nBasename;
  
  String                            servletBase;

  public FragmentServlet( @Nonnull String basePath ) {
    servletBase = basePath;
    fragments   = new HashMap<>();
  }

  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
    process( request, response, true );
  }

  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
    process( request, response, false );
  }

  /**
   * Extracts the identifying segment for the {@link FragmentDefinition} and an optional subpath.
   * 
   * @param requestUri   The uri that is being processed.
   * 
   * @return   A segment plus it's optional subpath. Both non null. If the {@link Pair} itself is <code>null</code>
   *           the supplied URI will not be handled by this servlet.
   */
  private Pair<String, String> getFragmentPath( @Nonnull String requestUri ) {
    StringBuilder         uri    = new StringBuilder( requestUri );
    int                   idx    = uri.indexOf( servletBase );
    Pair<String, String>  result = null;
    if( idx != -1 ) {
      // drop the url base including slashes
      uri.delete( 0, idx + servletBase.length() );
      while( (uri.length() > 0) && (uri.charAt(0) == '/') ) {
        uri.deleteCharAt(0);
      }
      // drop the suffix if there's one
      idx = uri.lastIndexOf( "." );
      if( idx != -1 ) {
        uri.delete( idx, uri.length() );
      }
      // determine segment and subpath
      idx = uri.indexOf( "/" );
      if( idx != -1 ) {
        // segment + subpath
        result = new Pair<>( uri.substring( 0, idx ), uri.substring( idx + 1 ) );
      } else {
        // segment only
        result = new Pair<>( uri.toString(), "" );
      }
    }
    return result;
  }
  
  private void process( HttpServletRequest request, HttpServletResponse response, Boolean post ) {
    
    // identify the fragment that is supposed to be rendered
    Pair<String, String>  fragmentpath = getFragmentPath( request.getRequestURI() );
    FragmentDefinition    fragment     = fragments.get( fragmentpath.getValue1() );
    if( isValidFragment( fragment ) ) {
      
      // verify that the method is allowed if specified by the fragment
      if( isAllowed( fragment.getGetOrPost(), post ) ) {
        
        try {
          
          // perform the actual rendering
          render( fragment, fragmentpath.getValue2(), response );
          
          response.setContentType( getContentType( fragment ) );
          response.setStatus( HttpStatus.SC_OK );
          
        } catch( Exception ex ) {
          // wow, something went wrong here
          log.error( error_failed_to_render.format( fragmentpath.getValue1(), ex.getLocalizedMessage() ), ex );
          response.setStatus( fragment.getErrorCode() );
        }
        
      } else {
        // this method is not allowed for this fragment
        response.setStatus( HttpStatus.SC_NOT_FOUND );
      }
      
    } else {
      response.setStatus( HttpStatus.SC_NOT_FOUND );
    }
    
  }
  
  private boolean isValidFragment( FragmentDefinition fragment ) {
    boolean result = fragment != null;
    if( result ) {
      result = fragment.isValid();
      if( ! result ) {
        log.warn( error_invalid_fragment_definition.format( fragment ) );
      }
    }
    return result;
  }
  
  private String getContentType( FragmentDefinition fragment ) {
    String result = fragment.getContentType();
    if( result == null ) {
      result = MimeType.Html.getMimeType();
    }
    return result;
  }
  
  private boolean isAllowed( Boolean required, boolean ispost ) {
    boolean result = true;
    if( required != null ) {
      // the FragmentDefinition is only supposed to work on a GET or POST request
      result = required.booleanValue() == ispost;
    }
    return result;
  }
  
  private Consumer<Map<String, Object>> getModelInitializer( FragmentDefinition fragment ) {
    Consumer<Map<String, Object>> result = fragment.getModelInitializer();
    if( result == null ) {
      result = $ -> {};
    }
    return result;
  }
  
  private Function<Map<String, Object>, Node> getNodeIdentifier( FragmentDefinition fragment ) {
    Function<Map<String, Object>, Node> result = fragment.getNodeIdentifier();
    if( result == null ) {
      result = $ -> null;
    }
    return result;
  }

  private void render( FragmentDefinition fragment, String subpath, HttpServletResponse response ) throws TemplateException, IOException {
    
    Map<String, Object> model = new HashMap();
    
    // apply default fragment related values
    model.put( "segment" , fragment.getName() );
    model.put( "subpath" , subpath            );
    
    // provide the definition itself, so it can be overridden with custom modifications to be processed
    // by the model initializer
    model.put( "def"     , fragment           ); 
    
    // initialize the current model
    getModelInitializer( fragment ).accept( model );
    
    // setup site and nodes for the aggregation state if desired. 
    Node content = getNodeIdentifier( fragment ).apply( model );
    if( content != null ) {
      if( MgnlContext.getAggregationState() instanceof ExtendedAggregationState ) {
        ExtendedAggregationState state = (ExtendedAggregationState) MgnlContext.getAggregationState();
        state.setSite( siteFunctions.site( content ) );
        state.setCurrentContentNode( content );
        state.setMainContentNode( NodeFunctions.getPageNode( content ) );
      }
      model.put( "content", new ContentMap( content ) );
    } else {
      // there's no dedicated node so lets see if we can pass the current content node
      WebContext webcontext = MgnlContext.getWebContextOrNull();
      if( (webcontext != null) && (webcontext.getAggregationState() != null) && (webcontext.getAggregationState().getCurrentContentNode() != null) ) {
        model.put( "content", new ContentMap( webcontext.getAggregationState().getCurrentContentNode() ) );
      }
    }
    
    if( MgnlContext.getAggregationState() != null ) {
      model.put( "state", MgnlContext.getAggregationState() );
    }
    
    String i18n = getI18n( fragment );
    if( i18n != null ) {
      model.put( "i18n", i18n );
    }
    
    if( fragment.getModelClass() != null ) {
      Object obj = Components.getComponentProvider().newInstance( fragment.getModelClass(), model );
      if( obj != null ) {
        model.put( "model", obj );
      }
    }

    fmHelper.render( fragment.getTemplate(), model, response.getWriter() );
    
  }
  
  private String getI18n( FragmentDefinition fragment ) {
    String result = fragment.getI18nBasename();
    if( result == null ) {
      result = i18nBasename;
    }
    return result;
  }

  protected void addFragment( @Nonnull FragmentDefinition fragment ) {
    fragments.put( fragment.getName(), fragment );
  }

  @Nonnull
  public Map<String, FragmentDefinition> getFragments() {
    return fragments;
  }
  
  @Override
  public String getSelfMappingPath() {
    return servletBase;
  }

} /* ENDCLASS */

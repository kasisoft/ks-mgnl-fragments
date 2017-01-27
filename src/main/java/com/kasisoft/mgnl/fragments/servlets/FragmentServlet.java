package com.kasisoft.mgnl.fragments.servlets;

import info.magnolia.templating.functions.*;

import info.magnolia.templating.freemarker.*;

import info.magnolia.objectfactory.*;

import info.magnolia.module.site.*;
import info.magnolia.module.site.functions.*;

import info.magnolia.dam.templating.functions.*;

import info.magnolia.jcr.util.*;

import com.kasisoft.libs.common.text.*;

import com.kasisoft.libs.common.model.*;
import com.kasisoft.mgnl.util.*;

import org.apache.http.*;
import org.slf4j.*;

import javax.servlet.http.*;

import javax.servlet.*;

import javax.annotation.*;
import javax.inject.*;
import javax.jcr.*;

import java.util.*;

import java.io.*;

import freemarker.template.*;
import info.magnolia.cms.filters.*;
import info.magnolia.context.*;
import info.magnolia.freemarker.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FragmentServlet extends HttpServlet implements SelfMappingServlet {

  private static final Logger log = LoggerFactory.getLogger( FragmentServlet.class );

  @Inject
  private transient FreemarkerHelper        fmHelper;
  
  @Inject
  private transient SiteFunctions           siteFunctions;

  private Map<String, FragmentDefinition>   fragments;

  private String                            i18nBasename;
  
  private String                            servletBase;

  public FragmentServlet( @Nonnull String basePath ) {
    servletBase = basePath;
    fragments   = new HashMap<>();
  }

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
      idx = uri.indexOf( "/" );
      if( idx != -1 ) {
        result = new Pair<>( uri.substring( 0, idx ), uri.substring( idx + 1 ) );
      } else {
        result = new Pair<>( uri.toString(), "" );
      }
    }
    return result;
  }
  
  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
    process( request, response, true );
  }

  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
    process( request, response, false );
  }

  private void process( HttpServletRequest request, HttpServletResponse response, Boolean post ) {
    
    // identify the fragment that is supposed to be rendered
    Pair<String, String>  fragmentpath = getFragmentPath( request.getRequestURI() );
    FragmentDefinition    fragment     = fragments.get( fragmentpath.getValue1() );
    if( (fragment != null) && fragment.isValid() ) {
      
      // verify that the method is allowed if specified by the fragment
      boolean allowed = true;
      if( fragment.getGetOrPost() != null ) {
        allowed = fragment.getGetOrPost().booleanValue() == post;
      }
      
      if( allowed ) {
        
        try {
          
          render( fragment, fragmentpath.getValue2(), response );
          
          if( fragment.getContentType() != null ) {
            response.setContentType( fragment.getContentType() );
          }
          
          response.setStatus( HttpStatus.SC_OK );
          
        } catch( Exception ex ) {
          log.error( ex.getLocalizedMessage(), ex );
          response.setStatus( fragment.getErrorCode() );
        }
        
      } else {
        // this method is not allowed for this fragment
        response.setStatus( HttpStatus.SC_NOT_FOUND );
      }
      
    } else {
      if( (fragment != null) && (! fragment.isValid()) ) {
        log.warn( "Invalid fragment definition: {}", fragment );
      }
      response.setStatus( HttpStatus.SC_NOT_FOUND );
    }
  }

  private void render( FragmentDefinition fragment, String subpath, HttpServletResponse response ) throws TemplateException, IOException {
    
    Map<String, Object> model = new HashMap();
    
    // apply default fragment related values
    model.put( "segment" , fragment.getName() );
    model.put( "subPath" , subpath            );
    
    // initialized the current model
    fragment.getModelInitializer().accept( model );
    
    // setup site and nodes for the aggregation state if desired. 
    Node content = fragment.getNodeIdentifier().apply( model );
    if( content != null ) {
      if( MgnlContext.getAggregationState() instanceof ExtendedAggregationState ) {
        ExtendedAggregationState state = (ExtendedAggregationState) MgnlContext.getAggregationState();
        state.setSite( siteFunctions.site( content ) );
        state.setCurrentContentNode( content );
        state.setMainContentNode( NodeFunctions.getPageNode( content ) );
      }
      model.put( "content", new ContentMap( content ) );
    }
    
    // provide some context objects
    model.put( "cms", Components.getComponent( Directives.class ) );
    model.put( "cmsFn", Components.getComponent( TemplatingFunctions.class ) );
    model.put( "siteFn", siteFunctions );
    model.put( "damFn", Components.getComponent( DamTemplatingFunctions.class ) );
    String i18n = getI18n( fragment );
    if( i18n != null ) {
      model.put( "i18n", i18n );
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
  
  public void setI18nBasename( String newI18nBasename ) {
    i18nBasename = StringFunctions.cleanup( newI18nBasename );
  }
  
  public String getI18nBasename() {
    return i18nBasename;
  }

  @Override
  public String getSelfMappingPath() {
    return servletBase;
  }

} /* ENDCLASS */

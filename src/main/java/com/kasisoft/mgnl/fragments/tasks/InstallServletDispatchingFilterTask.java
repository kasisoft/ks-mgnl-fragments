package com.kasisoft.mgnl.fragments.tasks;

import static com.kasisoft.mgnl.fragments.internal.Messages.*;

import info.magnolia.module.delta.*;

import com.kasisoft.libs.common.text.*;

import com.kasisoft.mgnl.versionhandler.*;
import com.kasisoft.mgnl.versionhandler.tbm.*;

import javax.servlet.http.*;

import javax.annotation.*;

import java.util.function.*;

import java.util.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class InstallServletDispatchingFilterTask extends JcrConfigurationTask {

  public InstallServletDispatchingFilterTask( @Nonnull Class<? extends HttpServlet> servletClass ) {
    this( null, servletClass, null, null );
  }
  
  public InstallServletDispatchingFilterTask( @Nonnull Class<? extends HttpServlet> servletClass, Map<String, String> mappings ) {
    this( null, servletClass, mappings, null );
  }

  public InstallServletDispatchingFilterTask( @Nullable String name, @Nonnull Class<? extends HttpServlet> servletClass, Map<String, String> mappings ) {
    this( name, servletClass, mappings, null );
  }
  
  public InstallServletDispatchingFilterTask( @Nullable String name, @Nonnull Class<? extends HttpServlet> servletClass, Map<String, String> mappings, @Nullable String after ) {
    super( task_install_filter_servlet, task_install_filter_servlet_desc.format( servletClass.getName() ) );
    if( name == null ) {
      name = StringFunctions.firstDown( servletClass.getSimpleName() );
    }
    register( servlet( name, servletClass, mappings ) );
    if( after != null ) {
      register( new OrderNodeAfterTask( task_positioning_filter_servlet.format( name, after ), String.format( "/server/filters/servlets/%s", name ), after ) );
    }
  }

  private TreeBuilder servlet( String name, Class<? extends HttpServlet> servletClass, Map<String, String> mappings ) {
    TreeBuilder result = new TreeBuilder();
    Function<TreeBuilder, ServletDispatchingFilterModifier> apiProvider = ServletDispatchingFilterModifier::create;
    ((ServletDispatchingFilterModifier) result.sStartModifier( apiProvider ))
      .sServlet( name, servletClass )
        .enabled( true )
        .comment( name )
        .mapping( mappings )
      .sEnd()
    .sEndModifier();
    return result;
  }

} /* ENDCLASS */

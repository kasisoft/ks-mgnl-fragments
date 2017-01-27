package com.kasisoft.mgnl.fragments.internal;

import info.magnolia.module.*;

import org.slf4j.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FragmentsModule implements ModuleLifecycle {

  private static final Logger log = LoggerFactory.getLogger( FragmentsModule.class );
  
  @Override
  public void start( ModuleLifecycleContext moduleLifecycleContext ) {
    log.info( "Starting FragmentsModule..." );
  }

  @Override
  public void stop( ModuleLifecycleContext moduleLifecycleContext ) {
    log.info( "Stopped FragmentsModule !" );
  }
  
} /* ENDCLASS */
